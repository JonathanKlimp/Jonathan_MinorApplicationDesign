package nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping;

import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugDao;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugSubstance;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content.UseIndication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Jonathan Klimp
 */
@SpringBootTest
class IndicationScraperTest {

    @Autowired
    DrugDao drugDao;

    @Autowired
    IndicationScraper indicationScraper;

    @Autowired
    DrugFetcher drugFetcher;

    @BeforeEach
    public void storeDrugs() throws IOException {
        SSLHelper.bypassSSL();
        drugFetcher.parseHtml();
        indicationScraper.parseHtml();
    }

    @AfterEach
    public void cleanUpTest() {
        drugDao.removeAllDrugs();
    }

    /**
     * Method that tests if each drug is assigned to the correct and expected use indication.
     * It loops trough all drug substances present in the dao, then gets the use indication(s) of that drug.
     * after that there is another loop through the use indications of that drug which is then used to check if
     * for that use indication the specific drug is present in the useIndicationsExpected hashmap
     *
     * @throws IOException
     */
    @Test
    void parseHtml_checkIfCorrectIndicationIsAdded() throws IOException {
        HashMap<String, List<String>> useIndicationsExpected = new HashMap<>();
        useIndicationsExpected.put("ADHD bij kinderen", Arrays.asList("atomoxetine", "dexamfetamine", "guanfacine", "lisdexamfetamine", "methylfenidaat"));
        useIndicationsExpected.put("angststoornissen", Arrays.asList("alprazolam", "bromazepam", "buspiron", "citalopram", "clobazam", "clomipramine", "clorazepinezuur", "diazepam", "duloxetine", "escitalopram", "fluoxetine", "fluvoxamine", "hydroxyzine", "imipramine", "lorazepam", "oxazepam", "paroxetine", "prazepam", "pregabaline", "propranolol (cardiovasculair of neurologisch)", "sertraline", "valeriaan", "venlafaxine"));
        useIndicationsExpected.put("bipolaire stoornis", Arrays.asList("agomelatine", "amitriptyline", "aripiprazol", "broomperidol", "bupropion", "carbamazepine", "chloorprotixeen", "citalopram", "clomipramine", "dosulepine", "duloxetine", "escitalopram", "fluoxetine", "flupentixol", "fluvoxamine", "haloperidol", "imipramine", "lamotrigine", "lithium", "maprotiline", "mianserine", "mirtazapine", "moclobemide", "nortriptyline", "olanzapine", "paroxetine", "periciazine", "quetiapine", "risperidon", "sertraline", "sulpiride", "tranylcypromine", "trazodon", "valproïnezuur", "venlafaxine", "vortioxetine", "zuclopentixol"));
        useIndicationsExpected.put("delier", Arrays.asList("clorazepinezuur", "diazepam", "haloperidol", "lorazepam", "midazolam", "oxazepam", "clozapine", "lorazepam", "midazolam", "quetiapine", "haloperidol", "lorazepam", "midazolam", "risperidon"));
        useIndicationsExpected.put("depressie", Arrays.asList("agomelatine", "amitriptyline", "bupropion", "citalopram", "clomipramine", "dosulepine", "duloxetine", "escitalopram", "esketamine (nasaal)", "fenelzine", "fluoxetine", "fluvoxamine", "imipramine", "lithium", "maprotiline", "mianserine", "mirtazapine", "moclobemide", "nortriptyline", "paroxetine", "quetiapine", "sertraline", "tranylcypromine", "trazodon", "venlafaxine", "vortioxetine"));
        useIndicationsExpected.put("psychose", Arrays.asList("amisulpride", "aripiprazol", "brexpiprazol", "broomperidol", "cariprazine", "chloorprotixeen", "clozapine", "diazepam", "droperidol (intramusculair)", "flupentixol", "fluspirileen", "haloperidol", "lorazepam", "lurasidon", "midazolam", "olanzapine", "oxazepam", "paliperidon", "penfluridol", "periciazine", "pimozide", "pipamperon", "quetiapine", "risperidon", "sertindol", "sulpiride", "zuclopentixol"));
        useIndicationsExpected.put("slapeloosheid", Arrays.asList("brotizolam", "chloralhydraat", "diazepam", "flunitrazepam", "flurazepam", "loprazolam", "lorazepam", "lormetazepam", "melatonine", "midazolam", "nitrazepam", "oxazepam", "temazepam", "valeriaan", "zolpidem", "zopiclon"));
        useIndicationsExpected.put("stoornissen bij het gebruik van alcohol", Arrays.asList("acamprosaat", "carbamazepine", "clorazepinezuur", "diazepam", "disulfiram", "foliumzuur", "lorazepam", "midazolam", "nalmefeen", "naltrexon", "oxazepam", "thiamine"));
        useIndicationsExpected.put("stoppen met roken", Arrays.asList("bupropion", "nicotine", "nortriptyline", "varenicline"));

        List<DrugSubstance> allDrugs = drugDao.getDrugSubstances();
        for (DrugSubstance drug1 : allDrugs) {
            List<UseIndication> useIndications = drug1.getUseIndications();
            for (UseIndication useIndication : useIndications) {
                assertTrue(useIndicationsExpected.get(useIndication.getName()).contains(drug1.getName()));
            }
        }
    }
}