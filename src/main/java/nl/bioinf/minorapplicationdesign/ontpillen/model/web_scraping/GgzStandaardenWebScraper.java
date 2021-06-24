package nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping;

import io.github.bonigarcia.wdm.WebDriverManager;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugDao;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugGroup;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content.Content;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content.ContentLeaf;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content.ContentNode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;

/**
 *
 * @author Naomi Hindriks
 */
@Component
public class GgzStandaardenWebScraper implements AbstractWebScraper {
    private DrugDao drugDao;
    private String url;
    private String csvFileLocation;
    private int maxWaitingTime;
    private static final Logger LOGGER = LoggerFactory.getLogger(GgzStandaardenWebScraper.class);

    private Map<String, List<String>> drugGroupNames = Map.of("ontpillen", new ArrayList<>(), "ggzstandaarden", new ArrayList<>());
    private WebDriver driver;

    private GgzStandaardenWebScraper(
            @Value("${ggz.generieke.module.bijwerkingen.url}") String url,
            @Value("${csv.group.between.websites.combiner}") String csvFile,
            @Value("${ggz.standaarden.waiting.time.seconds}") String maxWaitingTime) {
        this.url = url;
        this.csvFileLocation = csvFile;
        this.maxWaitingTime = Integer.parseInt(maxWaitingTime);
    }

    @Autowired
    public void setDrugDao(DrugDao drugDao) {
        this.drugDao = drugDao;
    }

    @Override
    public void parseHtml() throws Exception {
        LOGGER.info("Running parseHtml");
        this.fillDrugGroupNamesWithCsv();
        this.getDriver();

        for (int i = 0; i < drugGroupNames.get("ontpillen").size(); i++) {
            DrugGroup drug = (DrugGroup) this.drugDao.getDrugByName(drugGroupNames.get("ontpillen").get(i));
            // System.out.println("Drugname = " + drug.getName());
            String hyperLinkTextToClick = drugGroupNames.get("ggzstandaarden").get(i);
            this.openWebsiteInDriver(this.url, "#chapter-detail-paragraph-5b04514a-050f-44e6-836f-80a7cd1d2d3f .ul-level-0");
            List<WebElement> hyperLinks = driver.findElements(By.linkText(hyperLinkTextToClick));

            if (hyperLinks.size() > 0) {
                this.processDrugGroupHyperLink(hyperLinks.get(0), drug);
            }
        }
        this.driver.close();
    }

    private void processDrugGroupHyperLink(WebElement hyperLink, DrugGroup drugGroup) throws InterruptedException {
        System.out.println(hyperLink.getText());
        hyperLink.click();
        this.waitForPageLoad("#main-content");

        Document doc = Jsoup.parse(driver.getPageSource());

        String mainContentHeading = doc.select("#main-content h1.heading").text();
        mainContentHeading = getTitleWithoutNumber(mainContentHeading);

        ContentNode contentNode = new ContentNode();
        contentNode.setContentTitle(mainContentHeading);

        drugGroup.getSideEffects().addSideEffectPractitioner("ggz-standaarden", contentNode);

        Elements elements = doc.select(".chapter-content");

        Elements elementsOfInterest = elements.stream()
                .filter(element -> element.text().contains("Vroege onderkenning en preventie") || element.text().contains("Behandeling, begeleiding en terugvalpreventie"))
                .collect(Collector.of(
                        () -> new Elements(),
                        (Elements resultElements, Element element) -> {resultElements.add(element);},
                        (Elements result1, Elements result2) -> {Elements result = new Elements(); result.addAll(result1); result.addAll(result2); return result;}
                ));

        processHtmlElement(elementsOfInterest, contentNode);
    }

    /**
     * @param title Title from the website ggzstandaarden that starts with 1 or two numbers and a . before the actual title name
     * @return the title name without numbers or a .
     */
    private static String getTitleWithoutNumber(String title) {
        Pattern pattern = Pattern.compile("(\\d[\\.?\\d?]*\\s)(.*)");
        Matcher matcher = pattern.matcher(title);

        if (matcher.find()) {
            return matcher.group(2);
        }
        else {
            return title;
        }
    }

    private static void processHtmlElement(Elements elements, ContentNode currentContentNode) {
        for (Element element : elements) {
            if (element.tagName().equals("div")) {
                processHtmlElement(element.children(), currentContentNode);
            } else if (element.tagName().equals("h2") || element.tagName().equals("h3")) {
                ContentNode newContentNode = processTitleElement(element, currentContentNode);
                processHtmlElement(element.nextElementSiblings(), newContentNode);
                break;
            } else if (element.tagName().equals("p")) {
                if (processParagraphElement(element, currentContentNode)) {
                    break;
                }
            } else if (element.tagName().equals("ul") || element.tagName().equals("ol")) {
                processListElement(element, currentContentNode);
            } else if (element.tagName().equals("li")) {
                processListItemElement(element, currentContentNode);
            } else {
                System.out.println(element);
            }
        }
    }

    private static void processListItemElement(Element element, ContentNode currentContentNode) {
        Map<String, Element> copiesOfElement = Util.getCopiesOfElementWithoutAndOnlySpanEmSubA(element);

        if (copiesOfElement.get("elementWithoutSpanEmSubA").childrenSize() == 0) {
            ContentLeaf newLeaf = new ContentLeaf();
            newLeaf.setContentType("PARAGRAPH");
            newLeaf.addContent(element.text());
            currentContentNode.addContent(newLeaf);
        } else if (copiesOfElement.get("elementOnlySpanEmSubA").text().length() > 0) {
            ContentLeaf newLeaf = new ContentLeaf();
            newLeaf.setContentType("PARAGRAPH");
            newLeaf.addContent(copiesOfElement.get("elementOnlySpanEmSubA").text());

            ContentNode newNode = new ContentNode();
            newNode.addContent(newLeaf);
            currentContentNode.addContent(newNode);

            Elements  childrenToProcess = copiesOfElement.get("elementWithoutSpanEmSubA").children();
            processHtmlElement(childrenToProcess,newNode);
        } else if (copiesOfElement.get("elementWithoutSpanEmSubA").childrenSize() == 1) {
            processHtmlElement(element.children(), currentContentNode);
        } else {
            ContentNode newNode = new ContentNode();
            currentContentNode.addContent(newNode);
            processHtmlElement(element.children(), newNode);
        }

    }

    private static void processListElement(Element element, ContentNode currentContentNode) {
        Element copyOfElement = element.clone();
        copyOfElement.select("li a, li span, li em, li sub").remove();

        if (Util.getMaxDepth(copyOfElement) > 2) {
//            If the depth is more than 2, (at least one of) the list elements will have children and therefore
//            this will be a node that needs further processing
            ContentNode newNode = new ContentNode();
            newNode.setContentType("LIST");
            currentContentNode.addContent(newNode);
            processHtmlElement(element.children(), newNode);
        } else if (Util.getMaxDepth(copyOfElement) >= 1) {
            ContentLeaf newLeaf = new ContentLeaf();
            newLeaf.setContentType("LIST");
            newLeaf.setContent(element.children().eachText());
            currentContentNode.addContent(newLeaf);
        }
    }

    private static boolean processParagraphElement(Element element, ContentNode currentContentNode) {
        Content newContent;

        if (elementHasChildrenOfTag(element, "a")) {
            try {
                element = removeHyperlinkFromElement(element);
            } catch (IllegalArgumentException e) {
                // do nothing
            }
        }

        if (pElementIsTitle(element)) {
            newContent = processTitleElement(element, currentContentNode);
            processHtmlElement(element.nextElementSiblings(), (ContentNode) newContent);
            return true;
        } else {
            newContent = new ContentLeaf();
            currentContentNode.addContent(newContent);
            ((ContentLeaf) newContent).addContent(element.text());
            return false;
        }
    }

    private static boolean pElementIsTitle(Element element) {
        return (elementHasChildrenOfTag(element, "strong")) &&
                (!elementHasChildrenOfTag(element.select("strong").get(0), "em"));
    }


    private static boolean elementHasChildrenOfTag(Element element, String tagName) {
        return element.children().stream()
                .anyMatch((Element e) -> {return e.tagName().equals(tagName);});
    }

    private static ContentNode processTitleElement(Element element, ContentNode currentContentNode) {
        ContentNode newContentNode = new ContentNode();
        currentContentNode.addContent(newContentNode);
        newContentNode.setContentTitle(getTitleWithoutNumber(element.text()));
        return newContentNode;
    }

    private static Element removeHyperlinkFromElement(Element element) {
        Pattern pattern = Pattern.compile("(?:.*?)(("+ Pattern.quote("(") + "?)([Zz]ie\\s(?:ook\\s)?(?:aanbevelingen\\s)?(?:</?strong>)?<a)(.*?)(</a>)(?:<strong>)?(?:" + Pattern.quote(")") + ")?\\.?)");
        Matcher matcher = pattern.matcher(element.outerHtml());
        if (matcher.find()) {
            Document doc = Jsoup.parse(element.outerHtml().replace(matcher.group(1), ""));
            return doc.select("html body *").get(0);
        } else {
            throw new IllegalArgumentException("Element has no hyperlink or hyperlink is not removable");
        }
    }

    private void fillDrugGroupNamesWithCsv() throws IOException {
        String row;

        BufferedReader csvReader = new BufferedReader(new FileReader(this.csvFileLocation));
        boolean firstline = true;
        while ((row = csvReader.readLine()) != null) {
            if (!firstline) {
                if (row.endsWith(";")) {
                    row = row + " ";
                }
                String[] data = row.split(";");
                this.drugGroupNames.get("ontpillen").add(data[0]);
                this.drugGroupNames.get("ggzstandaarden").add(data[2]);
            }
            firstline = false;
        }
        csvReader.close();
    }

    private void getDriver() {
        WebDriverManager.chromedriver().browserVersion("90.0.4430.78").setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200","--ignore-certificate-errors");
        this.driver = new ChromeDriver(options);
    }

    private void openWebsiteInDriver(String url, String cssSelectorCheck) throws InterruptedException {
        this.driver.get(url);
        waitForPageLoad(cssSelectorCheck);
    }

    //    TODO what sould happen if maxSecondsToTry runs out?
    private void waitForPageLoad(String cssSelectorCheck) throws InterruptedException {
        int maxSecondToTry = this.maxWaitingTime;
        boolean done = false;
        while(!done && maxSecondToTry > 0) {
            maxSecondToTry--;
            if (this.driver.findElements( By.cssSelector(cssSelectorCheck) ).size() > 0) {
                done = true;
            } else {
                TimeUnit.SECONDS.sleep(1);
            }
        }
    }
}
