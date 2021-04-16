package nl.bioinf.minorapplicationdesign.ontpillen;

import nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping.ImplementationWebcrawler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class OntpillenApplication {

    public static void main(String[] args) throws IOException {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(MyApplicationContextConfiguration.class);

        ImplementationWebcrawler myCrawlers = ctx.getBean(ImplementationWebcrawler.class);
        myCrawlers.runWebcrawlers();

        ctx.close();

        SpringApplication.run(OntpillenApplication.class, args);
    }

}
