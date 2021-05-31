package nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping;

import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugDao;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content.ContentNode;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content.UseIndication;
import org.apache.hc.core5.http.HttpException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 *
 * @author Naomi Hindriks
 */
@Component
public class RichtlijnenNhgWebScraper implements AbstractWebScraper {
    private DrugDao drugDao;
    private String url;
    private String csvFileLocation;
    private Map<String, List<String>> drugIndicationsNames = new HashMap<>() {{
        put("ontpillen", new ArrayList<>());
        put("nhgrichtlijnen", new ArrayList<>());
    }};

    @Autowired
    public void setDrugDao(DrugDao drugDao) {
        this.drugDao = drugDao;
    }

    private RichtlijnenNhgWebScraper(
            @Value("${nhg.richtlijnen.indicaties.base.url}") String url,
            @Value("${csv.indication.between.websites.combiner}") String csvFile) {
        this.url = url;
        this.csvFileLocation = csvFile;
    }

    @Override
    public void parseHtml() throws IOException {
        this.drugIndicationsNamesWithCsv();

        for (int i = 0; i < this.drugIndicationsNames.get("ontpillen").size(); i++) {
            String ontpillenIndicationName = this.drugIndicationsNames.get("ontpillen").get(i).strip();
            String nhgIndicationName = this.drugIndicationsNames.get("nhgrichtlijnen").get(i).strip();

//            If there is no indication name in ontpillen or in nhg richtlijnen for this index, continue to next index
            if(ontpillenIndicationName.equals("") || nhgIndicationName.equals("")) {
                continue;
            }

//            Get Jsoup document of the webpage for this indication
            Document doc = this.getIndicationWebDocument(nhgIndicationName);

//            Collect the contest of interest from the document and save in this map.
            Map<String, Elements> contentOfInterest = this.getContentOfInterest(doc);

//            Get the use indication from the drugDao that belongs to this indication
            UseIndication currentUseIndication = this.drugDao.getUseIndication(ontpillenIndicationName);

//
            for (int j = 0; j < contentOfInterest.size(); j++) {
                ContentNode newContentNode = new ContentNode();
                String title = new ArrayList<String>(contentOfInterest.keySet()).get(j);
                newContentNode.setContentTitle(title);
                currentUseIndication.addContentPractitioner(title, newContentNode);
            }

//            break;
        }
    }

    private Map<String, Elements> getContentOfInterest(Document doc) {
        Map<String, Elements> contentOfInterest = new HashMap<>();

        Elements elements = doc.select(".field--name-main-text .collapsible-section-wrapper:has(#volledige-tekst-richtlijnen-beleid)");

        if (elements.size() == 0) {
            for (Element element : doc.select(".field--name-main-text .collapsible-section-wrapper")) {
                if (element.text().toLowerCase().contains("richtlijnen beleid")) {
                    Elements h3Elements = geth3Elements(element,  "medicamenteuze behandeling", "niet-medicamenteuze");
                    contentOfInterest.put(element.text(), getContentOfInterestFromH3Elements(h3Elements));
                }
            }
        } else {
            for (Element element : elements) {
                Elements h3Elements = geth3Elements(element,  "medicamenteuze behandeling", "niet-medicamenteuze");
                contentOfInterest.put(element.text(), getContentOfInterestFromH3Elements(h3Elements));
            }
        }
        return contentOfInterest;
    }

    private Elements geth3Elements(Element element, String h3Query, String h3AntiQuery) {
        Elements h3Elements = new Elements();
        Elements paragraphs = element.select(".paragraph");

        for (Element paragraph : paragraphs) {
            for (Element h3Element : paragraph.select("h3")) {
                String h3Text = h3Element.text().toLowerCase();
                if (h3Text.contains(h3Query) && !h3Text.contains(h3AntiQuery)) {
                    System.out.println(h3Element);
                    h3Elements.add(h3Element);
                }
            }
        }
        return h3Elements;
    }

    private Elements getContentOfInterestFromH3Elements(Elements h3Elements) {
        Elements contentOfInterest = new Elements();
        for (Element h3Element : h3Elements) {
            Element currentElement = h3Element.parent();;
            contentOfInterest.add(currentElement);

            while (currentElement.nextElementSibling() != null) {
//                Go to next element
                currentElement = currentElement.nextElementSibling();

//                If the next element has the class "paragraph--normaal" and has a child of tag "h3" break the loop
                if (currentElement.hasClass("paragraph--normaal")) {
                    if (currentElement.children().get(0).tagName().equals("h3")) {
                        break;
                    }
                }

//                If currentElement is not yet in contentOfInterest, add the currentElement to the contentOfInterest
                if (!contentOfInterest.contains(currentElement)) {
                    contentOfInterest.add(currentElement);
                    System.out.println("");
                    System.out.println(currentElement);
                }
            }
        }
        return contentOfInterest;
    }

    private Document getIndicationWebDocument(String nhgIndicationName) {
        String urlIndicationName = String.join("-", nhgIndicationName
                .toLowerCase()
                .split(" ")
        );
        String totalUrl = this.url + urlIndicationName + "#volledige-tekst";
        try {
            Document doc = Jsoup.connect(totalUrl).get();
            return doc;
        } catch (IOException e) {
            String message = "With the nhgIndicationName (" + nhgIndicationName + ") the url " + totalUrl + " was generated. No connection could be made to this website.";
            throw new IllegalArgumentException(message);
        }
    }

    private void drugIndicationsNamesWithCsv() throws IOException {
        String row;

        BufferedReader csvReader = new BufferedReader(new FileReader(this.csvFileLocation));
        boolean firstline = true;
        while ((row = csvReader.readLine()) != null) {
            if (!firstline) {
                if (row.endsWith(";")) {
                    row = row + " ";
                }
                String[] data = row.split(";");
                this.drugIndicationsNames.get("ontpillen").add(data[0]);
                this.drugIndicationsNames.get("nhgrichtlijnen").add(data[2]);
            }
            firstline = false;
        }
        csvReader.close();
    }

}
