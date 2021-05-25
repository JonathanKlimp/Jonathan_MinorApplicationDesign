package nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content;

import java.util.List;

public interface Content {

    void setContentTitle(String contentTitle);

    String getContentTitle();

    String getContentClass();

    int getId();

    void setId(int id);
}
