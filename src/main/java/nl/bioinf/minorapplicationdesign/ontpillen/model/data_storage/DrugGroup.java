package nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * DrugGroup is the child class of abstract class Drug.
 * The class contains the name of the drug group and the
 * drugs that belong to the drug group as children.
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
