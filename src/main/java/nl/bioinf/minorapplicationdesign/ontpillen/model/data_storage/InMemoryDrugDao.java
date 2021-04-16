package nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryDrugDao implements DrugDao {
    static private Map<String, DrugSubstance> drugSubstances = new HashMap();
    static private Map<String, DrugGroup> mainDrugGroups = new HashMap();
    static private Map<String, Drug> allDrugs = new HashMap<>();


    @Override
    public void addDrug(Drug drug) {
        if(drugInDrugDao(drug)){
            throw new IllegalArgumentException("Cannot add drug that already exists");
        }
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
    public List<Drug> getDrugSubstances() {
        return new ArrayList<>(drugSubstances.values());
    }

    @Override
    public List<Drug> getMainDrugGroups() {
        return new ArrayList<>(mainDrugGroups.values());
    }

    private boolean drugInDrugDao(Drug drug) throws IllegalArgumentException {
        return allDrugs.containsKey(drug.name);
    }
}