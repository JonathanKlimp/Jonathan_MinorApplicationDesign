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
    public void setContent() {
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
        assertTrue(contentNode.getContent() != null);

//        Test if the content that has been added is the correct content
        for (Content content: contentNode.getContent()) {
            ContentLeaf contentAsLeaf = (ContentLeaf) content;
            assertEquals("PARAGRAPH",  contentAsLeaf.contentType.name());
            int i = 0;
            for (String contentOfLeaf : contentAsLeaf.getContent()) {
                i += 1;
                assertEquals("test" + Integer.toString(i), contentOfLeaf);
            }
        };
    }

    @Test
    void addContent_addSingleContent() {
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

//        Test if the content that has been added is the correct content
        ContentLeaf contentAsLeaf = (ContentLeaf) contentNode.getContent().get(0);
        assertEquals("PARAGRAPH",  contentAsLeaf.contentType.name());
        int i = 0;
        for (String contentOfLeaf : contentAsLeaf.getContent()) {
            i += 1;
            assertEquals("test" + Integer.toString(i), contentOfLeaf);
        }
    }

    @Test
    void addContent_addMultipleContents() {
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

//        Test if content has been added
        assertEquals(2, contentNode.getContent().size());

//        Test if the content that has been added is the correct content
        ContentLeaf contentAsLeaf = (ContentLeaf) contentNode.getContent().get(0);
        assertEquals("PARAGRAPH",  contentAsLeaf.contentType.name());
        int i = 0;
        for (String contentOfLeaf : contentAsLeaf.getContent()) {
            i += 1;
            assertEquals("test" + Integer.toString(i), contentOfLeaf);
        }

        contentAsLeaf = (ContentLeaf) contentNode.getContent().get(1);
        assertEquals("LIST",  contentAsLeaf.contentType.name());
        i = 0;
        for (String contentOfLeaf : contentAsLeaf.getContent()) {
            i += 1;
            assertEquals("test" + Integer.toString(i), contentOfLeaf);
        }
    }

    @Test
    void getContent_tryingTOModifyList() {
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

//        Test if an UnsupportedOperationException is thrown when removing an item of the list
        Assertions.assertThrows(UnsupportedOperationException.class, () -> {
            content.remove(contentToAdd);
        });
    }
}