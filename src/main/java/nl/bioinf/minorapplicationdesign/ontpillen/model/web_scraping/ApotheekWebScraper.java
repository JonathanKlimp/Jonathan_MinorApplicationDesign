package nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping;

import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugDao;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 *
 * @author Larissa Bouwknegt, Jonathan Klimp, Naomi Hindriks
 */
@Component
public class ApotheekWebScraper implements AbstractWebScraper {
    private DrugDao drugDao;

    @Autowired
    public void setDrugDao(DrugDao drugDao) {
        this.drugDao = drugDao;
    }

    @Override
    public void parseHtml() {
        throw new UnsupportedOperationException("Method not implemented yet");
    }

    private static String getInteractions(Document doc) {
        Elements interactions = doc.getElementsByAttributeValueContaining("data-print", "andere medicijnen gebruiken").select(".listItemContent_text__otIdg ");
        System.out.println(interactions.eachText());
        return null;
    }

    private String getStopIndication() {
        return null;
    }

    private static String getSideEffects(Document doc) {

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

    private static String getDescription(Document doc) {
        Element useIndicationTag = doc.getElementsByAttributeValueContaining("data-print", "waarbij gebruik").select(".listItemContent_text__otIdg").get(0);
        System.out.println(useIndicationTag.children().eachText());
        //TODO add to the datamodel
        return null;
    }

    private static Document getConnection(String medicine) throws IOException {
        String basicUrl = "https://www.apotheek.nl/medicijnen/";
        String completeUrl = basicUrl + medicine.toLowerCase();
        Document doc = Jsoup.connect(completeUrl).get();
        return doc;
    }

    public static void main(String[] args) throws IOException {
        List<String> medicines = List.of("citalopram", "lorazepam", "Temazepam");
        for (String drug: medicines) {
            Document doc = getConnection(drug);
//            getDescription(doc);
//            getSideEffects(doc);
            getInteractions(doc);


        }
    }
}