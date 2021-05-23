package nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.side_effects;

import java.util.*;

/**
 * A class to store the information about side effects. It has a list that stores the SideEffectContent for patients
 * and a list that stores the SideEffectContent for practitioners content can be added to these lists and the content
 * of these lists can be returned in unmodifiable form
 * @author Naomi Hindriks
 */
public class SideEffects {
    private List<SideEffectContent> sideEffectContentsPatient = new ArrayList<>();
    private List<SideEffectContent> sideEffectContentPractitioner = new ArrayList<>();

    public void addSideEffectPatient(SideEffectContent sideEffectContent) {
        this.getSideEffectsPatient().add(sideEffectContent);
    }

    public void addSideEffectPractitioner(SideEffectContent sideEffectContent) {
        this.getSideEffectsPractitioner().add(sideEffectContent);
    }

    public List<SideEffectContent> getSideEffectsPatient() {
        return Collections.unmodifiableList(this.sideEffectContentsPatient);
    }

    public List<SideEffectContent> getSideEffectsPractitioner() {
        return Collections.unmodifiableList(this.sideEffectContentPractitioner);
    }
}
