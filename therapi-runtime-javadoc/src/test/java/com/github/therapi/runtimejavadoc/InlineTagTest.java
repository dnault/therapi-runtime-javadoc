package com.github.therapi.runtimejavadoc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InlineTagTest {

    @Test
    public void nullValueBecomesEmpty() {
        InlineTag tag = new InlineTag("inheritDoc", null);
        assertEquals("", tag.getValue());
        assertEquals(new InlineTag("inheritDoc", ""), tag);
    }

    @Test
    public void nullNameIsRejected() {
        assertThrows(NullPointerException.class, () -> new InlineTag(null, "value"));
    }
}
