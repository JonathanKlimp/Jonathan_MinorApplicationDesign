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


    @Override
    public void addDrug(Drug drug) {
        if(drugInDrugDao(drug)){
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
        for(String drugName : this.drugSubstances) {
            returnList.add((DrugSubstance) this.allDrugs.get(drugName));
        }
        return returnList;
    }

    @Override
    public List<DrugGroup> getMainDrugGroups() {
        List<DrugGroup> returnList = new ArrayList<>();
        for(String drugName : this.drugSubstances) {
            returnList.add((DrugGroup) this.allDrugs.get(drugName));
        }
        return returnList;
    }

    private boolean drugInDrugDao(Drug drug) throws IllegalArgumentException {
        return allDrugs.containsKey(drug.name);
    }

    public void removeAllDrugs() {
        drugSubstances.clear();
        mainDrugGroups.clear();
        allDrugs.clear();
    }
}