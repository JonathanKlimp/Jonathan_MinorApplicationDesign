package nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping;

import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugDao;

import java.util.ArrayList;
import java.util.List;

public class GgzStandaardenWebScraper extends AbstractWebScraper {
    List<String> information = new ArrayList<>();

    GgzStandaardenWebScraper(DrugDao drugDao) {
        super(drugDao);
    }

    @Override
    public void parseHtml() {
        String sideEffects = getSideEffects();

        information.add(sideEffects);
    }

    private String getSideEffects() {
        return null;
    }
}
