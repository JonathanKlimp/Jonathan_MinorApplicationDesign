package nl.bioinf.minorapplicationdesign.ontpillen.model.MedicineDAO;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class InMemoryDrugDaoTest {

    @Test
    void addDrug_addNewSubstance_returnNewDrugSubstance() {
        DrugSubstance newDrugSubstance = new DrugSubstance("Citalopram");
        InMemoryDrugDao informationStorage = InMemoryDrugDao.getInstance();
        informationStorage.addDrug(newDrugSubstance);
        assertEquals(newDrugSubstance, informationStorage.getDrugByName("Citalopram"));
    }

    @Test
    void addDrug_addDuplicateDrugSubstance_throwIllegalArgumentException() {
        DrugSubstance newDrugSubstance1 = new DrugSubstance("Citalopram");
        DrugSubstance newDrugSubstance2 = new DrugSubstance("Citalopram");
        InMemoryDrugDao informationStorage = InMemoryDrugDao.getInstance();
        try {
            informationStorage.addDrug(newDrugSubstance1);
            informationStorage.addDrug(newDrugSubstance2);
            fail();
        }
        catch (IllegalArgumentException exception){
            assertEquals("Cannot add drug that already exists", exception.getMessage());
        }
    }

    @Test
    void addDrug_addNewDrugGroup_returnNewDrugGroup(){
        DrugGroup newDrugGroup = new DrugGroup("Serotonineheropnameremmers, selectief");
        InMemoryDrugDao informationStorage = InMemoryDrugDao.getInstance();
        informationStorage.addDrug(newDrugGroup);
        assertEquals(newDrugGroup, informationStorage.getDrugByName("Serotonineheropnameremmers, selectief"));
    }

    @Test
    void getDrugByName_DrugInStorage_returnDrug() {
        DrugGroup newDrugGroup = new DrugGroup("Citalopram");
        InMemoryDrugDao informationStorage = InMemoryDrugDao.getInstance();
        informationStorage.addDrug(newDrugGroup);
        assertEquals(newDrugGroup, informationStorage.getDrugByName("Citalopram"));
    }

    @Test
    void getDrugByName_DrugNotInStorage_throwIllegalArgumentException() {
        InMemoryDrugDao informationStorage = InMemoryDrugDao.getInstance();
        try {
            informationStorage.getDrugByName("Nicotine");
        }
        catch(IllegalArgumentException exception){
            assertEquals("Nicotine does not exist.", exception.getMessage());
        }
    }


}