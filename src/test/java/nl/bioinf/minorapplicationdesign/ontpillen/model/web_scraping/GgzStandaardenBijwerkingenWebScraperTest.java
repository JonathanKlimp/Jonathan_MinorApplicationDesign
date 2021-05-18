package nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping;

import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugDao;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GgzStandaardenBijwerkingenWebScraperTest {
    @Autowired
    DrugDao drugDao;

    @Autowired
    DrugFetcher drugFetcher;

    @AfterEach
    public void cleanUpTest() {
        drugDao.removeAllDrugs();
    }

    @BeforeEach
    public void bypassSSL() {
        SSLHelper.bypassSSL();
    }

    @Test
    void parseHtml() {

    }
}