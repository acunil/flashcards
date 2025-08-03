package com.example.flashcards_backend.testutils;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public final class ShuffleTestUtils {
    private ShuffleTestUtils() { /* no-op */ }

    /**
     * Assert that calling supplier.get() up to maxAttempts times
     * eventually returns a list whose order differs from `original`,
     * while always containing the same elements.
     */
    public static <T> void assertEventuallyReorders(
        Supplier<List<T>> supplier,
        List<T> original,
        int maxAttempts
    ) {
        boolean reordered = IntStream.range(0, maxAttempts)
            .mapToObj(i -> supplier.get())
            .peek(shuffled -> assertThat(shuffled)
                .hasSameSizeAs(original)
                .containsExactlyInAnyOrderElementsOf(original))
            .anyMatch(shuffled -> !shuffled.equals(original));

        assertThat(reordered)
            .withFailMessage("Expected a different ordering within %d shuffles", maxAttempts)
            .isTrue();
    }

    /** convenience overload with a default attempt count */
    public static <T> void assertEventuallyReorders(
        Supplier<List<T>> supplier,
        List<T> original
    ) {
        assertEventuallyReorders(supplier, original, 20);
    }
}
