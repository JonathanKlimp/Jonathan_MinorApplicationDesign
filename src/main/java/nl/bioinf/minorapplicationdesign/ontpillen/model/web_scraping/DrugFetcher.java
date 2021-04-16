package nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping;


import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public class DrugFetcher extends AbstractWebScraper {
    private List<Element> drugGroups;

    DrugFetcher(DrugDao drugDao) {
        super(drugDao);
    }

    public void parseDrugs() throws IOException {
//        Bypass certificate security for all connections after this point
        SSLHelper.bypassSSL();
        String url = "https://www.farmacotherapeutischkompas.nl/bladeren/categorie/psychiatrie";
        Document doc = Jsoup.connect(url).get();
        this.drugGroups = doc.getElementsByClass("pat-rich group-2").select("h2");
        checkUl();
    }

//    To do: change name into something more appropriate
//    To do: Method too long, try to extract methods from it.
    private void checkUl(){
        Element currentDrugElement = this.drugGroups.get(0);
        DrugGroup currentDrug;

        if (!this.drugDao.getAllDrugNames().contains(currentDrugElement.text())) {
            this.drugDao.addDrug(new DrugGroup(currentDrugElement.text()));
        }

        currentDrug = (DrugGroup) drugDao.getDrugByName(currentDrugElement.text());

        if (currentDrugElement.nextElementSibling().is("ul")) {
            List<String> childrenNames = currentDrugElement.nextElementSibling().select("li").eachText();

            for (String childName:childrenNames) {
                DrugSubstance newDrugSubstance = new DrugSubstance(childName);
                newDrugSubstance.setParent(currentDrug);
                currentDrug.addChild(newDrugSubstance);
                drugDao.addDrug(newDrugSubstance);
            }

        } else {
            String query = currentDrugElement.nextElementSibling().tagName();
            Elements nextGroupSiblings = currentDrugElement.nextElementSiblings().select(query);

            List<String> childrenNames = nextGroupSiblings.eachText();

            for (String childName:childrenNames) {
                DrugGroup newDrugGroup = new DrugGroup(childName);
                newDrugGroup.setParent(currentDrug);
                currentDrug.addChild(newDrugGroup);
                drugDao.addDrug(newDrugGroup);
            }

            this.drugGroups.addAll(nextGroupSiblings);
        }

        this.drugGroups.remove(0);

//        As long as there are elements in this.drugGroups run this method again
        if (this.drugGroups.size() != 0) {
            checkUl();
        }
    }

    @Override
    public void parseHtml() throws IOException {
        parseDrugs();
    }

//    public static void main(String[] args) throws IOException {
//        DrugFetcher myDrugFetcher = new DrugFetcher(InMemoryDrugDao.getInstance());
//        myDrugFetcher.parseDrugs();
//
////        Print drugs for demo sprint 2
//        for (Drug drug : InMemoryDrugDao.getInstance().getMainDrugGroups()) {
//            printDrugsRecursive(drug, 0);
//        }
//
//    }


    /**
     * Method for demo end of sprint 2
     * @param drug The drug to print and print the children from
     * @param depth The current depth (number of parents above this drug)
     */
//    public static void printDrugsRecursive(Drug drug, int depth) {
//        System.out.println("\t".repeat(depth) + drug.getName());
//        if (drug instanceof DrugGroup) {
//            for (Drug child : ((DrugGroup) drug).getChildren())
//            printDrugsRecursive(child, depth + 1);
//        }
//    }
}