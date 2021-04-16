package nl.bioinf.minorapplicationdesign.ontpillen.model.webcrawling;


import nl.bioinf.minorapplicationdesign.ontpillen.model.MedicineDAO.DrugDao;
import nl.bioinf.minorapplicationdesign.ontpillen.model.MedicineDAO.InMemoryDrugDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

//TODO Hier heb je unit tests voor

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
