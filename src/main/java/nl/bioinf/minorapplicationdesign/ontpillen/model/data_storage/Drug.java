package nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage;


import org.jsoup.helper.Validate;


/**
 * Drug is the abstract base class for all drug types.
 * A Drug object contains the name of the drug and the parent.
 * Parent is used to maintain the structure of for example drug substances
 * which are part of a drug group. In that case the drugSubstance child classes
 * will their respective drug group as their parent.
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