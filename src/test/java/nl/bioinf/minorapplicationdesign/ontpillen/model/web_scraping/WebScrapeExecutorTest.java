package nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping;

import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WebScrapeExecutorTest {
    @Autowired
    DrugDao drugDao;

    @Autowired
    WebScrapeExecutor webScrapeExecutor;

    @Test
    void runWebcrawlers_test() throws IOException {
        webScrapeExecutor.runWebcrawlers();
    }
}