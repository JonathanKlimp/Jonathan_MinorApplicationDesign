package nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping.nhg;

import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugDao;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content.Content;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content.ContentLeaf;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content.ContentNode;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content.UseIndication;
import nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping.AbstractWebScraper;
import nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping.Util;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * In this class it is assumed that the HTML is always structured so that the loose text in an element comes before
 * other elements (see example correct way).
 * If it is the other way around (see example wrong way) the content will not be saved in the propper order.
 *
 * Example correct way:
 * <div>
 *     Loose text
 *     <p>other elements</p>
 * </div>
 *
 * Example wrong way:
 * <div>
 *     <p>other elements</p>
 *     Loose text
 * </div>
 *
 * @author Naomi Hindriks
 */
@Component
public class RichtlijnenNhgWebScraper implements AbstractWebScraper {
    private DrugDao drugDao;
    private String url;
    private String csvFileLocation;
    private Map<String, List<String>> drugIndicationsNames = new HashMap<>() {{
        put("ontpillen", new ArrayList<>());
        put("nhgrichtlijnen", new ArrayList<>());
    }};
    private static final Logger LOGGER = LoggerFactory.getLogger(RichtlijnenNhgWebScraper.class);


    @Autowired
    public void setDrugDao(DrugDao drugDao) {
        this.drugDao = drugDao;
    }

    private RichtlijnenNhgWebScraper(
            @Value("${nhg.richtlijnen.indicaties.base.url}") String url,
            @Value("${csv.indication.between.websites.combiner}") String csvFile) {
        this.url = url;
        this.csvFileLocation = csvFile;
    }

    @Override
    public void parseHtml() throws IOException {
        this.readDrugIndicationsNamesFromCsv();
        LOGGER.info("Parsing html");

        for (int i = 0; i < this.drugIndicationsNames.get("ontpillen").size(); i++) {
            String ontpillenIndicationName = this.drugIndicationsNames.get("ontpillen").get(i).strip();
            String nhgIndicationName = this.drugIndicationsNames.get("nhgrichtlijnen").get(i).strip();

            if(ontpillenIndicationName.equals("") || nhgIndicationName.equals("")) {
                continue;
            }

            Document doc = this.getIndicationWebDocument(nhgIndicationName);
            Map<String, Elements> contentOfInterest = this.getContentOfInterest(doc);
            UseIndication currentUseIndication = this.drugDao.getUseIndication(ontpillenIndicationName);

            processContentOfInterest(contentOfInterest, currentUseIndication);
        }
    }

    /**
     * For every entry in contentOfInterest map , add a ContentNode to the current UseIndication for the practitioner
     * and set the title of this node to the key of the current map entry. Then process the value of the current
     * entry further into ContentNodes and ContentLeafs.
     * @param contentOfInterest Map with String as key that will be the title of a ContentNode and a Jsoup Elements
     *                          object as value, that will be processed in the content of the ContentNode
     * @param currentUseIndication UseIndication object that this content belongs to.
     */
    private void processContentOfInterest(Map<String, Elements> contentOfInterest, UseIndication currentUseIndication) {
        for (Map.Entry<String,Elements> entry : contentOfInterest.entrySet()) {
            if (entry.getValue().size() > 0) {
                ContentNode newContentNode = new ContentNode();
                String title = entry.getKey();
                newContentNode.setContentTitle(title);
                currentUseIndication.addContentPractitioner(title, newContentNode);
                this.process(newContentNode, entry.getValue());
            }
        }
    }

    private void process(ContentNode currentContentNode, Elements elementsToProcess) {
        for (Element element : elementsToProcess) {
            if (element.classNames().contains("summary-main-text-crosslink") || (element.classNames().contains("embedded-entity") &&  element.classNames().contains("literature"))) {
//                If the element has the class summary-main-text-crosslink or the classes embedded-entity and literature, skip this element
                LOGGER.debug("Skipping element");
                continue;
            } else if(this.elementIsTitle(element)) {
//                If the element is a title element execute this block
                LOGGER.debug("Processing title element");
                if (this.processTitleElement(currentContentNode, element)) {
                    break;
                }
            } else {
                switch(element.tagName()) {
                    case "ul":
                    case "ol":
                        LOGGER.debug("Processing list");
                        this.processListElement(currentContentNode, element);
                        break;
                    case "p":
                        LOGGER.debug("Processing paragraph");
                        processParagraphElement(currentContentNode, element);
                        break;
                    case "div":
                        LOGGER.debug("Processing div");
                        processDivElement(currentContentNode, element);
                        break;
                    case "li":
                        LOGGER.debug("Processing list element");
                        processListItemElement(currentContentNode, element);
                        break;
                    case "table":
                        LOGGER.debug("Processing table");
                        processTableElement(currentContentNode, element);
                        break;
                    case "thead":
                    case "tbody":
                    case "tfoot":
                        LOGGER.debug("Processing table section");
                        processTableSectionElement(currentContentNode, element);
                        break;
                    case "tr":
                        LOGGER.debug("Processing table row");
                        processTableRowElement(currentContentNode, element);
                        break;
                    case "th":
                    case "td":
                        LOGGER.debug("Processing td/th element");
                        processTableDataElement(currentContentNode, element);
                        break;
                    default:
                        LOGGER.error("not able to process: " + element);
                        throw new IllegalArgumentException("Unable to process element with tag " + element.tagName() + " and classes " + element.classNames());
                }
            }
        }
    }

    /**
     * Processes a title element into a ContentNode or ContentLeaf object or set title from currentNode to text in
     * the title element
     * @param currentContentNode The node to process the title into
     * @param element The title element to process
     * @return a boolean indicating if a new node has been started, if false the title of the currentContentNode has been set
     */
    protected boolean processTitleElement(ContentNode currentContentNode, Element element) {
        if (!this.elementIsTitle(element)) {
            String message = "element should be a title element, if the element is given as paremeter to the method elementIsTitle, it should return true";
            throw new IllegalArgumentException(message);
        }
        if (element.elementSiblingIndex() != 0) {
            try {
                Element prevTitleElement = this.getPrevTitleElement(element);
                int compareValue = TitleElementComparerNhg.compareTo(element, prevTitleElement);
                if (compareValue < 0) {
                    this.createNewLeafOrNodeWithTitle(currentContentNode, element);
                } else if (compareValue > 0) {
                    ContentNode higherNode = this.getContentNodeOfPrevTitleOfSameHeightOrHigher(element, currentContentNode);
                    this.createNewLeafOrNodeWithTitle(higherNode, element);
                } else {
                    this.createNewLeafOrNodeWithTitle(currentContentNode.getParent(), element);
                }
            } catch (IllegalArgumentException e) {
                this.createNewLeafOrNodeWithTitle(currentContentNode, element);
            }
        } else {
                if (currentContentNode.getContentTitle() != null) {
                    this.createNewLeafOrNodeWithTitle(currentContentNode, element);
                } else {
                    currentContentNode.setContentTitle(element.text());
                    return false;
                }
            }
        return true;
    }

    private void createNewLeafOrNodeWithTitle(ContentNode parentNode, Element titleElement) {
        boolean siblingElementTitle = false;
        for (Element siblingELement : titleElement.nextElementSiblings()) {
            if (this.elementIsTitle(siblingELement)) {
                siblingElementTitle = true;
            }
        }

        if (siblingElementTitle || Util.getMaxDepth(titleElement) > 2) {
            ContentNode newNode = new ContentNode();
            newNode.setContentTitle(titleElement.text());
            parentNode.addContent(newNode);
            this.process(newNode, titleElement.nextElementSiblings());
        } else {
            ContentLeaf newLeaf = new ContentLeaf();
            newLeaf.setContentTitle(titleElement.text());
            newLeaf.setContent(titleElement.nextElementSiblings().eachText());
            parentNode.addContent(newLeaf);
        }
    }

    protected void processListElement(ContentNode currentContentNode, Element element) {
        if (!(element.tagName().equals("ul") || element.tagName().equals("ol") || element.childrenSize() == 0)) {
            throw new IllegalArgumentException("element should be an element of the tag <ul> or <ol>");
        }

//        remove the <span> and <a> children of <li> elements before getting the maxdepth
        Element copyOfElement = element.clone();
        copyOfElement.select("li a, li span, li em, li sub").remove();

        if (Util.getMaxDepth(copyOfElement) > 2) {
//            If the depth is more than 2, (at least one of) the list elements will have children and therefore
//            this will be a node that needs further processing
            ContentNode newNode = new ContentNode();
            newNode.setContentType("LIST");
            currentContentNode.addContent(newNode);
            this.process(newNode, element.children());
        } else if (Util.getMaxDepth(copyOfElement) >= 1) {
            ContentLeaf newLeaf = new ContentLeaf();
            newLeaf.setContentType("LIST");
            newLeaf.setContent(element.children().eachText());
            currentContentNode.addContent(newLeaf);
        } else {
            throw new IllegalArgumentException("Given element (" + element + ") is not a valid ul or ol, it does not have children.");
        }
    }

    protected void processParagraphElement(ContentNode currentContentNode, Element element) {
        if (!element.tagName().equals("p")) {
            throw new IllegalArgumentException("element should be an element of the tag <p>");
        }

        ContentLeaf newLeaf = new ContentLeaf();
        newLeaf.setContentType("PARAGRAPH");
        currentContentNode.addContent(newLeaf);

        newLeaf.addContent(element.text());
    }

    protected void processDivElement(ContentNode currentContentNode, Element element) {
        if (!element.tagName().equals("div")) {
            throw new IllegalArgumentException("element should be an element of the tag <div>");
        }

        String looseDivText = this.getLooseTextFromElement(element);

        if (element.childrenSize() == 0) {
            if (looseDivText.length() > 0) {
                ContentLeaf newLeaf = new ContentLeaf();
                newLeaf.setContentType("PARAGRAPH");
                newLeaf.addContent(looseDivText);
                currentContentNode.addContent(newLeaf);
            }
        } else if (element.childrenSize() == 1) {
//            Go deeper without making new node for this div to prevent making a node contianing a single node.
            this.process(currentContentNode, element.children());
        } else if (Util.getMaxDepth(element) == 2 && this.allElementsAreOfTags(element.children(), new String[]{"p"})) {
            ContentLeaf newLeaf = new ContentLeaf();
            if (looseDivText.length() > 0) {
                newLeaf.addContent(looseDivText);
                for (String contentToAdd : element.children().eachText()) {
                    newLeaf.addContent(contentToAdd);
                }
            } else {
                newLeaf.setContent(element.children().eachText());
            }
            currentContentNode.addContent(newLeaf);
        } else {
//            If we come here we know the maxdepth is bigger than 2 and the element has more than 1 child, a new node needs to be made
            ContentNode newNode = new ContentNode();
            if (looseDivText.length() > 0) {
                ContentLeaf newLeaf = new ContentLeaf();
                newLeaf.addContent(looseDivText);
                newNode.addContent(newLeaf);
            }
            currentContentNode.addContent(newNode);
            this.process(newNode, element.children());
        }
    }

    protected String getLooseTextFromElement(Element element) {
        Element copyWithoutChildren = element.clone();
        copyWithoutChildren.children().remove();

        return copyWithoutChildren.text();
    }

    protected void processListItemElement(ContentNode currentContentNode, Element element) {
        if (!element.tagName().equals("li")) {
            throw new IllegalArgumentException("element should be an element of the tag <li>");
        }

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
            this.process(newNode, childrenToProcess);
        } else if (copiesOfElement.get("elementWithoutSpanEmSubA").childrenSize() == 1) {
            this.process(currentContentNode, element.children());
        } else {
            ContentNode newNode = new ContentNode();
            currentContentNode.addContent(newNode);
            this.process(newNode, element.children());
        }
    }

    protected void processTableElement(ContentNode currentContentNode, Element element) {
        if (!element.tagName().equals("table")) {
            throw new IllegalArgumentException("element should be an element of the tag <table>");
        }
        ContentNode tableNode = new ContentNode();
        tableNode.setContentType("TABLE");
        currentContentNode.addContent(tableNode);

        Elements tableContent = element.children();

        Elements tableCaption = element.children().select("caption");
        if (tableCaption.size() > 0) {
            if (tableCaption.size() > 1) {
                throw new IllegalArgumentException("Multiple captions found in table. Table can only have one caption.");
            }
            tableNode.setContentTitle(tableCaption.get(0).text());
            tableContent = tableCaption.get(0).nextElementSiblings();
        }

        if (this.allElementsAreOfTags(tableContent, new String[] {"tr"}) || this.allElementsAreOfTags(tableContent, new String[] {"thead", "tbody", "tfoot"})) {
            this.process(tableNode, tableContent);
        } else {
            throw new IllegalArgumentException("Table contained an illegal combination of children tags. Table can either have children of tag <tr> or have children with the tags <thead>, <tbody> and <tfoot>");
        }
    }

    protected void processTableSectionElement(ContentNode currentContentNode, Element element) {
        List<String> validTags = List.of("thead", "tbody", "tfoot");
        if (!validTags.contains(element.tagName())) {
            throw new IllegalArgumentException("element should be an element of the tag <thead>, <tbody> or <tfoot>");
        }
        if (!element.parent().tagName().equals("table")) {
            throw new IllegalArgumentException("element should be a child element of a table element");
        }

        ContentNode newNode = new ContentNode();
        newNode.setContentType(element.tagName().toUpperCase());
        currentContentNode.addContent(newNode);
        this.process(newNode, element.children());
    }

    protected void processTableRowElement(ContentNode currentContentNode, Element element) {
        if (!this.allElementsAreOfTags(element.children(), new String[]{"th", "td"})) {
            throw new IllegalArgumentException("All children of <tr> element should be of the tag <th> or <td>");
        }
        if (!element.tagName().equals("tr")) {
            throw new IllegalArgumentException("element should be of tag <tr> (not of tag " + element.tagName() + ")");
        }

        ContentNode newNode = new ContentNode();
        newNode.setContentType("TR");
        currentContentNode.addContent(newNode);

        if ((this.allElementsAreOfTags(element.children(), new String[]{"td"}) ||
                this.allElementsAreOfTags(element.children(), new String[]{"th"})) &
                Util.getMaxDepth(element) == 2) {
            ContentLeaf newLeaf = new ContentLeaf();
            newLeaf.setContentType(element.child(0).tagName().toUpperCase());
            newLeaf.setContent(element.children().eachText());
            newNode.addContent(newLeaf);
        } else {
            this.process(newNode, element.children());
        }
    }

    protected void processTableDataElement(ContentNode currentContentNode, Element element) {
        Map<String, Element> copiesOfElement = Util.getCopiesOfElementWithoutAndOnlySpanEmSubA(element);
        String contentType = element.tagName().toUpperCase();

        if (copiesOfElement.get("elementWithoutSpanEmSubA").childrenSize() == 0) {
            ContentLeaf newLeaf = new ContentLeaf();
            newLeaf.setContentType(contentType);
            newLeaf.addContent(element.text());
            addAttributesToContent(element, newLeaf);
            currentContentNode.addContent(newLeaf);
        } else if (copiesOfElement.get("elementOnlySpanEmSubA").text().length() > 0) {
            ContentLeaf newLeaf = new ContentLeaf();
            newLeaf.setContentType("PARAGRAPH");
            newLeaf.addContent(copiesOfElement.get("elementOnlySpanEmSubA").text());

            ContentNode newNode = new ContentNode();
            newNode.setContentType(contentType);
            newNode.addContent(newLeaf);
            addAttributesToContent(element, newNode);
            currentContentNode.addContent(newNode);

            Elements  childrenToProcess = copiesOfElement.get("elementWithoutSpanEmSubA").children();

            this.process(newNode, childrenToProcess);
        } else if (!Util.elementHasChildrenOfTag(element, "[^p]")) {
            ContentLeaf newLeaf = new ContentLeaf();
            newLeaf.setContentType("TD");
            newLeaf.setContent(element.children().eachText());
            currentContentNode.addContent(newLeaf);
        } else {
            System.out.println("else");
            ContentNode newNode = new ContentNode();
            newNode.setContentType(contentType);
            addAttributesToContent(element, newNode);
            currentContentNode.addContent(newNode);
            this.process(newNode, element.children());
        }
    }

    private void addAttributesToContent(Element element, Content content) {
        if (content.getClass().equals(ContentLeaf.class)) {
            ContentLeaf newContent = (ContentLeaf) content;
            if (element.hasAttr("colspan")) {
                newContent.addAttribute("colspan", element.attributes().get("colspan"));
            }
            if (element.hasAttr("rowspan")) {
                newContent.addAttribute("rowspan", element.attributes().get("rowspan"));
            }
        } else {
            ContentNode newContent = (ContentNode) content;
            if (element.hasAttr("colspan")) {
                newContent.addAttribute("colspan", element.attributes().get("colspan"));
            }
            if (element.hasAttr("rowspan")) {
                newContent.addAttribute("rowspan", element.attributes().get("rowspan"));
            }
        }
    }

//    private boolean elementIsSameDepthTitle(Element firstElement, Element secondElement) {
//        return this.elementIsTitle(firstElement) && firstElement.tagName().equals(secondElement.tagName());
//    }

    /**
     * Test if a Jsoup Element object represents a header (the tag is one of h1 to h6 or the element has a class collapsible-toggler or field--name-heading)
     * @param element A Jsoup Element
     * @return boolean indicating if the Element represents a header
     */
    protected boolean elementIsTitle(Element element) {
        String[] headingTags = {"h1", "h2", "h3", "h4", "h5", "h6"};
        return element.hasClass("collapsible-toggler") || element.hasClass("field--name-heading") || Arrays.asList(headingTags).contains(element.tagName());
    }

    /**
     * A method to check if all the Element objects in the elements parameter are one of the given tags
     * @param elements A Jsoup Elements object with the elements to check
     * @param tagNames an array with strings of the tags to check
     * @return a boolean indicating if all the given elements are of one og the given tags
     */
    private boolean allElementsAreOfTags(Elements elements, String[] tagNames) {
        return elements.stream().allMatch(element -> {
            for (String tag : tagNames) {
                if (element.tagName().equals(tag)) {
                    return true;
                }
            }
            return false;
        });
    }

    /**
     * Gets the first title element preceding the input element. If no element that is a title element is found in the
     * preceding sibling elements an IllegalArgumentException will be thrown
     * @param element A Jsoup Element object
     * @return the first element preceding the input element that returns true in the elementIsTitle method
     */
    protected Element getPrevTitleElement(Element element) {
        while (element.previousElementSibling() != null) {
            element = element.previousElementSibling();
            if (this.elementIsTitle(element)) {
                return element;
            }
        }
        throw new IllegalArgumentException("Element does not have a previous title element.");
    }

    private ContentNode getContentNodeOfPrevTitleOfSameHeightOrHigher(Element element, ContentNode currentContentNode) {
        if (!this.elementIsTitle(element)) {
            String message = "Element has te be a title element, given element (" + element.tagName() + " with classes " + element.classNames() + ") is not a title element";
            throw new IllegalArgumentException(message);
        }


        try {
            Element sameOrHigherTitleElement = this.findSameOrHigherTitleElement(element, element);
            return this.findParentContentWithTitle(sameOrHigherTitleElement.text(), currentContentNode);
        } catch (IllegalArgumentException e) {
            ContentNode parentNode = currentContentNode;
            while (parentNode.getParent() != null) {
                parentNode = parentNode.getParent();
            }
            return parentNode;
        }
    }

    private Element findSameOrHigherTitleElement(Element element, Element titleElement) {
        while (titleElement != null) {
            try {
                titleElement = this.getPrevTitleElement(element);
                int compareValue = TitleElementComparerNhg.compareTo(element, titleElement);
                if (compareValue == 0 || compareValue > 0) {
                    return titleElement;
                }
            } catch (IllegalArgumentException e) {
                if (titleElement.hasParent()) {
                    this.findSameOrHigherTitleElement(element, titleElement.parent());
                } else {
                    throw new IllegalArgumentException("There is no title element in the previous part of the html document with same or higher height.");
                }
            }
        }
        return null;
    }

    private ContentNode findParentContentWithTitle(String titleToLookFor, ContentNode currentContentNode) {
        ContentNode parentNode = currentContentNode;
        while (parentNode.getParent() != null) {
            String parentTitle = parentNode.getContentTitle();
            if (titleToLookFor.toLowerCase().equals(parentTitle.toLowerCase())) {
                return parentNode;
            }
            parentNode = parentNode.getParent();
        }
        throw new IllegalArgumentException("No parent ContentNode found with given title");
    }

    /**
     * A method to extract the content of interest (medication information of the richtlijnen.nhg website) from a Jsoup
     * document.
     * @param doc a Jsoup Document object to extract content from
     * @return a Map where the keys are strings set to the title of the section and the values are Jsoup Elements objects
     * that hold all the Elements of interest from that section.
     */
    private Map<String, Elements> getContentOfInterest(Document doc) {
        Map<String, Elements> contentOfInterest = new HashMap<>();

        Elements elements = doc.select(".field--name-main-text .collapsible-section-wrapper:has(#volledige-tekst-richtlijnen-beleid)");

        if (elements.size() == 0) {
            for (Element element : doc.select(".field--name-main-text .collapsible-section-wrapper")) {
                if (element.text().toLowerCase().contains("richtlijnen beleid")) {
                    Elements h3Elements = geth3Elements(element,  "medicamenteuze behandeling", "niet-medicamenteuze");
                    contentOfInterest.put(element.select("h2").text(), getContentOfInterestFromH3Elements(h3Elements));
                }
            }
        } else {
            for (Element element : elements) {
                Elements h3Elements = geth3Elements(element,  "medicamenteuze behandeling", "niet-medicamenteuze");
                contentOfInterest.put(element.select("h2").text(), getContentOfInterestFromH3Elements(h3Elements));
            }
        }
        return contentOfInterest;
    }

    /**
     * Method to find h3 elements in a given element, the h3 elements that contain the h3Query and do not contain
     * the h3AntiQuery will be returned in a Jsoup Elements object.
     * @param element Jsoup Element that is used to find h3 elements in.
     * @param h3Query The returned h3 elements will contain this query as text (use lowercase)
     * @param h3AntiQuery h3 elements that contain this query as text will not be returned even if they contain the h3Query (use lowercase)
     * @return A Jsoup Elements object that contains the found h3 elements
     */
    private Elements geth3Elements(Element element, String h3Query, String h3AntiQuery) {
        Elements h3Elements = new Elements();
        Elements paragraphs = element.select(".paragraph");

        for (Element paragraph : paragraphs) {
            for (Element h3Element : paragraph.select("h3")) {
                String h3Text = h3Element.text().toLowerCase();
                if (h3Text.contains(h3Query) && !h3Text.contains(h3AntiQuery)) {
                    h3Elements.add(h3Element);
                }
            }
        }
        return h3Elements;
    }

    /**
     * Given an h3 element get all the content that is part of this section of the webpage. To do this the parent of the
     * h3 element is retrieved and saved as well as all the next siblings until a next sibling has the class paragraph--normaal
     * and has a  child that is an h3 element in it.
     * @param h3Elements Jsoup Elements of the type h3 that belong to sections of the website that are of interest
     * @return a Jsoup Elements object containing all the content of interest.
     */
    private Elements getContentOfInterestFromH3Elements(Elements h3Elements) {
        Elements contentOfInterest = new Elements();
        for (Element h3Element : h3Elements) {
            if (!h3Element.tagName().equals("h3")) {
                throw new IllegalArgumentException("h3Elements must only contain Elements that are of the tag h3");
            }
            Element currentElement = h3Element.parent();

//            If the contewnt of interest doesn't already have the current element add to content of interest
            if (!contentOfInterest.contains(currentElement)) {
                contentOfInterest.add(currentElement);
            }

            while (currentElement.nextElementSibling() != null) {
//                Go to next element
                currentElement = currentElement.nextElementSibling();

//                If the next element has the class "paragraph--normaal" and has a child of tag "h3" break the loop
                if (currentElement.hasClass("paragraph--normaal") && currentElement.children().get(0).tagName().equals("h3")) {
                        break;
                }

//                If currentElement is not yet in contentOfInterest, add the currentElement to the contentOfInterest
                if (!contentOfInterest.contains(currentElement)) {
                    contentOfInterest.add(currentElement);
                }
            }
        }
        return contentOfInterest;
    }

    /**
     * @param nhgIndicationName the indication name as it can be found on https://richtlijnen.nhg.org/ website
     * @return A Jsoup Document object of the NHG webpage for given indication
     */
    private Document getIndicationWebDocument(String nhgIndicationName) {
        String urlIndicationName = String.join("-", nhgIndicationName
                .toLowerCase()
                .split(" ")
        );
        String totalUrl = this.url + urlIndicationName + "#volledige-tekst";
        try {
            Document doc = Jsoup.connect(totalUrl).get();
            return doc;
        } catch (IOException e) {
            String message = "With the nhgIndicationName (" + nhgIndicationName + ") the url " + totalUrl + " was generated. No connection could be made to this website.";
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * A method to read a csv file with the drug indication names for different sources, the information is then stored
     * drugIndicationsNames map.
     * @throws IOException
     */
    private void readDrugIndicationsNamesFromCsv() throws IOException {
        LOGGER.info("Reading csv file with indication names");
        String row;

        BufferedReader csvReader = new BufferedReader(new FileReader(this.csvFileLocation));
        boolean firstline = true;
        while ((row = csvReader.readLine()) != null) {
            if (!firstline) {
                if (row.endsWith(";")) {
                    row = row + " ";
                }
                String[] data = row.split(";");
                this.drugIndicationsNames.get("ontpillen").add(data[0]);
                this.drugIndicationsNames.get("nhgrichtlijnen").add(data[2]);
            }
            firstline = false;
        }
        csvReader.close();
    }

}
