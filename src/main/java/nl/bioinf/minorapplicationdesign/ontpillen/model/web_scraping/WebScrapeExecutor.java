package nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

//TODO Hier heb je unit tests voor

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

    public void runWebcrawlers() throws IOException {
        //Bypass certificate security for all connections after this point
        SSLHelper.bypassSSL();
        this.drugFetcher.parseHtml();

        this.farmacoWebScraper.parseHtml();
    }
}
