package nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.NodeVisitor;

import java.util.Map;

/**
 * @author Naomi Hindriks
 *
 * A class that holds functions that can be used for multiple webcrawlers, these are general methods that can work on the
 * HTML of any website
 */
public class Util {

    /**
     * @param element Jsoup Element to find the maximum node depth of.
     * @return an integer presenting the maximmum node depth of the element, including the element itself (if it is
     * an element without children the return value is 1).
     */
    public static int getMaxDepth(Element element) {
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
    }

    public static Map<String, Element> getCopiesOfElementWithoutAndOnlySpanEmSubA(Element element) {
        Element elementWithoutSpanEmSubA = element.clone();
        elementWithoutSpanEmSubA.children().select("a, span, em, sub").remove();

        Element elementOnlySpanEmSubA = element.clone();
        elementOnlySpanEmSubA.children().select(":not(a, span, em, sub)").remove();
        return Map.of("elementWithoutSpanEmSubA", elementWithoutSpanEmSubA, "elementOnlySpanEmSubA", elementOnlySpanEmSubA);
    }

    public static boolean elementHasChildrenOfTag(Element element, String tagNameRegex) {
        return element.children().stream()
                .anyMatch((Element e) -> {return e.tagName().matches(tagNameRegex);});
    }

}
