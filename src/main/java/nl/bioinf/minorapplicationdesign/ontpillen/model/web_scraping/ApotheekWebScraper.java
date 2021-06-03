package nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping;

import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugDao;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugSubstance;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content.Content;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content.ContentLeaf;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content.ContentNode;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content.SideEffects;
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

    private ApotheekWebScraper(@Value("${apotheek.url}") String url) {this.basicUrl = url;}

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
            if (drugNotOnWebsite.contains(drug.getName())) {
                drug.setDescriptionPatient(drug.getDescriptionPractitioner());
                for (Content content : drug.getSideEffects().getSideEffectsPractitioner()) {
                    drug.getSideEffects().addSideEffectPatient("apotheek", content);
                }
                drug.setInteractionsPatient(drug.getInteractionsPatient());
                drug.setStopIndications(defaultStopIndication);
            } else if (drugWithDifferentStructure.contains(drug.getName())) {
                Document doc = getDrugWebpage(drug.getName());
                getSideEffectsFromList(doc, drug);
            } else {
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
            LOGGER.debug("SideEffects in the dao: " + drug.getSideEffects().getSideEffectsPatient());
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
        SideEffects sideEffectsDao = new SideEffects();

//        ContentNode mainContentNode = new ContentNode();
//        mainContentNode.setContentTitle("Side Effects");

        ContentLeaf newContentLeaf = new ContentLeaf();
        newContentLeaf.setContentType("PARAGRAPH");
        newContentLeaf.setContent(sideEffectsIntro);
        drug.getSideEffects().addSideEffectPatient("apotheek", newContentLeaf);
//        mainContentNode.addContent(newContentLeaf);

        for (Element element: frequency) {
            Elements sideEffects = element.nextElementSibling().getElementsByClass("sideEffectsItem_button__V-L1C");
            ContentNode newContentNode = new ContentNode();
            newContentNode.setContentTitle(element.text());

            ContentNode newContentNode1 = new ContentNode();
            newContentNode1.setContentTitle(String.valueOf(sideEffects.eachText()));

            LOGGER.debug("Chance of side effect: " + element.text() + sideEffects.eachText());
            for (Element sideEffect: sideEffects) {
                Elements sideEffectDescription = sideEffect.nextElementSibling().select(".sideEffectsItem_content__10s1c");
                ContentLeaf newContentLeaf1 = new ContentLeaf();

                newContentLeaf1.setContentType("PARAGRAPH");
                newContentLeaf1.setContent(sideEffectDescription.eachText());

                newContentNode.addContent(newContentLeaf1);
                LOGGER.debug("side effects: " + sideEffect.text() + sideEffectDescription.eachText());
            }
            drug.getSideEffects().addSideEffectPatient("apotheek", newContentNode);
//            mainContentNode.addContent(newContentNode);
        }
        System.out.println(drug.getName() + ":  " + drug.getSideEffects().getSideEffectsPatient());
//        drug.getSideEffects().addSideEffectPatient("apotheek", mainContentNode);
    }

    private void getSideEffectsFromList(Document doc, DrugSubstance drug) {
        Elements sideEffectsHtmlLocation = doc.getElementsByAttributeValueContaining("data-print", "bijwerkingen");
        Element sideEffectElements =  sideEffectsHtmlLocation.select(".listItemContent_text__otIdg ").get(0);

        ContentNode mainContentNode = new ContentNode();
        mainContentNode.setContentTitle("Side effects");
        // TODO buprenorfine (bij verslaving) probably still not saves correctly fix this
        // TODO items here may not have the correct structure while saving

//            System.out.println("??" + sideEffectElements);

            for (Element element : sideEffectElements.getAllElements()) {
                if (element.tagName().equals("p")) {
//                    System.out.println("P VALUE: " + element.text());

                    ContentLeaf newContentLeaf = new ContentLeaf();
                    newContentLeaf.setContentType("PARAGRAPH");
                    newContentLeaf.setContent(Collections.singletonList(element.text()));
                    mainContentNode.addContent(newContentLeaf);

                } else if (element.tagName().equals("ul")) {
//                    System.out.println("UL value: " + element.getElementsByTag("ul").eachText());
                    ContentLeaf newContentLeaf = new ContentLeaf();
                    newContentLeaf.setContentType("LIST");
                    newContentLeaf.setContent(element.getElementsByTag("li").eachText());
                    mainContentNode.addContent(newContentLeaf);
                }
            }
            drug.getSideEffects().addSideEffectPatient("apotheek" ,mainContentNode);
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