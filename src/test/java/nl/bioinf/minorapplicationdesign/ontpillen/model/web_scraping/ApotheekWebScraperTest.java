package nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping;

import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugDao;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugSubstance;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
            assertNotNull(drugSubstance.getDescription());
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
            assertNotNull(drugSubstance.getSideEffectsPatient());
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
            assertNotNull(drugSubstance.getInteractions());
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
            assertNotNull(drugSubstance.getSideEffectsPatient());
        }
    }
}