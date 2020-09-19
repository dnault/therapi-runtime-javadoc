package com.github.therapi.runtimejavadoc;

import junit.framework.TestCase;
import org.junit.Test;


public class ImportTest extends TestCase {
    @Test
    public void testToString() {
        assertEquals( "com.foo.Bar", new Import("com.foo", "Bar").toString() );
        assertEquals( "com.foo.Bar.rar", new Import("com.foo", "Bar", "rar").toString() );
        assertEquals( "com.foo.*", new Import("com.foo", "*").toString() );
        assertEquals( "com.foo.Tar.*", new Import("com.foo", "Tar", "*").toString() );
        assertEquals( "Tar.*", new Import(null, "Tar", "*").toString() );
        assertEquals( "Tar", new Import(null, "Tar").toString() );
    }
}
