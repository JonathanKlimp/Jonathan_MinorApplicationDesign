package nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Larissa Bouwknegt, Jonathan Klimp, Naomi Hindriks
 */
public class DrugSubstance extends Drug {
    private List<String> brandNames = new ArrayList<>();
    private String description;
    private List<String> sideEffects;
    private List<String> useIndications;
    private List<StopIndication>  stopIndications;

    public DrugSubstance(String name) {
        super(name);
    }

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
        return this.name;
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
