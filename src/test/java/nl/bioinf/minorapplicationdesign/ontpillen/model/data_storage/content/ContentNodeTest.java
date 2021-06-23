package nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Naomi Hindriks
 */
@SpringBootTest
class ContentNodeTest {

    @Test
    public void setContent_contentNotNull() {
//        Create ContentNode object
        ContentNode contentNode = new ContentNode();
//        Create new content to be added to ContentNode object
        ContentLeaf contentToAdd = new ContentLeaf();
        contentToAdd.setContent(new ArrayList<>(Arrays.asList("test1", "test2", "test3")));
        contentToAdd.setContentType("PARAGRAPH");
        List<Content> contentToAddInList = new ArrayList<>() {{
            add(contentToAdd);
        }};
//        set the created content to the ContentNode object
        contentNode.setContent(contentToAddInList);

//        Test if content has been added
        assertNotNull(contentNode.getContent());

    }

    @Test
    public void setContent_CorrectContentType() {
//        Create ContentNode object
        ContentNode contentNode = new ContentNode();
//        Create new content to be added to ContentNode object
        ContentLeaf contentToAdd = new ContentLeaf();
        contentToAdd.setContent(new ArrayList<>(Arrays.asList("test1", "test2", "test3")));
        contentToAdd.setContentType("PARAGRAPH");
        List<Content> contentToAddInList = new ArrayList<>() {{
            add(contentToAdd);
        }};
//        set the created content to the ContentNode object
        contentNode.setContent(contentToAddInList);

//        Test if the content that has been added is the correct content
        for (Content content: contentNode.getContent()) {
            ContentLeaf contentAsLeaf = (ContentLeaf) content;
            assertEquals("PARAGRAPH",  contentAsLeaf.contentType.name());
        }
    }

    @Test
    public void setContent_CorrectContentValue() {
//        Create ContentNode object
        ContentNode contentNode = new ContentNode();
//        Create new content to be added to ContentNode object
        ContentLeaf contentToAdd = new ContentLeaf();
        contentToAdd.setContent(new ArrayList<>(Arrays.asList("test1", "test2", "test3")));
        contentToAdd.setContentType("PARAGRAPH");
        List<Content> contentToAddInList = new ArrayList<>() {{
            add(contentToAdd);
        }};
//        set the created content to the ContentNode object
        contentNode.setContent(contentToAddInList);

//        Test if the content that has been added is the correct content
        for (Content content: contentNode.getContent()) {
            ContentLeaf contentAsLeaf = (ContentLeaf) content;
            int i = 0;
            for (String contentOfLeaf : contentAsLeaf.getContent()) {
                i += 1;
                assertEquals("test" + Integer.toString(i), contentOfLeaf);
            }
        }
    }

    @Test
    void addContent_addSingleContent_contentAdded() {
//        Create ContentNode object
        ContentNode contentNode = new ContentNode();
//        Create new content to be added to ContentNode object
        ContentLeaf contentToAdd = new ContentLeaf();
        contentToAdd.setContent(new ArrayList<>(Arrays.asList("test1", "test2", "test3")));
        contentToAdd.setContentType("PARAGRAPH");
//        Add content to ContentNode object
        contentNode.addContent(contentToAdd);

//        Test if content has been added
        assertEquals(1, contentNode.getContent().size());
    }

    @Test
    void addContent_addSingleContent_correctContentType() {
//        Create ContentNode object
        ContentNode contentNode = new ContentNode();
//        Create new content to be added to ContentNode object
        ContentLeaf contentToAdd = new ContentLeaf();
        contentToAdd.setContent(new ArrayList<>(Arrays.asList("test1", "test2", "test3")));
        contentToAdd.setContentType("PARAGRAPH");
//        Add content to ContentNode object
        contentNode.addContent(contentToAdd);

//        Test if the content that has been added is the correct content
        ContentLeaf contentAsLeaf = (ContentLeaf) contentNode.getContent().get(0);
        assertEquals("PARAGRAPH",  contentAsLeaf.contentType.name());
    }

    @Test
    void addContent_addSingleContent_correctContentValue() {
//        Create ContentNode object
        ContentNode contentNode = new ContentNode();
//        Create new content to be added to ContentNode object
        ContentLeaf contentToAdd = new ContentLeaf();
        contentToAdd.setContent(new ArrayList<>(Arrays.asList("test1", "test2", "test3")));
        contentToAdd.setContentType("PARAGRAPH");
//        Add content to ContentNode object
        contentNode.addContent(contentToAdd);

        ContentLeaf contentAsLeaf = (ContentLeaf) contentNode.getContent().get(0);
        int i = 0;
        for (String contentOfLeaf : contentAsLeaf.getContent()) {
            i += 1;
            assertEquals("test" + Integer.toString(i), contentOfLeaf);
        }
    }

    /**
     * Test checks if adding multiple content doesnt overwrite the older ones.
     */
    @Test
    void addContent_addMultipleContents_CorrectContentTypes() {
//        Create ContentNode object
        ContentNode contentNode = new ContentNode();
//        Create new content to be added to ContentNode object
        ContentLeaf contentToAdd1 = new ContentLeaf();
        contentToAdd1.setContent(new ArrayList<>(Arrays.asList("test1", "test2", "test3")));
        contentToAdd1.setContentType("PARAGRAPH");

//        Create new content to be added to ContentNode object
        ContentLeaf contentToAdd2 = new ContentLeaf();
        contentToAdd2.setContent(new ArrayList<>(Arrays.asList("test1", "test2", "test3")));
        contentToAdd2.setContentType("LIST");

//        Add content to ContentNode object
        contentNode.addContent(contentToAdd1);
        contentNode.addContent(contentToAdd2);

//        Test if the content that has been added is the correct content and is not overwritten
        ContentLeaf contentAsLeaf = (ContentLeaf) contentNode.getContent().get(0);
        assertEquals("PARAGRAPH",  contentAsLeaf.contentType.name());
    }

    @Test
    void getContent_tryingToRemoveFromList() {
//        Create ContentNode object
        ContentNode contentNode = new ContentNode();
//        Create new content to be added to ContentNode object
        ContentLeaf contentToAdd = new ContentLeaf();
        contentToAdd.setContent(new ArrayList<>(Arrays.asList("test1", "test2", "test3")));
        contentToAdd.setContentType("PARAGRAPH");

//        Get the content of the ContentNode object
        List<Content> content = contentNode.getContent();

//        Test if an UnsupportedOperationException is thrown when removing an item of the list
        Assertions.assertThrows(UnsupportedOperationException.class, () -> {
            content.remove(contentToAdd);
        });
    }

    @Test
    void getContent_tryingToAddToList() {
//        Create ContentNode object
        ContentNode contentNode = new ContentNode();
//        Create new content to be added to ContentNode object
        ContentLeaf contentToAdd = new ContentLeaf();
        contentToAdd.setContent(new ArrayList<>(Arrays.asList("test1", "test2", "test3")));
        contentToAdd.setContentType("PARAGRAPH");

//        Get the content of the ContentNode object
        List<Content> content = contentNode.getContent();

//        Test if an UnsupportedOperationException is thrown when adding to the list
        Assertions.assertThrows(UnsupportedOperationException.class, () -> {
            content.add(new ContentLeaf());
        });
    }
}