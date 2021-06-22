package nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage;

import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content.UseIndication;

import java.util.List;


/**
 * DrugDao is the interface for DrugDao classes which allows the drug objects
 * to be saved in various ways. It contains all necessary methods for example: addDrug,
 * getAllDrugs and removeAllDrugs.
 * @author Larissa Bouwknegt, Jonathan Klimp, Naomi Hindriks
 */
public interface DrugDao {

    /**
     * Method that will add a new Drug object to the Dao.
     * It will check if the given drug is a DrugSubstance or a DrugGroup and add
     * it to the correct structure accordingly.
     * @param drug Drug object to be added
     */
    void addDrug(Drug drug);

    /**
     * Method that will return a Drug object given a drug name
     * that is in the Dao. If the drug is not found
     * it will throw an IllegalArgumentException.
     * @param drugName Name of the drug to be returned
     * @return Drug object
     */
    Drug getDrugByName(String drugName);

    /**
     * Method that will return all drug substances in the Dao.
     * @return List of all drugSubstances
     */
    List<DrugSubstance> getDrugSubstances();

    /**
     * Method that will return all main drug groups in the Dao.
     * @return list of all main drug groups
     */
    List<DrugGroup> getMainDrugGroups();

    /**
     * Method that will return all drugs in the Dao.
     * @return List of all drugs
     */
    List<Drug> getAllDrugs();

    /**
     * Method that will return all drug names in the Dao.
     * @return List of all drug names
     */
    List<String> getAllDrugNames();

    /**
     * Method that will remove all drugs in the Dao
     */
    void removeAllDrugs();


    /**
     * Method that checks if a drug with a given name is present in the DAO
     * @param drugName String name of the drug
     * @return a boolean, true if a drug with this name is present in the DAO.
     */
    boolean drugInDrugDao(String drugName);

    /**
     * Method that returns specific use indications of a drug given a indication name
     * @param indicationName String of the name of the use indication
     * @return The found UseIndication Object
     * @throws IllegalArgumentException if the indication is not found.
     */
    UseIndication getUseIndication(String indicationName);

    /**
     * Method that returns all use indications of a drug
     * @return List of UseIndication objects
     */
    List<UseIndication> getAllUseIndications();

    /**
     * Method that adds a new use indication to the drug
     * @param useIndication UseIndication object to be added
     */
    void addUseIndication(UseIndication useIndication);
}
