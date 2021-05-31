package nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content;

import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.Drug;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Class UseIndication that contains all information about the different use indications.
 * This information is the name of the use indication and the drugs linked to that indication.
 * @author Larissa Bouwknegt en Jonathan Klimp
 */
public class UseIndication {
    private String name;
    private List<Drug> drugs;
    private Map<String, Content> indicationContentsPatient = new HashMap<>();
    private Map<String, Content> indicationContentPractitioner = new HashMap<>();

    public void setName(String useIndication) {
        this.name = useIndication;
    }

    public String getName() {return name;}

    public List<Drug> getDrugs() {
        return Collections.unmodifiableList(drugs);
    }

    public void setDrugs(List<Drug> drugs) {
        this.drugs = drugs;
    }

    public void addContentPatient(String key, Content sideEffectContent) {
        this.indicationContentsPatient.put(key, sideEffectContent);
    }

    public void addContentPractitioner(String key, Content sideEffectContent) {
        this.indicationContentPractitioner.put(key, sideEffectContent);
    }

    public List<Content> getContentPatient() {
        return new ArrayList<>(this.indicationContentsPatient.values());
    }

    public List<Content> getContentPractitioner() {
        return new ArrayList<>(this.indicationContentPractitioner.values());
    }
}