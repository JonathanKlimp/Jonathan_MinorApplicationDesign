package nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 *
 * @author Larissa Bouwknegt, Jonathan Klimp
 */
public class DrugGroup extends Drug{
    private List<Drug> children = new ArrayList<>();

    public DrugGroup(String name) {
        super(name);
    }

    /**
     * Method returns all drug substances from the current drug group
     * @return list of drug substances
     */
    public List<Drug> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public void addChild(Drug child) {
        children.add(child);
    }
}
