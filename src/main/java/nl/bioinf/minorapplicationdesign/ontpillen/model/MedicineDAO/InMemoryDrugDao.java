package nl.bioinf.minorapplicationdesign.ontpillen.model.MedicineDAO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InMemoryDrugDao implements DrugsDao {
    static Map<String, DrugSubstance> drugMap = new HashMap();
    static Map<String, DrugsGroup> drugsGroupMap = new HashMap();

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
     * @param drugGroupName new drug group to be added
     * @param drugsInGroup List of abstract Drug objects with all drugs from the DrugGroup
     */
    @Override
    public void addDrugsGroup(String drugGroupName, List<Drug> drugsInGroup) {
        if(!drugMap.containsKey(drugGroupName)){
            DrugsGroup drugsGroup = new DrugsGroup();
            drugsGroup.setChildren(drugsInGroup);
            drugsGroupMap.put(drugGroupName, drugsGroup);
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
            DrugSubstance drugSubstance = new DrugSubstance();
            drugSubstance.setName(drugName);
            drugSubstance.addBrandName(drugName);
            drugMap.put(drugName, drugSubstance);
        }
    }

    private DrugSubstance getDrugSubstance(String drugName) {
        return drugMap.get(drugName);
    }
}