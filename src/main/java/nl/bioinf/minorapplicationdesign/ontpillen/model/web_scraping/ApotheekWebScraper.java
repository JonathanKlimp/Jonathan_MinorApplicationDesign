package nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping;

import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.Drug;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugDao;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugSubstance;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ApotheekWebScraper implements AbstractWebScraper {
    private DrugDao drugDao;

    @Autowired
    public void setDrugDao(DrugDao drugDao) {
        this.drugDao = drugDao;
    }

    @Override
    public void parseHtml() throws IOException {
        List<String> drugSubstances = new ArrayList<>();
        for(Drug drugSubstance : drugDao.getDrugSubstances()){
            drugSubstances.add(drugSubstance.getName());
        }

        for (String drug: drugSubstances) {

            System.out.println("CURRENT DRUG IN THE LOOP: " +  drug);
            Document doc = getConnection(drug);
            getDescription(doc, drug);
//            getSideEffects(doc);
//            getInteractions(doc);

            // code to print the discrption in the Dao
            Drug drugSubstance = drugDao.getDrugByName(drug);
            System.out.println("DISCRIPTION OF THE DRUG IN THE DAO: " + drug);
            DrugSubstance drugSubstance1 = (DrugSubstance) drugSubstance;
            System.out.println(drugSubstance1.getDescription());

        }



    }

    private String getInteractions(Document doc) {
        Elements interactions = doc.getElementsByAttributeValueContaining("data-print", "andere medicijnen gebruiken").select(".listItemContent_text__otIdg ");
        System.out.println(interactions.eachText());
        return null;
    }

    private String getStopIndication() {
        return null;
    }

    private String getSideEffects(Document doc) {

        Elements sideEffectsHtmlLocation = doc.getElementsByAttributeValueContaining("data-print", "bijwerkingen");
        List<String> sideEffectsIntro = sideEffectsHtmlLocation.select(".listItemContent_text__otIdg p, p.listItemContent_text__otIdg").eachText();
        System.out.println(sideEffectsIntro);
        Element frequencyAndSideEffect = sideEffectsHtmlLocation.select(".sideEffects_sideEffects__sczbd").get(0);
        Elements frequency = frequencyAndSideEffect.getElementsByTag("h3");
        for (Element element: frequency) {
            Elements sideEffects = element.nextElementSibling().getElementsByClass("sideEffectsItem_button__V-L1C");
            System.out.println(element.text() + sideEffects.eachText());
            for (Element sideEffect: sideEffects) {
                Elements sideEffectDescription = sideEffect.nextElementSibling().select(".sideEffectsItem_content__10s1c");
                System.out.println(sideEffect.text() + sideEffectDescription.eachText());
            }
        }


        //TODO Add to datamodel
        return null;
    }

    private void getDescription(Document doc, String drug) {
        Element useIndicationTag = doc.getElementsByAttributeValueContaining("data-print", "waarbij gebruik").select(".listItemContent_text__otIdg").get(0);
//        System.out.println(useIndicationTag.children().eachText());
        //TODO add to the datamodel
        DrugSubstance myDrug = (DrugSubstance) drugDao.getDrugByName(drug);
        myDrug.setDescription(useIndicationTag.children().eachText());
    }

    private Document getConnection(String medicine) throws IOException {
        String basicUrl = "https://www.apotheek.nl/medicijnen/";
        String completeUrl = basicUrl + medicine.toLowerCase();
        Document doc = Jsoup.connect(completeUrl).get();
        return doc;
    }

    public static void main(String[] args) throws IOException {
        List<String> medicines = List.of("citalopram", "lorazepam", "Temazepam");
//        for (String drug: medicines) {
//            Document doc = getConnection(drug);
//            getDescription(doc);
//            getSideEffects(doc);
//            getInteractions(doc);

    }
}