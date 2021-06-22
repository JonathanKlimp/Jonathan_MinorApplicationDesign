package nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content;

import java.util.*;

/**
 * A class to store the information about side effects. It has a map that stores the SideEffectContent for patients
 * and a map that stores the SideEffectContent for practitioners content can be added to these maps and the values
 * of these maps can be returned in a list.
 * @author Naomi Hindriks
 */
public class SideEffects {
    private Map<String, Content> sideEffectContentsPatient = new HashMap<>();
    private Map<String, Content> sideEffectContentPractitioner = new HashMap<>();

    public void addSideEffectPatient(String key, Content sideEffectContent) {
        this.sideEffectContentsPatient.put(key, sideEffectContent);
    }

    public void addSideEffectPractitioner(String key, Content sideEffectContent) {
        this.sideEffectContentPractitioner.put(key, sideEffectContent);
    }

    public List<Content> getSideEffectsPatient() {
        return new ArrayList<>(this.sideEffectContentsPatient.values());
    }

    public List<Content> getSideEffectsPractitioner() {
        return new ArrayList<>(this.sideEffectContentPractitioner.values());
    }
}
