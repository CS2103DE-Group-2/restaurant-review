package application.review;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class SortOrderTest {

    @Test
    public void getSortOrder_validStrings_returnsCorrectOrder() {
        // Partition: Exact match
        assertEquals(SortOrder.ASCENDING, SortOrder.getSortOrder("ascending"));
        assertEquals(SortOrder.DESCENDING, SortOrder.getSortOrder("descending"));

        // Partition: Starts with (case insensitive)
        assertEquals(SortOrder.ASCENDING, SortOrder.getSortOrder("asc"));
        assertEquals(SortOrder.DESCENDING, SortOrder.getSortOrder("DESC"));
    }

    @Test
    public void getSortOrder_invalidStrings_returnsUnknown() {
        // Partition: Unknown string
        assertEquals(SortOrder.UNKNOWN, SortOrder.getSortOrder("random"));
    }

    @Test
    public void getSortOrder_nullOrBlank_returnsUnknown() {
        // Partition: Null or blank input
        assertEquals(SortOrder.UNKNOWN, SortOrder.getSortOrder(null));
        assertEquals(SortOrder.UNKNOWN, SortOrder.getSortOrder("  "));
    }

    @Test
    public void toString_returnsCorrectString() {
        // Partition: Enum toString check
        assertEquals("ascending", SortOrder.ASCENDING.toString());
        assertEquals("descending", SortOrder.DESCENDING.toString());
        assertEquals("unknown", SortOrder.UNKNOWN.toString());
    }
}
