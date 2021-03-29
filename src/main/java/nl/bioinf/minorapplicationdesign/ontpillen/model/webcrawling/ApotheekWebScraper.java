package nl.bioinf.minorapplicationdesign.ontpillen.model.webcrawling;

import nl.bioinf.minorapplicationdesign.ontpillen.model.MedicineDAO.DrugDao;

import java.util.ArrayList;
import java.util.List;

public class ApotheekWebScraper extends AbstractWebScraper {

    ApotheekWebScraper(DrugDao drugDao) {
        super(drugDao);
    }

    List<String> information = new ArrayList<>();

    @Override
    public void parseHtml() {
        String description = getDescription();
        String sideEffects = getSideEffects();
        String stopIndication = getStopIndication();
        String interactions = getInteractions();

        information.add(description);
        information.add(sideEffects);
        information.add(stopIndication);
        information.add(interactions);
    }

    private String getInteractions() {
        return null;
    }

    private String getStopIndication() {
        return null;
    }

    private String getSideEffects() {
        return null;
    }

    private String getDescription() {
        return null;
    }
}
