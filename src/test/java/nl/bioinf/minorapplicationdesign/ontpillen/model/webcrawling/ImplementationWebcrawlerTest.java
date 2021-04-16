package nl.bioinf.minorapplicationdesign.ontpillen.model.webcrawling;

import nl.bioinf.minorapplicationdesign.ontpillen.model.MedicineDAO.DrugDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest
class ImplementationWebcrawlerTest {

    @Autowired
    DrugDao drugDao;


    @Test
    void testRunWebcrawlers() throws IOException {
        String[] drugNamesArray = {"alprazolam", "oxazepam", "nalmefeen", "zuclopentixol", "fenelzine", "mianserine", "antipsychotica, atypische", "nitrazepam", "amisulpride", "clobazam", "dapoxetine", "midazolam", "chloorprotixeen", "psychostimulantia", "buprenorfine/naloxon", "benzodiazepine agonisten", "dosulepine", "sertraline", "methylfenidaat", "nortriptyline", "middelen bij verslavingsziekten", "prazepam", "clorazepinezuur", "modafinil", "middelen bij alcoholverslaving", "broomperidol", "zolpidem", "melatonine", "tranylcypromine", "dexamfetamine", "risperidon", "diazepam", "coffeïne", "antipsychotica", "antipsychotica, klassieke", "brotizolam", "paliperidon", "varenicline", "venlafaxine", "fluoxetine", "antidepressiva", "periciazine", "moclobemide", "serotonineheropnameremmers, selectief", "sertindol", "amitriptyline", "imipramine", "fluvoxamine", "antidepressiva, overige", "paroxetine", "duloxetine", "cariprazine", "psycholeptica", "flunitrazepam", "brexpiprazol", "quetiapine", "pimozide", "flurazepam", "lithium", "buprenorfine (bij verslaving)", "serotonineheropnameremmers, niet-selectief", "aripiprazol", "citalopram", "clomipramine", "temazepam", "nicotine", "loprazolam", "lorazepam", "middelen bij nicotineverslaving", "psychofarmaca, overige", "fluspirileen", "lithiumzouten", "melatonine agonisten", "lormetazepam", "flupentixol", "clozapine", "buspiron", "bupropion", "MAO-remmers, niet-selectief", "methadon", "vortioxetine", "disulfiram", "atomoxetine", "lisdexamfetamine", "zopiclon", "acamprosaat", "olanzapine", "haloperidol", "bromazepam", "penfluridol", "trazodon", "pipamperon", "escitalopram", "MAO-A-remmers", "middelen bij opioïdverslaving", "slaapmiddelen", "sulpiride", "mirtazapine", "tricyclische antidepressiva", "psychostimulantia, overige", "maprotiline", "naltrexon", "agomelatine", "lurasidon", "amfetaminen", "esketamine (nasaal)", "tetracyclische antidepressiva"};
        List drugNames = new ArrayList<>(Arrays.asList(drugNamesArray));

        DrugFetcher drugFetcher = new DrugFetcher(drugDao);
        drugFetcher.parseHtml();

        assertEquals(drugNames, drugDao.getAllDrugNames());
    }
}