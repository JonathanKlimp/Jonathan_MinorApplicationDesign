package nl.bioinf.minorapplicationdesign.ontpillen.model.webcrawling;


import nl.bioinf.minorapplicationdesign.ontpillen.model.MedicineDAO.DrugsDao;
import nl.bioinf.minorapplicationdesign.ontpillen.model.MedicineDAO.InMemoryDrugDao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImplementationWebcrawler {
    DrugsDao informationStorage = new InMemoryDrugDao(); // make singleton

    public void runWebcrawlers() throws IOException {
//        FarmacoWebcrawler farmacoWebcrawler = new FarmacoWebcrawler(informationStorage);
//        farmacoWebcrawler.getInformation();

        DrugFetcher drugFetcher = new DrugFetcher(informationStorage);
        drugFetcher.getInformation();
        System.out.println(informationStorage.getListOfDrugs());

//        ApotheekWebcrawler apotheekWebcrawler = new ApotheekWebcrawler(informationStorage);
//        apotheekWebcrawler.getInformation();
//
//        RichtlijnenNhgWebcrawler richtlijnenNhgWebcrawler = new RichtlijnenNhgWebcrawler(informationStorage);
//        richtlijnenNhgWebcrawler.getInformation();
//
//        GgzStandaardenWebcrawler ggzStandaardenWebcrawler = new GgzStandaardenWebcrawler(informationStorage);
//        ggzStandaardenWebcrawler.getInformation();
    }

    public static void main(String[] args) throws IOException {
        ImplementationWebcrawler test = new ImplementationWebcrawler();
        test.runWebcrawlers();
    }
}
