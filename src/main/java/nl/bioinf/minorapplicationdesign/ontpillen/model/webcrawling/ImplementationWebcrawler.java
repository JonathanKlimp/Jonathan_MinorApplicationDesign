package nl.bioinf.minorapplicationdesign.ontpillen.model.webcrawling;


import nl.bioinf.minorapplicationdesign.ontpillen.model.MedicineDAO.DrugsDao;
import nl.bioinf.minorapplicationdesign.ontpillen.model.MedicineDAO.InMemoryDrugDao;

import java.util.ArrayList;
import java.util.List;

public class ImplementationWebcrawler {
    static DrugsDao informationStorage = new InMemoryDrugDao();

    public void runWebcrawlers() {
        FarmacoWebcrawler farmacoWebcrawler = new FarmacoWebcrawler(informationStorage);
        farmacoWebcrawler.getInformation();

//        ApotheekWebcrawler apotheekWebcrawler = new ApotheekWebcrawler(informationStorage);
//        apotheekWebcrawler.getInformation();
//
//        RichtlijnenNhgWebcrawler richtlijnenNhgWebcrawler = new RichtlijnenNhgWebcrawler(informationStorage);
//        richtlijnenNhgWebcrawler.getInformation();
//
//        GgzStandaardenWebcrawler ggzStandaardenWebcrawler = new GgzStandaardenWebcrawler(informationStorage);
//        ggzStandaardenWebcrawler.getInformation();
    }
}

//{serotonineheropnameremmers, selectief=[citalopram, dapoxetine, escitalopram, fluoxetine, fluvoxamine, paroxetine, sertraline],
//        tricyclische antidepressiva=[amitriptyline, clomipramine, dosulepine, imipramine, maprotiline, nortriptyline],
//        psychostimulantia, overige=[atomoxetine, coffe�ne, modafinil], antipsychotica=[],
//        middelen bij alcoholverslaving=[acamprosaat, disulfiram, nalmefeen, naltrexon], middelen bij nicotineverslaving=[nicotine, varenicline],
//        antidepressiva, overige=[bupropion, vortioxetine], amfetaminen=[dexamfetamine, lisdexamfetamine, methylfenidaat], lithiumzouten=[lithium],
//        melatonine agonisten=[agomelatine, melatonine], serotonineheropnameremmers, niet-selectief=[duloxetine, trazodon, venlafaxine],
//        tetracyclische antidepressiva=[mianserine, mirtazapine], MAO-A-remmers=[moclobemide],
//        benzodiazepine agonisten=[alprazolam, bromazepam, brotizolam, clobazam, clorazepinezuur, diazepam, flunitrazepam, flurazepam, loprazolam, lorazepam, lormetazepam, midazolam, nitrazepam, oxazepam, prazepam, temazepam, zolpidem, zopiclon],
//        middelen bij opio�dverslaving=[buprenorfine (bij verslaving), buprenorfine/naloxon, methadon],
//        MAO-remmers, niet-selectief=[fenelzine, tranylcypromine]}