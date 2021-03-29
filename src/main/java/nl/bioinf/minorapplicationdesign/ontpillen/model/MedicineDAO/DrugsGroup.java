package nl.bioinf.minorapplicationdesign.ontpillen.model.MedicineDAO;

import java.util.List;

//TODO Private instance variable, get children needs a modifiable list, set children kan beter add zijn
public class DrugsGroup extends Drug{
    List<Drug> children;

    public List<Drug> getChildren() {
        return children;
    }

    public void setChildren(List<Drug> children) {
        this.children = children;
    }
}
