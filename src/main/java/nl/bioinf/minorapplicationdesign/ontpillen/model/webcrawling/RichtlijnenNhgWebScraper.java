package nl.bioinf.minorapplicationdesign.ontpillen.model.webcrawling;

import nl.bioinf.minorapplicationdesign.ontpillen.model.MedicineDAO.DrugDao;

import java.util.ArrayList;
import java.util.List;

public class RichtlijnenNhgWebScraper extends AbstractWebScraper {
    List<String> information = new ArrayList<>();

    RichtlijnenNhgWebScraper(DrugDao drugDao) {
        super(drugDao);
    }

    @Override
    public void parseHtml() {
        String description = getDescription();
        information.add(description);
    }

    private String getDescription() {
        return null;
    }
}
