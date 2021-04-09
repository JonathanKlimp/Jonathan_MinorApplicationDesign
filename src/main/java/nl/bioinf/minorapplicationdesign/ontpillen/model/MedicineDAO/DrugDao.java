package nl.bioinf.minorapplicationdesign.ontpillen.model.MedicineDAO;

import java.util.List;
import java.util.Set;

public interface DrugDao {

    /**
     * Function that will add a new Drug object to the InMemoryDrugDao.
     * It will check if the given drug is a DrugSubstance or a DrugGroup and add
     * it to the correct map accordingly.
     * @param drug Drug object to be added
     */
    void addDrug(Drug drug);

    /**
     * Function that will return a Drug object given a drug name
     * that is in the InmemoryDrugDao. If the drug is not found
     * it will throw an IllegalArgumentException.
     * @param drugName Name of the drug to be returned
     * @return Drug object
     */
    Drug getDrugByName(String drugName);

    /**
     * Function that will return all drug substances in the InmemoryDrugDao.
     * @return List of all drugSubstances
     */
    List<Drug> getDrugSubstances();

    /**
     * Function that will return all main drug groups in the InmemoryDrugDao.
     * @return list of all main drug groups
     */
    List<Drug> getMainDrugGroups();

    /**
     * Function that will return all drugs in the InmemoryDrugDao.
     * @return List of all drugs
     */
    public List<Drug> getAllDrugs();

    /**
     * Function that will return all drug names in the inMemoryDrugDao.
     * @return List of all drug names
     */
    public List<String> getAllDrugNames();
}
