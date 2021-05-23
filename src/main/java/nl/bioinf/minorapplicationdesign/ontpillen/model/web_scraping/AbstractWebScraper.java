package nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping;

import java.io.IOException;


/**
 * AbstractWebScraper is an interface for all webscrapers.
 * It contains one method: parseHtml.
 * @author Larissa Bouwknegt, Jonathan Klimp, Naomi Hindriks
 */
public interface AbstractWebScraper {

    /**
     * Method that will parse the html of the webscraper. It calls all function needed to do this.
     * After this method is run the html page is processed and the data is saved in the dao
     * @throws IOException
     */
    void parseHtml() throws Exception;
}
