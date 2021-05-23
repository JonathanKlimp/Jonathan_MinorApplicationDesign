package nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping;

import io.github.bonigarcia.wdm.WebDriverManager;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugDao;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Naomi Hindriks
 */
@Component
public class GgzStandaardenBijwerkingenWebScraper implements AbstractWebScraper {
    private DrugDao drugDao;
    private String url;
    private String csvFileLocation;
    private static final Logger LOGGER = LoggerFactory.getLogger(GgzStandaardenBijwerkingenWebScraper.class);

    private Map<String, List<String>> drugGroupNames = new HashMap<>() {{
        put("ontpillen", new ArrayList<>());
        put("ggzstandaarden", new ArrayList<>());
    }};
    private WebDriver driver;

    private GgzStandaardenBijwerkingenWebScraper(
            @Value("${ggz.generieke.module.bijwerkingen}") String url,
            @Value("${csv.group.between.websites.combiner}") String csvFile) {
        this.url = url;
        this.csvFileLocation = csvFile;
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
        this.openWebsiteInDriver(this.url, "#chapter-detail-paragraph-5b04514a-050f-44e6-836f-80a7cd1d2d3f .ul-level-0");

        for (int i = 0; i < drugGroupNames.get("ontpillen").size(); i++) {
            continue;
        }

        int i;
        for (String drugGroup : drugGroupNames.get("ontpillen")) {
            i = drugGroupNames.get("ontpillen").indexOf(drugGroup);
            continue;
        }

        for (String linkText : this.drugGroupNames.get("ggzstandaarden")) {
            openWebsiteInDriver(this.url, "#chapter-detail-paragraph-5b04514a-050f-44e6-836f-80a7cd1d2d3f .ul-level-0");
            List<WebElement> hyperLink = driver.findElements(By.linkText(linkText));

            if (hyperLink.size() > 0) {
                System.out.println(hyperLink.get(0).getText());
                hyperLink.get(0).click();
                this.waitForPageLoad("#main-content");

                Document doc = Jsoup.parse(driver.getPageSource());
                System.out.println(doc.select("#main-content h1.heading").text());
            }
        }

        this.driver.close();
    }

//
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

    private String getSideEffects() {

        return "";
    }

    private Document getJsoupDocument() {
        Document doc = Jsoup.parse(this.driver.getPageSource());
        return doc;
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

//    TODO what sould happen if maxSecondsToTry runs out? Add maxSecondsToTry to application.propperties?
    private void waitForPageLoad(String cssSelectorCheck) throws InterruptedException {
        int maxSecondToTry = 10;
        boolean done = false;
        while(!done && maxSecondToTry > 0) {
            maxSecondToTry--;
            if (driver.findElements( By.cssSelector(cssSelectorCheck) ).size() > 0) {
                done = true;
            } else {
                TimeUnit.SECONDS.sleep(1);
            }
        }
    }
}
