package nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * @author Larissa Bouwknegt, Jonathan Klimp, Naomi Hindriks
 */
public class InMemoryDrugDao implements DrugDao {
    static private List<String> drugSubstances = new ArrayList<>();
    static private List<String> mainDrugGroups = new ArrayList<>();
    static private Map<String, Drug> allDrugs = new HashMap<>();

    /**
     * Method that will add drug to the dao.
     * It checks if the drug already exists, if it exists the method throws an IllegalArgumentException.
     * It will check if the given drug is a DrugSubstance or a DrugGroup and will add it to the correct list accordingly
     * @param drug Drug object to be added
     */
    @Override
    public void addDrug(Drug drug) {
        if(drugInDrugDao(drug.name)){
            throw new IllegalArgumentException("Cannot add drug that already exists");
        }
        allDrugs.put(drug.getName(), drug);
        if (drug instanceof DrugSubstance) {
            drugSubstances.add(drug.getName());
        }
        else if (drug instanceof DrugGroup && drug.getParent() == null) {
            mainDrugGroups.add(drug.getName());
        }
    }

    /**
     * Method that will return a drug object given a drug name.
     * It checks if the drugs exists if it does not exist an IllegalArgumentException is thrown
     * @param drugName Name of the drug to be returned
     * @return Drug object
     */
    @Override
    public Drug getDrugByName(String drugName) {
        if (!allDrugs.containsKey(drugName)) {
            throw new IllegalArgumentException(drugName + " does not exist.");
        }
        return allDrugs.get(drugName);
    }

    @Override
    public List<Drug> getAllDrugs() {
        return new ArrayList<>(allDrugs.values());
    }

    @Override
    public List<String> getAllDrugNames() {
        return new ArrayList<>(allDrugs.keySet());
    }

    @Override
    public List<DrugSubstance> getDrugSubstances() {
        List<DrugSubstance> returnList = new ArrayList<>();
        for(String drugName : drugSubstances) {
            returnList.add((DrugSubstance) allDrugs.get(drugName));
        }
        return returnList;
    }

    @Override
    public List<DrugGroup> getMainDrugGroups() {
        List<DrugGroup> returnList = new ArrayList<>();
        for(String drugName : drugSubstances) {
            returnList.add((DrugGroup) allDrugs.get(drugName));
        }
        return returnList;
    }

    @Override
    public boolean drugInDrugDao(String drugName) {
        return allDrugs.containsKey(drugName);
    }

    /**
     * Method that will remove all drugs in the dao
     */
    public void removeAllDrugs() {
        drugSubstances.clear();
        mainDrugGroups.clear();
        allDrugs.clear();
    }
}