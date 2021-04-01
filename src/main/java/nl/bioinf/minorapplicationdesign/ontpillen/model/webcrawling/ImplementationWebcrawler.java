package nl.bioinf.minorapplicationdesign.ontpillen.model.webcrawling;


import nl.bioinf.minorapplicationdesign.ontpillen.model.MedicineDAO.DrugDao;
import nl.bioinf.minorapplicationdesign.ontpillen.model.MedicineDAO.InMemoryDrugDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ImplementationWebcrawler {
    DrugDao drugDao;

    @Autowired
    public void setDrugDao(DrugDao drugDao) {
        this.drugDao = drugDao;
    }

    public void runWebcrawlers() throws IOException {
//        FarmacoWebScraper farmacoWebcrawler = new FarmacoWebScraper(drugDao);
//        farmacoWebcrawler.getInformation();

        DrugFetcher drugFetcher = new DrugFetcher(drugDao);
        drugFetcher.parseHtml();

//        ApotheekWebScraper apotheekWebcrawler = new ApotheekWebScraper(drugDao);
//        apotheekWebcrawler.getInformation();
//
//        RichtlijnenNhgWebScraper richtlijnenNhgWebcrawler = new RichtlijnenNhgWebScraper(drugDao);
//        richtlijnenNhgWebcrawler.getInformation();
//
//        GgzStandaardenWebScraper ggzStandaardenWebcrawler = new GgzStandaardenWebScraper(drugDao);
//        ggzStandaardenWebcrawler.getInformation();
    }


//    public static void main(String[] args) throws IOException {
//        ImplementationWebcrawler test = new ImplementationWebcrawler();
//        test.runWebcrawlers();
//    }
}
