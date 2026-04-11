package application.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import application.review.ReviewList;

public class StorageLoadResultTest {

    @Test
    public void constructor_andGetters_workCorrectly() {
        // Partition: Basic record functionality
        ReviewList list = new ReviewList();
        List<String> warnings = List.of("Warning 1", "Warning 2");
        StorageLoadResult result = new StorageLoadResult(list, warnings);

        assertNotNull(result.reviewList());
        assertEquals(list, result.reviewList());
        assertEquals(2, result.warnings().size());
        assertEquals("Warning 1", result.warnings().get(0));
    }
}
