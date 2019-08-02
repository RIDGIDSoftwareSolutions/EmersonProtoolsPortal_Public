package com.ridgid.oss.common.tuple;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings({"SpellCheckingInspection", "FieldCanBeLocal", "SimplifiableJUnitAssertion", "EqualsBetweenInconvertibleTypes", "ResultOfMethodCallIgnored"})
class Pair_Test {

    private final Byte[] noncomparable1 = new Byte[]{0x01, 0x02, 0x03};
    private final Byte[] noncomparable1b = new Byte[]{0x01, 0x02, 0x04};

    private final byte[] noncomparable2 = new byte[]{0x01, 0x02, 0x03};
    private final byte[] noncomparable2b = new byte[]{0x01, 0x06, 0x03};

    private final Object[] noncomparable3 = new Object[]{new Object(), null, new Object(), null};
    private final Object[] noncomparable3b = new Object[]{null, new Object(), new Object(), null};

    private final int[] noncomparable4 = new int[]{1, 2, 3, 4};
    private final int[] noncomparable4b = new int[]{4, 1, 2, 3};

    private final Object noncomparable5 = new Object();
    private final Object noncomparable5b = new Exception();

    private final int comparable1 = 1;
    private final int comparable1b = 100;
    private final int comparable1c = 100;

    private final String comparable2 = "Hello George";
    private final String comparable2b = "Goodbye Dolly";
    private final String comparable2c = "Goodbye Dolly";

    private final BigInteger comparable3 = BigInteger.valueOf(1938741094L);
    private final BigInteger comparable3b = BigInteger.valueOf(918374091L);
    private final BigInteger comparable3c = BigInteger.valueOf(918374091L);

    private final Integer comparable4 = 123414;
    private final Integer comparable4b = 901837;
    private final Integer comparable4c = 901837;

    private final Object[] allTestObjects
            = new Object[]
            {
                    noncomparable1,
                    noncomparable1b,
                    noncomparable2,
                    noncomparable2b,
                    noncomparable3,
                    noncomparable3b,
                    noncomparable4,
                    noncomparable4b,
                    noncomparable5,
                    noncomparable5b,
                    comparable1,
                    comparable1b,
                    comparable1c,
                    comparable2,
                    comparable2b,
                    comparable2c,
                    comparable3,
                    comparable3b,
                    comparable3c,
                    comparable4,
                    comparable4b,
                    comparable4c,
            };

    @Test
    void it_constructs_with_2_differently_typed_noncomparables() {
        Assertions.assertDoesNotThrow(() -> new Pair<>(noncomparable1, noncomparable2));
        assertDoesNotThrow(() -> new Pair<>(noncomparable2, noncomparable3));
        assertDoesNotThrow(() -> new Pair<>(noncomparable3, noncomparable4));
        assertDoesNotThrow(() -> new Pair<>(noncomparable4, noncomparable5));
        assertDoesNotThrow(() -> new Pair<>(noncomparable5, noncomparable1));
    }

    @Test
    void it_constructs_with_2_differently_typed_comparables() {
        assertDoesNotThrow(() -> new Pair<>(comparable1, comparable2));
        assertDoesNotThrow(() -> new Pair<>(comparable2, comparable3));
        assertDoesNotThrow(() -> new Pair<>(comparable3, comparable4));
        assertDoesNotThrow(() -> new Pair<>(comparable4, comparable1));
    }

    @Test
    void it_constructs_with_1_comparable_and_1_noncomparable() {
        assertDoesNotThrow(() -> new Pair<>(comparable1, noncomparable2));
        assertDoesNotThrow(() -> new Pair<>(noncomparable2, comparable3));
        assertDoesNotThrow(() -> new Pair<>(comparable3, noncomparable4));
        assertDoesNotThrow(() -> new Pair<>(noncomparable4, comparable1));
        assertDoesNotThrow(() -> new Pair<>(noncomparable1, comparable2));
        assertDoesNotThrow(() -> new Pair<>(comparable2, noncomparable3));
        assertDoesNotThrow(() -> new Pair<>(noncomparable3, comparable4));
        assertDoesNotThrow(() -> new Pair<>(comparable4, noncomparable5));
        assertDoesNotThrow(() -> new Pair<>(noncomparable5, comparable1));
    }

    @Test
    void it_constructs_with_2_same_typed_noncomparables() {
        assertDoesNotThrow(() -> new Pair<>(noncomparable1, noncomparable1b));
        assertDoesNotThrow(() -> new Pair<>(noncomparable2, noncomparable2b));
        assertDoesNotThrow(() -> new Pair<>(noncomparable3, noncomparable3b));
        assertDoesNotThrow(() -> new Pair<>(noncomparable4, noncomparable4b));
        assertDoesNotThrow(() -> new Pair<>(noncomparable5, noncomparable5b));
    }

    @Test
    void it_constructs_with_2_same_typed_comparables() {
        assertDoesNotThrow(() -> new Pair<>(comparable1, comparable1b));
        assertDoesNotThrow(() -> new Pair<>(comparable2, comparable2b));
        assertDoesNotThrow(() -> new Pair<>(comparable3, comparable3b));
        assertDoesNotThrow(() -> new Pair<>(comparable4, comparable4b));
    }

    @Test
    void getLeft_works_with_2_differently_typed_noncomparables() {
        assertEquals(noncomparable1, new Pair<>(noncomparable1, noncomparable2).getLeft());
        assertEquals(noncomparable2, new Pair<>(noncomparable2, noncomparable3).getLeft());
        assertEquals(noncomparable3, new Pair<>(noncomparable3, noncomparable4).getLeft());
        assertEquals(noncomparable4, new Pair<>(noncomparable4, noncomparable5).getLeft());
        assertEquals(noncomparable5, new Pair<>(noncomparable5, noncomparable1).getLeft());
    }

    @Test
    void getLeft_works_with_2_differently_typed_comparables() {
        assertEquals(comparable1, new Pair<>(comparable1, comparable2).getLeft());
        assertEquals(comparable2, new Pair<>(comparable2, comparable3).getLeft());
        assertEquals(comparable3, new Pair<>(comparable3, comparable4).getLeft());
        assertEquals(comparable4, new Pair<>(comparable4, comparable1).getLeft());
    }

    @Test
    void getLeft_works_with_2_same_typed_noncomparables() {
        assertEquals(noncomparable1, new Pair<>(noncomparable1, noncomparable1b).getLeft());
        assertEquals(noncomparable2, new Pair<>(noncomparable2, noncomparable2b).getLeft());
        assertEquals(noncomparable3, new Pair<>(noncomparable3, noncomparable3b).getLeft());
        assertEquals(noncomparable4, new Pair<>(noncomparable4, noncomparable4b).getLeft());
        assertEquals(noncomparable5, new Pair<>(noncomparable5, noncomparable5b).getLeft());
    }

    @Test
    void getLeft_works_with_2_same_typed_comparables() {
        assertEquals(comparable1, new Pair<>(comparable1, comparable1b).getLeft());
        assertEquals(comparable2, new Pair<>(comparable2, comparable2b).getLeft());
        assertEquals(comparable3, new Pair<>(comparable3, comparable3b).getLeft());
        assertEquals(comparable4, new Pair<>(comparable4, comparable4b).getLeft());
    }

    @Test
    void getRight_works_with_2_differently_typed_noncomparables() {
        assertEquals(noncomparable2, new Pair<>(noncomparable1, noncomparable2).getRight());
        assertEquals(noncomparable3, new Pair<>(noncomparable2, noncomparable3).getRight());
        assertEquals(noncomparable4, new Pair<>(noncomparable3, noncomparable4).getRight());
        assertEquals(noncomparable5, new Pair<>(noncomparable4, noncomparable5).getRight());
        assertEquals(noncomparable1, new Pair<>(noncomparable5, noncomparable1).getRight());
    }

    @Test
    void getRight_works_with_2_differently_typed_comparables() {
        assertEquals(comparable2, new Pair<>(comparable1, comparable2).getRight());
        assertEquals(comparable3, new Pair<>(comparable2, comparable3).getRight());
        assertEquals(comparable4, new Pair<>(comparable3, comparable4).getRight());
        assertEquals(comparable1, new Pair<>(comparable4, comparable1).getRight());
    }

    @Test
    void getRight_works_with_2_same_typed_noncomparables() {
        assertEquals(noncomparable1b, new Pair<>(noncomparable1, noncomparable1b).getRight());
        assertEquals(noncomparable2b, new Pair<>(noncomparable2, noncomparable2b).getRight());
        assertEquals(noncomparable3b, new Pair<>(noncomparable3, noncomparable3b).getRight());
        assertEquals(noncomparable4b, new Pair<>(noncomparable4, noncomparable4b).getRight());
        assertEquals(noncomparable5b, new Pair<>(noncomparable5, noncomparable5b).getRight());
    }

    @Test
    void getRight_works_with_2_same_typed_comparables() {
        assertEquals(comparable1b, new Pair<>(comparable1, comparable1b).getRight());
        assertEquals(comparable2b, new Pair<>(comparable2, comparable2b).getRight());
        assertEquals(comparable3b, new Pair<>(comparable3, comparable3b).getRight());
        assertEquals(comparable4b, new Pair<>(comparable4, comparable4b).getRight());
    }

    @Test
    void equals_works_2_differently_typed_noncomparables() {
        assertTrue(new Pair<>(noncomparable1, noncomparable2).equals(new Pair<>(noncomparable1, noncomparable2)));
        assertTrue(new Pair<>(noncomparable2, noncomparable3).equals(new Pair<>(noncomparable2, noncomparable3)));
        assertTrue(new Pair<>(noncomparable3, noncomparable4).equals(new Pair<>(noncomparable3, noncomparable4)));
        assertTrue(new Pair<>(noncomparable4, noncomparable5).equals(new Pair<>(noncomparable4, noncomparable5)));
        assertTrue(new Pair<>(noncomparable5, noncomparable1).equals(new Pair<>(noncomparable5, noncomparable1)));

        assertFalse(new Pair<>(noncomparable1, noncomparable2).equals(new Pair<>(noncomparable2, noncomparable1)));
        assertFalse(new Pair<>(noncomparable2, noncomparable3).equals(new Pair<>(noncomparable3, noncomparable2)));
        assertFalse(new Pair<>(noncomparable3, noncomparable4).equals(new Pair<>(noncomparable4, noncomparable3)));
        assertFalse(new Pair<>(noncomparable4, noncomparable5).equals(new Pair<>(noncomparable5, noncomparable4)));
        assertFalse(new Pair<>(noncomparable5, noncomparable1).equals(new Pair<>(noncomparable1, noncomparable5)));

        assertFalse(new Pair<>(noncomparable1, noncomparable2).equals(new Pair<>(noncomparable1b, noncomparable2b)));
        assertFalse(new Pair<>(noncomparable2, noncomparable3).equals(new Pair<>(noncomparable2b, noncomparable3b)));
        assertFalse(new Pair<>(noncomparable3, noncomparable4).equals(new Pair<>(noncomparable3b, noncomparable4b)));
        assertFalse(new Pair<>(noncomparable4, noncomparable5).equals(new Pair<>(noncomparable4b, noncomparable5b)));
        assertFalse(new Pair<>(noncomparable5, noncomparable1).equals(new Pair<>(noncomparable5b, noncomparable1b)));
    }

    @Test
    void equals_works_2_differently_typed_comparables() {
        assertTrue(new Pair<>(comparable1, comparable2).equals(new Pair<>(comparable1, comparable2)));
        assertTrue(new Pair<>(comparable2, comparable3).equals(new Pair<>(comparable2, comparable3)));
        assertTrue(new Pair<>(comparable3, comparable4).equals(new Pair<>(comparable3, comparable4)));
        assertTrue(new Pair<>(comparable4, comparable1).equals(new Pair<>(comparable4, comparable1)));

        assertThrows(ClassCastException.class, () -> new Pair<>(comparable1, comparable2).equals(new Pair<>(comparable2, comparable1)));
        assertThrows(ClassCastException.class, () -> new Pair<>(comparable2, comparable3).equals(new Pair<>(comparable3, comparable2)));
        assertThrows(ClassCastException.class, () -> new Pair<>(comparable3, comparable4).equals(new Pair<>(comparable4, comparable3)));
        assertFalse(new Pair<>(comparable4, comparable1).equals(new Pair<>(comparable1, comparable4)));

        assertFalse(new Pair<>(comparable1, comparable2).equals(new Pair<>(comparable1b, comparable2b)));
        assertFalse(new Pair<>(comparable2, comparable3).equals(new Pair<>(comparable2b, comparable3b)));
        assertFalse(new Pair<>(comparable3, comparable4).equals(new Pair<>(comparable3b, comparable4b)));
        assertFalse(new Pair<>(comparable4, comparable1).equals(new Pair<>(comparable4b, comparable1b)));

        assertTrue(new Pair<>(comparable1c, comparable2c).equals(new Pair<>(comparable1b, comparable2b)));
        assertTrue(new Pair<>(comparable2c, comparable3c).equals(new Pair<>(comparable2b, comparable3b)));
        assertTrue(new Pair<>(comparable3c, comparable4c).equals(new Pair<>(comparable3b, comparable4b)));
        assertTrue(new Pair<>(comparable4c, comparable1c).equals(new Pair<>(comparable4b, comparable1b)));
    }


    @Test
    void hashCode_works_and_is_compatible_with_equals() {
        for (
                AbstractMap.SimpleEntry<? extends Pair<?, ?>, ? extends Pair<?, ?>> pairDouble
                : streamAllTestPairCombinationsThatShouldBeComparable()
        ) {
            assertFalse(pairDouble.getKey() == pairDouble.getValue());
            assertTrue
                    (
                            !pairDouble.getKey().equals(pairDouble.getValue())
                                    || pairDouble.getKey().hashCode() == pairDouble.getValue().hashCode(),
                            "Hashcode incompatible one equals: " + pairDouble.getKey() + " " + pairDouble.getValue()
                    );
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    void compareTo_works_and_is_compatible_with_equals() {
        for (AbstractMap.SimpleEntry<? extends Pair<?, ?>, ? extends Pair<?, ?>>
                pairDouble : streamAllTestPairCombinationsThatShouldBeComparable()
        ) {
            assertFalse(pairDouble.getKey() == pairDouble.getValue());
            assertTrue
                    (
                            (
                                    (
                                            pairDouble.getKey().equals(pairDouble.getValue())
                                                    && pairDouble.getKey().compareTo((Pair) pairDouble.getValue()) == 0
                                    )
                                            ||
                                            (
                                                    !pairDouble.getKey().equals(pairDouble.getValue())
                                                            && pairDouble.getKey().compareTo((Pair) pairDouble.getValue()) != 0
                                            )
                            ),
                            "CompareTo incompatible one equals: " + pairDouble.getKey() + " " + pairDouble.getValue()
                    );
        }
    }

    @Test
    void toString_works() {
        for (Pair<?, ?> pair : streamAllTestPairCombinations().collect(toList())) {
            assertNotNull(pair.toString());
        }
    }

    private List<AbstractMap.SimpleEntry<? extends Pair<?, ?>, ? extends Pair<?, ?>>> streamAllTestPairCombinationsThatShouldBeComparable() {
        return streamAllTestPairCombinations()
                .flatMap
                        (
                                p1 -> streamAllTestPairCombinations()
                                        .filter
                                                (
                                                        p2 -> p1 != p2
                                                                && p1.left.getClass().isAssignableFrom(p2.left.getClass())
                                                                && p1.right.getClass().isAssignableFrom(p2.right.getClass())
                                                )
                                        .map(p2 -> new AbstractMap.SimpleEntry<>(p1, p2))
                        )
                .collect(toList());
    }

    private Stream<Pair<?, ?>> streamAllTestPairCombinations() {
        return Arrays
                .stream(allTestObjects)
                .flatMap
                        (
                                o1 -> Arrays
                                        .stream(allTestObjects)
                                        .filter(o2 -> o2 != o1)
                                        .map(o2 -> new Pair<>(o1, o2))
                        );
    }

}