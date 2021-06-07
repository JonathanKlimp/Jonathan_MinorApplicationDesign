package nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping.nhg;

import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugDao;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content.Content;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content.ContentLeaf;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content.ContentNode;
import nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping.DrugFetcher;
import nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping.IndicationScraper;
import nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping.SSLHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Naomi Hindriks
 */
@SpringBootTest
class RichtlijnenNhgWebScraperTest {

    @Autowired
    DrugDao drugDao;

    @Autowired
    DrugFetcher drugFetcher;

    @Autowired
    IndicationScraper indicationScraper;

    @Autowired
    RichtlijnenNhgWebScraper richtlijnenNhgWebScraper;

    @BeforeEach
    public void bypassSSL() throws IOException {
        SSLHelper.bypassSSL();
        this.drugFetcher.parseHtml();
        this.indicationScraper.parseHtml();
    }

    @AfterEach
    public void cleanUpDao() {
        drugDao.removeAllDrugs();
    }

    @Test
    void getMaxDepth_oneEndLeave() {
        String testString = "<ul id='test-ul'><li>Test</li></ul>";
        Element testDiv = Jsoup.parse(testString).select("ul#test-ul").first();
        String message = "while finding depth of: " + testString;
        assertEquals(2, this.richtlijnenNhgWebScraper.getMaxDepth(testDiv));
    }

    @Test
    void getMaxDepth_multipleEndLeave() {
        String testString =
                "<ul id='test-ul'>" +
                    "<li>Depth here is 2</li>" +
                    "<li>" +
                        "<ul>" +
                            "<li>Depth here is 4</li>" +
                        "</ul>" +
                    "</li>" +
                "</ul>";
        Element testDiv = Jsoup.parse(testString).select("ul#test-ul").first();
        String message = "while finding depth of: " + testString;
        assertEquals(4, this.richtlijnenNhgWebScraper.getMaxDepth(testDiv), message);
    }

    @Test
    void elementIsTitle_titleElements() {
        Elements titleElements = new Elements();

        for (String titleTag : new String[]{"h1", "h2", "h3", "h4", "h5", "h6"}) {
            String testHtml = "<" + titleTag + " id='test-title'>This is a title</" + titleTag + ">";
            Element elementToTest = Jsoup.parse(testHtml).select("#test-title").get(0);
            titleElements.add(elementToTest);
        }

        for (String titleClass : new String[]{"collapsible-toggler", "field--name-heading"}) {
            String testHtml = "<div id='test-title' class="+ titleClass + ">This is a title</div>";
            Element elementToTest = Jsoup.parse(testHtml).select("#test-title").get(0);
            titleElements.add(elementToTest);
        }

        for (Element titleElement : titleElements) {
            String message = "when executing elementIsTitle(" +titleElement + ")";
            assertTrue(this.richtlijnenNhgWebScraper.elementIsTitle(titleElement), message);
        }
    }

    @Test
    void elementIsTitle_nonTitleElement() {
        String[] htmlToTestStrings = {
                "<p id='test-non-title'>Test paragraph</p>",
                "<div id='test-non-title'>this is not a title<h4>This is a title</h4></div>",
                "<div class='collapsible-toggler'>this is a title <p id='test-non-title'>but this is not a title</p> </div>"
        };

        for (String testHtml: htmlToTestStrings) {
            Element elementToTest = Jsoup.parse(testHtml).select("#test-non-title").get(0);
            String message = "when executing elementIsTitle(" + elementToTest + ")";
            assertFalse(this.richtlijnenNhgWebScraper.elementIsTitle(elementToTest), message);
        }
    }

    @Test
    void getPrevTitleElement_sunnyDay() {
        String testHtml =
                "<div> " +
                    "<h1 id='prev-title'>this is the previous title</h1>" +
                    "<p id='test-paragraph'>this is a test paragraph</p> " +
                    "<h1 id='test-title'>this is a title</h1>" +
                    "<p>this is a paragraph</p>" +
                "</div>";
        Document doc = Jsoup.parse(testHtml);
        Elements testElements = doc.select("#test-paragraph, #test-title");
        Element prevTitleElement = doc.select("#prev-title").get(0);

        for (Element testElement : testElements) {
            assertEquals(prevTitleElement, this.richtlijnenNhgWebScraper.getPrevTitleElement(testElement));
        }
    }

    @Test
    void getPrevTitleElement_noPrevTitlePresent() {
        String testHtml =
                "<div> " +
                    "<p>this is a paragraph</p>" +
                    "<p id='test-paragraph'>this is a test paragraph</p> " +
                    "<h1 id='test-title'>this is a title</h1>" +
                "</div>";
        Document doc = Jsoup.parse(testHtml);
        Elements testElements = doc.select("#test-paragraph, #test-title");

        for (Element testElement : testElements) {
            String message = "while executing getPrevTitleElement(" + testElement + ")";
            Assertions.assertThrows(
                    IllegalArgumentException.class,
                    () -> {this.richtlijnenNhgWebScraper.getPrevTitleElement(testElement);},
                    message
            );
        }
    }

    @Test
    void processTitleElement_singleTitle(){
        String htmlToTest = "<h1 id='test-title'>This is a title element</h1>";
        Element elementToTest = Jsoup.parse(htmlToTest).select("#test-title").get(0);
        ContentNode testNode = new ContentNode();

        Boolean returnValue = this.richtlijnenNhgWebScraper.processTitleElement(testNode, elementToTest);

        assertFalse(returnValue);
        assertEquals("This is a title element", testNode.getContentTitle());
    }

    @Test
    void processTitleElement_siblingTitle(){
        String htmlToTest =
                "<h1>This the prev title element</h1>" +
                "<h1 id='test-title'>This the title element</h1>";
        Element elementToTest = Jsoup.parse(htmlToTest).select("#test-title").get(0);

        ContentNode parentNode = new ContentNode();
        ContentNode testNode = new ContentNode();
        parentNode.addContent(testNode);

        Boolean returnValue = this.richtlijnenNhgWebScraper.processTitleElement(testNode, elementToTest);

        assertTrue(returnValue);
        assertEquals(null, testNode.getContentTitle());
        assertEquals(parentNode.getContent().get(1).getContentTitle(), "This the title element");
    }

    @Test
    void processTitleElement_parentTitle(){
        String htmlToTest =
                "<h1>This the parent title element</h1>" +
                "<h2 id='test-title'>This the title element</h2>";
        Element elementToTest = Jsoup.parse(htmlToTest).select("#test-title").get(0);
        ContentNode parentNode = new ContentNode();
        ContentNode testNode = new ContentNode();
        parentNode.addContent(testNode);

        Boolean returnValue = this.richtlijnenNhgWebScraper.processTitleElement(testNode, elementToTest);

        assertTrue(returnValue);
        assertEquals(null, testNode.getContentTitle());
        assertEquals(testNode.getContent().get(0).getContentTitle(), "This the title element");
    }

    @Test
    void processList_sunnyDay() {
        String htmlToTest =
                "<ul id='test-list'>" +
                    "<li>list element 1</li>" +
                    "<li>list element 2</li>" +
                    "<li>list element 3</li>" +
                "</ul>";
        Element elementToTest = Jsoup.parse(htmlToTest).select("#test-list").get(0);

        System.out.println(elementToTest);

        ContentNode testNode = new ContentNode();

        this.richtlijnenNhgWebScraper.processList(testNode, elementToTest);

        Content resultContent = testNode.getContent().get(0);
        assertEquals(ContentLeaf.class, resultContent.getClass());

        ContentLeaf resultContentLeaf = (ContentLeaf) resultContent;
        assertArrayEquals(new String[]{"list element 1", "list element 2", "list element 3"}, resultContentLeaf.getContent().toArray());
    }

    @Test
    void processList_recursiveList() {
        String htmlToTest =
                "<ul id='test-list'>" +
                    "<li>list 1 element 1</li>" +
                    "<li>list 1 element 2</li>" +
                    "<li>" +
                        "<ul>" +
                            "<li>list 2 element 1</li>" +
                            "<li>list 2 element 2</li>" +
                        "</ul>" +
                    "</li>" +
                "</ul>";

        Element elementToTest = Jsoup.parse(htmlToTest).select("#test-list").get(0);
        ContentNode testNode = new ContentNode();
        this.richtlijnenNhgWebScraper.processList(testNode, elementToTest);

        Content resultContent = testNode.getContent().get(0);
        assertEquals(ContentNode.class, resultContent.getClass());

        ContentNode resultContentNode = (ContentNode) resultContent;

        Content list1Element1 = resultContentNode.getContent().get(0);
        Content list1Element2 = resultContentNode.getContent().get(1);
        Content list2 = resultContentNode.getContent().get(2);

        assertEquals(ContentLeaf.class, list1Element1.getClass());
        assertEquals(ContentLeaf.class, list1Element2.getClass());
        assertEquals(ContentLeaf.class, list2.getClass());

        ContentLeaf list1Element1Leaf = (ContentLeaf) list1Element1;
        ContentLeaf list1Element2Leaf = (ContentLeaf) list1Element2;
        assertEquals("list 1 element 1", list1Element1Leaf.getContent().get(0));
        assertEquals("list 1 element 2", list1Element2Leaf.getContent().get(0));

        ContentLeaf list2Leaf = (ContentLeaf) list2;
        assertEquals("list 2 element 1", list2Leaf.getContent().get(0));
        assertEquals("list 2 element 2", list2Leaf.getContent().get(1));
    }

    @Test
    void processParagraph_sunnyDay() {
        String htmlToTest =
                "<p id='test-paragraph'>This is a paragraph<p>";
        Element elementToTest = Jsoup.parse(htmlToTest).select("#test-paragraph").get(0);

        ContentNode testNode = new ContentNode();
        this.richtlijnenNhgWebScraper.processParagraph(testNode, elementToTest);

        Content paragraphContent = testNode.getContent().get(0);
        assertEquals(ContentLeaf.class, paragraphContent.getClass());

        ContentLeaf paragraphLeaf = (ContentLeaf) paragraphContent;

        assertArrayEquals(new String[]{"This is a paragraph"}, paragraphLeaf.getContent().toArray());
    }

    @Test
    void parseHtml() throws IOException {
        this.richtlijnenNhgWebScraper.parseHtml();
    }
}