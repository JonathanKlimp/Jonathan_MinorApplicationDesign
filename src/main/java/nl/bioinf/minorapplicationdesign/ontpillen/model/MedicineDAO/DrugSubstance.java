package nl.bioinf.minorapplicationdesign.ontpillen.model.MedicineDAO;

import java.util.ArrayList;
import java.util.List;

//TODO needs to be private
public class DrugSubstance extends Drug {
    String name;
    List<String> brandNames = new ArrayList<>();
    String description;
    List<String> sideEffects;
    List<String> useIndications;
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

    public void setUseIndications(List<String> useIndications){
        this.useIndications = useIndications;
    }

    public void setStopIndications(List<StopIndication> stopIndications){
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

    public String getDescription() {
        return description;
    }

    public List<String> getSideEffects() {
        return sideEffects;
    }

    public List<String> getUseIndications() {
        return useIndications;
    }

    public List<StopIndication> getStopIndications() {
        return stopIndications;
    }
}
