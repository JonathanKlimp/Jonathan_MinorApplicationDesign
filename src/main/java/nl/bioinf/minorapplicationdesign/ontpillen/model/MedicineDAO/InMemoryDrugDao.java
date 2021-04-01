package nl.bioinf.minorapplicationdesign.ontpillen.model.MedicineDAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryDrugDao implements DrugDao {
    static private Map<String, DrugSubstance> drugSubstances = new HashMap();
    static private Map<String, DrugGroup> mainDrugGroups = new HashMap();
    static private Map<String, Drug> allDrugs = new HashMap<>();

//    private static InMemoryDrugDao informationStorage;
//
//    private InMemoryDrugDao() {}
//
//    public static InMemoryDrugDao getInstance() {
//        if (informationStorage == null)
//            informationStorage = new InMemoryDrugDao();
//        return informationStorage;
//    }

    @Override
    public void addDrug(Drug drug) {
        allDrugs.put(drug.getName(), drug);
        if (drug instanceof DrugSubstance) {
            drugSubstances.put(drug.name, (DrugSubstance) drug);
        }
        else if (drug instanceof DrugGroup && drug.getParent() == null) {
            mainDrugGroups.put(drug.getName(), (DrugGroup) drug);
        }
    }

    @Override
    public Drug getDrugByName(String drugName) {
        if (!allDrugs.containsKey(drugName)) {
            throw new IllegalArgumentException(drugName + "does not exist.");
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
    public List<Drug> getDrugSubstances() {
        return new ArrayList<>(drugSubstances.values());
    }

    @Override
    public List<Drug> getMainDrugGroups() {
        return new ArrayList<>(mainDrugGroups.values());
    }


    /**
     * This method adds a new drugGroup to the inMemoryDrugDao given a drugGroup.
     * It will check if the drugGroup already exists in the in memory storage.
     * If it does not exist it will add it. It will add the medicines of that group to the class DrugGroup
     * @param drugGroupName new drug group to be added
     * @param drugsInGroup List of abstract Drug objects with all drugs from the DrugGroup
     */
//    @Override
//    public void addDrugsGroup(String drugGroupName, List<String> drugsInGroup) {
//        if(!drugMap.containsKey(drugGroupName)){
//            List<Drug> newDrugsInGroup = new ArrayList<>();
//            DrugGroup drugsGroup = new DrugGroup();
//            for(String medicine : drugsInGroup){
//                addDrugSubstance(medicine);
//                newDrugsInGroup.add(getDrugByName(medicine));
//            }
//            drugsGroup.setChildren(newDrugsInGroup);
//            mainDrugGroups.put(drugGroupName, drugsGroup);
//        }
//    }


    /**
     * This method adds a new DrugSubstance to the inMemoryDrugDao given a drug.
     * It will check if the drugSubstance already exists in the in memory storage.
     * If it does not exists it will add it to the storage and add it to the drugMap.
     * @param drugName new Drug to be added.
     */
//    @Override
//    public void addDrugSubstance(String drugName) {
//        if(!drugMap.containsKey(drugName)){
//            DrugSubstance drugSubstance = new DrugSubstance();
//            drugSubstance.setName(drugName);
//            drugSubstance.addBrandName(drugName);
//            drugMap.put(drugName, drugSubstance);
//        }
//    }

}