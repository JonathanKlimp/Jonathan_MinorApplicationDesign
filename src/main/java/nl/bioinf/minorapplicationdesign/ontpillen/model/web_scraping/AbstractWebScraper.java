package nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping;

import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugDao;

import java.io.IOException;
import java.util.List;

public interface AbstractWebScraper {

//    TODO add javadoc to interface method
    public void parseHtml() throws IOException;

}
