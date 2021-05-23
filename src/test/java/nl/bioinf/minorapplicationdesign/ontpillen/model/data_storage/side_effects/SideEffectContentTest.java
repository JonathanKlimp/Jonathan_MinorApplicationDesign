package nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.side_effects;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


/**
 * @author Naomi Hindriks
 */
@SpringBootTest
class SideEffectContentTest {

    /**
     * Test adding content to a SideEffectContent object sunny day scenario
     */
    @Test
    public void addContent_single_sunnyDay() {
        for (SideEffectContent.ContentType contentType : SideEffectContent.ContentType.values()) {
            SideEffectContent mySideEffectContent = new SideEffectContent();
            List<String> contentToAdd = new ArrayList<>();
            contentToAdd.add("test1");
            contentToAdd.add("test2");
            contentToAdd.add("test3");
            mySideEffectContent.addContent(contentType.name(), contentToAdd);

//        Test if content has been added
            assertTrue(mySideEffectContent.getContent() != null);

//        Test if the content that has been added is the correct content
            for (SideEffectContent.Content content: mySideEffectContent.getContent()) {
                assertEquals(content.contentType, contentType);
                assertEquals(content.content, contentToAdd);
            };
        }
    }

    @Test
    public void addContent_multiple_sunnyDay() {
        SideEffectContent mySideEffectContent = new SideEffectContent();
        List<String> contentToAdd1 = new ArrayList<>();
        contentToAdd1.add("list1");
        contentToAdd1.add("list2");
        contentToAdd1.add("list3");

        List<String> contentToAdd2 = new ArrayList<>();
        contentToAdd2.add("paragraph1");
        contentToAdd2.add("paragraph2");
        contentToAdd2.add("paragraph3");

        mySideEffectContent.addContent("LIST", contentToAdd1);
        mySideEffectContent.addContent("PARAGRAPH", contentToAdd2);

        List<SideEffectContent.Content> addedContent = mySideEffectContent.getContent();

//        Test if two contents are added
        assertEquals(addedContent.size(), 2);

//        Test if the correct content is added

    }
}