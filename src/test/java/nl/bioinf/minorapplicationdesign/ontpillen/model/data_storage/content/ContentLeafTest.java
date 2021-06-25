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
class ContentLeafTest {

    private ContentLeaf myContentLeaf;

    @BeforeEach
    public void createContentLeaf() {
        this.myContentLeaf = new ContentLeaf();
    }

    @Test
    void setContentType_sunnyDay() {
        for (ContentType contentType : ContentType.values()) {
            myContentLeaf.setContentType(contentType.name());

            assertEquals(contentType.name(), myContentLeaf.getContentType());
        }
    }

    @Test()
    void setContentType_wrongContentType() {

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            myContentLeaf.setContentType("ThisIsAWrongContentType");
        });
    }


    @Test
    public void setContent_isContentAdded() {
        myContentLeaf.setContent(new ArrayList<>(Arrays.asList("test1", "test2", "test3")));
        assertNotNull(myContentLeaf.getContent());
    }

    @Test
    public void setContent_addContentCorrect() {
        myContentLeaf.setContent(new ArrayList<>(Arrays.asList("test1", "test2", "test3")));


//        Test if the content that has been added is the correct content
        int i = 0;
        for (String content: myContentLeaf.getContent()) {
            i += 1;
            String expected = "test" + Integer.toString(i);
            assertEquals(expected, content);
        };
    }

    @Test
    public void addContent_addSingleContent() {
        myContentLeaf.addContent("test1");

        List<String> content = myContentLeaf.getContent();
        assertEquals(1, content.size());
    }

    @Test
    public void addContent_addSingleContent_CorrectContent() {
        myContentLeaf.addContent("test1");
        List<String> content = myContentLeaf.getContent();
        String[] expectedContent = {"test1"};
        assertArrayEquals(expectedContent, content.toArray());
    }

    @Test
    public void addContent_addMultipleContents_CorrectSize() {
        myContentLeaf.addContent("test1");
        myContentLeaf.addContent("test2");
        myContentLeaf.addContent("test3");

        List<String> content = myContentLeaf.getContent();
        assertEquals(3, content.size());
    }

    @Test
    public void addContent_addMultipleContents_CorrectContent() {
        myContentLeaf.addContent("test1");
        myContentLeaf.addContent("test2");
        myContentLeaf.addContent("test3");

        List<String> content = myContentLeaf.getContent();

        String[] expectedContent = {"test1", "test2", "test3"};
        assertArrayEquals(expectedContent, content.toArray());
    }


    @Test
    public void getContent_tryingToAddItemToList() {
        myContentLeaf.setContent(new ArrayList<>(Arrays.asList("test1", "test2", "test3")));

        List<String> content = myContentLeaf.getContent();

        Assertions.assertThrows(UnsupportedOperationException.class, () -> {
            content.add("test4");
        });
    }

    @Test
    public void getContent_tryingToRemoveItemFromList() {
        myContentLeaf.setContent(new ArrayList<>(Arrays.asList("test1", "test2", "test3")));

        List<String> content = myContentLeaf.getContent();

        Assertions.assertThrows(UnsupportedOperationException.class, () -> {
            content.remove("test1");
        });
    }
}