package nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage;

import java.util.Collections;
import java.util.List;

/**
 * Class UseIndication that contains all information about the different use indications.
 * This information is the name of the use indication and the drugs linked to that indication.
 * @author Larissa Bouwknegt en Jonathan Klimp
 */
public class UseIndication {
    private String name;
    private List<Drug> drugs;

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
}
