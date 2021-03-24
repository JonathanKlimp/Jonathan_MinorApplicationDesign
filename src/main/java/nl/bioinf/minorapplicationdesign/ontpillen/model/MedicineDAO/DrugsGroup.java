package nl.bioinf.minorapplicationdesign.ontpillen.model.MedicineDAO;

import java.util.List;

public class DrugsGroup extends Drug{
    List<Drug> children;

    public List<Drug> getChildren() {
        return children;
    }

    public void setChildren(List<Drug> children) {
        this.children = children;
    }
}
