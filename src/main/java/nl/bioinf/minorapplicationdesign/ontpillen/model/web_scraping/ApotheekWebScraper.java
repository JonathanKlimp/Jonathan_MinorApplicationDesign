package nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping;

import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugDao;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugSubstance;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content.Content;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content.ContentLeaf;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content.ContentNode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Larissa Bouwknegt, Jonathan Klimp, Naomi Hindriks
 */
@Component
public class ApotheekWebScraper implements AbstractWebScraper {
    private DrugDao drugDao;
    private String basicUrl;
    private static final Logger LOGGER = LoggerFactory.getLogger(ApotheekWebScraper.class);

    private ApotheekWebScraper(@Value("${apotheek.site}") String url) {this.basicUrl = url;}

    @Autowired
    public void setDrugDao(DrugDao drugDao) {
        this.drugDao = drugDao;
    }

    @Override
    public void parseHtml() throws IOException {
        LOGGER.info("Parsing html");
        List<DrugSubstance> drugSubstances = drugDao.getDrugSubstances();
        List<String> drugNotOnWebsite = Arrays.asList("thiamine", "coffeïne", "esketamine (nasaal)","esketamine" , "valproïnezuur", "guanfacine");
        List<String> drugWithDifferentStructure = Arrays.asList("mianserine", "imipramine", "acamprosaat", "buprenorfine (bij verslaving)", "methadon",
                "prazepam", "paliperidon", "penfluridol", "periciazine", "pimozide", "pipamperon", "valeriaan");
        List<String> defaultStopIndication = Collections.singletonList("Op de website is geen informatie gevonden");

        for (DrugSubstance drug: drugSubstances) {
            System.out.println("DRUG: " + drug.getName());

            if (drugNotOnWebsite.contains(drug.getName())) {
                drug.setDescriptionPatient(drug.getDescriptionPsychiatrist());
                drug.setSideEffectsPatient(drug.getSideEffectsPsychiatrist());
                drug.setInteractionsPatient(drug.getInteractionsPatient());
                drug.setStopIndications(defaultStopIndication);
            } else if (drugWithDifferentStructure.contains(drug.getName())) {
                Document doc = getDrugWebpage(drug.getName());
                System.out.println("HEEFT RARE STRUCTUUR");
                getSideEffectsFromList(doc, drug);
            } else {
                System.out.println("GEWOON NORMAAl");
                Document doc = getDrugWebpage(drug.getName());
                getDescription(doc, drug);
                getSideEffects(doc, drug);
                getInteractions(doc, drug);
                getStopIndication(doc, drug);
            }
            // code to log the description of the Dao
            LOGGER.debug("Drug: " + drug.getName());
            LOGGER.debug("Description in the dao: " + drug.getDescriptionPatient());
            LOGGER.debug("Interactions in the dao: " + drug.getInteractionsPatient());
            LOGGER.debug("SideEffects in the dao: " + drug.getSideEffectsPatient());
            LOGGER.debug("Stop indication in the dao " + drug.getStopIndications());
        }
    }

    private void getInteractions(Document doc, DrugSubstance drug) {
        Elements interactions = doc.getElementsByAttributeValueContaining("data-print", "andere medicijnen gebruiken").select(".listItemContent_text__otIdg ");
        drug.setInteractionsPatient(interactions.eachText());
        LOGGER.debug("Interactions: " + interactions.eachText());
    }

    private void getStopIndication(Document doc, DrugSubstance drug) {
        Elements stopIndicationLocation = doc.getElementsByAttributeValueContaining("data-print", "Mag ik zomaar met dit medicijn stoppen?");
        drug.setStopIndications(stopIndicationLocation.eachText());
    }

    private void getSideEffects(Document doc, DrugSubstance drug) {
        Elements sideEffectsHtmlLocation = doc.getElementsByAttributeValueContaining("data-print", "bijwerkingen");
        List<String> sideEffectsIntro = sideEffectsHtmlLocation.select(".listItemContent_text__otIdg p, p.listItemContent_text__otIdg").eachText();
        LOGGER.debug("side effects intro: " + sideEffectsIntro);
        Element frequencyAndSideEffect = sideEffectsHtmlLocation.select(".sideEffects_sideEffects__sczbd").get(0);
        Elements frequency = frequencyAndSideEffect.getElementsByTag("h3");
        for (Element element: frequency) {
            Elements sideEffects = element.nextElementSibling().getElementsByClass("sideEffectsItem_button__V-L1C");
            LOGGER.debug("Chance of side effect: " + element.text() + sideEffects.eachText());
            for (Element sideEffect: sideEffects) {
                Elements sideEffectDescription = sideEffect.nextElementSibling().select(".sideEffectsItem_content__10s1c");
                LOGGER.debug("side effects: " + sideEffect.text() + sideEffectDescription.eachText());
            }
        }
        //TODO Add to datamodel
    }

    private void getSideEffectsFromList(Document doc, DrugSubstance drug) {
        Elements sideEffectsHtmlLocation = doc.getElementsByAttributeValueContaining("data-print", "bijwerkingen");
        Element sideEffectElements =  sideEffectsHtmlLocation.select(".listItemContent_text__otIdg ").get(0);


        List<String> keyWords = Arrays.asList("Zelden", "Soms", "Zeer zelden", "Regelmatig");
        // TODO buprenorfine (bij verslaving) probably still not saves correctly fix this
        if (sideEffectElements.select(":contains(Soms)").size() > 0) {

            for (Element element : sideEffectElements.select(":contains(Soms)")) {

                if (element.tagName().equals("ul")) {

//                    contentLeaf.setContentType("LIST");
//                    contentLeaf.setContent(element.getElementsByTag("li").eachText());
                } else if (element.tagName().equals("p")) {
//                    contentNode.setContentTitle(element.text());
                }

//                drug.getSideEffects().addSideEffectPatient("apotheek", maincontentNode);
            }
        } else {
            ContentLeaf contentLeaf = new ContentLeaf();
            contentLeaf.setContentType("PARAGRAPH");
            contentLeaf.setContent(sideEffectElements.getElementsByTag( "p").eachText());
            drug.getSideEffects().addSideEffectPatient("apotheek", contentLeaf);
        }
        List<Content> contentNode = drug.getSideEffects().getSideEffectsPatient();
        for (Content content : contentNode) {
            System.out.println(content.getContentTitle());
        }

    }

    private void getDescription(Document doc, DrugSubstance drug) {
        Element useIndicationTag = doc.getElementsByAttributeValueContaining("data-print", "waarbij gebruik").select(".listItemContent_text__otIdg").get(0);
        drug.setDescriptionPatient(useIndicationTag.children().eachText());
    }

    private Document getDrugWebpage(String drugName) throws IOException {
        if (drugName.contains("(")){
            drugName = drugName.replaceAll("\\((.*?)\\)", "");
        }
        if (drugName.contains("/")){
            drugName = drugName.replace("/", "-met-");
        }
        String completeUrl = basicUrl + drugName.toLowerCase();
        return Jsoup.connect(completeUrl).get();
    }
}