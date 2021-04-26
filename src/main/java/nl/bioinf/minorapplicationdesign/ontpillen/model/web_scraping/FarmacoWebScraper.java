package nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping;

import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.Drug;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugDao;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugSubstance;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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
import java.util.Locale;

/**
 *
 * @author Larissa Bouwknegt, Jonathan Klimp, Naomi Hindriks
 */
@Component
@PropertySource("classpath:application.properties")
public class FarmacoWebScraper implements AbstractWebScraper {
    private DrugDao drugDao;
    private String basicUrl;
    private static final Logger LOGGER = LoggerFactory.getLogger(FarmacoWebScraper.class);

    private FarmacoWebScraper(@Value("${farmaco.basic.site}") String url) {
        this.basicUrl = url;
    }

    @Autowired
    public void setDrugDao(DrugDao drugDao) {
        this.drugDao = drugDao;
    }


    @Override
    public void parseHtml() throws IOException {
        LOGGER.info("Running parseHtml");
        List<String> drugSubstances = new ArrayList<>();
        for(Drug drugSubstance : drugDao.getDrugSubstances()){
            LOGGER.debug("Fetching " + drugSubstance + "From the Dao");
            drugSubstances.add(drugSubstance.getName());
        }
        this.parseInformation(drugSubstances);
    }

    private Document getConnection(String medicine) throws IOException {
        if (medicine.contains("(") | medicine.contains("/")){
            //Replaces any non word characters, including whitespaces
            medicine = medicine.replaceAll("\\W", "_");
        }
        if (medicine.contains("ï")){
            medicine =medicine.replace("ï", "i");
        }
        String completeUrl = (this.basicUrl + medicine.charAt(0) + "/" + medicine).toLowerCase(Locale.ROOT);
        LOGGER.debug("Connecting to: " + completeUrl);
        return Jsoup.connect(completeUrl).get();
    }

    // FIXME needs to be linked to the data model
    private void parseInformation( List<String> druglist) throws IOException {
        SSLHelper.bypassSSL();

        for (String medicine : druglist) {
            Document doc = getConnection(medicine);
            Elements h2Tags = doc.getElementsByTag("h2");
            List<String> sideEffects = h2Tags.select(":contains(Bijwerkingen)").nextAll().select("p").eachText();
            List<String> drugDescription = h2Tags.select(":contains(Advies)").nextAll().eachText();
            List<String> interactions = h2Tags.select(":contains(interacties)").nextAll().eachText();
            Drug currentDrug = drugDao.getDrugByName(medicine);
            DrugSubstance drugsubstance = (DrugSubstance) currentDrug;
            drugsubstance.setDescription(drugDescription);
            //≥ gets parsed as a ?, this is a problem with some drugs
            drugsubstance.setSideEffects(sideEffects);
            drugsubstance.setInteractions(interactions);


            LOGGER.debug("Side effects for drug: " + medicine + "Side effects: " + sideEffects);
            LOGGER.debug("DrugDescription for drug: " + medicine + "Drug description: " + drugDescription);
            LOGGER.debug("Drug interactions for drug: " + medicine + "Drug interactions: " + interactions);
        }
    }
}