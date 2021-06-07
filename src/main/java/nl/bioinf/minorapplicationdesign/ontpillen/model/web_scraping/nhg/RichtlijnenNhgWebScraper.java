package nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping.nhg;

import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugDao;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content.ContentLeaf;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content.ContentNode;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content.UseIndication;
import nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping.AbstractWebScraper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeVisitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
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

        for (int i = 0; i < this.drugIndicationsNames.get("ontpillen").size(); i++) {
            String ontpillenIndicationName = this.drugIndicationsNames.get("ontpillen").get(i).strip();
            String nhgIndicationName = this.drugIndicationsNames.get("nhgrichtlijnen").get(i).strip();

//            If there is no indication name in ontpillen or in nhg richtlijnen for this index, continue to next index
            if(ontpillenIndicationName.equals("") || nhgIndicationName.equals("")) {
                continue;
            }

//            Get Jsoup document of the webpage for this indication
            Document doc = this.getIndicationWebDocument(nhgIndicationName);

//            Collect the contest of interest from the document and save in this map.
            Map<String, Elements> contentOfInterest = this.getContentOfInterest(doc);

//            Get the use indication from the drugDao that belongs to this indication
            UseIndication currentUseIndication = this.drugDao.getUseIndication(ontpillenIndicationName);


//            For every entry in contentOfInterest map , add a ContentNode to the current UseIndication for the practitioner
//            and set the title of this node to the key of the current map entry. Then process the value of the current
//            entry further into ContentNodes and ContentLeafs.
            for (Map.Entry<String,Elements> entry : contentOfInterest.entrySet()) {
                if (entry.getValue().size() > 0) {
                    ContentNode newContentNode = new ContentNode();
                    String title = entry.getKey();
                    newContentNode.setContentTitle(title);
                    currentUseIndication.addContentPractitioner(title, newContentNode);
//                System.out.println("key= " + entry.getKey());
//                for(Element element : entry.getValue()) {
//                    System.out.println("value element= " + element);
//                }
                    this.processContentOfInterest(newContentNode, entry.getValue());
                }
            }
//            break;
        }
    }

    private void processContentOfInterest(ContentNode currentContentNode, Elements elementsToProcess) {
        for (Element element : elementsToProcess) {
//            TODO remove the first if
            if (element.hasClass("summary-main-text-crosslink") || element.hasClass("embedded-entity literature")) {
//                If the element has the class summary-main-text-crosslink or the classes embedded-entity and literature, skip this element
                continue;
            } else if(this.elementIsTitle(element)) {
//                If the element is a title element execute this block
                if (this.processTitleElement(currentContentNode, element)) {
                    continue;
                } else {
                    break;
                }
            } else if (element.tagName().equals("ul") || element.tagName().equals("ol")) {
                this.processList(currentContentNode, element);
            } else if (element.tagName().equals("p")) {
                processParagraph(currentContentNode, element);
            } else if (element.tagName().equals("div")) {
                processDiv(currentContentNode, element);
            } else if (element.tagName().equals("li")) {
                processListElement(currentContentNode, element);
            } else if (element.tagName().equals("table")) {
//                This is a table, a new node with the ContentType table will be formed.
                ContentNode tableNode = new ContentNode();
                tableNode.setContentType("TABLE");
                currentContentNode.addContent(tableNode);

                Elements tableContent = element.children();

//                If hte table has a caption the text of this caption will be saved as the content title
                Elements tableCaption = element.children().select("caption");
                if (tableCaption.size() > 0) {
//                    If more than 1 caption in the table throw an exception
                    if (tableCaption.size() > 1) {
//                        TODO throw exception
                        System.out.println("Exception");
                    }
                    tableNode.setContentTitle("Caption = " + tableCaption.get(0).text());
                    tableContent = tableCaption.get(0).nextElementSiblings();
                }

                if (this.allElementsAreOfTag(tableContent, new String[] {"tr"}) || this.allElementsAreOfTag(tableContent, new String[] {"thead", "tbody", "tfoot"})) {
//                    TODO process table rows or process table head, body, footer
                    this.processContentOfInterest(tableNode, tableContent);
                } else {
//                    TODO throw exception
                    System.out.println("throw error");
                }

            } else if(element.tagName().equals("thead") || element.tagName().equals("tbody") || element.tagName().equals("tfoot")) {
                if (!this.allElementsAreOfTag(element.children(), new String[]{"tr"})) {
//                    TODO throw exception if not all children are tag tr
                    System.out.println("throw exception");
                }
                ContentNode newNode = new ContentNode();
                newNode.setContentType(element.tagName().toUpperCase());
                currentContentNode.addContent(newNode);
                this.processContentOfInterest(newNode, element.children());
            } else if  (element.tagName().equals("tr")) {
                if (!this.allElementsAreOfTag(element.children(), new String[]{"th", "td"})) {
//                    TODO throw exception if not all children are tag of type th or td
                    System.out.println("throw exception");
                }

                ContentNode newNode = new ContentNode();
                newNode.setContentType("TR");
                currentContentNode.addContent(newNode);

                this.processContentOfInterest(newNode, element.children());
            } else if(element.tagName().equals("th") || element.tagName().equals("td")) {
                ContentNode newNode = new ContentNode();
                newNode.setContentType(element.tagName().toUpperCase());

                if (element.hasAttr("colspan")) {
                    newNode.addAttribute("colspan", element.attributes().get("colspan"));
                }
                if (element.hasAttr("rowspan")) {
                    newNode.addAttribute("rowspan", element.attributes().get("rowspan"));
                }

                currentContentNode.addContent(newNode);

                this.processContentOfInterest(newNode, element.children());

            }else {
//                TODO throw exception
                System.out.println("Exception");
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
        if (element.elementSiblingIndex() != 0) {
            try {
                Element prevTitleElement = this.getPrevTitleElement(element);
                int compareValue = TitleElementComparerNhg.compareTo(element, prevTitleElement);
                if (compareValue < 0) {
                    this.createNewLeafOrNodeWithTitle(currentContentNode, element);
                } else if (compareValue > 0) {
                    throw new UnsupportedOperationException("new title node can not be bigger than previous title node");
                } else {
                    this.createNewLeafOrNodeWithTitle(currentContentNode.getParent(), element);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                System.out.println("No preceding title element");
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

        if (siblingElementTitle || this.getMaxDepth(titleElement) > 2) {
            ContentNode newNode = new ContentNode();
            newNode.setContentTitle(titleElement.text());
            parentNode.addContent(newNode);
            this.processContentOfInterest(newNode, titleElement.nextElementSiblings());
        } else {
            ContentLeaf newLeaf = new ContentLeaf();
            newLeaf.setContentTitle(titleElement.text());
            newLeaf.setContent(titleElement.nextElementSiblings().eachText());
            parentNode.addContent(newLeaf);
        }
    }

    protected void processList(ContentNode currentContentNode, Element element) {
//        remove the <span> and <a> children of <li> elements before getting the maxdepth
        Element copyOfElement = element.clone();
        copyOfElement.select("li a, li span, li em, li sub").remove();

        if (this.getMaxDepth(copyOfElement) == 2) {
//            If the depth is 2 it is a list with list elements that can be translated to a node
            ContentLeaf newLeaf = new ContentLeaf();
            newLeaf.setContentType("LIST");
            newLeaf.setContent(element.children().eachText());
            currentContentNode.addContent(newLeaf);
        } else if (this.getMaxDepth(copyOfElement) > 2) {
//            If the depth is more than 2, (at least one of) the list elements will have children and therefore
//            this will be a node that needs further processing
            ContentNode newNode = new ContentNode();
            newNode.setContentType("LIST");
            currentContentNode.addContent(newNode);
            this.processContentOfInterest(newNode, element.children());
        } else {
            throw new IllegalArgumentException("Given element (" + element + ") is not a valid ul or ol, it does not have children.");
        }
    }

    protected void processParagraph(ContentNode currentContentNode, Element element) {
        ContentLeaf newLeaf = new ContentLeaf();
        newLeaf.setContentType("PARAGRAPH");
        newLeaf.addContent(element.text());
        currentContentNode.addContent(newLeaf);
    }

    private void processDiv(ContentNode currentContentNode, Element element) {
        if (element.childrenSize() == 0) {
            if (element.text().length() > 0) {
                ContentLeaf newLeaf = new ContentLeaf();
                newLeaf.setContentType("PARAGRAPH");
                newLeaf.addContent(element.text());
                currentContentNode.addContent(newLeaf);
            }
        } else if (element.childrenSize() == 1) {
//            Go deeper without making new node for this div to prevent making a node contianing a single node.
            this.processContentOfInterest(currentContentNode, element.children());
        } else if (this.getMaxDepth(element) == 2) {
            ContentLeaf newLeaf = new ContentLeaf();
            newLeaf.setContentTitle(element.text());
            newLeaf.setContent(element.nextElementSiblings().eachText());
        } else {
//            If we come here we know the maxdepth is bigger than 2 and the element has more than 1 child, a new node needs to be made
            ContentNode newNode = new ContentNode();
            currentContentNode.addContent(newNode);
            this.processContentOfInterest(newNode, element.children());
        }
    }

    protected void processListElement(ContentNode currentContentNode, Element element) {
//        Make a copy of element, of this copy remove the children that are of the tag a, span, em or sub
        Element copyOfElement1 = element.clone();
        copyOfElement1.children().select("a, span, em, sub").remove();

        Element copyOfElement2 = element.clone();
        copyOfElement2.children().select(":not(a, span, em, sub)").remove();

        if (copyOfElement1.childrenSize() == 0) {
            ContentLeaf newLeaf = new ContentLeaf();
            newLeaf.setContentType("PARAGRAPH");
            newLeaf.addContent(element.text());
            currentContentNode.addContent(newLeaf);
        } else if (copyOfElement2.text().length() > 0) {
            System.out.println("test1");
            ContentLeaf newLeaf = new ContentLeaf();
            newLeaf.setContentType("PARAGRAPH");
            newLeaf.addContent(copyOfElement2.text());

            ContentNode newNode = new ContentNode();
            newNode.addContent(newLeaf);

            currentContentNode.addContent(newNode);

            Element copyOfElement3 = element.clone();
            Elements  childrenToProcess = copyOfElement3.children().select(":not(a, span, em, sub)");

            this.processContentOfInterest(newNode, childrenToProcess);
        } else if (copyOfElement1.childrenSize() == 1) {
            this.processContentOfInterest(currentContentNode, element.children());
        } else {
            ContentNode newNode = new ContentNode();
            currentContentNode.addContent(newNode);
            this.processContentOfInterest(newNode, element.children());
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
    private boolean allElementsAreOfTag(Elements elements, String[] tagNames) {
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

    /**
     * @param element Jsoup Element to find the maximum node depth of.
     * @return an integer presenting the maximmum node depth of the element, including the element itself (if it is
     * an element without children the return value is 1).
     */
    protected int getMaxDepth(Element element) {
        MaxDepthNodeVisitor myNodeVisitor = new MaxDepthNodeVisitor();
        element.traverse(myNodeVisitor);
        return myNodeVisitor.maxDepth;
    }

    /**
     * A class to use in the Jsoup Element.traverse method, used to find the maxiimum node depth of the element
     */
    private static final class MaxDepthNodeVisitor implements NodeVisitor {
        private int maxDepth = 0;

        @Override
        public void head(Node node, int depth) {
            if (node.childNodes().size() == 0 && depth > this.maxDepth) {
                this.maxDepth = depth;
            }
        }

        @Override
        public void tail(Node node, int depth) {
        }

        public int getMaxDepth() {
            return maxDepth;
        }
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
