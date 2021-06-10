package nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage;

import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content.UseIndication;
import java.util.*;

/**
 * DrugSubstance is a child class of abstract class Drug
 * It contains all necessary information about the drug substance
 * that needs to be presented on the website. Which are:
 * the brand names, the description, use and stop indications
 * and interactions with other drugs.
 * @author Larissa Bouwknegt, Jonathan Klimp, Naomi Hindriks
 */
public class DrugSubstance extends Drug {
    private List<String> brandNames = new ArrayList<>();
//    private Map<String, List<String>> description = Map.of("patient", new ArrayList<>(), "practitioner", new ArrayList<>());
//    private Map<String, List<String>> interactions = Map.of("patient", new ArrayList<>(), "practitioner", new ArrayList<>());7
    private Map<String, List<String>> description = new HashMap<>() {{
        put("patient", new ArrayList<>());
        put("practitioner", new ArrayList<>());
    }};
    private Map<String, List<String>> interactions = new HashMap<>() {{
        put("patient", new ArrayList<>());
        put("practitioner", new ArrayList<>());
    }};
    private List<UseIndication> useIndications = new ArrayList<>();
    private List<String>  stopIndications = new ArrayList<>();


    public DrugSubstance(String name) {
        super(name);
    }

    public void setDescriptionPatient(List<String> descriptionPatient) {
        this.description.get("patient").clear();
        this.description.get("patient").addAll(descriptionPatient);
    }

    public void setDescriptionPractitioner(List<String> descriptionPsychiatrist) {
        this.description.get("practitioner").clear();
        this.description.get("practitioner").addAll(descriptionPsychiatrist);
    }

    public void addBrandName(String brandName){
        this.brandNames.add(brandName);
    }

    public void addUseIndication(UseIndication useIndication){
        this.useIndications.add(useIndication);
    }

    public void setStopIndications(List<String> stopIndications){
        this.stopIndications = stopIndications;
    }

    public void setInteractionsPatient(List<String> interactionsPatient) {
        this.interactions.get("patient").clear();
        this.interactions.get("patient").addAll(interactionsPatient);
    }

    public void setInteractionsPractitioner(List<String> interactionsPsychiatrist) {
        this.interactions.get("practitioner").clear();
        this.interactions.get("practitioner").addAll(interactionsPsychiatrist);
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

    public List<String> getDescriptionPatient() {
        return Collections.unmodifiableList(description.get("patient"));
    }

    public List<String> getDescriptionPractitioner() {
        return Collections.unmodifiableList(description.get("practitioner"));
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

    public List<String> getInteractionsPractitioner() {
        return Collections.unmodifiableList(interactions.get("practitioner"));
    }
}