package nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping;

import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugDao;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author Naomi Hindriks
 */

@SpringBootTest
class GgzStandaardenWebScraperTest {
    @Autowired
    DrugDao drugDao;

    @Autowired
    DrugFetcher drugFetcher;

    @Autowired
    GgzStandaardenWebScraper ggzStandaardenWebScraper;

    @BeforeEach
    public void bypassSSL() throws IOException {
        SSLHelper.bypassSSL();
        this.drugFetcher.parseHtml();
    }

    @AfterEach
    public void cleanUpDao() {
        drugDao.removeAllDrugs();
    }

    @Test
    void parseHtml() throws Exception {
        ggzStandaardenWebScraper.parseHtml();
    }
}