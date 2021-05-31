package nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping;

import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugDao;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RichtlijnenNhgWebScraperTest {

    @Autowired
    DrugDao drugDao;

    @Autowired
    DrugFetcher drugFetcher;

    @Autowired
    IndicationScraper indicationScraper;

    @Autowired
    RichtlijnenNhgWebScraper richtlijnenNhgWebScraper;

    @BeforeEach
    public void bypassSSL() throws IOException {
        SSLHelper.bypassSSL();
        this.drugFetcher.parseHtml();
        this.indicationScraper.parseHtml();
    }

    @AfterEach
    public void cleanUpDao() {
        drugDao.removeAllDrugs();
    }


    @Test
    void parseHtml() throws IOException {
        this.richtlijnenNhgWebScraper.parseHtml();
    }
}