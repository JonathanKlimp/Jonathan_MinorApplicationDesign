package nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content;

import java.util.Map;

/**
 * @author Naomi Hindiks
 */
public interface Content {

    void setContentTitle(String contentTitle);

    String getContentTitle();

    Content getParent();

    void setParent(ContentNode parent);

    void setContentType(String contentType);

    int getId();

    void setId(int id);

    void addAttribute(String attributeName, String attributeValue);

    Map<String, String> getAttributes();
}
