package nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping;

import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugDao;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugSubstance;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content.Content;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content.ContentLeaf;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content.ContentNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Jonathan Klimp
 */

@SpringBootTest
class ApotheekWebScraperTest {

    @Autowired
    DrugDao drugDao;

    @Autowired
    ApotheekWebScraper apotheekWebScraper;

    @Autowired
    DrugFetcher drugFetcher;

    @BeforeEach
    public void storeDrugs() throws IOException {
        SSLHelper.bypassSSL();
        drugFetcher.parseHtml();
    }

    @AfterEach
    public void cleanUpDao() {
        drugDao.removeAllDrugs();
    }


    /**
     * Method checks if ApotheekWebScraper adds a description to each drug substance
     * @throws IOException
     */
    @Test
    void parseHtml_DescriptionContainsItems() throws IOException {
        apotheekWebScraper.parseHtml();

        List<DrugSubstance> drugSubstances = drugDao.getDrugSubstances();

        for (DrugSubstance drugSubstance: drugSubstances) {
            assertNotNull(drugSubstance.getDescriptionPatient());
        }
    }

    /**
     * Method checks if ApotheekWebScraper adds side effects to each drug substance
     * @throws IOException
     */
    @Test
    void parseHtml_sideEffectsContainsItems() throws IOException {
        apotheekWebScraper.parseHtml();

        List<DrugSubstance> drugSubstances = drugDao.getDrugSubstances();

        for (DrugSubstance drugSubstance: drugSubstances) {
            assertNotNull(drugSubstance.getSideEffects().getSideEffectsPatient());
        }
    }


    /**
     * Method checks if ApotheekWebScraper adds interactions to each drug substance
     * @throws IOException
     */
    @Test
    void parseHtml_interactionsContainsItems() throws IOException {
        apotheekWebScraper.parseHtml();

        List<DrugSubstance> drugSubstances = drugDao.getDrugSubstances();

        for (DrugSubstance drugSubstance: drugSubstances) {
            assertNotNull(drugSubstance.getInteractionsPatient());
        }
    }

    /**
     * Method checks if ApotheekWebScraper adds stop indications to each drug substance
     * @throws IOException
     */
    @Test
    void parseHtml_stopIndicationsContainsItems() throws IOException {
        apotheekWebScraper.parseHtml();

        List<DrugSubstance> drugSubstances = drugDao.getDrugSubstances();

        for (DrugSubstance drugSubstance: drugSubstances) {
            assertNotNull(drugSubstance.getStopIndications());
        }
    }

    /**
     * Method checks if ApotheekWebScraper adds side effects to each drug substance
     * @throws IOException
     */
    @Test
    void parseHtml_SideEffectsContainsItems() throws IOException {
        apotheekWebScraper.parseHtml();

        List<DrugSubstance> drugSubstances = drugDao.getDrugSubstances();

        for (DrugSubstance drugSubstance: drugSubstances) {
            assertNotNull(drugSubstance.getSideEffects().getSideEffectsPatient());
        }
    }


    @Test
    void parseHtml_SideEffectGetContent_ContainsString() throws IOException {
        apotheekWebScraper.parseHtml();

        List<DrugSubstance> drugSubstances = drugDao.getDrugSubstances();
        List<Content> contentList = new ArrayList<>();
        for (DrugSubstance drugSubstance: drugSubstances) {
            contentList = new ArrayList<>(drugSubstance.getSideEffects().getSideEffectsPatient());
        }
        for (Content content : contentList) {
            assertNotNull(getContentText(content));
        }
    }


    private List<String> getContentText(Content content) {
        String classType = content.getClass().toString();
        switch (classType) {
            case "class nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content.ContentNode":
                for (Content content1 : ((ContentNode) content).getContent()) {
                    return Collections.singletonList(content1.getContentTitle());
                }
            case "class nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content.ContentLeaf":
                return ((ContentLeaf) content).getContent();
        }
        return null;
    }
}