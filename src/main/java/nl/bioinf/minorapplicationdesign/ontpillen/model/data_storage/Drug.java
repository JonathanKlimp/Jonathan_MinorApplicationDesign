package nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage;


public abstract class Drug {
    protected String name;
    protected Drug parent;

    public Drug(String name) {
        this.setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drug getParent() {
        return parent;
    }

    public void setParent(Drug parent) {
        this.parent = parent;
    }
}