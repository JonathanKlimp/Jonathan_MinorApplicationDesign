package nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping;

import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.Drug;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugDao;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Component
@PropertySource("classpath:application.properties")
public class FarmacoWebScraper implements AbstractWebScraper {
    private DrugDao drugDao;
    private String basicUrl;

    private FarmacoWebScraper(@Value("${farmaco.basic.site}") String url) {
        this.basicUrl = url;
    }

    @Autowired
    public void setDrugDao(DrugDao drugDao) {
        this.drugDao = drugDao;
    }


    @Override
    public void parseHtml() throws IOException {
        List<String> drugSubstances = new ArrayList<>();
        for(Drug drugSubstance : drugDao.getDrugSubstances()){
            drugSubstances.add(drugSubstance.getName());
        }
        this.parseInformation(drugSubstances);
    }

    private Document getConnection(String medicine) throws IOException {
        String completeUrl = (this.basicUrl + medicine.charAt(0) + "/" + medicine).toLowerCase(Locale.ROOT);
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

            System.out.println(sideEffects);
            System.out.println(drugDescription);
            System.out.println(interactions);
        }

    }
}

