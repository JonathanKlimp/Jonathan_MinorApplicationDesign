package nl.bioinf.minorapplicationdesign.ontpillen.model.webcrawling;

import nl.bioinf.minorapplicationdesign.ontpillen.model.MedicineDAO.DrugDao;

import java.io.IOException;
import java.util.List;

public abstract class AbstractWebScraper {
    List<String> information = null;
    DrugDao drugDao;

    AbstractWebScraper(DrugDao drugDao){
        this.drugDao = drugDao;
    }

    public abstract void parseHtml() throws IOException;

}
