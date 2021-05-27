package nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContentNode implements Content {
    private String contentTitle;
    protected List<Content> content = new ArrayList<>();
    int id;

    public void setContent(List<Content> content) {
        this.content = content;
    }

    public void addContent(Content newContent){
        this.content.add(newContent);
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
    public String getContentClass() {
        return this.getClass().toString();
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
