package nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping;

import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.Drug;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugDao;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugSubstance;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content.UseIndication;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Class IndicationScraper scrapes all indications of drugs used in psychiatry and the drugs that belong to the said
 * indication. The fetched result is a string with the name of the indication and a list with string of the
 * corresponding drugs. This list is used to fetch the Drug objects from the Dao to create a new list of drug objects.
 * The name and list of drug objects are added to the UseIndication class for each drug on the webpage.
 * @author Larissa Bouwknegt and Jonathan Klimp
 */

@Component
@PropertySource("classpath:application.properties")
public class IndicationScraper implements AbstractWebScraper{
    private DrugDao drugDao;
    private String url;
    private static final Logger LOGGER = LoggerFactory.getLogger(IndicationScraper.class);

    private IndicationScraper(@Value("${farmaco.medicines.url}") String url) {
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
            addUseIndication(parsedDrugs, indication.text());
            LOGGER.debug("The indication is "+ useIndication + " the drugs are " + parsedDrugs);
        }
    }

    private void addUseIndication(List<String> parsedDrugs, String indication){
        List<Drug> drugList = fetchDrugsFromDao(parsedDrugs);
        UseIndication newUseIndication = new UseIndication();
        newUseIndication.setName(indication);
        newUseIndication.setDrugs(drugList);
        this.drugDao.addUseIndication(newUseIndication);
        for (String drug: parsedDrugs) {
            Drug currentDrug = drugDao.getDrugByName(drug);
            DrugSubstance drugSubstance = (DrugSubstance) currentDrug;
            drugSubstance.addUseIndication(newUseIndication);
        }
    }

    private List<Drug> fetchDrugsFromDao(List<String> drugs) {
        List<Drug> drugList = new ArrayList<>();
        for (String drug : drugs) {
            if(drugDao.drugInDrugDao(drug)) {
                drugList.add(drugDao.getDrugByName(drug));
            }
        }
        return drugList;
    }
}