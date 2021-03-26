package nl.bioinf.minorapplicationdesign.ontpillen.model.MedicineDAO;

import java.util.List;
import java.util.Set;

public interface DrugsDao {
    Drug getDrugByName(String drugName);
    Set<String> getListOfDrugs();
    List<Drug> listDrugsRecursive();
    void addDrugSubstance(String drugName);
    void addDrugsGroup(String drugGroupName, List<String> drugsInGroup);
}
