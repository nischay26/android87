package com.android87.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Photo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String uri;
    private List<Tag> tags;

    public Photo(String uri) {
        this.uri = uri;
        this.tags = new ArrayList<>();
    }

    public String getUri() { return uri; }
    public List<Tag> getTags() { return tags; }

    public boolean addTag(String name, String value) {
        Tag newTag = new Tag(name, value);
        if (!tags.contains(newTag)) {
            tags.add(newTag);
            return true;
        }
        return false;
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
    }
}
