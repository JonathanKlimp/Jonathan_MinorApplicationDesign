package nl.bioinf.minorapplicationdesign.ontpillen.model.MedicineDAO;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ComponentScan(basePackageClasses = {DrugDao.class})
class InMemoryDrugDaoTest {

    @Autowired
    DrugDao drugDao;

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
    void getDrugByName_DrugInStorage_returnDrug() {
        DrugGroup newDrugGroup = new DrugGroup("Citalopram");
        drugDao.addDrug(newDrugGroup);
        assertEquals(newDrugGroup, drugDao.getDrugByName("Citalopram"));
    }

    @Test
    void getDrugByName_DrugNotInStorage_throwIllegalArgumentException() {
        try {
            drugDao.getDrugByName("Nicotine");
        }
        catch(IllegalArgumentException exception){
            assertEquals("Nicotine does not exist.", exception.getMessage());
        }
    }
}