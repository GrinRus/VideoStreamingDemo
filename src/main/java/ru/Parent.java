package ru;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME
)
@JsonSubTypes({@JsonSubTypes.Type(value = Child.class), @JsonSubTypes.Type(value = ParentImpl.class)})
public abstract class Parent {
    public Parent() {
    }
}
