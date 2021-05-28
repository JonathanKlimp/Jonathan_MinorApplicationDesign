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
    private Map<String, List<String>> description = new HashMap<>() {{
        put("patient", new ArrayList<>());
        put("psychiatrist", new ArrayList<>());
    }};
    private Map<String, List<String>> sideEffects = new HashMap<>() {{
        put("patient", new ArrayList<>());
        put("psychiatrist", new ArrayList<>());
    }};
    private Map<String, List<String>> interactions = new HashMap<>() {{
        put("patient", new ArrayList<>());
        put("psychiatrist", new ArrayList<>());
    }};
    private List<UseIndication> useIndications = new ArrayList<>();
    private List<String>  stopIndications;


    public DrugSubstance(String name) {
        super(name);
    }

    public void setDescriptionPatient(List<String> descriptionPatient) {
        this.description.put("patient", descriptionPatient);
    }

    public void setDescriptionPsychiatrist(List<String> descriptionPsychiatrist) {
        this.description.put("psychiatrist", descriptionPsychiatrist);
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

    public void addUseIndication(UseIndication useIndication){
        this.useIndications.add(useIndication);
    }

    public void setStopIndications(List<String> stopIndications){
        this.stopIndications = stopIndications;
    }

    public void setInteractionsPatient(List<String> interactionsPatient) {
        this.interactions.put("patient", interactionsPatient);
    }

    public void setInteractionsPsychiatrist(List<String> interactionsPsychiatrist) {
        this.interactions.put("psychiatrist", interactionsPsychiatrist);
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
    public List<String> getDescriptionPatient() {
        return Collections.unmodifiableList(description.get("patient"));
    }

    public List<String> getDescriptionPsychiatrist() {
        return Collections.unmodifiableList(description.get("psychiatrist"));
    }

    public List<String> getSideEffectsPatient() {
        return Collections.unmodifiableList(sideEffects.get("patient"));
    }

    public List<String> getSideEffectsPsychiatrist() {
        return Collections.unmodifiableList(sideEffects.get("psychiatrist"));
    }

    public Map<String, List<String>> getAllSideEffects() {
        return Collections.unmodifiableMap(sideEffects);
    }

    public List<UseIndication> getUseIndications() {
        return Collections.unmodifiableList(useIndications);
    }

    public List<String> getStopIndications() {
        return Collections.unmodifiableList(stopIndications);
    }

    public List<String> getInteractionsPatient() {
        return Collections.unmodifiableList(interactions.get("patient"));
    }

    public List<String> getInteractionsPsychiatrist() {
        return Collections.unmodifiableList(interactions.get("psychiatrist"));
    }
}
