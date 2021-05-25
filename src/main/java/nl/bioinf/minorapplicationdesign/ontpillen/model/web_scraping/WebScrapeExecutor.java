package nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 *
 * @author Larissa Bouwknegt, Jonathan Klimp, Naomi Hindriks
 */
@Component
public class WebScrapeExecutor {
    private DrugFetcher drugFetcher;
    private FarmacoWebScraper farmacoWebScraper;
    private ApotheekWebScraper apotheekWebScraper;
    private GgzStandaardenWebScraper ggzStandaardenWebScraper;
    private RichtlijnenNhgWebScraper richtlijnenNhgWebScraper;
    private IndicationScraper indicationScraper;
    private static final Logger LOGGER = LoggerFactory.getLogger(WebScrapeExecutor.class);

    @Autowired
    public void setDrugFetcher(DrugFetcher drugFetcher) {
        this.drugFetcher = drugFetcher;
    }

    @Autowired
    public void setFarmacoWebScraper(FarmacoWebScraper farmacoWebScraper) {
        this.farmacoWebScraper =  farmacoWebScraper;
    }

    @Autowired
    public void setApotheekWebScraper(ApotheekWebScraper apotheekWebScraper) {
        this.apotheekWebScraper = apotheekWebScraper;
    }

    @Autowired
    public void setGgzStandaardenWebScraper(GgzStandaardenWebScraper ggzStandaardenWebScraper) {
        this.ggzStandaardenWebScraper = ggzStandaardenWebScraper;
    }

    @Autowired
    public void setRichtlijnenNhgWebScraper(RichtlijnenNhgWebScraper richtlijnenNhgWebScraper) {
        this.richtlijnenNhgWebScraper = richtlijnenNhgWebScraper;
    }

    @Autowired
    public void setIndicationScraper(IndicationScraper indicationScraper) {
        this.indicationScraper = indicationScraper;
    }

    public void runWebcrawlers() throws IOException {
        LOGGER.info("Running all webcrawlers");
        //Bypass certificate security for all connections after this point
        SSLHelper.bypassSSL();

        this.drugFetcher.parseHtml(); // needs to run first
        this.indicationScraper.parseHtml();
        this.farmacoWebScraper.parseHtml(); // needs to run before apotheek
        this.apotheekWebScraper.parseHtml();
    }
}
