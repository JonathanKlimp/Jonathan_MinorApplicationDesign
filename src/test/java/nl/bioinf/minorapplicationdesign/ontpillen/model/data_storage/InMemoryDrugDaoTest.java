package nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage;

import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content.UseIndication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Jonathan Klimp
 */ //TODO add more tests + javadoc
@SpringBootTest
class InMemoryDrugDaoTest {

    @Autowired
    DrugDao drugDao;

    @AfterEach
    public void cleanUpDao() {
        drugDao.removeAllDrugs();
    }

    @Test
    void addDrug_addNewSubstance_returnNewDrugSubstance() {
        DrugSubstance newDrugSubstance = new DrugSubstance("Citalopram");
        drugDao.addDrug(newDrugSubstance);
        assertEquals(newDrugSubstance, drugDao.getDrugByName("Citalopram"));
    }

    @Test
    void addDrug_addDuplicateDrugSubstance_throwIllegalArgumentException() {
        DrugSubstance newDrugSubstance1 = new DrugSubstance("Citalopram");
        DrugSubstance newDrugSubstance2 = new DrugSubstance("Citalopram");
        try {
            drugDao.addDrug(newDrugSubstance1);
            drugDao.addDrug(newDrugSubstance2);
            fail();
        }
        catch (IllegalArgumentException exception){
            assertEquals("Cannot add drug that already exists", exception.getMessage());
        }
    }

    @Test
    void addDrug_addNewDrugGroup_returnNewDrugGroup(){
        DrugGroup newDrugGroup = new DrugGroup("Serotonineheropnameremmers, selectief");
        drugDao.addDrug(newDrugGroup);
        assertEquals(newDrugGroup, drugDao.getDrugByName("Serotonineheropnameremmers, selectief"));
    }

    @Test
    void addDrug_addDuplicateDrugGroup_throwIllegalArgumentException() {
        DrugGroup newDrugGroup1 = new DrugGroup("Serotonineheropnameremmers, selectief");
        DrugGroup newDrugGroup2 = new DrugGroup("Serotonineheropnameremmers, selectief");
        try {
            drugDao.addDrug(newDrugGroup1);
            drugDao.addDrug(newDrugGroup2);
            fail();
        }
        catch (IllegalArgumentException exception){
            assertEquals("Cannot add drug that already exists", exception.getMessage());
        }
    }

    @Test
    void addDrug_nullValue_throwIllegalArgumentException() {
        try {
            DrugGroup newDrugGroup = new DrugGroup(null);
            drugDao.addDrug(newDrugGroup);
            fail();
        }
        catch (Exception exception) {
            assertEquals("name can't be null", exception.getMessage());
        }
    }

    @Test
    void getDrugByName_DrugInStorage_returnDrug() {
        DrugGroup newDrugGroup = new DrugGroup("Citalopram");
        drugDao.addDrug(newDrugGroup);
        assertEquals(newDrugGroup, drugDao.getDrugByName("Citalopram"));
    }

    @Test
    void getDrugByName_DrugNotInStorage_throwIllegalArgumentException() {
        try {
            drugDao.getDrugByName("Nicotine");
            fail();
        }
        catch(IllegalArgumentException exception){
            assertEquals("Nicotine does not exist.", exception.getMessage());
        }
    }

    @Test
    void getDrugByName_nullValue_throwIllegalArgumentException() {
        try {
            drugDao.getDrugByName(null);
        }
        catch (Exception exception){
            assertEquals("null does not exist.", exception.getMessage());
        }
    }


    @Test
    void getUseIndication_returnUseIndication() {
        UseIndication testUseIndication = new UseIndication();
        testUseIndication.setName("testUseIndication");
        drugDao.addUseIndication(testUseIndication);
        assertEquals("testUseIndication", drugDao.getUseIndication("testUseIndication").getName());
    }

    @Test
    void getUseIndication_nonExistentValue_throwIllegalArgumentException() {
        try{
            drugDao.getUseIndication("test");
        } catch (Exception exception) {
            assertEquals("test Is not found in the dao", exception.getMessage());
        }
    }

    @Test
    void getAllUseIndications_returnAllUseIndications() {
        UseIndication testUseIndication = new UseIndication();
        testUseIndication.setName("testUseIndication");
        drugDao.addUseIndication(testUseIndication);

        UseIndication testUseIndication2 = new UseIndication();
        testUseIndication2.setName("testUseIndication2");
        drugDao.addUseIndication(testUseIndication2);

        assertEquals(2, drugDao.getAllUseIndications().size());
    }

    @Test
    void getDrugSubstances_returnSubstances() {
        ArrayList<String> drugNamesExpected = new ArrayList<>();
        drugNamesExpected.add("Citalopram");
        drugNamesExpected.add("Guanfacine");
        drugNamesExpected.add("Sulpiride");
        DrugSubstance drugSubstance1 = new DrugSubstance("Citalopram");
        DrugSubstance drugSubstance2 = new DrugSubstance("Guanfacine");
        DrugSubstance drugSubstance3 = new DrugSubstance("Sulpiride");
        drugDao.addDrug(drugSubstance1);
        drugDao.addDrug(drugSubstance2);
        drugDao.addDrug(drugSubstance3);

        ArrayList<String> drugNames = new ArrayList<>();
        for (DrugSubstance drug : drugDao.getDrugSubstances()) {
            drugNames.add(drug.getName());
        }
        assertEquals(drugNamesExpected, drugNames);
    }

    @Test
    void getAllDrugNames_returnAllDrugNames() {
        ArrayList<String> drugNamesExpected = new ArrayList<>();
        drugNamesExpected.add("Citalopram");
        drugNamesExpected.add("Guanfacine");
        drugNamesExpected.add("Sulpiride");
        DrugSubstance drugSubstance1 = new DrugSubstance("Citalopram");
        DrugSubstance drugSubstance2 = new DrugSubstance("Guanfacine");
        DrugSubstance drugSubstance3 = new DrugSubstance("Sulpiride");
        drugDao.addDrug(drugSubstance1);
        drugDao.addDrug(drugSubstance2);
        drugDao.addDrug(drugSubstance3);

        List<String> drugNames = drugDao.getAllDrugNames();
        assertEquals(drugNamesExpected.containsAll(drugNames), drugNames.containsAll(drugNamesExpected));
    }

    @Test
    void getAllDrugs_returnAllDrugs() {
        ArrayList<String> drugNamesExpected = new ArrayList<>();
        drugNamesExpected.add("Citalopram");
        drugNamesExpected.add("Guanfacine");
        drugNamesExpected.add("Sulpiride");
        drugNamesExpected.add("testgroup");
        DrugSubstance drugSubstance1 = new DrugSubstance("Citalopram");
        DrugSubstance drugSubstance2 = new DrugSubstance("Guanfacine");
        DrugSubstance drugSubstance3 = new DrugSubstance("Sulpiride");
        DrugGroup drugGroup = new DrugGroup("testgroup");
        drugDao.addDrug(drugSubstance1);
        drugDao.addDrug(drugSubstance2);
        drugDao.addDrug(drugSubstance3);
        drugDao.addDrug(drugGroup);

        ArrayList<String> drugNames = new ArrayList<>();
        for (Drug drug : drugDao.getAllDrugs()) {
            drugNames.add(drug.getName());
        }
        assertEquals(drugNamesExpected.containsAll(drugNames), drugNames.containsAll(drugNamesExpected));
    }

    @Test
    void getMainDrugGroups_returnMainDrugGroups() {
        ArrayList<String> drugNamesExpected = new ArrayList<>();
        drugNamesExpected.add("testgroup1");
        drugNamesExpected.add("testgroup2");
        DrugSubstance drugSubstance1 = new DrugSubstance("Citalopram");
        DrugGroup drugGroup1 = new DrugGroup("testgroup1");
        DrugSubstance drugSubstance3 = new DrugSubstance("Sulpiride");
        DrugGroup drugGroup2 = new DrugGroup("testgroup2");
        drugDao.addDrug(drugSubstance1);
        drugDao.addDrug(drugGroup1);
        drugDao.addDrug(drugSubstance3);
        drugDao.addDrug(drugGroup2);

        ArrayList<String> drugNames = new ArrayList<>();
        for (Drug drug : drugDao.getMainDrugGroups()) {
            drugNames.add(drug.getName());
        }
        assertEquals(drugNamesExpected.containsAll(drugNames), drugNames.containsAll(drugNamesExpected));
        System.out.println(drugDao.getMainDrugGroups());
    }
}