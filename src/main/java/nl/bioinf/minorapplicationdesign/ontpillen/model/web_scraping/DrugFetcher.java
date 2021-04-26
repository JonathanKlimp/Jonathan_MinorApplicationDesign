package nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping;


import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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

    private DrugFetcher(@Value("${farmaco.medicines.site}") String url) {
        this.url = url;
    }

    @Autowired
    public void setDrugDao(DrugDao drugDao) {
        this.drugDao = drugDao;
    }

    @Override
    public void parseHtml() throws IOException {
        List<Element> drugGroups = this.getDrugGroups();
        storeDrugsInDao(drugGroups);
    }

    private List<Element> getDrugGroups() throws IOException {
        Document doc = Jsoup.connect(this.url).get();
        return doc.getElementsByClass("pat-rich group-2").select("h2");
    }

    //TODO: Method too long, try to extract methods from it.
    private void storeDrugsInDao(List<Element> drugGroups){
        Element currentDrugElement = drugGroups.get(0);
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

            drugGroups.addAll(nextGroupSiblings);
        }

        drugGroups.remove(0);

//        As long as there are elements in this.drugGroups run this method again
        if (drugGroups.size() != 0) {
            storeDrugsInDao(drugGroups);
        }
    }
}