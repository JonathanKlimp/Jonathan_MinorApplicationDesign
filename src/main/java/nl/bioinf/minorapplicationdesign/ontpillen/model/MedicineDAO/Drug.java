package nl.bioinf.minorapplicationdesign.ontpillen.model.MedicineDAO;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Drug {
    int ID;
    String name;
    Drug parent;
}