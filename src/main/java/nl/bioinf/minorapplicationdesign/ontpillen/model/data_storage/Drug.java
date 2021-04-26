package nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage;


import org.jsoup.helper.Validate;


/**
 * @author Larissa Bouwknegt, Jonathan Klimp, Naomi Hindriks
 */
public abstract class Drug {
    protected String name;
    protected Drug parent;

    public Drug(String name) {
        Validate.notNull(name, "name can't be null");
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