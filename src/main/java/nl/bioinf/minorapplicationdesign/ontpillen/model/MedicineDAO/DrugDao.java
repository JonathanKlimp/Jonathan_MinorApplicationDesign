package nl.bioinf.minorapplicationdesign.ontpillen.model.MedicineDAO;

import java.util.List;
import java.util.Set;

public interface DrugDao {

    void addDrug(Drug drug);

    Drug getDrugByName(String drugName);

    List<Drug> getDrugSubstances();

    List<Drug> getMainDrugGroups();

    public List<Drug> getAllDrugs();

    public List<String> getAllDrugNames();
}
