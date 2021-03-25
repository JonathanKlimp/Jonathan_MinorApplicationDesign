package nl.bioinf.minorapplicationdesign.ontpillen.model.MedicineDAO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InMemoryDrugDao implements DrugsDao {
    static Map<String, DrugSubstance> drugMap = new HashMap(); // DrugSubstance needs to be abstract class Drug?

    @Override
    public Drug getDrugByName(String drugName) {
        return getDrugSubstance(drugName);
    }

    @Override
    public Set<String> getListOfDrugs() {
        return drugMap.keySet();
    }

    @Override
    public List<Drug> listDrugsRecursive() {
        return null;
    }

    /**
     * This method adds a new drugGroup to the inMemoryDrugDao given a drugGroup.
     * It will check if the drugGroup already exists in the in memory storage.
     * If it does not exist it will add it.
     * @param drugGroup new drug group to be added
     */
    @Override
    public void addDrugsGroup(List<Drug> drugGroup) {
        if(!drugMap.containsKey(drugGroup)){
            DrugsGroup drugsGroup = new DrugsGroup();
            drugsGroup.setChildren(drugGroup);
        }
    }

    /**
     * This method adds a new DrugSubstance to the inMemoryDrugDao given a drug.
     * It will check if the drugSubstance already exists in the in memory storage.
     * If it does not exists it will add it to the storage and add it to the drugMap.
     * @param drugName new Drug to be added.
     */
    @Override
    public void addDrugSubstance(String drugName) {
        if(!drugMap.containsKey(drugName)){
            DrugSubstance drugSubstance = new DrugSubstance(); // same here DrugSubstance needs to be abstract class Drug?
            drugSubstance.setName(drugName);
            drugSubstance.addBrandNames(drugName);
            drugMap.put(drugName, drugSubstance);
        }
    }

    static DrugSubstance getDrugSubstance(String drugName) {
        return drugMap.get(drugName);
    }
}