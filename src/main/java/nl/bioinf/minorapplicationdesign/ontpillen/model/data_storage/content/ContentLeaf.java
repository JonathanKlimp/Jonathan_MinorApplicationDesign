package nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ContentLeaf implements Content {
    protected ContentType contentType;
    private String contentTitle;
    private List<String> content = new ArrayList<>();
    private ContentNode parent;
    private int id;

    public void setContentType(String contentType) {
        if (!Arrays.stream(ContentType.values()).anyMatch(str -> str.name().equals(contentType))) {
            List<String> newList = Arrays.stream(ContentType.values()).map(Enum::name).collect(Collectors.toList());
            String message = "contentType is not any of the allowed values, the allowed values are: " + String.join(", ", newList);
            throw new IllegalArgumentException(message);
        }
        this.contentType = ContentType.valueOf(contentType);
    }

    public void setContent(List<String> newContent) {
        this.content = newContent;
    }

    public void addContent(String newContent) {
        this.content.add(newContent);
    }

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
    public String getContentClass() {
        return null;
    }

    @Override
    public Content getParent() {
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
}