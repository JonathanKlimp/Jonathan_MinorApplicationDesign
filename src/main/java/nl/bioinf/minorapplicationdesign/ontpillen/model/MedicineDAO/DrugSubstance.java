package nl.bioinf.minorapplicationdesign.ontpillen.model.MedicineDAO;

import java.util.List;

public class DrugSubstance extends Drug {
    String name;
    List<String> brandNames;
    String description;
    List<String> sideEffects;
    List<UseIndication> useIndications;
    List<StopIndication>  stopIndications;

    public void setDescription(String Description){
        this.description = Description;
    }

    public void addBrandName(String brandName){
        this.brandNames.add(brandName);
    }

    public void setSideEffects(List<String> sideEffects){
        this.sideEffects = sideEffects;
    }

    public void setUseIndications(List<UseIndication> useIndications){ // or the class Useindication?
        this.useIndications = useIndications;
    }

    public void setStopIndications(List<StopIndication> stopIndications){ // or the class Stopindication?
        this.stopIndications = stopIndications;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getBrandNames() {
        return brandNames;
    }

    public void addBrandNames(String brandNames) {
        this.brandNames.add(brandNames);
    }

    public String getDescription() {
        return description;
    }

    public List<String> getSideEffects() {
        return sideEffects;
    }

    public List<UseIndication> getUseIndications() {
        return useIndications;
    }

    public List<StopIndication> getStopIndications() {
        return stopIndications;
    }
}
