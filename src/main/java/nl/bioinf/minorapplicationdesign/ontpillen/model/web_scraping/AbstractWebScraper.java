package nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping;

import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugDao;

import java.io.IOException;
import java.util.List;

public abstract class AbstractWebScraper {
    List<String> information = null;
    protected DrugDao drugDao;

    AbstractWebScraper(DrugDao drugDao){
        this.drugDao = drugDao;
    }

    public abstract void parseHtml() throws IOException;

}
