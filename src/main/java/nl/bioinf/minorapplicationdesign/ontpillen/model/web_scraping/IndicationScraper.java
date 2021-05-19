package nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping;

import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.Drug;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugDao;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugSubstance;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.UseIndication;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 *
 * @author Larissa Bouwknegt en Jonathan Klimp
 */

@Component
@PropertySource("classpath:application.properties")
public class IndicationScraper implements AbstractWebScraper{
    private DrugDao drugDao;
    private String url;
    private static final Logger LOGGER = LoggerFactory.getLogger(IndicationScraper.class);

    private IndicationScraper(@Value("${farmaco.medicines.site}") String url) {
        this.url = url;
    }


    @Autowired
    public void setDrugDao(DrugDao drugDao) {
        this.drugDao = drugDao;
    }

    @Override
    public void parseHtml() throws IOException {
        LOGGER.info("Running parseHtml");
        SSLHelper.bypassSSL();
        Document doc = Jsoup.connect(this.url).get();
        Elements indicationHtmlElements = doc.getElementsByClass("pat-rich group-1").select("h2");
        for (Element indication: indicationHtmlElements) {
            String useIndication = indication.text();
            List<String> parsedDrugs = indication.nextElementSiblings().select("li").eachText();
            checkDrugDao(parsedDrugs, indication.text());
            LOGGER.debug("The indication is "+ useIndication + " the drugs are " + parsedDrugs);
        }
    }

    private void checkDrugDao(List<String> parsedDrugs, String indication){
        for (String drug: parsedDrugs) {
            if (drugDao.drugInDrugDao(drug)) {
                Drug currentDrug = drugDao.getDrugByName(drug);
                DrugSubstance drugSubstance = (DrugSubstance) currentDrug;
                UseIndication newUseIndication = new UseIndication();
                newUseIndication.setName(indication);
                newUseIndication.setDrugs(parsedDrugs);
                drugSubstance.addUseIndication(newUseIndication);
            }
            else {
                LOGGER.info(drug + " is not present in the dao");
            }
        }
    }
}