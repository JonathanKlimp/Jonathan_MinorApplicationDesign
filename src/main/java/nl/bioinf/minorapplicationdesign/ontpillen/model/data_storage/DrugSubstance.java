package nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage;

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
    private List<String> description;
    private Map<String, List<String>> sideEffects = new HashMap<>() {{
        put("patient", new ArrayList<>());
        put("psychiatrist", new ArrayList<>());
    }};
    private List<String> useIndications;
    private List<StopIndication>  stopIndications;
    private List<String> interactions;

    public DrugSubstance(String name) {
        super(name);
    }

    // Description needs to be changed. There no is no option to add description for patient or psychiatrist
    // maybe a hashmap with patient, psychiatrist with each having a list with their description
    public void setDescription(List<String> Description){
        this.description = Description;
    }

    public void addBrandName(String brandName){
        this.brandNames.add(brandName);
    }

    public void setSideEffectsPatient(List<String> sideEffectsPatient) {
        this.sideEffects.put("patient", sideEffectsPatient);
    }

    public void setSideEffectsPsychiatrist(List<String> sideEffectsPatient) {
        this.sideEffects.put("psychiatrist", sideEffectsPatient);
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

    /**
     * Method that returns list with all brandnames of the drug substance.
     * @return List with brandnames
     */
    public List<String> getBrandNames() {
        return Collections.unmodifiableList(brandNames);
    }

    // TODO add javadoc
    public List<String> getDescription() {
        return Collections.unmodifiableList(description);
    }

    public List<String> getSideEffectsPatient() {
        return Collections.unmodifiableList(sideEffects.get("patient"));
    }

    public List<String> getSideEffectsPsychiatrist() {
        return Collections.unmodifiableList(sideEffects.get("psychiatrist"));
    }

    public HashMap<String, List<String>> getAllSideEffects() {
        return (HashMap<String, List<String>>) Collections.unmodifiableMap(sideEffects);
    }

    public List<String> getUseIndications() {
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
