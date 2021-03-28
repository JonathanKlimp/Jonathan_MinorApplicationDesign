package nl.bioinf.minorapplicationdesign.ontpillen.model.MedicineDAO;

import java.util.*;

public class InMemoryDrugDao implements DrugsDao {
    static Map<String, DrugSubstance> drugMap = new HashMap();
    static Map<String, DrugsGroup> drugsGroupMap = new HashMap();

    private static InMemoryDrugDao informationStorage;

    private InMemoryDrugDao() {}

    public static InMemoryDrugDao getInstance() {
        if (informationStorage == null)
            informationStorage = new InMemoryDrugDao();
        return informationStorage;
    }

    @Override
    public Drug getDrugByName(String drugName) {
        return getDrugSubstance(drugName);
    }

    @Override
    public Set<String> getListOfDrugs() {
        return drugMap.keySet();
    }

    public Set<String> getListOfDrugsGroups() {return drugsGroupMap.keySet();}

    @Override
    public List<Drug> listDrugsRecursive() {
        return null;
    }

    /**
     * This method adds a new drugGroup to the inMemoryDrugDao given a drugGroup.
     * It will check if the drugGroup already exists in the in memory storage.
     * If it does not exist it will add it. It will add the medicines of that group to the class DrugGroup
     * @param drugGroupName new drug group to be added
     * @param drugsInGroup List of abstract Drug objects with all drugs from the DrugGroup
     */
    @Override
    public void addDrugsGroup(String drugGroupName, List<String> drugsInGroup) {
        if(!drugMap.containsKey(drugGroupName)){
            List<Drug> newDrugsInGroup = new ArrayList<>();
            DrugsGroup drugsGroup = new DrugsGroup();
            for(String medicine : drugsInGroup){
                addDrugSubstance(medicine);
                newDrugsInGroup.add(getDrugByName(medicine));
            }
            drugsGroup.setChildren(newDrugsInGroup);
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