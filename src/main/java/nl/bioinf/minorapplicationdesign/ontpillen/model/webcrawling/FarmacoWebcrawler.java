package nl.bioinf.minorapplicationdesign.ontpillen.model.webcrawling;


import java.io.IOException;
import java.util.List;
import java.util.Locale;

import nl.bioinf.minorapplicationdesign.ontpillen.model.MedicineDAO.Drug;
import nl.bioinf.minorapplicationdesign.ontpillen.model.MedicineDAO.DrugsDao;
import nl.bioinf.minorapplicationdesign.ontpillen.model.MedicineDAO.DrugsGroup;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class FarmacoWebcrawler extends AbstractWebcrawler {

    FarmacoWebcrawler(DrugsDao drugsDao) {
        super(drugsDao);
    }


    @Override
    public List<String> getInformation() {
        String drugs = getDrugs(informationStorage);
        information.add(drugs);
        return information;
    }


    private String getDrugs(DrugsDao informationStorage) {
        Drug drug = new DrugsGroup();
        informationStorage.addDrugSubstance(null);
        return null;
    }

    private Document getConnection(String medicine) throws IOException {
        String basicUrl = "https://www.farmacotherapeutischkompas.nl/bladeren/preparaatteksten/";
        String completeUrl = (basicUrl + medicine.charAt(0) + "/" + medicine).toLowerCase(Locale.ROOT);
        Document doc = Jsoup.connect(completeUrl).get();
        return doc;
    }
    // FIXME needs to be linked to the data model
    private void parseInformation(DrugsDao informationStorage, List<String> druglist) throws IOException {
        SSLHelper.bypassSSL();

        for (String medicine: druglist) {
            Document doc = getConnection(medicine);
            Elements h2Tags = doc.getElementsByTag("h2");
            List<String> sideEffects = h2Tags.select(":contains(Bijwerkingen)").nextAll().select("p").eachText();
            List<String> drugDescription = h2Tags.select(":contains(Advies)").nextAll().eachText();

        }




    }
}
