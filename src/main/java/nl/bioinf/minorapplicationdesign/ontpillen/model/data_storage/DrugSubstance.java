package nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage;

import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content.UseIndication;

import java.util.*;

/**
 * DrugSubstance is a child class of abstract class Drug
 * It contains all necessary information about the drug substance
 * that needs to be presented on the website. Which are:
 * the brand names, the description, side effects, use and stop indications
 * and interactions with other drugs.
 * @author Larissa Bouwknegt, Jonathan Klimp, Naomi Hindriks
 */
public class DrugSubstance extends Drug {
    private List<String> brandNames = new ArrayList<>();
    private List<String> description = new ArrayList<>();
    private List<UseIndication> useIndications = new ArrayList<>();
    private List<StopIndication>  stopIndications;
    private List<String> interactions;

    public DrugSubstance(String name) {
        super(name);
    }

    // TODO add javadoc: Description needs to be changed. There no is no option to add description for patient or psychiatrist
    // maybe a hashmap with patient, psychiatrist with each having a list with their description
    public void setDescription(List<String> Description){
        this.description = Description;
    }

    public void addBrandName(String brandName){
        this.brandNames.add(brandName);
    }

    public void addUseIndication(UseIndication useIndication){
        this.useIndications.add(useIndication);
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
        return Collections.unmodifiableList(brandNames);
    }

    // TODO add javadoc: what is in the list that is going to be returned?
    public List<String> getDescription() {
        return Collections.unmodifiableList(this.description);
    }

    public List<UseIndication> getUseIndications() {
        return Collections.unmodifiableList(useIndications);
    }

    public List<StopIndication> getStopIndications() {
        return Collections.unmodifiableList(stopIndications);
    }

    public List<String> getInteractions() {
        return Collections.unmodifiableList(interactions);
    }

    public void setInteractions(List<String> interactions) {
        this.interactions = interactions;
    }
}
