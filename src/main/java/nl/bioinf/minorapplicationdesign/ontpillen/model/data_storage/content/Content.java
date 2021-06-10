package nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content;

import java.util.Map;

/**
 * @author Naomi Hindriks
 */
public interface Content {

    void setContentTitle(String contentTitle);

    String getContentTitle();

    ContentNode getParent();

    void setParent(ContentNode parent);

    void setContentType(String contentType);

    String getContentType();

    int getId();

    void setId(int id);

    void addAttribute(String attributeName, String attributeValue);

    Map<String, String> getAttributes();
}
