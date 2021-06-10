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
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Class ApotheekWebscraper implements AbstractWebscraper. It
 * fetches all drug names from the drug dao and uses these to connect
 * to the correct page on apotheek.nl. Some drugs are not present on the website
 * these drugs are assigned information fetched by FarmacoWebScraper.
 * The fetching of the side effects has two methods because not every page has
 * the same structure. The drugs with a different structure are stated in the list
 * drugWithDifferentStructure.
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

    // TODO add javadoc
    @Override
    public void parseHtml() throws IOException {
        LOGGER.info("Parsing html");
        List<DrugSubstance> drugSubstances = drugDao.getDrugSubstances();
        List<String> drugNotOnWebsite = Arrays.asList("thiamine", "coffeïne", "esketamine (nasaal)","esketamine" , "guanfacine");

        for (DrugSubstance drug: drugSubstances) {
            if (drugNotOnWebsite.contains(drug.getName())) {
                addInformationFromFarmaco(drug);
            } else {
                getInformation(drug);
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
        LOGGER.debug("Stop indications: " + stopIndicationLocation.eachText());
    }


    private void getDescription(Document doc, DrugSubstance drug) {
        Element useIndicationTag = doc.getElementsByAttributeValueContaining("data-print", "waarbij gebruik").select(".listItemContent_text__otIdg").get(0);
        drug.setDescriptionPatient(useIndicationTag.children().eachText());
        LOGGER.debug("Description: " + useIndicationTag.children().eachText());
    }


    private void getSideEffects(Document doc, DrugSubstance drug) {
        Elements sideEffectsHtmlLocation = doc.getElementsByAttributeValueContaining("data-print", "bijwerkingen");
        List<String> sideEffectsIntro = sideEffectsHtmlLocation.select(".listItemContent_text__otIdg p, p.listItemContent_text__otIdg").eachText();
        LOGGER.debug("side effects intro: " + sideEffectsIntro);
        Element frequencyAndSideEffect = sideEffectsHtmlLocation.select(".sideEffects_sideEffects__sczbd").get(0);
        Elements frequency = frequencyAndSideEffect.getElementsByTag("h3");
        ContentNode mainContentNode = new ContentNode();

        List<ContentNode> contentList = new ArrayList<>();
        for (int i = 0; i < frequency.size(); i++) {
            contentList.add(new ContentNode());
        }
        int i = 0;
        for (Element element: frequency) {
            Elements sideEffects = element.nextElementSibling().getElementsByClass("sideEffectsItem_button__V-L1C");
            contentList.get(i).setContentTitle(element.text());
            contentList.get(i).setContentType("PARAGRAPH");
            LOGGER.debug("Chance of side effect: " + element.text() +  sideEffects.eachText());

            addContentValues(sideEffects, contentList.get(i));
            mainContentNode.addContent(contentList.get(i));
            i++;
        }
        drug.getSideEffects().addSideEffectPatient("apotheek", mainContentNode);
    }


    private void addContentValues(Elements sideEffects, ContentNode contentNode) {
        List<ContentNode> contentList = new ArrayList<>();
        for (int i = 0; i < sideEffects.eachText().size(); i++) {
            contentList.add(new ContentNode());
        }
        int i = 0;
        for (Element sideEffect: sideEffects) {

            Elements sideEffectDescription = sideEffect.nextElementSibling().select(".sideEffectsItem_content__10s1c");

            ContentLeaf newContentLeaf = new ContentLeaf();
            newContentLeaf.setContentType("DESCRIPTION_LIST");
            newContentLeaf.setContentTitle(sideEffect.text());
            newContentLeaf.setContent(sideEffectDescription.eachText());

            contentList.get(i).addContent(newContentLeaf);
            contentNode.addContent(contentList.get(i));
            LOGGER.debug("side effects: " + sideEffect.text() + sideEffectDescription.eachText());
            i++;
        }
    }


    private void getSideEffectsFromList(Document doc, DrugSubstance drug) {
        Elements sideEffectsHtmlLocation = doc.getElementsByAttributeValueContaining("data-print", "bijwerkingen");
        Element sideEffectElements =  sideEffectsHtmlLocation.select(".listItemContent_text__otIdg ").get(0);
        ContentNode mainContentNode = new ContentNode();

        for (Element element : sideEffectElements.getAllElements()) {
            if (element.tagName().equals("p")) {
                ContentLeaf newContentLeaf = new ContentLeaf();
                newContentLeaf.setContentType("PARAGRAPH");
                newContentLeaf.setContent(Collections.singletonList(element.text()));
                mainContentNode.addContent(newContentLeaf);
                LOGGER.debug("P element from getSideEffectsFromList: " + element.text());

            } else if (element.tagName().equals("ul")) {
                ContentLeaf newContentLeaf = new ContentLeaf();
                newContentLeaf.setContentType("LIST");
                newContentLeaf.setContent(element.getElementsByTag("li").eachText());
                mainContentNode.addContent(newContentLeaf);
                LOGGER.debug("UL element from getSideEffectsFromList: " + element.getElementsByTag("li").eachText());
            }
        }
        drug.getSideEffects().addSideEffectPatient("apotheek", mainContentNode);
    }


    private void addInformationFromFarmaco(DrugSubstance drug) {
        List<String> defaultStopIndication = Collections.singletonList("Op de website apotheek.nl is geen informatie gevonden voor dit medicijn");
        drug.setDescriptionPatient(drug.getDescriptionPractitioner());
        for (Content content : drug.getSideEffects().getSideEffectsPractitioner()) {
            drug.getSideEffects().addSideEffectPatient("apotheek", content);
        }
        drug.setInteractionsPatient(drug.getInteractionsPatient());
        drug.setStopIndications(defaultStopIndication);
    }


    private void getInformation(DrugSubstance drug) throws IOException {
        List<String> drugWithDifferentStructure = Arrays.asList("mianserine", "imipramine", "acamprosaat", "buprenorfine (bij verslaving)", "methadon",
                "prazepam", "paliperidon", "penfluridol", "periciazine", "pimozide", "pipamperon", "valeriaan");
        Document doc = getDrugWebpage(drug.getName());
        getDescription(doc, drug);
        getInteractions(doc, drug);
        getStopIndication(doc, drug);
        if (drugWithDifferentStructure.contains(drug.getName())) {
            getSideEffectsFromList(doc, drug);

        } else {
            getSideEffects(doc, drug);
        }
    }


    private Document getDrugWebpage(String drugName) throws IOException {
        if (drugName.contains("(")){
            drugName = drugName.replaceAll("\\((.*?)\\)", "");
        }
        if (drugName.contains("/")){
            drugName = drugName.replace("/", "-met-");
        }
        if (drugName.contains("ï")) {
            drugName = drugName.replace("ï", "i");
        }
        String completeUrl = basicUrl + drugName.toLowerCase();

        return Jsoup.connect(completeUrl).get();
    }
}