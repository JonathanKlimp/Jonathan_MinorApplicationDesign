package nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Naomi Hindriks
 */ //TODO add javadoc to ContentNode
public class ContentNode implements Content {
    protected ContentType contentType;
    private String contentTitle;
    private List<Content> content = new ArrayList<>();
    private ContentNode parent;
    private int id;
    private Map<String, String> attributes = new HashMap<>();

    {
        attributes.put("colspan", "1");
        attributes.put("rowspan", "1");
    }

    public ContentNode() {
        this(ContentType.PARAGRAPH);
    }

    private ContentNode(ContentType contentType) {
        this.contentType = contentType;
    }

    public void setContent(List<Content> contentList) {
        for (Content content : contentList) {
            content.setParent(this);
        }
        this.content = contentList;
    }

    public void addContent(Content newContent){
        newContent.setParent(this);
        this.content.add(newContent);
    }

    @Override
    public void setContentType(String contentType) {
        if (!Arrays.stream(ContentType.values()).anyMatch(str -> str.name().equals(contentType))) {
            List<String> newList = Arrays.stream(ContentType.values()).map(Enum::name).collect(Collectors.toList());
            String message = "Given contentType (" + contentType + ") is not any of the allowed values, the allowed values are: " + String.join(", ", newList);
            throw new IllegalArgumentException(message);
        }
        this.contentType = ContentType.valueOf(contentType);
    }

    @Override
    public String getContentType() {
        return contentType.name();
    }

    public List<Content> getContent() {
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
