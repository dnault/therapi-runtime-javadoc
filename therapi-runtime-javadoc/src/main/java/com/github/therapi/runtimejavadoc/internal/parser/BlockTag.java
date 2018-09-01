package com.github.therapi.runtimejavadoc.internal.parser;

public class BlockTag {

    final String name;

    final String value;

    public BlockTag(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return "@" + name + " : " + value;
    }
}
