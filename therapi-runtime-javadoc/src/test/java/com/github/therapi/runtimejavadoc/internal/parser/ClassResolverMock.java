package com.github.therapi.runtimejavadoc.internal.parser;

import com.github.therapi.runtimejavadoc.ClassResolver;

import java.util.HashMap;
import java.util.Map;


public class ClassResolverMock extends ClassResolver {
    private final Map<String,String> mappings = new HashMap<>();

    public ClassResolverMock() {
        super( 0 );
    }

    public void addMapping( String from, String to ) {
        mappings.put( from, to );
    }

    public Class resolveClass( String classRef ) {
        return null;
    }

    public String resolveRef( String classRef ) {
        return mappings.containsKey(classRef) ? mappings.get(classRef) : classRef;
    }
}
