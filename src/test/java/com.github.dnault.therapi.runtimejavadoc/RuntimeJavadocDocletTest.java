package com.github.dnault.therapi.runtimejavadoc;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RuntimeJavadocDocletTest {

    @Test
    public void foo() throws Exception {
        assertEquals(0, com.sun.tools.javadoc.Main.execute(new String[]{
                "@" + "/Users/dnault/work/therapi-runtime-javadoc/build/tmp/runtimeJavadoc/javadoc.options"}));
    }
}
