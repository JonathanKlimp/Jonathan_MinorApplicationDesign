package nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping;


import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/**
 *
 * @ author Larissa Bouwknegt, Jonathan Klimp, Naomi Hindriks
 */
@Component
@PropertySource("classpath:application.properties")
public class DrugFetcher implements AbstractWebScraper {
    private DrugDao drugDao;
    private String url;
    private static final Logger LOGGER = LoggerFactory.getLogger(DrugFetcher.class);

    private DrugFetcher(@Value("${farmaco.medicines.site}") String url) {
        this.url = url;
    }

    @Autowired
    public void setDrugDao(DrugDao drugDao) {
        this.drugDao = drugDao;
    }

    @Override
    public void parseHtml() throws IOException {
        LOGGER.info("Running parseHtml");
        Document doc = Jsoup.connect(this.url).get();
        List<Element> drugGroups = this.getDrugGroups(doc);
        storeDrugsInDao(drugGroups);

        List<String> remainingDrugs = this.getRemainingDrugs(doc);
        storeOtherDrugsInDao(remainingDrugs);
    }


    private List<Element> getDrugGroups(Document doc) {
        LOGGER.debug("Fetching drugs from: " + doc.getElementsByClass("pat-rich group-2"));
        return doc.getElementsByClass("pat-rich group-2").select("h2");
    }


    /**
     * Method that will fetch the remaining drugs that are not present in "pat-rich group-2"
     * it fetches the "pat-rich group-3"
     * @return List elements of the html containing the drug names
     */
    private List<String> getRemainingDrugs(Document doc) {
        LOGGER.debug("Fetching drugs from: " + doc.getElementsByClass("pat-rich group-3"));
        List<Element> allDrugs = doc.getElementsByClass("pat-rich group-3").select("li");
        List<String> drugToBeAdded = new ArrayList<>();

        for(Element drugElement : allDrugs) {
            if(!drugDao.drugInDrugDao(drugElement.text())) {
                drugToBeAdded.add(drugElement.text());
            }
        }
        return drugToBeAdded;
    }


    /**
     * Method that will store the drugs in the Dao. The input is an List containing Element
     * objects. Since there can be multiple drug Groups in a single drug Group the method will
     * run recursive until there are no drugGroups left and all drugs are added.
     * @param drugGroups is a list with Element objects containing the drugGroups
     */
    private void storeDrugsInDao(List<Element> drugGroups){
        Element currentDrugElement = drugGroups.get(0);
        DrugGroup currentDrug;

        if (!drugDao.getAllDrugNames().contains(currentDrugElement.text())) {
            drugDao.addDrug(new DrugGroup(currentDrugElement.text()));
        }
        currentDrug = (DrugGroup) drugDao.getDrugByName(currentDrugElement.text());

        addDrugs(drugGroups, currentDrugElement, currentDrug);
        drugGroups.remove(0);

        // As long as there are elements in this.drugGroups run this method again
        if (drugGroups.size() != 0) {
            storeDrugsInDao(drugGroups);
        }
    }


    /**
     * Method that will check which drug from "pat-rich group-3" are not present in the dao
     * and will add them to the drugGroup psychofarmaca, overige
     * @param remainingDrugs List with strings of the remaining drugs to be added.
     */
    private void storeOtherDrugsInDao(List<String> remainingDrugs) {
        DrugGroup drugGroupOther = (DrugGroup) drugDao.getDrugByName("psychofarmaca, overige");
        addDrugSubstances(drugGroupOther, remainingDrugs);
    }

    /**
     * Method that checks if the given drug element is a drugGroup of drugSubstance and will add it to the
     * dao.
     * @param drugGroups List with html elements containing the remaining drug groups
     * @param currentDrugElement html element with the current drug
     * @param currentDrugGroup current drugGroup
     */
    private void addDrugs(List<Element> drugGroups, Element currentDrugElement, DrugGroup currentDrugGroup) {
        if (currentDrugElement.nextElementSibling().is("ul")) {
            List<String> childrenNames = currentDrugElement.nextElementSibling().select("li").eachText();
            LOGGER.debug("Adding: " + currentDrugGroup.getName() + " to drugSubstances");
            addDrugSubstances(currentDrugGroup, childrenNames);
        } else {
            String query = currentDrugElement.nextElementSibling().tagName();
            Elements nextGroupSiblings = currentDrugElement.nextElementSiblings().select(query);

            List<String> childrenNames = nextGroupSiblings.eachText();

            LOGGER.debug("Adding: " + currentDrugGroup.getName() + " to DrugGroups");
            addDrugGroup(currentDrugGroup, childrenNames);

            drugGroups.addAll(nextGroupSiblings);
        }
    }

    private void addDrugGroup(DrugGroup currentDrug, List<String> childrenNames) {
        for (String childName: childrenNames) {
            DrugGroup newDrugGroup = new DrugGroup(childName);
            newDrugGroup.setIsSubstance(false);
            newDrugGroup.setParent(currentDrug);
            currentDrug.addChild(newDrugGroup);
            drugDao.addDrug(newDrugGroup);
        }
    }

    private void addDrugSubstances(DrugGroup parentDrug, List<String> drugSubstances) {
        for (String childName: drugSubstances) {
            DrugSubstance newDrugSubstance = new DrugSubstance(childName);
            newDrugSubstance.setParent(parentDrug);
            newDrugSubstance.setIsSubstance(true);
            parentDrug.addChild(newDrugSubstance);
            drugDao.addDrug(newDrugSubstance);
        }
    }
}