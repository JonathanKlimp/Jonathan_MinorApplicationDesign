package nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage;

import java.util.ArrayList;
import java.util.List;

//TODO needs to be private
public class DrugSubstance extends Drug {
    private List<String> brandNames = new ArrayList<>();
    private List<String> description;
    private List<String> sideEffects;
    private List<String> useIndications;
    private List<nl.bioinf.minorapplicationdesign.ontpillen.model.MedicineDAO.StopIndication>  stopIndications;

    public DrugSubstance(String name) {
        super(name);
    }

    public void setDescription(List<String> Description){
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

    public void setStopIndications(List<nl.bioinf.minorapplicationdesign.ontpillen.model.MedicineDAO.StopIndication> stopIndications){
        this.stopIndications = stopIndications;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getBrandNames() {
        return brandNames;
    }

    public List<String> getDescription() {
        return description;
    }

    public List<String> getSideEffects() {
        return sideEffects;
    }

    public List<String> getUseIndications() {
        return useIndications;
    }

    public List<nl.bioinf.minorapplicationdesign.ontpillen.model.MedicineDAO.StopIndication> getStopIndications() {
        return stopIndications;
    }
}
