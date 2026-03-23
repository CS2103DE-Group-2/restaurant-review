package application.review;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

public class TagTest {

    @Test
    public void constructor_validTagName_success() {
        Tag tag = new Tag("Good Food");
        assertEquals("Good Food", tag.getTagName());
    }

    @Test
    public void constructor_nullOrBlankTagName_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new Tag(null));
        assertThrows(IllegalArgumentException.class, () -> new Tag(""));
        assertThrows(IllegalArgumentException.class, () -> new Tag("   "));
    }

    @Test
    public void equals_sameTagNameIgnoreCase_true() {
        Tag tag1 = new Tag("Good");
        Tag tag2 = new Tag("good");
        assertEquals(tag1, tag2);
        assertEquals(tag1.hashCode(), tag2.hashCode());
    }

    @Test
    public void equals_differentTagName_false() {
        Tag tag1 = new Tag("Good");
        Tag tag2 = new Tag("Bad");
        assertNotEquals(tag1, tag2);
    }

    @Test
    public void toTags_validString_returnsSet() {
        String tagsString = "Good Food, SlowService";
        Set<Tag> tags = Tag.toTags(tagsString);
        assertEquals(2, tags.size());
        assertTrue(tags.contains(new Tag("Good Food")));
        assertTrue(tags.contains(new Tag("SlowService")));
    }

    @Test
    public void toTags_invalidString_returnsEmptySet() {
        assertTrue(Tag.toTags(null).isEmpty());
        assertTrue(Tag.toTags("").isEmpty());
    }

    @Test
    public void toString_returnsTagName() {
        Tag tag = new Tag("TestTag");
        assertEquals("TestTag", tag.toString());
    }
}
