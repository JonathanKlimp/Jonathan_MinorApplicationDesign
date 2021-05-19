package nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping;

import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.Drug;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugDao;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugGroup;
import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.DrugSubstance;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Jonathan Klimp
 */


@SpringBootTest
class DrugFetcherTest {
    @Autowired
    DrugDao drugDao;

    @Autowired
    DrugFetcher drugFetcher;

    @AfterEach
    public void cleanUpTest() {
        drugDao.removeAllDrugs();
    }

    @BeforeEach
    public void bypassSSL() {
        SSLHelper.bypassSSL();
    }

    /**
     * Method that tests if drugFetcher correctly adds all expected drugs. The expected drug list and the actual
     * drug list are compared independent of order.
     * @throws IOException
     */
    @Test
    void parseHtml_scrapeDrugs_saveToTheDao() throws IOException {
        String[] drugNamesArray = {"alprazolam", "oxazepam", "nalmefeen", "zuclopentixol", "fenelzine", "mianserine", "antipsychotica, atypische", "nitrazepam", "amisulpride", "clobazam", "dapoxetine", "midazolam", "chloorprotixeen", "psychostimulantia", "buprenorfine/naloxon", "benzodiazepine agonisten", "dosulepine", "sertraline", "methylfenidaat", "nortriptyline", "middelen bij verslavingsziekten", "prazepam", "clorazepinezuur", "modafinil", "middelen bij alcoholverslaving", "broomperidol", "zolpidem", "melatonine", "tranylcypromine", "dexamfetamine", "risperidon", "diazepam", "coffeïne", "antipsychotica", "antipsychotica, klassieke", "brotizolam", "paliperidon", "varenicline", "venlafaxine", "fluoxetine", "antidepressiva", "periciazine", "moclobemide", "serotonineheropnameremmers, selectief", "sertindol", "amitriptyline", "imipramine", "fluvoxamine", "antidepressiva, overige", "paroxetine", "duloxetine", "cariprazine", "psycholeptica", "flunitrazepam", "brexpiprazol", "quetiapine", "pimozide", "flurazepam", "lithium", "buprenorfine (bij verslaving)", "serotonineheropnameremmers, niet-selectief", "aripiprazol", "citalopram", "clomipramine", "temazepam", "nicotine", "loprazolam", "lorazepam", "middelen bij nicotineverslaving", "psychofarmaca, overige", "fluspirileen", "lithiumzouten", "melatonine agonisten", "lormetazepam", "flupentixol", "clozapine", "buspiron", "bupropion", "MAO-remmers, niet-selectief", "methadon", "vortioxetine", "disulfiram", "atomoxetine", "lisdexamfetamine", "zopiclon", "acamprosaat", "olanzapine", "haloperidol", "bromazepam", "penfluridol", "trazodon", "pipamperon", "escitalopram", "MAO-A-remmers", "middelen bij opioïdverslaving", "slaapmiddelen", "sulpiride", "mirtazapine", "tricyclische antidepressiva", "psychostimulantia, overige", "maprotiline", "naltrexon", "agomelatine", "lurasidon", "amfetaminen", "esketamine (nasaal)", "tetracyclische antidepressiva", "carbamazepine", "chloralhydraat", "droperidol (intramusculair)", "foliumzuur", "guanfacine", "hydroxyzine", "lamotrigine", "pregabaline", "propranolol (cardiovasculair of neurologisch)", "thiamine", "valeriaan", "valproïnezuur"};
        ArrayList<String> drugNames = new ArrayList<>(Arrays.asList(drugNamesArray));

        drugFetcher.parseHtml();

        List<String> expectedDrugs = drugDao.getAllDrugNames();
        expectedDrugs.removeAll(drugNames);

        drugNames.removeAll(drugDao.getAllDrugNames());
        assertEquals(drugNames, expectedDrugs);
    }

    /**
     * Method that tests if drugFetcher correctly adds all expected drug groups. The expected drug group list and the actual
     * drug group list are compared independent of order.
     * @throws IOException
     */
    @Test
    void parseHtml_scrapeDrugs_CheckDrugGroups() throws IOException {
        String[] drugGroupsArray = {"antidepressiva", "middelen bij verslavingsziekten", "psychofarmaca, overige", "psycholeptica", "psychostimulantia", "slaapmiddelen"};
        ArrayList<String> drugGroupsExpected = new ArrayList<>(Arrays.asList(drugGroupsArray));

        drugFetcher.parseHtml();

        List<DrugGroup> drugGroupsDao = drugDao.getMainDrugGroups();
        List<String> drugGroupsActual = new ArrayList<>();

        for (DrugGroup drugGroup: drugGroupsDao) {
            drugGroupsActual.add(drugGroup.getName());
        }

        drugGroupsActual.removeAll(drugGroupsExpected);
        drugGroupsExpected.removeAll(Arrays.asList(drugGroupsArray));


        assertEquals(drugGroupsActual, drugGroupsExpected);
    }

    /**
     * Method that tests if drugFetcher correctly adds all expected drug substances. The expected drug substances list and the actual
     * drug substances list are compared independent of order.
     * @throws IOException
     */
    @Test
    void parseHtml_scrapeDrugs_CheckDrugSubstance() throws IOException {
        String[] drugSubstanceArray = {"buspiron", "bupropion", "esketamine (nasaal)", "vortioxetine", "lithium", "moclobemide", "fenelzine", "tranylcypromine", "agomelatine", "melatonine", "duloxetine", "trazodon", "venlafaxine", "citalopram", "dapoxetine", "escitalopram", "fluoxetine", "fluvoxamine", "paroxetine", "sertraline", "mianserine", "mirtazapine", "amitriptyline", "clomipramine", "dosulepine", "imipramine", "maprotiline", "nortriptyline", "acamprosaat", "disulfiram", "nalmefeen", "naltrexon", "nicotine", "varenicline", "buprenorfine (bij verslaving)", "buprenorfine/naloxon", "methadon", "dexamfetamine", "lisdexamfetamine", "methylfenidaat", "atomoxetine", "coffeïne", "modafinil", "alprazolam", "bromazepam", "brotizolam", "clobazam", "clorazepinezuur", "diazepam", "flunitrazepam", "flurazepam", "loprazolam", "lorazepam", "lormetazepam", "midazolam", "nitrazepam", "oxazepam", "prazepam", "temazepam", "zolpidem", "zopiclon", "aripiprazol", "brexpiprazol", "cariprazine", "clozapine", "lurasidon", "olanzapine", "paliperidon", "quetiapine", "risperidon", "sertindol", "amisulpride", "broomperidol", "chloorprotixeen", "flupentixol", "fluspirileen", "haloperidol", "penfluridol", "periciazine", "pimozide", "pipamperon", "sulpiride", "zuclopentixol", "carbamazepine", "chloralhydraat", "droperidol (intramusculair)", "foliumzuur", "guanfacine", "hydroxyzine", "lamotrigine", "pregabaline", "propranolol (cardiovasculair of neurologisch)", "thiamine", "valeriaan", "valproïnezuur"};
        ArrayList<String> drugSubstancesExpected = new ArrayList<>(Arrays.asList(drugSubstanceArray));

        drugFetcher.parseHtml();

        List<DrugSubstance> drugGroupsInDao = drugDao.getDrugSubstances();
        List<String> drugSubstancesActual = new ArrayList<>();

        for (DrugSubstance drugSubstance: drugGroupsInDao) {
            drugSubstancesActual.add(drugSubstance.getName());
        }

        drugSubstancesActual.removeAll(drugSubstancesExpected);
        drugSubstancesExpected.removeAll(Arrays.asList(drugSubstanceArray));

        assertEquals(drugSubstancesActual, drugSubstancesExpected);
    }
}