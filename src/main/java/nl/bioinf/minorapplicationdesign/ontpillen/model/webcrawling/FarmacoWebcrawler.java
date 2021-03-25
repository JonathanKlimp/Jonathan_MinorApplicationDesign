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
        informationStorage.addDrug(null);
        return null;
    }

    public static void main(String[] args) throws IOException {
        SSLHelper.bypassSSL();
        List<String> list = List.of("Temazepam", "Citalopram", "Lorazepam");
        String basicUrl = "https://www.farmacotherapeutischkompas.nl/bladeren/preparaatteksten/";
        for (String medicine: list) {
            String completeUrl = (basicUrl + medicine.charAt(0) + "/" + medicine).toLowerCase(Locale.ROOT);
            System.out.println(completeUrl);
            Document doc = Jsoup.connect(completeUrl).get();
            Elements h2Tags = doc.getElementsByTag("h2").select(":contains(Bijwerkingen)");
            System.out.println(h2Tags.nextAll().select("p").eachText());

        }




    }
}
