package nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping;

import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Naomi Hindriks
 */
@Component
public class GgzStandaardenBijwerkingenWebScraper implements AbstractWebScraper {
    private DrugDao drugDao;
    private String url;
    private String csvFileLocation;
    private static final Logger LOGGER = LoggerFactory.getLogger(GgzStandaardenBijwerkingenWebScraper.class);

    private GgzStandaardenBijwerkingenWebScraper(@Value("${ggz.generieke.module.bijwerkingen}") String url, @Value("${csv.group.between.websites.combiner}") String csvFile) {
        this.url = url;
        this.csvFileLocation = csvFile;
    }

    @Autowired
    public void setDrugDao(DrugDao drugDao) {
        this.drugDao = drugDao;
    }

    @Override
    public void parseHtml() throws IOException {
        LOGGER.info("Running parseHtml");
        this.getCombiningInformationFromCsv();
//        throw new UnsupportedOperationException("Method not implemented");
    }

    private void getCombiningInformationFromCsv() throws IOException {
        String row;
        BufferedReader csvReader = new BufferedReader(new FileReader(this.csvFileLocation));
        while ((row = csvReader.readLine()) != null) {
            String[] data = row.split(",");
            System.out.println(data);
        }
        csvReader.close();
    }
}
