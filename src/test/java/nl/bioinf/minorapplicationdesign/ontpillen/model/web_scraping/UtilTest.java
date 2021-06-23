package nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilTest {

    @Test
    void getMaxDepth_depthTwoSingleEndLeave() {
        String testString = "<ul id='test-ul'><li>Test</li></ul>";
        Element testDiv = Jsoup.parse(testString).select("ul#test-ul").first();
        String message = "while finding depth of: " + testString;
        assertEquals(2, Util.getMaxDepth(testDiv));
    }

    @Test
    void getMaxDepth_depthFourMultipleEndLeave() {
        String testHtml =
                "<ul id='test-ul'>" +
                        "<li>Depth here is 2</li>" +
                        "<li>" +
                        "<ul>" +
                        "<li>Depth here is 4</li>" +
                        "</ul>" +
                        "</li>" +
                        "</ul>";
        Element testDiv = Jsoup.parse(testHtml).select("ul#test-ul").first();
        String message = "while finding depth of: " + testHtml;
        assertEquals(4, Util.getMaxDepth(testDiv), message);
    }

}