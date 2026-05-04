package com.android87.model;

import java.io.Serializable;
import java.util.Objects;

public class Tag implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String value;

    public Tag(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() { return name; }
    public String getValue() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return name.equalsIgnoreCase(tag.name) && value.equalsIgnoreCase(tag.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase(), value.toLowerCase());
    }

    @Override
    public String toString() {
        return name + ": " + value;
    }
}
