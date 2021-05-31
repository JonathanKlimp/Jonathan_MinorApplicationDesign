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


//            For every entry in contentOfInterest map , add a ContentNode to the current UseIndication for the practitioner
//            and set the title of this node to the key of the current map entry. Then process the value of the current
//            entry further into ContentNodes and ContentLeafs.
            for (Map.Entry<String,Elements> entry : contentOfInterest.entrySet()) {
                ContentNode newContentNode = new ContentNode();
                String title = entry.getKey();
                newContentNode.setContentTitle(title);
                currentUseIndication.addContentPractitioner(title, newContentNode);
//                System.out.println("key= " + entry.getKey());
//                for(Element element : entry.getValue()) {
//                    System.out.println("value element= " + element);
//                }
                this.processContentOfInterest(newContentNode, entry.getValue());

            }
            break;
        }
    }

    private void processContentOfInterest(ContentNode currentContentNode, Elements elementsToProcess) {
        System.out.println(currentContentNode.getContentTitle());
        for (Element element : elementsToProcess) {
            System.out.println("Element= " + element);
        }
    }

    /**
     * A method to extract the content of interest (medication information of the richtlijnen.nhg website) from a Jsoup
     * document.
     * @param doc a Jsoup Document object to extract content from
     * @return a Map where the keys are strings set to the title of the section and the values are Jsoup Elements objects
     * that hold all the Elements of interest from that section.
     */
    private Map<String, Elements> getContentOfInterest(Document doc) {
        Map<String, Elements> contentOfInterest = new HashMap<>();

        Elements elements = doc.select(".field--name-main-text .collapsible-section-wrapper:has(#volledige-tekst-richtlijnen-beleid)");

        if (elements.size() == 0) {
            for (Element element : doc.select(".field--name-main-text .collapsible-section-wrapper")) {
                if (element.text().toLowerCase().contains("richtlijnen beleid")) {
                    Elements h3Elements = geth3Elements(element,  "medicamenteuze behandeling", "niet-medicamenteuze");
                    contentOfInterest.put(element.select("h2").text(), getContentOfInterestFromH3Elements(h3Elements));
                }
            }
        } else {
            for (Element element : elements) {
                Elements h3Elements = geth3Elements(element,  "medicamenteuze behandeling", "niet-medicamenteuze");
                contentOfInterest.put(element.select("h2").text(), getContentOfInterestFromH3Elements(h3Elements));
            }
        }
        return contentOfInterest;
    }

    /**
     * Method to find h3 elements in a given element, the h3 elements that contain the h3Query and do not contain
     * the h3AntiQuery will be returned in a Jsoup Elements object.
     * @param element Jsoup Element that is used to find h3 elements in.
     * @param h3Query The returned h3 elements will contain this query as text (use lowercase)
     * @param h3AntiQuery h3 elements that contain this query as text will not be returned even if they contain the h3Query (use lowercase)
     * @return A Jsoup Elements object that contains the found h3 elements
     */
    private Elements geth3Elements(Element element, String h3Query, String h3AntiQuery) {
        Elements h3Elements = new Elements();
        Elements paragraphs = element.select(".paragraph");

        for (Element paragraph : paragraphs) {
            for (Element h3Element : paragraph.select("h3")) {
                String h3Text = h3Element.text().toLowerCase();
                if (h3Text.contains(h3Query) && !h3Text.contains(h3AntiQuery)) {
                    h3Elements.add(h3Element);
                }
            }
        }
        return h3Elements;
    }

    /**
     * Given an h3 element get all the content that is part of this section of the webpage. To do this the parent of the
     * h3 element is retrieved and saved as well as all the next sibblings until a next sibbling has the class paragraph--normaal
     * and has a  child that is an h3 element in it.
     * @param h3Elements Jsoup Elements of the type h3 that belong to sections of the website that are of interest
     * @return a Jsoup Elements object containing all the content of interest.
     */
    private Elements getContentOfInterestFromH3Elements(Elements h3Elements) {
        Elements contentOfInterest = new Elements();
        for (Element h3Element : h3Elements) {
            if (!h3Element.tagName().equals("h3")) {
                throw new IllegalArgumentException("h3Elements must only contain Elements that are of the tag h3");
            }
            Element currentElement = h3Element.parent();

//            If the contewnt of interest doesn't already have the current element add to content of interest
            if (!contentOfInterest.contains(currentElement)) {
                contentOfInterest.add(currentElement);
            }

            while (currentElement.nextElementSibling() != null) {
//                Go to next element
                currentElement = currentElement.nextElementSibling();

//                If the next element has the class "paragraph--normaal" and has a child of tag "h3" break the loop
                if (currentElement.hasClass("paragraph--normaal") && currentElement.children().get(0).tagName().equals("h3")) {
                        break;
                }

//                If currentElement is not yet in contentOfInterest, add the currentElement to the contentOfInterest
                if (!contentOfInterest.contains(currentElement)) {
                    contentOfInterest.add(currentElement);
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
