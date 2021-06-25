package nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

    private ContentNode myContentNode;
    private ContentLeaf myContentLeaf;

    @BeforeEach
    public void createContentNode() {
        this.myContentNode = new ContentNode();
        this.myContentLeaf = new ContentLeaf();
        myContentLeaf.setContent(new ArrayList<>(Arrays.asList("test1", "test2", "test3")));
        myContentLeaf.setContentType("PARAGRAPH");
    }

    @Test
    public void setContent_contentNotNull() {
        List<Content> contentToAddInList = new ArrayList<>() {{
            add(myContentLeaf);
        }};

        myContentNode.setContent(contentToAddInList);

        assertNotNull(myContentNode.getContent());
    }

    @Test
    public void setContent_CorrectContentType() {
        List<Content> contentToAddInList = new ArrayList<>() {{
            add(myContentLeaf);
        }};

        myContentNode.setContent(contentToAddInList);

        for (Content content: myContentNode.getContent()) {
            ContentLeaf contentAsLeaf = (ContentLeaf) content;
            assertEquals("PARAGRAPH",  contentAsLeaf.contentType.name());
        }
    }

    @Test
    public void setContent_CorrectContentValue() {
        List<Content> contentToAddInList = new ArrayList<>() {{
            add(myContentLeaf);
        }};
        myContentNode.setContent(contentToAddInList);

        for (Content content: myContentNode.getContent()) {
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
        myContentNode.addContent(myContentLeaf);

//        Test if content has been added
        assertEquals(1, myContentNode.getContent().size());
    }

    @Test
    void addContent_addSingleContent_correctContentType() {
        myContentNode.addContent(myContentLeaf);

//        Test if the content that has been added is the correct content
        ContentLeaf contentAsLeaf = (ContentLeaf) myContentNode.getContent().get(0);
        assertEquals("PARAGRAPH",  contentAsLeaf.contentType.name());
    }

    @Test
    void addContent_addSingleContent_correctContentValue() {
        myContentNode.addContent(myContentLeaf);

        ContentLeaf contentAsLeaf = (ContentLeaf) myContentNode.getContent().get(0);
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
        myContentNode.addContent(myContentLeaf);

        ContentLeaf contentToAdd = new ContentLeaf();
        contentToAdd.setContent(new ArrayList<>(Arrays.asList("test1", "test2", "test3")));
        contentToAdd.setContentType("LIST");

        myContentNode.addContent(contentToAdd);
        myContentNode.addContent(contentToAdd);

        ContentLeaf contentAsLeaf = (ContentLeaf) myContentNode.getContent().get(0);
        assertEquals("PARAGRAPH",  contentAsLeaf.contentType.name());
    }

    @Test
    void getContent_tryingToRemoveFromList() {
        List<Content> content = myContentNode.getContent();

//        Test if an UnsupportedOperationException is thrown when removing an item of the list
        Assertions.assertThrows(UnsupportedOperationException.class, () -> {
            content.remove(myContentLeaf);
        });
    }

    @Test
    void getContent_tryingToAddToList() {
        List<Content> content = myContentNode.getContent();

        Assertions.assertThrows(UnsupportedOperationException.class, () -> {
            content.add(new ContentLeaf());
        });
    }
}