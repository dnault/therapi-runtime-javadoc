package com.github.dnault.therapi.runtimejavadoc;

import com.google.common.collect.ImmutableList;

import java.io.Serializable;
import java.util.List;

public class RuntimeDoc implements Serializable {
    private static final long serialVersionUID = 1;

    private final ImmutableList<RuntimeTag> tags;

    public RuntimeDoc(List<RuntimeTag> tags) {
        this.tags = ImmutableList.copyOf(tags);
    }

    public ImmutableList<RuntimeTag> getTags() {
        return tags;
    }
}
