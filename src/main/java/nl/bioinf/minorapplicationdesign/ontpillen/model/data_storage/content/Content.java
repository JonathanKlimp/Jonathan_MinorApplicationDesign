package nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content;

import java.util.Map;

/**
 * Content is the interface for the Content classes which are used to save the information
 * about the side effects for each drug.
 * @author Naomi Hindriks and Jonathan Klimp
*/
public interface Content {

    /**
     * Method that sets the title of the content class.
     * @param contentTitle String with the desired title.
     */
    void setContentTitle(String contentTitle);

    /**
     * Method that returns the content title
     * @return String of the content title
     */
    String getContentTitle();

    /**
     * Method that returns the parent of the Content object
     * @return ContentNode object
     */
    ContentNode getParent();

    /**
     * Method that sets the parent of the Content object
     * @param parent ContentNode object
     */
    void setParent(ContentNode parent);

    /**
     * Method that sets the content type of the parsed information
     * @param contentType String content type (defined in the enum ContentType)
     */
    void setContentType(String contentType);

    /**
     * Method that returns the content type of the object
     * @return String content type
     */
    String getContentType();

    /**
     * Method that returns the id of the object
     * @return int id
     */
    int getId();

    /**
     * Method that sets the id
     * @param id integer id
     */
    void setId(int id);

    /**
     * // TODO add javadoc
     * @param attributeName
     * @param attributeValue
     */
    void addAttribute(String attributeName, String attributeValue);

    /** // TODO add javadoc
     * @return
     */
    Map<String, String> getAttributes();
}
