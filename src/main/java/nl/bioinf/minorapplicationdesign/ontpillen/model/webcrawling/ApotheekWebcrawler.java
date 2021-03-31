package nl.bioinf.minorapplicationdesign.ontpillen.model.webcrawling;

import nl.bioinf.minorapplicationdesign.ontpillen.model.MedicineDAO.DrugsDao;
import nl.bioinf.minorapplicationdesign.ontpillen.model.MedicineDAO.UseIndication;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ApotheekWebcrawler extends AbstractWebcrawler {

    ApotheekWebcrawler(DrugsDao drugsDao) {
        super(drugsDao);
    }

    List<String> information = new ArrayList<>();

    @Override
    public List<String> getInformation() {
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
        return null;
    }

    private String getInteractions() {
        return null;
    }

    private String getStopIndication() {
        return null;
    }

    private static String getSideEffects(Document doc) {
        Elements sideEffectsHtmlLocation = doc.getElementsByAttributeValueContaining("data-print", "bijwerkingen");
        List<String> sideEffectsIntro = sideEffectsHtmlLocation.select(".listItemContent_text__otIdg p, p.listItemContent_text__otIdg").eachText();
        System.out.println(sideEffectsIntro);
        System.out.println(sideEffectsHtmlLocation.select(".sideEffects_sideEffects__sczbd").eachText());


        //TODO Titel weg en frequenties niet nodig
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
            //getDescription(doc);
            getSideEffects(doc);

        }
    }
}
