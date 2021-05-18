package nl.bioinf.minorapplicationdesign.ontpillen.model.web_scraping;

import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Jonathan Klimp
 */

@SpringBootTest
class ImplementationWebscraperTest {

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


    @Test
    void runWebcrawlers_parseHtml_returnsAllDrugs() throws IOException {
        String[] drugNamesArray = {"alprazolam", "oxazepam", "nalmefeen", "zuclopentixol", "fenelzine", "mianserine", "antipsychotica, atypische", "nitrazepam", "amisulpride", "clobazam", "dapoxetine", "midazolam", "chloorprotixeen", "psychostimulantia", "buprenorfine/naloxon", "benzodiazepine agonisten", "dosulepine", "sertraline", "methylfenidaat", "nortriptyline", "middelen bij verslavingsziekten", "prazepam", "clorazepinezuur", "modafinil", "middelen bij alcoholverslaving", "broomperidol", "zolpidem", "melatonine", "tranylcypromine", "dexamfetamine", "risperidon", "diazepam", "coffeïne", "antipsychotica", "antipsychotica, klassieke", "brotizolam", "paliperidon", "varenicline", "venlafaxine", "fluoxetine", "antidepressiva", "periciazine", "moclobemide", "serotonineheropnameremmers, selectief", "sertindol", "amitriptyline", "imipramine", "fluvoxamine", "antidepressiva, overige", "paroxetine", "duloxetine", "cariprazine", "psycholeptica", "flunitrazepam", "brexpiprazol", "quetiapine", "pimozide", "flurazepam", "lithium", "buprenorfine (bij verslaving)", "serotonineheropnameremmers, niet-selectief", "aripiprazol", "citalopram", "clomipramine", "temazepam", "nicotine", "loprazolam", "lorazepam", "middelen bij nicotineverslaving", "psychofarmaca, overige", "fluspirileen", "lithiumzouten", "melatonine agonisten", "lormetazepam", "flupentixol", "clozapine", "buspiron", "bupropion", "MAO-remmers, niet-selectief", "methadon", "vortioxetine", "disulfiram", "atomoxetine", "lisdexamfetamine", "zopiclon", "acamprosaat", "olanzapine", "haloperidol", "bromazepam", "penfluridol", "trazodon", "pipamperon", "escitalopram", "MAO-A-remmers", "middelen bij opioïdverslaving", "slaapmiddelen", "sulpiride", "mirtazapine", "tricyclische antidepressiva", "psychostimulantia, overige", "maprotiline", "naltrexon", "agomelatine", "lurasidon", "amfetaminen", "esketamine (nasaal)", "tetracyclische antidepressiva"};
        List<String> drugNames = new ArrayList<>(Arrays.asList(drugNamesArray));

        drugFetcher.parseHtml();

        assertEquals(drugNames, drugDao.getAllDrugNames());
    }

    @Test
    void runWebcrawlers_parseHtml_returnsCorrectDrugSubstances() throws IOException {
        drugFetcher.parseHtml();
        String[] drugSubstances = {"alprazolam", "oxazepam", "nalmefeen", "quetiapine", "pimozide", "flurazepam", "zuclopentixol", "fenelzine", "lithium", "mianserine", "buprenorfine (bij verslaving)", "nitrazepam", "amisulpride", "clobazam", "dapoxetine", "midazolam", "chloorprotixeen", "buprenorfine/naloxon", "aripiprazol", "citalopram", "dosulepine", "sertraline", "methylfenidaat", "nortriptyline", "clomipramine", "temazepam", "nicotine", "loprazolam", "prazepam", "clorazepinezuur", "modafinil", "lorazepam", "broomperidol", "fluspirileen", "lormetazepam", "zolpidem", "flupentixol", "melatonine", "tranylcypromine", "clozapine", "dexamfetamine", "buspiron", "bupropion", "methadon", "risperidon", "diazepam", "vortioxetine", "coffeïne", "disulfiram", "brotizolam", "atomoxetine", "lisdexamfetamine", "zopiclon", "paliperidon", "acamprosaat", "varenicline", "olanzapine", "haloperidol", "venlafaxine", "fluoxetine", "bromazepam", "penfluridol", "trazodon", "pipamperon", "periciazine", "escitalopram", "moclobemide", "sulpiride", "sertindol", "amitriptyline", "mirtazapine", "imipramine", "maprotiline", "naltrexon", "fluvoxamine", "agomelatine", "lurasidon", "paroxetine", "esketamine (nasaal)", "duloxetine", "cariprazine", "flunitrazepam", "brexpiprazol"};
        List<String> expectedDrugSubstances = new ArrayList<>(Arrays.asList(drugSubstances));
        List<String> actualDrugSubstances = new ArrayList<>();

        for(Drug drugSubstance : drugDao.getDrugSubstances()){
            actualDrugSubstances.add(drugSubstance.getName());
        }
        boolean itemsMatch = false;

        // check if the actual drug substances are in the expected list
        for (String drug : actualDrugSubstances) {
            itemsMatch = expectedDrugSubstances.contains(drug);
        }
        assertTrue(itemsMatch);
    }

    @Test
    void runWebcrawlers_parseHtml_returnCorrectDrugGroups() throws IOException {
        drugFetcher.parseHtml();

        String[] drugGroups = {"middelen bij verslavingsziekten", "antidepressiva", "psychostimulantia", "slaapmiddelen", "psychofarmaca, overige", "psycholeptica"};
        List<String> expectedDrugGroups = new ArrayList<>(Arrays.asList(drugGroups));
        List<String> actualDrugGroups = new ArrayList<>();

        for(Drug drugGroup: drugDao.getMainDrugGroups()){
            actualDrugGroups.add(drugGroup.getName());
        }
        boolean itemsMatch = false;

        // check if the actual drug substances are in the expected list
        for (String drug : actualDrugGroups) {
            itemsMatch = expectedDrugGroups.contains(drug);
        }
        assertTrue(itemsMatch);
    }
}