package com.ridgid.oss.common.collection;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
class ComparableList_Test
{
    private static List<String> baseTestList;
    private static List<String> prefixTestList;
    private static List<String> extendedTestList;
    private static List<String> baseTestListWithNulls1;
    private static List<String> baseTestListWithNulls2;
    private static List<String> baseTestListWithNulls3;
    private static List<String> baseTestListWithNulls4;
    private static List<String> baseTestListWithNulls5;
    private static List<String> extendedTestListWithNulls0;
    private static List<String> extendedTestListWithNulls1;
    private static List<String> extendedTestListWithNulls2;
    private static List<String> extendedTestListWithNulls3;
    private static List<String> extendedTestListWithNulls4;
    private static List<String> extendedTestListWithNulls5;
    private static List<String> extendedTestListWithNulls6;
    private static List<String> extendedTestListWithNulls7;
    private static List<String> extendedTestListBeforeBase;
    private static List<String> extendedTestListBeforeBaseWithNull;

    @BeforeAll
    static void setup() {
        baseTestList                       = Arrays.asList("One", "Two", "Three", "Four");
        prefixTestList                     = Arrays.asList("One", "Two");
        extendedTestList                   = Arrays.asList("One", "Two", "Three", "Four", "Five", "Six");
        baseTestListWithNulls1             = Arrays.asList(null, "Two", "Three", "Four");
        baseTestListWithNulls2             = Arrays.asList("One", null, "Three", "Four");
        baseTestListWithNulls3             = Arrays.asList("One", "Two", null, "Four");
        baseTestListWithNulls4             = Arrays.asList("One", "Two", "Three", null);
        baseTestListWithNulls5             = Arrays.asList(null, null, null, null);
        extendedTestListWithNulls0         = Arrays.asList("One", "Two", "Three", "Four", null, "Six");
        extendedTestListWithNulls1         = Arrays.asList("One", "Two", "Three", "Four", null);
        extendedTestListWithNulls2         = Arrays.asList(null, "Two", "Three", "Four", "Five", "Six");
        extendedTestListWithNulls3         = Arrays.asList("One", null, "Three", "Four", "Five", "Six");
        extendedTestListWithNulls4         = Arrays.asList("One", "Two", null, "Four", "Five", "Six");
        extendedTestListWithNulls5         = Arrays.asList("One", "Two", "Three", null, "Five", "Six");
        extendedTestListWithNulls6         = Arrays.asList(null, null, null, null, "Five", "Six");
        extendedTestListWithNulls7         = Arrays.asList(null, null, null, null, null, null);
        extendedTestListBeforeBase         = Arrays.asList("One", "Two", "Three", "Befour", "Five", "Six");
        extendedTestListBeforeBaseWithNull = Arrays.asList("One", "Two", "Bethree", null, "Five", "Six");
    }

    @SuppressWarnings("SpellCheckingInspection")
    @Test
    void compareTo() {
        // Arrange
        List<ComparableList<String>> unsortedList = Arrays.asList
            (
                ComparableList.from(baseTestList),
                ComparableList.from(extendedTestListWithNulls4),
                ComparableList.from(prefixTestList),
                ComparableList.from(extendedTestList),
                ComparableList.from(baseTestListWithNulls1),
                ComparableList.from(extendedTestListBeforeBase),
                ComparableList.from(extendedTestListWithNulls2),
                ComparableList.from(baseTestListWithNulls4),
                ComparableList.from(baseTestListWithNulls5),
                ComparableList.from(extendedTestListWithNulls0),
                ComparableList.from(extendedTestListWithNulls1),
                ComparableList.from(extendedTestListWithNulls3),
                ComparableList.from(baseTestListWithNulls2),
                ComparableList.from(extendedTestListBeforeBaseWithNull),
                ComparableList.from(extendedTestListWithNulls5),
                ComparableList.from(extendedTestListWithNulls6),
                ComparableList.from(baseTestListWithNulls3),
                ComparableList.from(extendedTestListWithNulls7)
            );
        List<ComparableList<String>> expectedSortedList = Arrays.asList
            (
                ComparableList.from(prefixTestList),                         // "One", "Two"
                ComparableList.from(extendedTestListBeforeBaseWithNull),
                // "One", "Two", "Bethree", null, "Five", "Six"
                ComparableList.from(extendedTestListBeforeBase),
                // "One", "Two", "Three", "Befour", "Five", "Six"
                ComparableList.from(baseTestList),                           // "One", "Two", "Three", "Four"
                ComparableList.from(extendedTestList),
                // "One", "Two", "Three", "Four", "Five", "Six"
                ComparableList.from(extendedTestListWithNulls1),             // "One", "Two", "Three", "Four", null
                ComparableList.from(extendedTestListWithNulls0),
                // "One", "Two", "Three", "Four", null, "Six"
                ComparableList.from(baseTestListWithNulls4),                 // "One", "Two", "Three", null
                ComparableList.from(extendedTestListWithNulls5),
                // "One", "Two", "Three", null, "Five", "Six"
                ComparableList.from(baseTestListWithNulls3),                 // "One", "Two", null, "Four"
                ComparableList.from(extendedTestListWithNulls4),
                // "One", "Two", null, "Four", "Five", "Six"
                ComparableList.from(baseTestListWithNulls2),                 // "One", null, "Three", "Four"
                ComparableList.from(extendedTestListWithNulls3),
                // "One", null, "Three", "Four", "Five", "Six"
                ComparableList.from(baseTestListWithNulls1),                 // null, "Two", "Three", "Four"
                ComparableList.from(extendedTestListWithNulls2),
                // null, "Two", "Three", "Four", "Five", "Six"
                ComparableList.from(baseTestListWithNulls5),                 // null, null, null, null
                ComparableList.from(extendedTestListWithNulls6),             // null, null, null, null, "Five", "Six"
                ComparableList.from(extendedTestListWithNulls7)              // null, null, null, null, null, null
            );

        // Act
        List<ComparableList<String>> actualSortedList
            = unsortedList
            .stream()
            .sorted()
            .collect(toList());

        // Assert
        assertEquals
            (
                expectedSortedList,
                actualSortedList,
                "Expected " + expectedSortedList + ", but got " + actualSortedList
            );

    }

    @Test
    void it_provides_a_usable_implementation_when_from_is_called() {
        // Arrange

        // Act
        ComparableList<String> actualComparableList = ComparableList.from(baseTestList);

        // Assert
        assertNotNull(actualComparableList);
        assertEquals(baseTestList.size(), actualComparableList.size());
        assertEquals(baseTestList.isEmpty(), actualComparableList.isEmpty());
        assertEquals(baseTestList, actualComparableList);
        assertEquals(baseTestList.hashCode(), actualComparableList.hashCode());
        // ... should be more
    }
}
