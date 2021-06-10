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


//    @Test
//    void parseHtml_SideEffectGetContent_ContainsString() throws IOException {
//        apotheekWebScraper.parseHtml();
//
//        List<DrugSubstance> drugSubstances = drugDao.getDrugSubstances();
//
//        for (DrugSubstance drugSubstance: drugSubstances) {
//            List<Content> contentList = new ArrayList<>();
//            for (Content content : drugSubstance.getSideEffects().getSideEffectsPatient()) {
//                contentList.add(content);
//                assertNotNull(getContentText(contentList));
//            }
//        }
//    }


//    private List<String> getContentText(List<Content> content) {
//        for (Content content1 : content) {
//            String classType = content1.getClass().toString();
//            switch (classType) {
//                case "class nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content.ContentNode":
//                    getContentText(con)
//                    getContentText(((Content) content1).getContent());
//                case "class nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content.ContentLeaf":
//                    return ((ContentLeaf) content1).getContent();
//            }
//        }
//        return null;
//    }
}