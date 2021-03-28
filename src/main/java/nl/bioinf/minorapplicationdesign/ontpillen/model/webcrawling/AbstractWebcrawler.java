package nl.bioinf.minorapplicationdesign.ontpillen.model.webcrawling;

import nl.bioinf.minorapplicationdesign.ontpillen.model.MedicineDAO.DrugsDao;

import java.io.IOException;
import java.util.List;

public abstract class AbstractWebcrawler{
    List<String> information = null;
    public DrugsDao informationStorage;

    AbstractWebcrawler(DrugsDao drugsDao){
        informationStorage = drugsDao;
    }

    public abstract List<String> getInformation() throws IOException;

}
