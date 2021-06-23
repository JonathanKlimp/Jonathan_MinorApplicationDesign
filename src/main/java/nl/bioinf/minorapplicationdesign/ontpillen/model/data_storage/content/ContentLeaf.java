package nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Class contentLeaf implements the interface Content. This class is used to store the string values of the
 * content that is parsed from the websites. It has a parent which is the corresponding ContentNode class.
 * For each contentLeaf the contentType needs to be set. The options for this are stated in the enum ContentType.
 * @author Naomi Hindriks
 */
public class ContentLeaf implements Content {
    protected ContentType contentType;
    private String contentTitle;
    private List<String> content = new ArrayList<>();
    private ContentNode parent;
    private int id;
    private Map<String, String> attributes = new HashMap<>();


    public ContentLeaf() {
        this(ContentType.PARAGRAPH);
    }

    private ContentLeaf(ContentType contentType) {
        this.contentType = contentType;
    }

    /**
     * Method that sets the ContentType of the ContentLeaf.
     * @param contentType String content type (defined in the enum ContentType)
     * @throws IllegalArgumentException if the given content type isn't defined in the enum ContentType
     */
    @Override
    public void setContentType(String contentType) {
        if (isValidContent(contentType)) {
            List<String> newList = Arrays.stream(ContentType.values()).map(Enum::name).collect(Collectors.toList());
            String message = "contentType is not any of the allowed values, the allowed values are: " + String.join(", ", newList);
            throw new IllegalArgumentException(message);
        }
        this.contentType = ContentType.valueOf(contentType);
    }

    private boolean isValidContent(String contentType) {
        return !Arrays.stream(ContentType.values()).anyMatch(str -> str.name().equals(contentType));
    }

    public void setContent(List<String> newContent) {
        this.content = newContent;
    }

    public void addContent(String newContent) {
        this.content.add(newContent);
    }

    @Override
    public String getContentType() {
        return contentType.name();
    }

    public List<String> getContent() {
        return Collections.unmodifiableList(content);
    }

    @Override
    public void setContentTitle(String contentTitle) {
        this.contentTitle = contentTitle;
    }

    @Override
    public String getContentTitle() {
        return this.contentTitle;
    }

    @Override
    public ContentNode getParent() {
        return this.parent;
    }

    @Override
    public void setParent(ContentNode parent) {
        this.parent = parent;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public void addAttribute(String attributeName, String attributeValue) {
        this.attributes.put(attributeName, attributeValue);
    }

    @Override
    public Map<String, String> getAttributes() {
        return Collections.unmodifiableMap(this.attributes);
    }
}
