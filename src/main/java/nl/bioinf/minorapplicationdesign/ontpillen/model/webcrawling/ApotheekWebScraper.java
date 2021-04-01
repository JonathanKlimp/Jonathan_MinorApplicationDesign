package nl.bioinf.minorapplicationdesign.ontpillen.model.webcrawling;

import nl.bioinf.minorapplicationdesign.ontpillen.model.MedicineDAO.DrugDao;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ApotheekWebScraper extends AbstractWebScraper {

    ApotheekWebScraper(DrugDao drugDao) {
        super(drugDao);
    }

    List<String> information = new ArrayList<>();

    @Override
    public void parseHtml() {
//        String description = getDescription();
//        String sideEffects = getSideEffects();
//        String stopIndication = getStopIndication();
//        String interactions = getInteractions();
//
//        //information.add(description);
//        information.add(sideEffects);
//        information.add(stopIndication);
//        information.add(interactions);
//        return information;
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