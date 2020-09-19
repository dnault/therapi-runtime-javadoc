package com.github.therapi.runtimejavadoc.internal.parser;

import com.github.therapi.runtimejavadoc.Import;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class ImportParserTest {
    @Test
    public void testParseImport() {
        assertEquals( new Import("java.util","Map"), ImportParser.parseImport("import java.util.Map;") );
        assertEquals( new Import("java.util","*"), ImportParser.parseImport("import java.util.*;") );

        assertEquals( new Import("java.util","Map", "toString"), ImportParser.parseImport("import static java.util.Map.toString;") );
        assertEquals( new Import("java.util","Map", "*"), ImportParser.parseImport("import static java.util.Map.*;") );
    }
}
