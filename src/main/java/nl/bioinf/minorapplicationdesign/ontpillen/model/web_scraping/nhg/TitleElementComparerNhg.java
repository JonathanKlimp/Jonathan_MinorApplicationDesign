package nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping.nhg;

import org.jsoup.nodes.Element;

public class TitleElementComparerNhg {

    public static int compareTo(Element titleElement, Element otherTitleElement) {
        if (titleElement.tagName().matches("h[1-6]") && otherTitleElement.tagName().matches("h[1-6]")) {
            int thisTitleElementInt = Integer.parseInt(titleElement.tagName().substring(1,2));
            int otherTitleElementInt = Integer.parseInt(otherTitleElement.tagName().substring(1,2));
            return otherTitleElementInt - thisTitleElementInt;
        } else if (titleElement.hasClass("collapsible-toggler")) {
            return TitleElementComparerNhg.compareCollapsibleToggler(otherTitleElement);
        } else if (titleElement.hasClass("field--name-heading")) {
            return TitleElementComparerNhg.compareFieldNameHEading(otherTitleElement);
        } else {
            throw new IllegalArgumentException("The this (" + titleElement + ") is not a valid title element");
        }
    }

    private static int compareCollapsibleToggler(Element otherTitleElement) {
        if (otherTitleElement.hasClass("collapsible-toggler")) {
            return 0;
        } else if (otherTitleElement.hasClass("field--name-heading")) {
            return 1;
        } else if (otherTitleElement.tagName().matches("h[1-6]")) {
            if (Integer.parseInt(otherTitleElement.tagName().substring(1, 2)) < 3) {
                return -1;
            } else if (Integer.parseInt(otherTitleElement.tagName().substring(1, 2)) > 4) {
                return 1;
            } else {
                Element parentOtherTitleElement = otherTitleElement.parent();
                if (parentOtherTitleElement.hasClass("field--name-tab-1-text")) {
                    return 1;
                } else if (parentOtherTitleElement.hasClass("paragraph--type--section")) {
                    return -1;
                } else {
                    return 0;
                }
            }
        } else {
            throw new IllegalArgumentException("The otherTitleElement (" + otherTitleElement + ") is not a valid title element");
        }
    }

    private static  int compareFieldNameHEading(Element otherTitleElement) {
        if (otherTitleElement.hasClass("collapsible-toggler")) {
            return -1;
        } else if (otherTitleElement.hasClass("field--name-heading")) {
            return 0;
        } else if (otherTitleElement.tagName().matches("h[1-6]")) {
            if (Integer.parseInt(otherTitleElement.tagName().substring(1, 2)) < 4) {
                return -1;
            } else if (Integer.parseInt(otherTitleElement.tagName().substring(1, 2)) > 4) {
                return 1;
            } else {
                Element parentOtherTitleElement = otherTitleElement.parent();
                if (parentOtherTitleElement.hasClass("field--name-tab-1-text")) {
                    return 1;
                } else {
                    return -1;
                }
            }
        } else {
            throw new IllegalArgumentException("The otherTitleElement (" + otherTitleElement + ") is not a valid title element");
        }
    }
}
