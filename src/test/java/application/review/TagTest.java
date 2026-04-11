package application.review;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

public class TagTest {

    @Test
    public void constructor_validTagName_success() {
        // Partition: Valid tag name
        Tag tag = new Tag("Good Food");
        assertEquals("Good Food", tag.getTagName());
    }

    @Test
    public void equals_sameTagNameIgnoreCase_true() {
        // Partition: Same tag name, different case
        Tag tag1 = new Tag("Good");
        Tag tag2 = new Tag("good");
        assertEquals(tag1, tag2);
        assertEquals(tag1.hashCode(), tag2.hashCode());
    }

    @Test
    public void equals_differentTagName_false() {
        // Partition: Different tag names
        Tag tag1 = new Tag("Good");
        Tag tag2 = new Tag("Bad");
        assertNotEquals(tag1, tag2);
    }

    @Test
    public void toTags_validString_returnsSet() {
        // Partition: Valid string with multiple tags
        String tagsString = "Good Food, SlowService";
        Set<Tag> tags = Tag.toTags(tagsString);
        assertEquals(2, tags.size());
        assertTrue(tags.contains(new Tag("Good Food")));
        assertTrue(tags.contains(new Tag("SlowService")));
    }

    @Test
    public void toTags_stringWithBlanks_filtersBlanks() {
        // Partition: Valid string with some blank tags (e.g., ", , tag")
        String tagsString = "tag1, , tag2, ";
        Set<Tag> tags = Tag.toTags(tagsString);
        assertEquals(2, tags.size());
        assertTrue(tags.contains(new Tag("tag1")));
        assertTrue(tags.contains(new Tag("tag2")));
    }

    @Test
    public void toTags_invalidString_returnsEmptySet() {
        // Partition: Null or empty input
        assertTrue(Tag.toTags(null).isEmpty());
        assertTrue(Tag.toTags("").isEmpty());
        assertTrue(Tag.toTags("   ").isEmpty());
    }

    @Test
    public void toString_returnsTagName() {
        // Partition: Check string representation
        Tag tag = new Tag("TestTag");
        assertEquals("TestTag", tag.toString());
    }
}
