package nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping.nhg;

import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugDao;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Naomi Hindriks
 */
@SpringBootTest
class RichtlijnenNhgWebScraperTest {

    private static final String  olToTest =
            "<ol id='test-list'>" +
                "<li>list element 1</li>" +
                "<li>list element 2</li>" +
                "<li>list element 3</li>" +
            "</ol>";

    private static final String  ulToTest =
            "<ul id='test-list'>" +
                "<li>list element 1</li>" +
                "<li>list element 2</li>" +
                "<li>list element 3</li>" +
            "</ul>";

    private static final String  simplePToTest =
            "<p id='test-paragraph'>This is a paragraph<p>";

    private static final String  pWithSubElementsToTest =
            "<p id='test-paragraph'>" +
                "This is text in a paragraph. " +
                "<a>This is a link. </a>" +
                "<span>This is a span. </span>" +
                "<em>This text is emphasized. </em>" +
                "<sub>This is subtext.</sub>" +
            "</p>";

    private static final String simpleDiv =
            "<div id='test-div'>" +
                "<p>element 1.</p>" +
                "<p>element 2.</p>" +
                "<p>element 3.</p>" +
            "</div>";

    private static final String divWithLooseText =
            "<div id='test-div'>" +
                "This is also part of the leaf." +
                "<p>element 1.</p>" +
                "<p>element 2.</p>" +
                "<p>element 3.</p>" +
            "</div>";

    private static final String recursiveListToTestUlUl =
            "<ul id='test-list'>" +
                "<li>list 1 element 1</li>" +
                "<li>list 1 element 2</li>" +
                "<li>" +
                    "<ul>" +
                        "<li>list 2 element 1</li>" +
                    "</ul>" +
                "</li>" +
            "</ul>";

    private static final String recursiveListToTestUlOl =
            "<ul id='test-list'>" +
                "<li>list 1 element 1</li>" +
                "<li>list 1 element 2</li>" +
                "<li>" +
                    "<ol>" +
                        "<li>list 2 element 1</li>" +
                    "</ol>" +
                "</li>" +
            "</ul>";

    private static final String recursiveListToTestOlUl =
            "<ol id='test-list'>" +
                "<li>list 1 element 1</li>" +
                "<li>list 1 element 2</li>" +
                "<li>" +
                    "<ul>" +
                        "<li>list 2 element 1</li>" +
                    "</ul>" +
                "</li>" +
            "</ol>";

    private static final String recursiveListToTestOlOl =
            "<ol id='test-list'>" +
                "<li>list 1 element 1</li>" +
                "<li>list 1 element 2</li>" +
                "<li>" +
                    "<ol>" +
                        "<li>list 2 element 1</li>" +
                    "</ol>" +
                "</li>" +
            "</ol>";

    private static final String validTestTable1 =
            "<table id='test-table'>" +
                "<caption>This is the caption</caption>" +
                "<tr>" +
                    "<th>tr 0 td 0</th>" +
                    "<th>tr 0 td 1</th>" +
                "</tr>" +
                "<tr>" +
                    "<td>tr 0 td 0</td>" +
                    "<td>tr 0 td 1</td>" +
                "</tr>" +
                "<tr>" +
                    "<th>tr 1 td 0</th>" +
                    "<td>tr 1 td 1</td>" +
                "</tr>" +
                "<tr>" +
                    "<td>tr 2 td 0</td>" +
                        "<p>sub content 1 in td</p>" +
                        "<p>sub content 2 in td</p>" +
                    "<td>tr 2 td 1</td>" +
                "</tr>" +
            "</table>";

    private static final String validTestTable2 =
            "<table id='test-table'>" +
                "<caption>This is the caption</caption>" +
                "<thead id='test-section-1'>" +
                    "<tr>" +
                        "<td>tr 0 td 0</td>" +
                        "<td>tr 0 td 1</td>" +
                    "</tr>" +
                "</thead>" +
                "<tbody id='test-section-2'>" +
                    "<tr>" +
                        "<td>tr 1 td 0</td>" +
                        "<td>tr 1 td 1</td>" +
                    "</tr>" +
                "</tbody>" +
                "<tfoot id='test-section-3'>" +
                    "<tr>" +
                        "<td>tr 2 td 0</td>" +
                        "<td>tr 2 td 1</td>" +
                    "</tr>" +
                "</tfoot>" +
            "</table>";

    private static final String validTestTable3 =
            "<table id='test-table'>" +
                "<tr>" +
                    "<th>tr 0 element 0</th>" +
                    "<td>tr 0 element 1</td>" +
                    "<td>tr 0 element 2</td>" +
                    "<td id='colspan-test' colspan='2'>tr 0 element 3</td>" +
                    "<td id='rowspan-test' rowspan='2'>tr 0 element 4</td>" +
                    "<td>" +
                        "<p>Td element with p elements 0</p>" +
                        "<p>Td element with p elements 1</p>" +
                    "</td>" +
                    "<td>" +
                        "<ul>" +
                            "<li>Td element with list 0</li>" +
                            "<li>Td element with list 1</li>" +
                        "</ul>" +
                    "</td>" +
                    "<td>" +
                        "Loose text in td" +
                        "<p>Td element with sub content element 1</p>" +
                        "<p>Td element with sub content element 2</p>" +
                        "<ul>" +
                            "<li>list element</li>" +
                        "</ul>" +
                    "</td>" +
                "</tr>" +
            "</table>";

    private static final String illegalTableNotATable =
            "<p id='test-table'>This is not a table</p>";

    private static final String illegalTableDoubleCaption =
            "<table id='test-table'>" +
                "<caption>This is the caption</caption>" +
                "<caption>This is another caption</caption>" +
                "<tr>" +
                    "<td>tr 0 td 0</td>" +
                    "<td>tr 0 td 1</td>" +
                "</tr>" +
                "<tr>" +
                    "<td>tr 1 td 0</td>" +
                    "<td>tr 1 td 1</td>" +
                "</tr>" +
                "<tr>" +
                    "<td>tr 2 td 0</td>" +
                    "<td>tr 2 td 1</td>" +
                "</tr>" +
            "</table>";

    private static final String illegalTableIllegalCombinedChildren =
            "<table id='test-table'>" +
                "<caption>This is the caption</caption>" +
                "<caption>This is another caption</caption>" +
                "<tr>" +
                    "<td>tr 0 td 0</td>" +
                    "<td>tr 0 td 1</td>" +
                "</tr>" +
                "<tr>" +
                    "<td>tr 1 td 0</td>" +
                    "<td>tr 1 td 1</td>" +
                "</tr>" +
                "<thead>" +
                    "<tr>" +
                        "<td>tr 1 td 0</td>" +
                        "<td>tr 1 td 1</td>" +
                    "</tr>" +
                "</thead>" +
            "</table>";



    private ContentNode testNode;
    private ContentNode parentNode;

    @Autowired
    private DrugDao drugDao;

    @Autowired
    private DrugFetcher drugFetcher;

    @Autowired
    private IndicationScraper indicationScraper;

    @Autowired
    private RichtlijnenNhgWebScraper richtlijnenNhgWebScraper;

    @BeforeEach
    public void bypassSSL() throws IOException {
        SSLHelper.bypassSSL();
        this.drugFetcher.parseHtml();
        this.indicationScraper.parseHtml();

        this.testNode = new ContentNode();
        this.parentNode = new ContentNode();
        parentNode.addContent(testNode);
    }

    @AfterEach
    public void cleanUpDao() {
        drugDao.removeAllDrugs();
    }

    @Test
    void elementIsTitle_sunnyDay() {
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
        String[] testHtmlArray = {
                "<p id='test-non-title'>Test paragraph</p>",
                "<div id='test-non-title'>this is not a title<h4>This is a title</h4></div>",
                "<div class='collapsible-toggler'>this is a title <p id='test-non-title'>but this is not a title</p> </div>"
        };

        for (String testHtml: testHtmlArray) {
            Element elementToTest = Jsoup.parse(testHtml).select("#test-non-title").get(0);
            String message = "when executing elementIsTitle(" + elementToTest + ")";
            assertFalse(this.richtlijnenNhgWebScraper.elementIsTitle(elementToTest), message);
        }
    }

    @Test
    void getPrevTitleElement_sunnyDayFromInputParagraph() {
        String testHtml =
                "<div> " +
                    "<h1 id='prev-title'>this is the previous title</h1>" +
                    "<p id='test-paragraph'>this is a test paragraph</p> " +
                    "<h1>this is a title</h1>" +
                    "<p>this is a paragraph</p>" +
                "</div>";
        Document doc = Jsoup.parse(testHtml);
        Elements testElements = doc.select("#test-paragraph");
        Element prevTitleElement = doc.select("#prev-title").get(0);

        assertEquals(prevTitleElement, this.richtlijnenNhgWebScraper.getPrevTitleElement(testElements.get(0)));
    }

    @Test
    void getPrevTitleElement_sunnyDayFromInputTitle() {
        String testHtml =
                "<div> " +
                    "<h1 id='prev-title'>this is the previous title</h1>" +
                    "<p>this is a test paragraph</p> " +
                    "<h1 id='test-title'>this is a title</h1>" +
                    "<p>this is a paragraph</p>" +
                "</div>";
        Document doc = Jsoup.parse(testHtml);
        Elements testElements = doc.select("#test-title");
        Element prevTitleElement = doc.select("#prev-title").get(0);

        assertEquals(prevTitleElement, this.richtlijnenNhgWebScraper.getPrevTitleElement(testElements.get(0)));
    }

    @Test
    void getPrevTitleElement_noPrevTitlePresent() {
        String testHtml =
                "<div> " +
                    "<p>this is a paragraph</p>" +
                    "<p>this is a test paragraph</p> " +
                    "<h1 id='test-title'>this is a title</h1>" +
                "</div>";
        Document doc = Jsoup.parse(testHtml);
        Elements testElements = doc.select("#test-title");

        String message = "while executing getPrevTitleElement(" + testElements.get(0) + ")";
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {this.richtlijnenNhgWebScraper.getPrevTitleElement(testElements.get(0));},
                message
        );
    }

    @Test
    void processTitleElement_singleTitle_TestReturnValue(){
        String htmlToTest = "<h1 id='test-title'>This is a title element</h1>";
        Element elementToTest = Jsoup.parse(htmlToTest).select("#test-title").get(0);

        boolean returnValue = this.richtlijnenNhgWebScraper.processTitleElement(testNode, elementToTest);

        assertFalse(returnValue);
    }

    @Test
    void processTitleElement_singleTitle_TestContentTitle(){
        String htmlToTest = "<h1 id='test-title'>This is a title element</h1>";
        Element elementToTest = Jsoup.parse(htmlToTest).select("#test-title").get(0);

        this.richtlijnenNhgWebScraper.processTitleElement(testNode, elementToTest);

        assertEquals("This is a title element", testNode.getContentTitle());
    }

    @Test
    void processTitleElement_siblingTitle_testReturnValue(){
        String htmlToTest =
                "<h1>This the prev title element</h1>" +
                "<h1 id='test-title'>This is a title element</h1>";
        Element elementToTest = Jsoup.parse(htmlToTest).select("#test-title").get(0);

        boolean returnValue = this.richtlijnenNhgWebScraper.processTitleElement(testNode, elementToTest);

        assertTrue(returnValue);
    }

    @Test
    void processTitleElement_siblingTitle_testContentTitle(){
        String htmlToTest =
                "<h1>This the prev title element</h1>" +
                "<h1 id='test-title'>This is a title element</h1>";
        Element elementToTest = Jsoup.parse(htmlToTest).select("#test-title").get(0);

        this.richtlijnenNhgWebScraper.processTitleElement(testNode, elementToTest);

        assertNull(testNode.getContentTitle());
    }

    @Test
    void processTitleElement_siblingTitle_testProcessedContent(){
        String htmlToTest =
                "<h1>This the prev title element</h1>" +
                "<h1 id='test-title'>This is a title element</h1>";
        Element elementToTest = Jsoup.parse(htmlToTest).select("#test-title").get(0);

        this.richtlijnenNhgWebScraper.processTitleElement(testNode, elementToTest);

        assertEquals("This is a title element", parentNode.getContent().get(1).getContentTitle());
    }

    @Test
    void processTitleElement_parentTitle_testReturnValue(){
        String htmlToTest =
                "<h1>This the parent title element</h1>" +
                "<h2 id='test-title'>This is a title element</h2>";
        Element elementToTest = Jsoup.parse(htmlToTest).select("#test-title").get(0);
        boolean returnValue = this.richtlijnenNhgWebScraper.processTitleElement(testNode, elementToTest);

        assertTrue(returnValue);
    }

    @Test
    void processTitleElement_parentTitle_testContentTitle(){
        String htmlToTest =
                "<h1>This the parent title element</h1>" +
                "<h2 id='test-title'>This is a title element</h2>";
        Element elementToTest = Jsoup.parse(htmlToTest).select("#test-title").get(0);

        this.richtlijnenNhgWebScraper.processTitleElement(testNode, elementToTest);

        assertNull(testNode.getContentTitle());
    }

    @Test
    void processTitleElement_parentTitle_testProcessedContent(){
        String htmlToTest =
                "<h1>This the parent title element</h1>" +
                        "<h2 id='test-title'>This is the title element</h2>";
        Element elementToTest = Jsoup.parse(htmlToTest).select("#test-title").get(0);

        this.richtlijnenNhgWebScraper.processTitleElement(testNode, elementToTest);

        assertEquals("This is the title element", testNode.getContent().get(0).getContentTitle());
    }

    @Test
    void processTitleElement_noPrecedingTitle(){
        String htmlToTest =
                "<p>This not a title element</p>" +
                "<h1 id='test-title'>This a title element without a preceding title element</h1>";

        Element elementToTest = Jsoup.parse(htmlToTest).select("#test-title").get(0);

        this.richtlijnenNhgWebScraper.processTitleElement(testNode, elementToTest);
        ContentLeaf createdLeaf = (ContentLeaf) testNode.getContent().get(0);

        assertEquals("This a title element without a preceding title element", createdLeaf.getContentTitle());
    }

    @Test
    void processTitleElement_inputNotATitle(){
        String htmlToTest =
                "<h1>This a title</h1>" +
                        "<p id='test-title'>This a fake title element</p>";

        Element elementToTest = Jsoup.parse(htmlToTest).select("#test-title").get(0);

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {this.richtlijnenNhgWebScraper.processTitleElement(testNode, elementToTest);}
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {olToTest, ulToTest})
    void processListElement_sunnyDay(String htmlToTest) {
        Element elementToTest = Jsoup.parse(htmlToTest).select("#test-list").get(0);

        this.richtlijnenNhgWebScraper.processListElement(testNode, elementToTest);
        ContentLeaf resultContent = (ContentLeaf) testNode.getContent().get(0);

        assertArrayEquals(new String[]{"list element 1", "list element 2", "list element 3"}, resultContent.getContent().toArray());
    }

    @ParameterizedTest
    @ValueSource(strings = {recursiveListToTestUlUl, recursiveListToTestUlOl, recursiveListToTestOlUl, recursiveListToTestOlOl})
    void processListElement_recursiveList_testContentOuterList(String htmlToTest) {

        Element elementToTest = Jsoup.parse(htmlToTest).select("#test-list").get(0);
        this.richtlijnenNhgWebScraper.processListElement(testNode, elementToTest);

        ContentNode resultContent = (ContentNode) testNode.getContent().get(0);
        ContentLeaf list1Element1 = (ContentLeaf) resultContent.getContent().get(0);

        assertEquals("list 1 element 1", list1Element1.getContent().get(0));
    }

    @ParameterizedTest
    @ValueSource(strings = {recursiveListToTestUlUl, recursiveListToTestUlOl, recursiveListToTestOlUl, recursiveListToTestOlOl})
    void processListElement_recursiveList_testContentInnerList(String htmlToTest) {

        Element elementToTest = Jsoup.parse(htmlToTest).select("#test-list").get(0);
        this.richtlijnenNhgWebScraper.processListElement(testNode, elementToTest);

        ContentNode resultContent = (ContentNode) testNode.getContent().get(0);
        ContentLeaf list2 = (ContentLeaf) resultContent.getContent().get(2);

        assertEquals("list 2 element 1", list2.getContent().get(0));
    }

    @Test
    void processListElement_inputNotAList() {
        String htmlToTest =
                "<div id='test-list'>" +
                    "<p>Fake list element 1</p>" +
                    "<p>Fake list element 2</p>" +
                    "<p>Fake list element 3</p>" +
                "</div>";
        Element elementToTest = Jsoup.parse(htmlToTest).select("#test-list").get(0);

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {this.richtlijnenNhgWebScraper.processListElement(testNode, elementToTest);}
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {simplePToTest, pWithSubElementsToTest})
    void processParagraphElement_sunnyDay(String htmlToTest) {
        Element elementToTest = Jsoup.parse(htmlToTest).select("#test-paragraph").get(0);

        System.out.println(elementToTest);

        this.richtlijnenNhgWebScraper.processParagraphElement(testNode, elementToTest);

        ContentLeaf paragraphContent = (ContentLeaf) testNode.getContent().get(0);

        String[] expected = new String[]{elementToTest.text()};
        assertArrayEquals(expected, paragraphContent.getContent().toArray());
    }

    @Test
    void processParagraphElement_inputNotAParagraph() {
        String htmlToTest =
                "<div id='test-paragraph'>" +
                    "This is not a paragraph. " +
                    "<p>This is a paragraph. </p>" +
                "</div>";
        Element elementToTest = Jsoup.parse(htmlToTest).select("#test-paragraph").get(0);

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {this.richtlijnenNhgWebScraper.processParagraphElement(testNode, elementToTest);}
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {simpleDiv, divWithLooseText})
    void processDivElement_sunnyDay_single(String htmlToTest) {
        Element elementToTest = Jsoup.parse(htmlToTest).select("#test-div").get(0);

        this.richtlijnenNhgWebScraper.processDivElement(testNode, elementToTest);

        ContentLeaf createdLeaf = (ContentLeaf) testNode.getContent().get(0);

        String[] expected = Arrays.stream(elementToTest.text().split("\\.")).map(str -> str = (str + ".").strip()).toArray(String[]::new);
        assertArrayEquals(expected, createdLeaf.getContent().toArray());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    void processDivElement_sunnyDay_recursiveOuterContent(int testNthElement) {
        String htmlToTest =
                "<div id='test-div'>" +
                    "div 1 child 0" +
                    "<p>div 1 child 1</p>" +
                    "<div>" +
                        "<p>div 2 element 0</p>" +
                        "<p>div 2 element 1</p>" +
                    "</div>" +
                "</div>";

        Element elementToTest = Jsoup.parse(htmlToTest).select("#test-div").get(0);

        this.richtlijnenNhgWebScraper.processDivElement(testNode, elementToTest);

        ContentNode createdContent = (ContentNode) testNode.getContent().get(0);
        ContentLeaf div1Element1 = (ContentLeaf) createdContent.getContent().get(testNthElement);

        String[] expected = new String[]{"div 1 child " + testNthElement};
        assertArrayEquals(expected, div1Element1.getContent().toArray());
    }

    @Test
    void processDivElement_sunnyDay_recursiveInnerContent() {
        String htmlToTest =
                "<div id='test-div'>" +
                    "div 1 child 0" +
                    "<p>div 1 child 1</p>" +
                    "<div>" +
                        "<p>div 2 child 0</p>" +
                        "<p>div 2 child 1</p>" +
                    "</div>" +
                "</div>";

        Element elementToTest = Jsoup.parse(htmlToTest).select("#test-div").get(0);

        this.richtlijnenNhgWebScraper.processDivElement(testNode, elementToTest);

        ContentNode createdContent = (ContentNode) testNode.getContent().get(0);
        ContentLeaf div2 = (ContentLeaf) createdContent.getContent().get(2);

        String[] expected = new String[]{"div 2 child 0", "div 2 child 1"};
        assertArrayEquals(expected, div2.getContent().toArray());
    }

    @Test
    void processDivElement_inputNotADiv() {
        String htmlToTest =
                "<a id='test-div'>" +
                    "<p>fake div element 1</p>" +
                    "<p>fake div element 2</p>" +
                "</a>";

        Element elementToTest = Jsoup.parse(htmlToTest).select("#test-div").get(0);

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {this.richtlijnenNhgWebScraper.processDivElement(testNode, elementToTest);}
        );
    }

    @Test
    void getLooseTextFromElement_sunnyDay() {
        String htmlToTest =
                "<div id='test-div'> " +
                    "This is loose text" +
                    "<p>fake div element 1</p>" +
                    "<p>fake div element 2</p>" +
                "</div>";

        Element elementToTest = Jsoup.parse(htmlToTest).select("#test-div").get(0);

        assertEquals("This is loose text", this.richtlijnenNhgWebScraper.getLooseTextFromElement(elementToTest));
    }

    @Test
    void processListElement_sunnyDayNoChildren() {
        String htmlToTest =
                "<ul>" +
                "   <li id='test-list-element'>Element to test</li>" +
                "</ul>";

        Element elementToTest = Jsoup.parse(htmlToTest).select("#test-list-element").get(0);

        this.richtlijnenNhgWebScraper.processListItemElement(testNode, elementToTest);
        ContentLeaf createdContent = (ContentLeaf) testNode.getContent().get(0);

        String[] expected = new String[]{"Element to test"};
        assertArrayEquals(expected, createdContent.getContent().toArray());
    }

    @Test
    void processListElement_sunnyDaySpanEmSubAChildren() {
        String htmlToTest =
                "<ul>" +
                    "<li id='test-list-element'><span>Element</span> <a>to</a> <em>test</em><sub>1</sub></li>" +
                "</ul>";

        Element elementToTest = Jsoup.parse(htmlToTest).select("#test-list-element").get(0);

        this.richtlijnenNhgWebScraper.processListItemElement(testNode, elementToTest);
        ContentLeaf createdContent = (ContentLeaf) testNode.getContent().get(0);

        String[] expected = new String[]{"Element to test1"};
        assertArrayEquals(expected, createdContent.getContent().toArray());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    void processListElement_sunnyDayBlockElementChildren(int testNthElement) {
        String htmlToTest =
                "<ul>" +
                    "<li id='test-list-element'>" +
                        "<div>" +
                            "<p>div 0 p 0</p>" +
                            "<p>div 0 p 1</p>" +
                        "</div>" +
                        "<div>" +
                            "<p>div 1 p 0</p>" +
                            "<p>div 1 p 1</p>" +
                        "</div>" +
                        "<div>" +
                            "<p>div 2 p 0</p>" +
                            "<p>div 2 p 1</p>" +
                        "</div>" +
                    "</li>" +
                "</ul>";

        Element elementToTest = Jsoup.parse(htmlToTest).select("#test-list-element").get(0);

        this.richtlijnenNhgWebScraper.processListItemElement(testNode, elementToTest);

        ContentNode createdContent = (ContentNode) testNode.getContent().get(0);
        ContentLeaf resultLeaf = (ContentLeaf) createdContent.getContent().get(testNthElement);

        String[] expected = new String[]{"div " + testNthElement + " p 0", "div " + testNthElement + " p 1",};
        assertArrayEquals(expected, resultLeaf.getContent().toArray());
    }

    @Test
    void processListElement_inputNotAListElement() {
        String htmlToTest =
                "<p id='test-element'>this is not a list element</p>";

        Element elementToTest = Jsoup.parse(htmlToTest).select("#test-element").get(0);

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {this.richtlijnenNhgWebScraper.processListItemElement(testNode, elementToTest);}
        );
    }


    @ParameterizedTest
    @ValueSource(strings = {validTestTable1, validTestTable2})
    void processTableElement_sunnyDay_someContentIsAdded(String htmlToTest) {
        Element elementToTest = Jsoup.parse(htmlToTest).select("#test-table").get(0);
        this.richtlijnenNhgWebScraper.processTableElement(testNode, elementToTest);

        ContentNode createdNode = (ContentNode) testNode.getContent().get(0);

        assertTrue(createdNode.getContent().size() > 0);
    }

    @ParameterizedTest
    @ValueSource(strings = {validTestTable1, validTestTable2})
    void processTableElement_sunnyDay_captionIsSet(String htmlToTest) {
        Element elementToTest = Jsoup.parse(htmlToTest).select("#test-table").get(0);
        this.richtlijnenNhgWebScraper.processTableElement(testNode, elementToTest);

        String expected = elementToTest.select("caption").get(0).text();
        assertEquals(expected, testNode.getContent().get(0).getContentTitle());
    }

    @ParameterizedTest
    @ValueSource(strings = {illegalTableDoubleCaption, illegalTableIllegalCombinedChildren, illegalTableNotATable})
    void processTableElement_tableWithTwoCaptions(String htmlToTest) {
        Element elementToTest = Jsoup.parse(htmlToTest).select("#test-table").get(0);

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {this.richtlijnenNhgWebScraper.processTableElement(testNode, elementToTest);}
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void processTableSectionElement_sunnyDay_someContentIsAdded(int testNthSection) {
        String cssQuery = "#test-section-" + testNthSection;
        Element elementToTest = Jsoup.parse(validTestTable2).select(cssQuery).get(0);

        this.richtlijnenNhgWebScraper.processTableSectionElement(testNode, elementToTest);
        ContentNode createdNode = (ContentNode) testNode.getContent().get(0);

        assertTrue(createdNode.getContent().size() > 0);
    }

    @Test
    void processTableSectionElement_invalidTableSection() {
        String htmlToTest =
                "<p id='test-section'>This is not a table section</p>";

        Element elementToTest = Jsoup.parse(htmlToTest).select("#test-section").get(0);

        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> {this.richtlijnenNhgWebScraper.processTableSectionElement(testNode, elementToTest);}
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1 , 2, 3})
    void processTableRowElement_sunnyDay_someContentIsAdded(int testNthtr) {
        Element elementToTest = Jsoup.parse(validTestTable1).select("#test-table tr").get(testNthtr);

        this.richtlijnenNhgWebScraper.processTableRowElement(testNode, elementToTest);
        ContentNode createdContent = (ContentNode) testNode.getContent().get(0);

        assertTrue(createdContent.getContent().size() > 0);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    void processTableRowElement_sunnyDay_thOrTdLeafAddedToNodeIsOfClassLeaf(int testNthtr) {
        Element elementToTest = Jsoup.parse(validTestTable1).select("#test-table tr").get(testNthtr);

        this.richtlijnenNhgWebScraper.processTableRowElement(testNode, elementToTest);
        ContentNode createdContent = (ContentNode) testNode.getContent().get(0);


        assertEquals(ContentLeaf.class, createdContent.getContent().get(0).getClass());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    void processTableRowElement_sunnyDay_thOrTdLeafAddedToNodeHasRightContentType(int testNthtr) {
        Element elementToTest = Jsoup.parse(validTestTable1).select("#test-table tr").get(testNthtr);

        this.richtlijnenNhgWebScraper.processTableRowElement(testNode, elementToTest);
        ContentNode createdContent = (ContentNode) testNode.getContent().get(0);

        ContentLeaf trChildContent = (ContentLeaf) createdContent.getContent().get(0);

        String expected = elementToTest.select("th, td").get(0).tagName().toUpperCase();
        assertEquals(expected, trChildContent.getContentType());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    void processTableRowElement_sunnyDay_childrenThAndTdAreOfClassNodes(int testNthChild) {
        Element elementToTest = Jsoup.parse(validTestTable1).select("#test-table tr").get(2);

        this.richtlijnenNhgWebScraper.processTableRowElement(testNode, elementToTest);
        ContentNode createdContent = (ContentNode) testNode.getContent().get(0);

        assertEquals(ContentLeaf.class, createdContent.getContent().get(testNthChild).getClass());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    void processTableRowElement_sunnyDay_childrenThAndTdAreOfCorrectContentType(int testNthChild) {
        Element elementToTest = Jsoup.parse(validTestTable1).select("#test-table tr").get(2);

        this.richtlijnenNhgWebScraper.processTableRowElement(testNode, elementToTest);
        ContentNode createdContent = (ContentNode) testNode.getContent().get(0);

        ContentLeaf resultingLeaf = (ContentLeaf) createdContent.getContent().get(testNthChild);
        String expected = elementToTest.select("th, td").get(testNthChild).tagName().toUpperCase();
        assertEquals(expected, resultingLeaf.getContentType());
    }

    @Test
    void processTableRowElement_inputNotTr() {
        String htmlToTest =
                "<p id='test-tr'>This is not a table row</p>";
        Element elementToTest = Jsoup.parse(htmlToTest).select("#test-tr").get(0);

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {this.richtlijnenNhgWebScraper.processTableRowElement(testNode, elementToTest);}
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7})
    void processTableDataElement_sunnyDay_someContentIsAdded(int testNthElement) {
        Element elementToTest = Jsoup.parse(validTestTable3).select("#test-table tr").get(0).children().get(testNthElement);

        this.richtlijnenNhgWebScraper.processTableDataElement(testNode, elementToTest);

        assertTrue(testNode.getContent().size() > 0);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7})
    void processTableDataElement_sunnyDay_ofCorrectContentTypeForLeafs(int testNthElement) {
        Element elementToTest = Jsoup.parse(validTestTable3).select("#test-table tr").get(0).children().get(testNthElement);

        this.richtlijnenNhgWebScraper.processTableDataElement(testNode, elementToTest);

        String actual;
        if (testNode.getContent().get(0).getClass().equals(ContentLeaf.class)) {
            actual = ((ContentLeaf) testNode.getContent().get(0)).getContentType();
        } else {
            actual = ((ContentNode) testNode.getContent().get(0)).getContentType();
        }

        String expected = elementToTest.tagName().toUpperCase();
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4})
    void processTableDataElement_sunnyDay_ofCorrectContent(int testNthElement) {
        Element elementToTest = Jsoup.parse(validTestTable3).select("#test-table tr").get(0).children().get(testNthElement);

        this.richtlijnenNhgWebScraper.processTableDataElement(testNode, elementToTest);

        String[] expected = new String[]{elementToTest.text()};
        List<String> content = ((ContentLeaf) testNode.getContent().get(0)).getContent();
        assertArrayEquals(expected, content.toArray());
    }

    @Test
    void processTableDataElement_sunnyDay_getsColSpanAttribute() {
        Element elementToTest = Jsoup.parse(validTestTable3).select("#test-table tr").get(0).children().get(3);

        this.richtlijnenNhgWebScraper.processTableDataElement(testNode, elementToTest);

        String colSpanValue = testNode.getContent().get(0).getAttributes().get("colspan");
        assertEquals("2", colSpanValue);
    }

    @Test
    void processTableDataElement_sunnyDay_getsRowSpanAttribute() {
        Element elementToTest = Jsoup.parse(validTestTable3).select("#test-table tr").get(0).children().get(4);

        this.richtlijnenNhgWebScraper.processTableDataElement(testNode, elementToTest);

        String rowSpanValue = testNode.getContent().get(0).getAttributes().get("rowspan");
        assertEquals("2", rowSpanValue);
    }

    @ParameterizedTest
    @ValueSource(ints = {5, 6})
    void processTableDataElement_sunnyDay_leafWithMultipleElements(int testNthElement) {
        Element elementToTest = Jsoup.parse(validTestTable3).select("#test-table tr").get(0).children().get(testNthElement);

        this.richtlijnenNhgWebScraper.processTableDataElement(testNode, elementToTest);

        assertEquals(ContentLeaf.class, testNode.getContent().get(0).getClass());
    }

    @ParameterizedTest
    @ValueSource(ints = {5, 6})
    void processTableDataElement_sunnyDay_contentTypeOfLeafWithMultipleElements(int testNthElement) {
        Element elementToTest = Jsoup.parse(validTestTable3).select("#test-table tr").get(0).children().get(testNthElement);

        this.richtlijnenNhgWebScraper.processTableDataElement(testNode, elementToTest);

        ContentLeaf resultLeaf = (ContentLeaf) testNode.getContent().get(0);

        String expected = elementToTest.tagName().toUpperCase();
        String actual = resultLeaf.getContentType();
        assertEquals(expected, actual);
    }
}