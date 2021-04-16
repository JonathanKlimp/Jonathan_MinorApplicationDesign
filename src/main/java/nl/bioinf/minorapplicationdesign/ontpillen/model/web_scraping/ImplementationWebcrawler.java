package nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping;


import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugDao;
import nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping.DrugFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

//TODO Hier heb je unit tests voor
//TODO naam veranderen, het is geen implementatie van webscraper meer, maar een soort main die alle scraping uitvoert.

@Component
public class ImplementationWebcrawler {
    DrugDao drugDao;

    @Autowired
    public void setDrugDao(DrugDao drugDao) {
        this.drugDao = drugDao;
    }

    public void runWebcrawlers() throws IOException {
        DrugFetcher drugFetcher = new DrugFetcher(drugDao);
        drugFetcher.parseHtml();
    }
}
