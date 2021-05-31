package nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content;

import java.util.List;

public interface Content {

    void setContentTitle(String contentTitle);

    String getContentTitle();

    String getContentClass();

    Content getParent();

    void setParent(ContentNode parent);

    int getId();

    void setId(int id);
}
