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
class ContentLeafTest {

    @Test
    void setContentType_sunnyDay() {
        for (ContentType contentType : ContentType.values()) {
            ContentLeaf mySideEffectContent = new ContentLeaf();
            mySideEffectContent.setContentType(contentType.name());

//            Test if contentType is set the right value
            assertEquals(contentType.name(), mySideEffectContent.getContentType());
        }
    }

    @Test()
    void setContentType_wrongContentType() {
        ContentLeaf mySideEffectContent = new ContentLeaf();

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            mySideEffectContent.setContentType("ThisIsAWrongContentType");
        });
    }


    @Test
    public void setContent() {
//        Create ContentLeaf object
        ContentLeaf mySideEffectContent = new ContentLeaf();
//        Add content to the ContentLeaf object
        mySideEffectContent.setContent(new ArrayList<>(Arrays.asList("test1", "test2", "test3")));

//        Test if content has been added
        assertTrue(mySideEffectContent.getContent() != null);

//        Test if the content that has been added is the correct content
        int i = 0;
        for (String content: mySideEffectContent.getContent()) {
            i += 1;
            assertEquals("test" + Integer.toString(i), content);
        };
    }

    @Test
    public void addContent_addSingleContent() {
//        Create ContentLeaf object
        ContentLeaf mySideEffectContent = new ContentLeaf();
//        Add content to the ContentLeaf object
        mySideEffectContent.addContent("test1");

//        Test if one item in content
        List<String> content = mySideEffectContent.getContent();
        assertEquals(1, content.size());

//        Test if the content that has been added to the ContentLeaf object is the correct content
        String[] expectedContent = {"test1"};
        assertArrayEquals(expectedContent, content.toArray());
    }

    @Test
    public void addContent_addMultipleContents() {
//        Create ContentLeaf object
        ContentLeaf mySideEffectContent = new ContentLeaf();
//        Add content to the ContentLeaf object
        mySideEffectContent.addContent("test1");
        mySideEffectContent.addContent("test2");
        mySideEffectContent.addContent("test3");

//        Test if three item in content
        List<String> content = mySideEffectContent.getContent();
        assertEquals(3, content.size());

//        Test if the content that has been added to the ContentLeaf object is the correct content
        String[] expectedContent = {"test1", "test2", "test3"};
        assertArrayEquals(expectedContent, content.toArray());
    }


    @Test
    public void getContent_tryingTOModifyList() {
//        Create ContentLeaf object
        ContentLeaf mySideEffectContent = new ContentLeaf();
//        Add content to the ContentLeaf object
        mySideEffectContent.setContent(new ArrayList<>(Arrays.asList("test1", "test2", "test3")));


//        Get the content of the ContentLeaf object
        List<String> content = mySideEffectContent.getContent();

//        Test if an UnsupportedOperationException is thrown when adding to the list
        Assertions.assertThrows(UnsupportedOperationException.class, () -> {
            content.add("test4");
        });

//        Test if an UnsupportedOperationException is thrown when removing an item of the list
        Assertions.assertThrows(UnsupportedOperationException.class, () -> {
            content.remove("test1");
        });
    }
}