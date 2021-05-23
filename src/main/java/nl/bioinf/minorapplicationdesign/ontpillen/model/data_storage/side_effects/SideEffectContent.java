package nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.side_effects;

import java.util.*;

/**
 * The SideEffectContent class is used to store content for the web page that holds information about side effects. It can
 * contain information that should be displayed as paragraphs or a list. It can have a title, but it doesn't need one.
 * Every SideEffectContent object will hold one piece of content about side effects, but is not necessarily limited to a
 * single side effect of a drug.
 * @author Naomi Hindriks
 */
public class SideEffectContent {
    private String Title;
    private List<Content> content = new ArrayList<>();

    public void setTitle(String title) {
        Title = title;
    }

    public String getTitle() {
        return Title;
    }

    public List<Content> getContent() {
        return Collections.unmodifiableList(content);
    }


    /**
     * @param contentTypeString String to indicate what content type is going to be stored (should be any of the values
     *                          found in ContentType: PARAGRAPH, LIST)
     * @param contentList A list of Strings to be stored as content.
     */
    public void addContent(String contentTypeString, List<String> contentList) {
        ContentType contentType = ContentType.valueOf(contentTypeString);
        Content newContent = new Content();
        newContent.setContentType(contentType);
        newContent.setContent(contentList);
        this.content.add(newContent);
    }

    /**
     * A class used to store the content and content type in an organized way.
     */
    protected class Content{
        protected ContentType contentType;
        protected List content = new ArrayList();

        public void setContentType(ContentType contentType) {
            this.contentType = contentType;
        }

        public void setContent(List content) {
            this.content = content;
        }
    }

    protected enum ContentType {
        PARAGRAPH,
        LIST
    }
}
