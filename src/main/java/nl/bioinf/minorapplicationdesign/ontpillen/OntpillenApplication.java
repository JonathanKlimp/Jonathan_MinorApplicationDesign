package nl.bioinf.minorapplicationdesign.ontpillen;

import nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping.WebScrapeExecutor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

/**
 *
 * @author Naomi Hindriks
 */
@SpringBootApplication
public class OntpillenApplication {

    public static void main(String[] args) throws IOException {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(MyApplicationContextConfiguration.class);

        WebScrapeExecutor myWebScraper = ctx.getBean(WebScrapeExecutor.class);
        ctx.close();

        SpringApplication.run(OntpillenApplication.class, args);

        myWebScraper.runWebcrawlers();
        ctx.close();
    }

}
