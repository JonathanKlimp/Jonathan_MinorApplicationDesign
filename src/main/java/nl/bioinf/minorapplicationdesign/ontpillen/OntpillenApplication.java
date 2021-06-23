package nl.bioinf.minorapplicationdesign.ontpillen;

import nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping.WebScrapeExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class OntpillenApplication this class executes the project and starts the webcrawlers.
 * @author Naomi Hindriks
 */
@SpringBootApplication
public class OntpillenApplication {
    private static Logger LOGGER = LoggerFactory.getLogger(OntpillenApplication.class);
    private static WebScrapeExecutor webScrapeExecutor;

    @Autowired
    public void setWebScrapeExecutor(WebScrapeExecutor webScrapeExecutor) {
        OntpillenApplication.webScrapeExecutor = webScrapeExecutor;
    }

    public static void main(String[] args) throws IOException {
        LOGGER.info("Starting the application");
        SpringApplication.run(OntpillenApplication.class, args);

        LOGGER.info("Starting the webcrawlers");
        webScrapeExecutor.runWebcrawlers();
        LOGGER.info("Done with webcrawling");
    }
}
