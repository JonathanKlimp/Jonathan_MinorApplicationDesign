package nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content;

import nl.bioinf.minorapplicationdesign.ontpillen.model.data_storage.content.Content;

import java.util.*;

/**
 * A class to store the information about side effects. It has a map that stores the SideEffectContent for patients
 * and a map that stores the SideEffectContent for practitioners content can be added to these maps and the values
 * of these maps can be returned in a list.
 * @author Naomi Hindriks
 */
public class SideEffects {
    private Map<String, Content> sideEffectContentsPatient = new HashMap<>();
    private Map<String, Content> sideEffectContentPractitioner = new HashMap<>();

    private Map<Integer, ContentLeaf> contentLeafs = new HashMap<>();
    private Map<Integer, ContentNode> contentNodes = new HashMap<>();

    public void addSideEffectPatient(String key, Content sideEffectContent) {
        this.sideEffectContentsPatient.put(key, sideEffectContent);
        this.addSideEffectToIdMap(sideEffectContent);
    }

    public void addSideEffectPractitioner(String key, Content sideEffectContent) {
        this.sideEffectContentPractitioner.put(key, sideEffectContent);
        this.addSideEffectToIdMap(sideEffectContent);
    }

    private void addSideEffectToIdMap(Content sideEffectContent) {
        int maxId = getCurrentMaxId();
//        One higher than the current id is the id for the next Content
        int newId = maxId + 1;
        sideEffectContent.setId(newId);

        if (sideEffectContent instanceof ContentLeaf) {
            this.contentLeafs.put(newId, (ContentLeaf) sideEffectContent);
        } else if (sideEffectContent instanceof ContentNode) {
            ContentNode sideEffectContentNode = (ContentNode) sideEffectContent;
            this.contentNodes.put(newId, sideEffectContentNode);
            for (Content content : sideEffectContentNode.getContent()) {
                this.addSideEffectToIdMap(content);
            }
        } else {
            throw new IllegalArgumentException("sideEffectContent is of an illegal Content class.");
        }
    }

    private int getCurrentMaxId() {
        int currentMaxId;
        try {
//            Create new HashSet with all the current ids
            Set<Integer> currentIds = new HashSet<>();
            currentIds.addAll(this.contentLeafs.keySet());
            currentIds.addAll(this.contentNodes.keySet());
//            Find the maximum id that currently exists
            currentMaxId = Collections.max(currentIds);
        } catch (NoSuchElementException e) {
            currentMaxId = 0;
        }
        return currentMaxId;
    }

    public List<Content> getSideEffectsPatient() {
        return new ArrayList<>(this.sideEffectContentsPatient.values());
    }

    public List<Content> getSideEffectsPractitioner() {
        return new ArrayList<>(this.sideEffectContentPractitioner.values());
    }

    public ContentNode getContentNodeById(int id) {
        return this.contentNodes.get(id);
    }

    public ContentLeaf getContentLeafById(int id) {
        return this.contentLeafs.get(id);
    }
}
