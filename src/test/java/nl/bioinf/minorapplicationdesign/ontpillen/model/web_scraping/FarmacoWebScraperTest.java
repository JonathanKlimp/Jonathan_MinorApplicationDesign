package nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping;

import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugDao;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugSubstance;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content.Content;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 *
 * @author Jonathan Klimp
 */

@SpringBootTest
class FarmacoWebScraperTest {

    @Autowired
    DrugDao drugDao;

    @Autowired
    FarmacoWebScraper farmacoWebScraper;

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
     * Method checks if FarmacoWebScraper adds a description to each drug substance
     * @throws IOException
     */
    @Test
    void parseHtml_descriptionContainsItems() throws IOException {
        farmacoWebScraper.parseHtml();

        List<DrugSubstance> drugSubstances = drugDao.getDrugSubstances();

        for (DrugSubstance drugSubstance: drugSubstances) {
            assertNotNull(drugSubstance.getDescriptionPractitioner());
        }
    }


    /**
     * Method checks if FarmacoWebScraper adds side effects to each drug substance
     * @throws IOException
     */
    @Test
    void parseHtml_sideEffectsContainsItems() throws IOException {
        farmacoWebScraper.parseHtml();

        List<DrugSubstance> drugSubstances = drugDao.getDrugSubstances();

        for (DrugSubstance drugSubstance: drugSubstances) {
            assertNotNull(drugSubstance.getSideEffects().getSideEffectsPractitioner());
        }

        for (DrugSubstance drugSubstance: drugSubstances) {
            for (Content content : drugSubstance.getSideEffects().getSideEffectsPractitioner()) {
                assertNotNull(content);
                System.out.println(content);
            }
        }
    }


    /**
     * Method checks if FarmacoWebScraper adds interactions to each drug substance
     * @throws IOException
     */
    @Test
    void parseHtml_interactionsContainsItems() throws IOException {
        farmacoWebScraper.parseHtml();

        List<DrugSubstance> drugSubstances = drugDao.getDrugSubstances();

        for (DrugSubstance drugSubstance: drugSubstances) {
            assertNotNull(drugSubstance.getInteractionsPractitioner());
        }
    }
}