package nl.bioinf.minorapplicationdesign.ontpillen.model.MedicineDAO;

import java.util.Collections;
import java.util.List;

public class DrugSubstance extends Drug {
    String name;
    List<String> brandNames;
    String description;
    List<String> sideEffects;
    List<UseIndication> useIndications;
    List<StopIndication>  stopIndications;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getBrandNames() {
        return brandNames;
    }

    public void setBrandNames(String brandNames) {
        this.brandNames = Collections.singletonList(brandNames);
    }

    public void addBrandNames(String brandNames) {
        this.brandNames.add(brandNames);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getSideEffects() {
        return sideEffects;
    }

    public void setSideEffects(List<String> sideEffects) {
        this.sideEffects = sideEffects;
    }

    public List<UseIndication> getUseIndications() {
        return useIndications;
    }

    public void setUseIndications(List<UseIndication> useIndications) {
        this.useIndications = useIndications;
    }

    public List<StopIndication> getStopIndications() {
        return stopIndications;
    }

    public void setStopIndications(List<StopIndication> stopIndications) {
        this.stopIndications = stopIndications;
    }
}
