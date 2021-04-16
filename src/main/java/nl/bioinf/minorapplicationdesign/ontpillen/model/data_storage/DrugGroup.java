package nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DrugGroup extends Drug{
    private List<Drug> children = new ArrayList<>();

    public DrugGroup(String name) {
        super(name);
    }

    public List<Drug> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public void addChild(Drug child) {
        children.add(child);
    }
}
