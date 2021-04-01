package nl.bioinf.minorapplicationdesign.ontpillen.model.webcrawling;


import nl.bioinf.minorapplicationdesign.ontpillen.model.MedicineDAO.InMemoryDrugDao;

import java.io.IOException;

//TODO Hier heb je unit tests voor
public class ImplementationWebcrawler {
    InMemoryDrugDao informationStorage = InMemoryDrugDao.getInstance();

    public void runWebcrawlers() throws IOException {
//        FarmacoWebScraper farmacoWebcrawler = new FarmacoWebScraper(drugDao);
//        farmacoWebcrawler.getInformation();

        DrugFetcher drugFetcher = new DrugFetcher(informationStorage);
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

    public static void main(String[] args) throws IOException {
        ImplementationWebcrawler test = new ImplementationWebcrawler();
        test.runWebcrawlers();
    }
}
