package nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping;

import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugDao;

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
