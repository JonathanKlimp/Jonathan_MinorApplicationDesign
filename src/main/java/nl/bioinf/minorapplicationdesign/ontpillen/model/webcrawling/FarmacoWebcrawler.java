package nl.bioinf.minorapplicationdesign.ontpillen.model.webcrawling;


import java.util.List;
import nl.bioinf.minorapplicationdesign.ontpillen.model.MedicineDAO.Drug;
import nl.bioinf.minorapplicationdesign.ontpillen.model.MedicineDAO.DrugsDao;
import nl.bioinf.minorapplicationdesign.ontpillen.model.MedicineDAO.DrugsGroup;

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
}
