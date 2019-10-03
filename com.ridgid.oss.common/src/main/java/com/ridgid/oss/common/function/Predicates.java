package com.ridgid.oss.common.function;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.DoublePredicate;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.LongPredicate;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class Predicates
{
    public Predicates() {
    }

    // equals
    public static <T>
    Predicate<T> whereEquals(T compareToValue) {
        return t -> t.equals(compareToValue);
    }

    public static IntPredicate whereEquals(int compareToValue) {
        return i -> i == compareToValue;
    }

    public static LongPredicate whereEquals(long compareToValue) {
        return l -> l == compareToValue;
    }

    public static DoublePredicate whereApproximatelyEquals(double compareToValue,
                                                           double delta)
    {
        return d -> areApproximatelyEqual(d, compareToValue, delta);
    }


    // lessThan
    public static <T extends Comparable<T>>
    Predicate<T> whereLessThan(T compareToValue) {
        return t -> t.compareTo(compareToValue) < 0;
    }

    public static IntPredicate whereLessThan(int compareToValue) {
        return i -> i < compareToValue;
    }

    public static LongPredicate whereLessThan(long compareToValue) {
        return l -> l < compareToValue;
    }

    public static DoublePredicate whereLessThan(double compareToValue,
                                                double delta)
    {
        return d -> d < compareToValue;
    }


    // lessThanOrEqualTo
    public static <T extends Comparable<T>>
    Predicate<T> whereLessThanOrEqualTo(T compareToValue) {
        return t -> t.compareTo(compareToValue) <= 0;
    }

    public static IntPredicate whereLessThanOrEqualTo(int compareToValue) {
        return i -> i <= compareToValue;
    }

    public static LongPredicate whereLessThanOrEqualTo(long compareToValue) {
        return l -> l <= compareToValue;
    }

    public static DoublePredicate whereLessThanOrEqualTo(double compareToValue,
                                                         double delta)
    {
        return d -> d <= compareToValue;
    }


    // greaterThan
    public static <T extends Comparable<T>>
    Predicate<T> whereGreaterThan(T compareToValue) {
        return t -> t.compareTo(compareToValue) > 0;
    }

    public static IntPredicate whereGreaterThan(int compareToValue) {
        return i -> i > compareToValue;
    }

    public static LongPredicate whereGreaterThan(long compareToValue) {
        return l -> l > compareToValue;
    }

    public static DoublePredicate whereGreaterThan(double compareToValue,
                                                   double delta)
    {
        return d -> d > compareToValue;
    }


    // greaterThanOrEqualTo
    public static <T extends Comparable<T>>
    Predicate<T> whereGreaterThanOrEqualTo(T compareToValue) {
        return t -> t.compareTo(compareToValue) >= 0;
    }

    public static IntPredicate whereGreaterThanOrEqualTo(int compareToValue) {
        return i -> i >= compareToValue;
    }

    public static LongPredicate whereGreaterThanOrEqualTo(long compareToValue) {
        return l -> l >= compareToValue;
    }

    public static DoublePredicate whereGreaterThanOrEqualTo(double compareToValue,
                                                            double delta)
    {
        return d -> d >= compareToValue;
    }


    // propertyEquals
    public static <T, FT>
    Predicate<T> wherePropertyEquals(Function<T, FT> valueSelector,
                                     FT compareToValue)
    {
        return t -> valueSelector.apply(t).equals(compareToValue);
    }

    public static <T>
    Predicate<T> wherePropertyEquals(ToIntFunction<T> valueSelector,
                                     int compareToValue)
    {
        return t -> valueSelector.applyAsInt(t) == compareToValue;
    }

    public static <T>
    Predicate<T> wherePropertyEquals(ToLongFunction<T> valueSelector,
                                     long compareToValue)
    {
        return t -> valueSelector.applyAsLong(t) == compareToValue;
    }

    public static <T>
    Predicate<T> wherePropertyApproximatelyEquals(ToDoubleFunction<T> valueSelector,
                                                  double compareToValue,
                                                  double delta)
    {
        return t -> areApproximatelyEqual(valueSelector.applyAsDouble(t), compareToValue, delta);
    }


    // propertyLessThan
    public static <T, FT extends Comparable<FT>>
    Predicate<T> wherePropertyLessThan(Function<T, FT> valueSelector,
                                       FT compareToValue)
    {
        return t -> valueSelector.apply(t).compareTo(compareToValue) < 0;
    }

    public static <T>
    Predicate<T> wherePropertyLessThan(ToIntFunction<T> valueSelector,
                                       int compareToValue)
    {
        return t -> valueSelector.applyAsInt(t) < compareToValue;
    }

    public static <T>
    Predicate<T> wherePropertyLessThan(ToLongFunction<T> valueSelector,
                                       long compareToValue)
    {
        return t -> valueSelector.applyAsLong(t) < compareToValue;
    }

    public static <T>
    Predicate<T> wherePropertyLessThan(ToDoubleFunction<T> valueSelector,
                                       double compareToValue,
                                       double delta)
    {
        return t -> valueSelector.applyAsDouble(t) < compareToValue;
    }


    // propertyLessThanOrEqualTo
    public static <T, FT extends Comparable<FT>>
    Predicate<T> wherePropertyLessThanOrEqualTo(Function<T, FT> valueSelector,
                                                FT compareToValue)
    {
        return t -> valueSelector.apply(t).compareTo(compareToValue) <= 0;
    }

    public static <T>
    Predicate<T> wherePropertyLessThanOrEqualTo(ToIntFunction<T> valueSelector,
                                                int compareToValue)
    {
        return t -> valueSelector.applyAsInt(t) <= compareToValue;
    }

    public static <T>
    Predicate<T> wherePropertyLessThanOrEqualTo(ToLongFunction<T> valueSelector,
                                                long compareToValue)
    {
        return t -> valueSelector.applyAsLong(t) <= compareToValue;
    }

    public static <T>
    Predicate<T> wherePropertyLessThanOrEqualTo(ToDoubleFunction<T> valueSelector,
                                                double compareToValue,
                                                double delta)
    {
        return t -> valueSelector.applyAsDouble(t) <= compareToValue;
    }


    // propertyGreaterThan
    public static <T, FT extends Comparable<FT>>
    Predicate<T> wherePropertyGreaterThan(Function<T, FT> valueSelector,
                                          FT compareToValue)
    {
        return t -> valueSelector.apply(t).compareTo(compareToValue) > 0;
    }

    public static <T>
    Predicate<T> wherePropertyGreaterThan(ToIntFunction<T> valueSelector,
                                          int compareToValue)
    {
        return t -> valueSelector.applyAsInt(t) > compareToValue;
    }

    public static <T>
    Predicate<T> wherePropertyGreaterThan(ToLongFunction<T> valueSelector,
                                          long compareToValue)
    {
        return t -> valueSelector.applyAsLong(t) > compareToValue;
    }

    public static <T>
    Predicate<T> wherePropertyGreaterThan(ToDoubleFunction<T> valueSelector,
                                          double compareToValue,
                                          double delta)
    {
        return t -> valueSelector.applyAsDouble(t) > compareToValue;
    }


    // propertyGreaterThanOrEqualTo
    public static <T, FT extends Comparable<FT>>
    Predicate<T> wherePropertyGreaterThanOrEqualTo(Function<T, FT> valueSelector,
                                                   FT compareToValue)
    {
        return t -> valueSelector.apply(t).compareTo(compareToValue) >= 0;
    }

    public static <T>
    Predicate<T> wherePropertyGreaterThanOrEqualTo(ToIntFunction<T> valueSelector,
                                                   int compareToValue)
    {
        return t -> valueSelector.applyAsInt(t) >= compareToValue;
    }

    public static <T>
    Predicate<T> wherePropertyGreaterThanOrEqualTo(ToLongFunction<T> valueSelector,
                                                   long compareToValue)
    {
        return t -> valueSelector.applyAsLong(t) >= compareToValue;
    }

    public static <T>
    Predicate<T> wherePropertyGreaterThanOrEqualTo(ToDoubleFunction<T> valueSelector,
                                                   double compareToValue,
                                                   double delta)
    {
        return t -> valueSelector.applyAsDouble(t) >= compareToValue;
    }


    // from (single)
    public static <T, FT>
    Predicate<T> from(Function<T, FT> valueSelector,
                      Predicate<FT> predicate)
    {
        return t -> predicate.test(valueSelector.apply(t));
    }

    public static <T>
    Predicate<T> from(ToIntFunction<T> valueSelector,
                      IntPredicate predicate)
    {
        return i -> predicate.test(valueSelector.applyAsInt(i));
    }

    public static <T>
    Predicate<T> from(ToLongFunction<T> valueSelector,
                      LongPredicate predicate)
    {
        return i -> predicate.test(valueSelector.applyAsLong(i));
    }

    public static <T>
    Predicate<T> from(ToDoubleFunction<T> valueSelector,
                      DoublePredicate predicate)
    {
        return i -> predicate.test(valueSelector.applyAsDouble(i));
    }

    // from (bi)
    public static <T, FT1, FT2>
    Predicate<T> from(Function<T, FT1> firstValueSelector,
                      Function<T, FT2> secondValueSelector,
                      BiPredicate<FT1, FT2> predicate)
    {
        return t -> predicate.test(firstValueSelector.apply(t),
                                   secondValueSelector.apply(t));
    }

    public static <T>
    Predicate<T> from(ToIntFunction<T> firstValueSelector,
                      ToIntFunction<T> secondValueSelector,
                      IntBiPredicate predicate)
    {
        return t -> predicate.test(firstValueSelector.applyAsInt(t),
                                   secondValueSelector.applyAsInt(t));
    }

    public static <T>
    Predicate<T> from(ToLongFunction<T> firstValueSelector,
                      ToLongFunction<T> secondValueSelector,
                      LongBiPredicate predicate)
    {
        return t -> predicate.test(firstValueSelector.applyAsLong(t),
                                   secondValueSelector.applyAsLong(t));
    }

    public static <T>
    Predicate<T> from(ToDoubleFunction<T> firstValueSelector,
                      ToDoubleFunction<T> secondValueSelector,
                      DoubleBiPredicate predicate)
    {
        return t -> predicate.test(firstValueSelector.applyAsDouble(t),
                                   secondValueSelector.applyAsDouble(t));
    }


    public static <T, FT1, FT2, FT3>
    Predicate<T> from(Function<T, FT1> firstValueSelector,
                      Function<T, FT2> secondValueSelector,
                      Function<T, FT3> thirdValueSelector,
                      TriPredicate<FT1, FT2, FT3> predicate)
    {
        return t -> predicate.test(firstValueSelector.apply(t),
                                   secondValueSelector.apply(t),
                                   thirdValueSelector.apply(t));
    }

    public static <T>
    Predicate<T> from(ToIntFunction<T> firstValueSelector,
                      ToIntFunction<T> secondValueSelector,
                      ToIntFunction<T> thirdValueSelector,
                      IntTriPredicate predicate)
    {
        return t -> predicate.test(firstValueSelector.applyAsInt(t),
                                   secondValueSelector.applyAsInt(t),
                                   thirdValueSelector.applyAsInt(t));
    }

    public static <T>
    Predicate<T> from(ToLongFunction<T> firstValueSelector,
                      ToLongFunction<T> secondValueSelector,
                      ToLongFunction<T> thirdValueSelector,
                      LongTriPredicate predicate)
    {
        return t -> predicate.test(firstValueSelector.applyAsLong(t),
                                   secondValueSelector.applyAsLong(t),
                                   thirdValueSelector.applyAsLong(t));
    }

    public static <T>
    Predicate<T> from(ToDoubleFunction<T> firstValueSelector,
                      ToDoubleFunction<T> secondValueSelector,
                      ToDoubleFunction<T> thirdValueSelector,
                      DoubleTriPredicate predicate)
    {
        return t -> predicate.test(firstValueSelector.applyAsDouble(t),
                                   secondValueSelector.applyAsDouble(t),
                                   thirdValueSelector.applyAsDouble(t));
    }


    public static <T, FT1, FT2, FT3, FT4>
    Predicate<T> from(Function<T, FT1> firstValueSelector,
                      Function<T, FT2> secondValueSelector,
                      Function<T, FT3> thirdValueSelector,
                      Function<T, FT4> forthValueSelector,
                      QuadPredicate<FT1, FT2, FT3, FT4> predicate)
    {
        return t -> predicate.test(firstValueSelector.apply(t),
                                   secondValueSelector.apply(t),
                                   thirdValueSelector.apply(t),
                                   forthValueSelector.apply(t));
    }

    public static <T>
    Predicate<T> from(ToIntFunction<T> firstValueSelector,
                      ToIntFunction<T> secondValueSelector,
                      ToIntFunction<T> thirdValueSelector,
                      ToIntFunction<T> forthValueSelector,
                      IntQuadPredicate predicate)
    {
        return t -> predicate.test(firstValueSelector.applyAsInt(t),
                                   secondValueSelector.applyAsInt(t),
                                   thirdValueSelector.applyAsInt(t),
                                   forthValueSelector.applyAsInt(t));
    }

    public static <T>
    Predicate<T> from(ToLongFunction<T> firstValueSelector,
                      ToLongFunction<T> secondValueSelector,
                      ToLongFunction<T> thirdValueSelector,
                      ToLongFunction<T> forthValueSelector,
                      LongQuadPredicate predicate)
    {
        return t -> predicate.test(firstValueSelector.applyAsLong(t),
                                   secondValueSelector.applyAsLong(t),
                                   thirdValueSelector.applyAsLong(t),
                                   forthValueSelector.applyAsLong(t));
    }

    public static <T>
    Predicate<T> from(ToDoubleFunction<T> firstValueSelector,
                      ToDoubleFunction<T> secondValueSelector,
                      ToDoubleFunction<T> thirdValueSelector,
                      ToDoubleFunction<T> forthValueSelector,
                      DoubleQuadPredicate predicate)
    {
        return t -> predicate.test(firstValueSelector.applyAsDouble(t),
                                   secondValueSelector.applyAsDouble(t),
                                   thirdValueSelector.applyAsDouble(t),
                                   forthValueSelector.applyAsDouble(t));
    }

    // from (single)
    public static <T, FT, SFT>
    Predicate<T> with(Function<T, FT> valueSelector,
                      Function<FT, SFT> subValueSelector,
                      Predicate<SFT> predicate)
    {
        return t -> predicate.test(subValueSelector.apply(valueSelector.apply(t)));
    }


    // where (single)
    public static <T, FT>
    Predicate<T> where(Function<T, FT> valueSelector,
                       Predicate<FT> predicate)
    {
        return t -> predicate.test(valueSelector.apply(t));
    }

    public static <T>
    Predicate<T> where(ToIntFunction<T> valueSelector,
                       IntPredicate predicate)
    {
        return i -> predicate.test(valueSelector.applyAsInt(i));
    }

    public static <T>
    Predicate<T> where(ToLongFunction<T> valueSelector,
                       LongPredicate predicate)
    {
        return i -> predicate.test(valueSelector.applyAsLong(i));
    }

    public static <T>
    Predicate<T> where(ToDoubleFunction<T> valueSelector,
                       DoublePredicate predicate)
    {
        return i -> predicate.test(valueSelector.applyAsDouble(i));
    }

    // where (bi)
    public static <T, FT1, FT2>
    Predicate<T> where(Function<T, FT1> firstValueSelector,
                       Function<T, FT2> secondValueSelector,
                       BiPredicate<FT1, FT2> predicate)
    {
        return t -> predicate.test(firstValueSelector.apply(t),
                                   secondValueSelector.apply(t));
    }

    public static <T>
    Predicate<T> where(ToIntFunction<T> firstValueSelector,
                       ToIntFunction<T> secondValueSelector,
                       IntBiPredicate predicate)
    {
        return t -> predicate.test(firstValueSelector.applyAsInt(t),
                                   secondValueSelector.applyAsInt(t));
    }

    public static <T>
    Predicate<T> where(ToLongFunction<T> firstValueSelector,
                       ToLongFunction<T> secondValueSelector,
                       LongBiPredicate predicate)
    {
        return t -> predicate.test(firstValueSelector.applyAsLong(t),
                                   secondValueSelector.applyAsLong(t));
    }

    public static <T>
    Predicate<T> where(ToDoubleFunction<T> firstValueSelector,
                       ToDoubleFunction<T> secondValueSelector,
                       DoubleBiPredicate predicate)
    {
        return t -> predicate.test(firstValueSelector.applyAsDouble(t),
                                   secondValueSelector.applyAsDouble(t));
    }


    public static <T, FT1, FT2, FT3>
    Predicate<T> where(Function<T, FT1> firstValueSelector,
                       Function<T, FT2> secondValueSelector,
                       Function<T, FT3> thirdValueSelector,
                       TriPredicate<FT1, FT2, FT3> predicate)
    {
        return t -> predicate.test(firstValueSelector.apply(t),
                                   secondValueSelector.apply(t),
                                   thirdValueSelector.apply(t));
    }

    public static <T>
    Predicate<T> where(ToIntFunction<T> firstValueSelector,
                       ToIntFunction<T> secondValueSelector,
                       ToIntFunction<T> thirdValueSelector,
                       IntTriPredicate predicate)
    {
        return t -> predicate.test(firstValueSelector.applyAsInt(t),
                                   secondValueSelector.applyAsInt(t),
                                   thirdValueSelector.applyAsInt(t));
    }

    public static <T>
    Predicate<T> where(ToLongFunction<T> firstValueSelector,
                       ToLongFunction<T> secondValueSelector,
                       ToLongFunction<T> thirdValueSelector,
                       LongTriPredicate predicate)
    {
        return t -> predicate.test(firstValueSelector.applyAsLong(t),
                                   secondValueSelector.applyAsLong(t),
                                   thirdValueSelector.applyAsLong(t));
    }

    public static <T>
    Predicate<T> where(ToDoubleFunction<T> firstValueSelector,
                       ToDoubleFunction<T> secondValueSelector,
                       ToDoubleFunction<T> thirdValueSelector,
                       DoubleTriPredicate predicate)
    {
        return t -> predicate.test(firstValueSelector.applyAsDouble(t),
                                   secondValueSelector.applyAsDouble(t),
                                   thirdValueSelector.applyAsDouble(t));
    }


    public static <T, FT1, FT2, FT3, FT4>
    Predicate<T> where(Function<T, FT1> firstValueSelector,
                       Function<T, FT2> secondValueSelector,
                       Function<T, FT3> thirdValueSelector,
                       Function<T, FT4> forthValueSelector,
                       QuadPredicate<FT1, FT2, FT3, FT4> predicate)
    {
        return t -> predicate.test(firstValueSelector.apply(t),
                                   secondValueSelector.apply(t),
                                   thirdValueSelector.apply(t),
                                   forthValueSelector.apply(t));
    }

    public static <T>
    Predicate<T> where(ToIntFunction<T> firstValueSelector,
                       ToIntFunction<T> secondValueSelector,
                       ToIntFunction<T> thirdValueSelector,
                       ToIntFunction<T> forthValueSelector,
                       IntQuadPredicate predicate)
    {
        return t -> predicate.test(firstValueSelector.applyAsInt(t),
                                   secondValueSelector.applyAsInt(t),
                                   thirdValueSelector.applyAsInt(t),
                                   forthValueSelector.applyAsInt(t));
    }

    public static <T>
    Predicate<T> where(ToLongFunction<T> firstValueSelector,
                       ToLongFunction<T> secondValueSelector,
                       ToLongFunction<T> thirdValueSelector,
                       ToLongFunction<T> forthValueSelector,
                       LongQuadPredicate predicate)
    {
        return t -> predicate.test(firstValueSelector.applyAsLong(t),
                                   secondValueSelector.applyAsLong(t),
                                   thirdValueSelector.applyAsLong(t),
                                   forthValueSelector.applyAsLong(t));
    }

    public static <T>
    Predicate<T> where(ToDoubleFunction<T> firstValueSelector,
                       ToDoubleFunction<T> secondValueSelector,
                       ToDoubleFunction<T> thirdValueSelector,
                       ToDoubleFunction<T> forthValueSelector,
                       DoubleQuadPredicate predicate)
    {
        return t -> predicate.test(firstValueSelector.applyAsDouble(t),
                                   secondValueSelector.applyAsDouble(t),
                                   thirdValueSelector.applyAsDouble(t),
                                   forthValueSelector.applyAsDouble(t));
    }


    public static <T1, T2, FT1, FT2>
    BiPredicate<T1, T2> whereBothMatchBy(Function<T1, FT1> firstValueSelector,
                                         Function<T2, FT2> secondValueSelector,
                                         BiPredicate<FT1, FT2> predicate)
    {
        return (t1, t2) -> predicate.test(firstValueSelector.apply(t1),
                                          secondValueSelector.apply(t2));
    }

    public static <T1, T2>
    BiPredicate<T1, T2> whereBothMatchBy(ToIntFunction<T1> firstValueSelector,
                                         ToIntFunction<T2> secondValueSelector,
                                         IntBiPredicate predicate)
    {
        return (t1, t2) -> predicate.test(firstValueSelector.applyAsInt(t1),
                                          secondValueSelector.applyAsInt(t2));
    }

    public static <T1, T2>
    BiPredicate<T1, T2> whereBothMatchBy(ToLongFunction<T1> firstValueSelector,
                                         ToLongFunction<T2> secondValueSelector,
                                         LongBiPredicate predicate)
    {
        return (t1, t2) -> predicate.test(firstValueSelector.applyAsLong(t1),
                                          secondValueSelector.applyAsLong(t2));
    }

    public static <T1, T2>
    BiPredicate<T1, T2> whereAllMatchBy(ToDoubleFunction<T1> firstValueSelector,
                                        ToDoubleFunction<T2> secondValueSelector,
                                        DoubleBiPredicate predicate)
    {
        return (t1, t2) -> predicate.test(firstValueSelector.applyAsDouble(t1),
                                          secondValueSelector.applyAsDouble(t2));
    }


    public static <T1, T2, T3, FT1, FT2, FT3>
    TriPredicate<T1, T2, T3> whereAllMatchBy(Function<T1, FT1> firstValueSelector,
                                             Function<T2, FT2> secondValueSelector,
                                             Function<T3, FT3> thirdValueSelector,
                                             TriPredicate<FT1, FT2, FT3> predicate)
    {
        return (t1, t2, t3) -> predicate.test(firstValueSelector.apply(t1),
                                              secondValueSelector.apply(t2),
                                              thirdValueSelector.apply(t3));
    }

    public static <T1, T2, T3>
    TriPredicate<T1, T2, T3> whereAllMatchBy(ToIntFunction<T1> firstValueSelector,
                                             ToIntFunction<T2> secondValueSelector,
                                             ToIntFunction<T3> thirdValueSelector,
                                             IntTriPredicate predicate)
    {
        return (t1, t2, t3) -> predicate.test(firstValueSelector.applyAsInt(t1),
                                              secondValueSelector.applyAsInt(t2),
                                              thirdValueSelector.applyAsInt(t3));
    }

    public static <T1, T2, T3>
    TriPredicate<T1, T2, T3> whereAllMatchBy(ToLongFunction<T1> firstValueSelector,
                                             ToLongFunction<T2> secondValueSelector,
                                             ToLongFunction<T3> thirdValueSelector,
                                             LongTriPredicate predicate)
    {
        return (t1, t2, t3) -> predicate.test(firstValueSelector.applyAsLong(t1),
                                              secondValueSelector.applyAsLong(t2),
                                              thirdValueSelector.applyAsLong(t3));
    }

    public static <T1, T2, T3>
    TriPredicate<T1, T2, T3> whereAllMatchBy(ToDoubleFunction<T1> firstValueSelector,
                                             ToDoubleFunction<T2> secondValueSelector,
                                             ToDoubleFunction<T3> thirdValueSelector,
                                             DoubleTriPredicate predicate)
    {
        return (t1, t2, t3) -> predicate.test(firstValueSelector.applyAsDouble(t1),
                                              secondValueSelector.applyAsDouble(t2),
                                              thirdValueSelector.applyAsDouble(t3));
    }


    public static <T1, T2, T3, T4, FT1, FT2, FT3, FT4>
    QuadPredicate<T1, T2, T3, T4> whereAllMatchBy(Function<T1, FT1> firstValueSelector,
                                                  Function<T2, FT2> secondValueSelector,
                                                  Function<T3, FT3> thirdValueSelector,
                                                  Function<T4, FT4> forthValueSelector,
                                                  QuadPredicate<FT1, FT2, FT3, FT4> predicate)
    {
        return (t1, t2, t3, t4) -> predicate.test(firstValueSelector.apply(t1),
                                                  secondValueSelector.apply(t2),
                                                  thirdValueSelector.apply(t3),
                                                  forthValueSelector.apply(t4));
    }

    public static <T1, T2, T3, T4>
    QuadPredicate<T1, T2, T3, T4> whereAllMatchBy(ToIntFunction<T1> firstValueSelector,
                                                  ToIntFunction<T2> secondValueSelector,
                                                  ToIntFunction<T3> thirdValueSelector,
                                                  ToIntFunction<T4> forthValueSelector,
                                                  IntQuadPredicate predicate)
    {
        return (t1, t2, t3, t4) -> predicate.test(firstValueSelector.applyAsInt(t1),
                                                  secondValueSelector.applyAsInt(t2),
                                                  thirdValueSelector.applyAsInt(t3),
                                                  forthValueSelector.applyAsInt(t4));
    }

    public static <T1, T2, T3, T4>
    QuadPredicate<T1, T2, T3, T4> whereAllMatchBy(ToLongFunction<T1> firstValueSelector,
                                                  ToLongFunction<T2> secondValueSelector,
                                                  ToLongFunction<T3> thirdValueSelector,
                                                  ToLongFunction<T4> forthValueSelector,
                                                  LongQuadPredicate predicate)
    {
        return (t1, t2, t3, t4) -> predicate.test(firstValueSelector.applyAsLong(t1),
                                                  secondValueSelector.applyAsLong(t2),
                                                  thirdValueSelector.applyAsLong(t3),
                                                  forthValueSelector.applyAsLong(t4));
    }

    public static <T1, T2, T3, T4>
    QuadPredicate<T1, T2, T3, T4> whereAllMatchBy(ToDoubleFunction<T1> firstValueSelector,
                                                  ToDoubleFunction<T2> secondValueSelector,
                                                  ToDoubleFunction<T3> thirdValueSelector,
                                                  ToDoubleFunction<T4> forthValueSelector,
                                                  DoubleQuadPredicate predicate)
    {
        return (t1, t2, t3, t4) -> predicate.test(firstValueSelector.applyAsDouble(t1),
                                                  secondValueSelector.applyAsDouble(t2),
                                                  thirdValueSelector.applyAsDouble(t3),
                                                  forthValueSelector.applyAsDouble(t4));
    }


    public static <T>
    Predicate<T> where(Predicate<T> predicate)
    {
        return predicate;
    }

    public static <T1, T2>
    BiPredicate<T1, T2> whereBothOf(Predicate<T1> predicate1,
                                    Predicate<T2> predicate2)
    {
        return (t1, t2) -> predicate1.test(t1)
                           && predicate2.test(t2);
    }

    public static <T1, T2, T3>
    TriPredicate<T1, T2, T3> whereAllOf(Predicate<T1> predicate1,
                                        Predicate<T2> predicate2,
                                        Predicate<T3> predicate3)
    {
        return (t1, t2, t3) -> predicate1.test(t1)
                               && predicate2.test(t2)
                               && predicate3.test(t3);
    }

    public static <T1, T2, T3, T4>
    QuadPredicate<T1, T2, T3, T4> whereAllOf(Predicate<T1> predicate1,
                                             Predicate<T2> predicate2,
                                             Predicate<T3> predicate3,
                                             Predicate<T4> predicate4)
    {
        return (t1, t2, t3, t4) -> predicate1.test(t1)
                                   && predicate2.test(t2)
                                   && predicate3.test(t3)
                                   && predicate4.test(t4);
    }

    public static <T1, T2>
    BiPredicate<T1, T2> whereEitherOf(Predicate<T1> predicate1,
                                      Predicate<T2> predicate2)
    {
        return (t1, t2) -> predicate1.test(t1)
                           && predicate2.test(t2);
    }

    public static <T1, T2, T3>
    TriPredicate<T1, T2, T3> whereAnyOf(Predicate<T1> predicate1,
                                        Predicate<T2> predicate2,
                                        Predicate<T3> predicate3)
    {
        return (t1, t2, t3) -> predicate1.test(t1)
                               && predicate2.test(t2)
                               && predicate3.test(t3);
    }

    public static <T1, T2, T3, T4>
    QuadPredicate<T1, T2, T3, T4> whereAnyOf(Predicate<T1> predicate1,
                                             Predicate<T2> predicate2,
                                             Predicate<T3> predicate3,
                                             Predicate<T4> predicate4)
    {
        return (t1, t2, t3, t4) -> predicate1.test(t1)
                                   && predicate2.test(t2)
                                   && predicate3.test(t3)
                                   && predicate4.test(t4);
    }

    public static <T>
    Predicate<T> whereBoth(Predicate<T> predicate1,
                           Predicate<T> predicate2)
    {
        return (t) -> predicate1.test(t)
                      && predicate2.test(t);
    }

    public static <T>
    Predicate<T> whereAll(Predicate<T> predicate1,
                          Predicate<T> predicate2,
                          Predicate<T> predicate3)
    {
        return (t) -> predicate1.test(t)
                      && predicate2.test(t)
                      && predicate3.test(t);
    }

    public static <T>
    Predicate<T> whereAll(Predicate<T> predicate1,
                          Predicate<T> predicate2,
                          Predicate<T> predicate3,
                          Predicate<T> predicate4)
    {
        return (t) -> predicate1.test(t)
                      && predicate2.test(t)
                      && predicate3.test(t)
                      && predicate4.test(t);
    }

    public static <T>
    Predicate<T> whereEither(Predicate<T> predicate1,
                             Predicate<T> predicate2)
    {
        return (t) -> predicate1.test(t)
                      || predicate2.test(t);
    }

    public static <T>
    Predicate<T> whereAny(Predicate<T> predicate1,
                          Predicate<T> predicate2,
                          Predicate<T> predicate3)
    {
        return (t) -> predicate1.test(t)
                      || predicate2.test(t)
                      || predicate3.test(t);
    }

    public static <T>
    Predicate<T> whereAny(Predicate<T> predicate1,
                          Predicate<T> predicate2,
                          Predicate<T> predicate3,
                          Predicate<T> predicate4)
    {
        return (t) -> predicate1.test(t)
                      || predicate2.test(t)
                      || predicate3.test(t)
                      || predicate4.test(t);
    }

    // equals
    public static <T>
    Predicate<T> isEqualTo(T compareToValue) {
        return t -> t.equals(compareToValue);
    }

    public static IntPredicate isEqualTo(int compareToValue) {
        return i -> i == compareToValue;
    }

    public static LongPredicate isEqualTo(long compareToValue) {
        return l -> l == compareToValue;
    }

    public static DoublePredicate isApproximatelyEqualTo(double compareToValue,
                                                         double delta)
    {
        return d -> areApproximatelyEqual(d, compareToValue, delta);
    }


    // isLessThan
    public static <T extends Comparable<T>>
    Predicate<T> isLessThan(T compareToValue) {
        return t -> t.compareTo(compareToValue) < 0;
    }

    public static IntPredicate isLessThan(int compareToValue) {
        return i -> i < compareToValue;
    }

    public static LongPredicate isLessThan(long compareToValue) {
        return l -> l < compareToValue;
    }

    public static DoublePredicate isLessThan(double compareToValue,
                                             double delta)
    {
        return d -> d < compareToValue;
    }


    // isLessThanOrEqualTo
    public static <T extends Comparable<T>>
    Predicate<T> isLessThanOrEqualTo(T compareToValue) {
        return t -> t.compareTo(compareToValue) <= 0;
    }

    public static IntPredicate isLessThanOrEqualTo(int compareToValue) {
        return i -> i <= compareToValue;
    }

    public static LongPredicate isLessThanOrEqualTo(long compareToValue) {
        return l -> l <= compareToValue;
    }

    public static DoublePredicate isLessThanOrEqualTo(double compareToValue,
                                                      double delta)
    {
        return d -> d <= compareToValue;
    }


    // isGreaterThan
    public static <T extends Comparable<T>>
    Predicate<T> isGreaterThan(T compareToValue) {
        return t -> t.compareTo(compareToValue) > 0;
    }

    public static IntPredicate isGreaterThan(int compareToValue) {
        return i -> i > compareToValue;
    }

    public static LongPredicate isGreaterThan(long compareToValue) {
        return l -> l > compareToValue;
    }

    public static DoublePredicate isGreaterThan(double compareToValue,
                                                double delta)
    {
        return d -> d > compareToValue;
    }


    // isGreaterThanOrEqualTo
    public static <T extends Comparable<T>>
    Predicate<T> isGreaterThanOrEqualTo(T compareToValue) {
        return t -> t.compareTo(compareToValue) >= 0;
    }

    public static IntPredicate isGreaterThanOrEqualTo(int compareToValue) {
        return i -> i >= compareToValue;
    }

    public static LongPredicate isGreaterThanOrEqualTo(long compareToValue) {
        return l -> l >= compareToValue;
    }

    public static DoublePredicate isGreaterThanOrEqualTo(double compareToValue,
                                                         double delta)
    {
        return d -> d >= compareToValue;
    }


    // equals
    public static <T1, T2>
    BiPredicate<T1, T2> areEqualTo(T1 compareToValue1,
                                   T2 compareToValue2)
    {
        return (t1, t2) -> t1.equals(compareToValue1)
                           && t2.equals(compareToValue2);
    }

    public static IntBiPredicate areEqualTo(int compareToValue1,
                                            int compareToValue2)
    {
        return (i1, i2) -> i1 == compareToValue1
                           && i2 == compareToValue2;
    }

    public static LongBiPredicate areEqualTo(long compareToValue1,
                                             long compareToValue2)
    {
        return (l1, l2) -> l1 == compareToValue1
                           && l2 == compareToValue2;
    }

    public static DoubleBiPredicate areApproximatelyEqualTo(double compareToValue1,
                                                            double compareToValue2,
                                                            double delta)
    {
        return (d1, d2) -> areApproximatelyEqual(d1, compareToValue1, delta)
                           && areApproximatelyEqual(d2, compareToValue2, delta);
    }


    // areLessThan
    public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>>
    BiPredicate<T1, T2> areLessThan(T1 compareToValue1,
                                    T2 compareToValue2)
    {
        return (t1, t2) -> t1.compareTo(compareToValue1) < 0
                           && t2.compareTo(compareToValue2) < 0;
    }

    public static IntBiPredicate areLessThan(int compareToValue1,
                                             int compareToValue2)
    {
        return (i1, i2) -> i1 < compareToValue1
                           && i2 < compareToValue2;
    }

    public static LongBiPredicate areLessThan(long compareToValue1,
                                              long compareToValue2)
    {
        return (l1, l2) -> l1 < compareToValue1
                           && l2 < compareToValue2;
    }

    public static DoubleBiPredicate areLessThan(double compareToValue1,
                                                double compareToValue2,
                                                double delta)
    {
        return (d1, d2) -> d1 < compareToValue1
                           && d2 < compareToValue2;
    }


    // areLessThanOrEqualTo
    public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>>
    BiPredicate<T1, T2> areLessThanOrEqualTo(T1 compareToValue1,
                                             T2 compareToValue2)
    {
        return (t1, t2) -> t1.compareTo(compareToValue1) <= 0
                           && t2.compareTo(compareToValue2) <= 0;
    }

    public static IntBiPredicate areLessThanOrEqualTo(int compareToValue1,
                                                      int compareToValue2)
    {
        return (i1, i2) -> i1 <= compareToValue1
                           && i2 <= compareToValue2;
    }

    public static LongBiPredicate areLessThanOrEqualTo(long compareToValue1,
                                                       long compareToValue2)
    {
        return (l1, l2) -> l1 <= compareToValue1
                           && l2 <= compareToValue2;
    }

    public static DoubleBiPredicate areLessThanOrEqualTo(double compareToValue1,
                                                         double compareToValue2,
                                                         double delta)
    {
        return (d1, d2) -> d1 <= compareToValue1
                           && d2 <= compareToValue2;
    }


    // areGreaterThan
    public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>>
    BiPredicate<T1, T2> areGreaterThan(T1 compareToValue1,
                                       T2 compareToValue2)
    {
        return (t1, t2) -> t1.compareTo(compareToValue1) > 0
                           && t2.compareTo(compareToValue2) > 0;
    }

    public static IntBiPredicate areGreaterThan(int compareToValue1,
                                                int compareToValue2)
    {
        return (i1, i2) -> i1 > compareToValue1
                           && i2 > compareToValue2;
    }

    public static LongBiPredicate areGreaterThan(long compareToValue1,
                                                 long compareToValue2)
    {
        return (l1, l2) -> l1 > compareToValue1
                           && l2 > compareToValue2;
    }

    public static DoubleBiPredicate areGreaterThan(double compareToValue1,
                                                   double compareToValue2,
                                                   double delta)
    {
        return (d1, d2) -> d1 > compareToValue1
                           && d2 > compareToValue2;
    }


    // areGreaterThanOrEqualTo
    public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>>
    BiPredicate<T1, T2> areGreaterThanOrEqualTo(T1 compareToValue1,
                                                T2 compareToValue2)
    {
        return (t1, t2) -> t1.compareTo(compareToValue1) >= 0
                           && t2.compareTo(compareToValue2) >= 0;
    }

    public static IntBiPredicate areGreaterThanOrEqualTo(int compareToValue1,
                                                         int compareToValue2)
    {
        return (i1, i2) -> i1 >= compareToValue1
                           && i2 >= compareToValue2;
    }

    public static LongBiPredicate areGreaterThanOrEqualTo(long compareToValue1,
                                                          long compareToValue2)
    {
        return (l1, l2) -> l1 >= compareToValue1
                           && l2 >= compareToValue2;
    }

    public static DoubleBiPredicate areGreaterThanOrEqualTo(double compareToValue1,
                                                            double compareToValue2,
                                                            double delta)
    {
        return (d1, d2) -> d1 >= compareToValue1
                           && d2 >= compareToValue2;
    }


    // equals
    public static <T1, T2, T3>
    TriPredicate<T1, T2, T3> areEqualTo(T1 compareToValue1,
                                        T2 compareToValue2,
                                        T3 compareToValue3)
    {
        return (t1, t2, t3) -> t1.equals(compareToValue1)
                               && t2.equals(compareToValue2)
                               && t3.equals(compareToValue3);
    }

    public static IntTriPredicate areEqualTo(int compareToValue1,
                                             int compareToValue2,
                                             int compareToValue3)
    {
        return (i1, i2, i3) -> i1 == compareToValue1
                               && i2 == compareToValue2
                               && i3 == compareToValue3;
    }

    public static LongTriPredicate areEqualTo(long compareToValue1,
                                              long compareToValue2,
                                              long compareToValue3)
    {
        return (l1, l2, l3) -> l1 == compareToValue1
                               && l2 == compareToValue2
                               && l3 == compareToValue3;
    }

    public static DoubleTriPredicate areApproximatelyEqualTo(double compareToValue1,
                                                             double compareToValue2,
                                                             double compareToValue3,
                                                             double delta)
    {
        return (d1, d2, d3) -> areApproximatelyEqual(d1, compareToValue1, delta)
                               && areApproximatelyEqual(d2, compareToValue2, delta)
                               && areApproximatelyEqual(d3, compareToValue3, delta);
    }

    // areLessThan
    public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>>
    TriPredicate<T1, T2, T3> areLessThan(T1 compareToValue1,
                                         T2 compareToValue2,
                                         T3 compareToValue3)
    {
        return (t1, t2, t3) -> t1.compareTo(compareToValue1) < 0
                               && t2.compareTo(compareToValue2) < 0
                               && t3.compareTo(compareToValue3) < 0;
    }

    public static IntTriPredicate areLessThan(int compareToValue1,
                                              int compareToValue2,
                                              int compareToValue3)
    {
        return (i1, i2, i3) -> i1 < compareToValue1
                               && i2 < compareToValue2
                               && i3 < compareToValue3;
    }

    public static LongTriPredicate areLessThan(long compareToValue1,
                                               long compareToValue2,
                                               long compareToValue3)
    {
        return (l1, l2, l3) -> l1 < compareToValue1
                               && l2 < compareToValue2
                               && l3 < compareToValue3;
    }

    public static DoubleTriPredicate areLessThan(double compareToValue1,
                                                 double compareToValue2,
                                                 double compareToValue3,
                                                 double delta)
    {
        return (d1, d2, d3) -> d1 < compareToValue1
                               && d2 < compareToValue2
                               && d3 < compareToValue3;
    }


    // areLessThanOrEqualTo
    public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>>
    TriPredicate<T1, T2, T3> areLessThanOrEqualTo(T1 compareToValue1,
                                                  T2 compareToValue2,
                                                  T3 compareToValue3)
    {
        return (t1, t2, t3) -> t1.compareTo(compareToValue1) <= 0
                               && t2.compareTo(compareToValue2) <= 0
                               && t3.compareTo(compareToValue3) <= 0;
    }

    public static IntTriPredicate areLessThanOrEqualTo(int compareToValue1,
                                                       int compareToValue2,
                                                       int compareToValue3)
    {
        return (i1, i2, i3) -> i1 <= compareToValue1
                               && i2 <= compareToValue2
                               && i3 <= compareToValue3;
    }

    public static LongTriPredicate areLessThanOrEqualTo(long compareToValue1,
                                                        long compareToValue2,
                                                        long compareToValue3)
    {
        return (l1, l2, l3) -> l1 <= compareToValue1
                               && l2 <= compareToValue2
                               && l3 <= compareToValue3;
    }

    public static DoubleTriPredicate areLessThanOrEqualTo(double compareToValue1,
                                                          double compareToValue2,
                                                          double compareToValue3,
                                                          double delta)
    {
        return (d1, d2, d3) -> d1 <= compareToValue1
                               && d2 <= compareToValue2
                               && d3 <= compareToValue3;
    }


    // areGreaterThan
    public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>>
    TriPredicate<T1, T2, T3> areGreaterThan(T1 compareToValue1,
                                            T2 compareToValue2,
                                            T3 compareToValue3)
    {
        return (t1, t2, t3) -> t1.compareTo(compareToValue1) > 0
                               && t2.compareTo(compareToValue2) > 0
                               && t3.compareTo(compareToValue3) > 0;
    }

    public static IntTriPredicate areGreaterThan(int compareToValue1,
                                                 int compareToValue2,
                                                 int compareToValue3)
    {
        return (i1, i2, i3) -> i1 > compareToValue1
                               && i2 > compareToValue2
                               && i3 > compareToValue3;
    }

    public static LongTriPredicate areGreaterThan(long compareToValue1,
                                                  long compareToValue2,
                                                  long compareToValue3)
    {
        return (l1, l2, l3) -> l1 > compareToValue1
                               && l2 > compareToValue2
                               && l3 > compareToValue3;
    }

    public static DoubleTriPredicate areGreaterThan(double compareToValue1,
                                                    double compareToValue2,
                                                    double compareToValue3,
                                                    double delta)
    {
        return (d1, d2, d3) -> d1 > compareToValue1
                               && d2 > compareToValue2
                               && d3 > compareToValue3;
    }


    // areGreaterThanOrEqualTo
    public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>>
    TriPredicate<T1, T2, T3> areGreaterThanOrEqualTo(T1 compareToValue1,
                                                     T2 compareToValue2,
                                                     T3 compareToValue3)
    {
        return (t1, t2, t3) -> t1.compareTo(compareToValue1) >= 0
                               && t2.compareTo(compareToValue2) >= 0
                               && t3.compareTo(compareToValue3) >= 0;
    }

    public static IntTriPredicate areGreaterThanOrEqualTo(int compareToValue1,
                                                          int compareToValue2,
                                                          int compareToValue3)
    {
        return (i1, i2, i3) -> i1 >= compareToValue1
                               && i2 >= compareToValue2
                               && i3 >= compareToValue3;
    }

    public static LongTriPredicate areGreaterThanOrEqualTo(long compareToValue1,
                                                           long compareToValue2,
                                                           long compareToValue3)
    {
        return (l1, l2, l3) -> l1 >= compareToValue1
                               && l2 >= compareToValue2
                               && l3 >= compareToValue3;
    }

    public static DoubleTriPredicate areGreaterThanOrEqualTo(double compareToValue1,
                                                             double compareToValue2,
                                                             double compareToValue3,
                                                             double delta)
    {
        return (d1, d2, d3) -> d1 >= compareToValue1
                               && d2 >= compareToValue2
                               && d3 >= compareToValue3;
    }


    // equals
    public static <T1, T2, T3, T4>
    QuadPredicate<T1, T2, T3, T4> areEqualTo(T1 compareToValue1,
                                             T2 compareToValue2,
                                             T3 compareToValue3,
                                             T4 compareToValue4)
    {
        return (t1, t2, t3, t4) -> t1.equals(compareToValue1)
                                   && t2.equals(compareToValue2)
                                   && t3.equals(compareToValue3)
                                   && t4.equals(compareToValue4);
    }

    public static IntQuadPredicate areEqualTo(int compareToValue1,
                                              int compareToValue2,
                                              int compareToValue3,
                                              int compareToValue4)
    {
        return (i1, i2, i3, i4) -> i1 == compareToValue1
                                   && i2 == compareToValue2
                                   && i3 == compareToValue3
                                   && i4 == compareToValue4;
    }

    public static LongQuadPredicate areEqualTo(long compareToValue1,
                                               long compareToValue2,
                                               long compareToValue3,
                                               long compareToValue4)
    {
        return (l1, l2, l3, l4) -> l1 == compareToValue1
                                   && l2 == compareToValue2
                                   && l3 == compareToValue3
                                   && l4 == compareToValue4;
    }

    public static DoubleQuadPredicate areApproximatelyEqualTo(double compareToValue1,
                                                              double compareToValue2,
                                                              double compareToValue3,
                                                              double compareToValue4,
                                                              double delta)
    {
        return (d1, d2, d3, d4) -> areApproximatelyEqual(d1, compareToValue1, delta)
                                   && areApproximatelyEqual(d2, compareToValue2, delta)
                                   && areApproximatelyEqual(d3, compareToValue3, delta)
                                   && areApproximatelyEqual(d4, compareToValue4, delta);
    }

    // areLessThan
    public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>>
    QuadPredicate<T1, T2, T3, T4> areLessThan(T1 compareToValue1,
                                              T2 compareToValue2,
                                              T3 compareToValue3,
                                              T4 compareToValue4)
    {
        return (t1, t2, t3, t4) -> t1.compareTo(compareToValue1) < 0
                                   && t2.compareTo(compareToValue2) < 0
                                   && t3.compareTo(compareToValue3) < 0
                                   && t4.compareTo(compareToValue4) < 0;
    }

    public static IntQuadPredicate areLessThan(int compareToValue1,
                                               int compareToValue2,
                                               int compareToValue3,
                                               int compareToValue4)
    {
        return (i1, i2, i3, i4) -> i1 < compareToValue1
                                   && i2 < compareToValue2
                                   && i3 < compareToValue3
                                   && i4 < compareToValue4;
    }

    public static LongQuadPredicate areLessThan(long compareToValue1,
                                                long compareToValue2,
                                                long compareToValue3,
                                                long compareToValue4)
    {
        return (l1, l2, l3, l4) -> l1 < compareToValue1
                                   && l2 < compareToValue2
                                   && l3 < compareToValue3
                                   && l4 < compareToValue4;
    }

    public static DoubleQuadPredicate areLessThan(double compareToValue1,
                                                  double compareToValue2,
                                                  double compareToValue3,
                                                  double compareToValue4,
                                                  double delta)
    {
        return (d1, d2, d3, d4) -> d1 < compareToValue1
                                   && d2 < compareToValue2
                                   && d3 < compareToValue3
                                   && d4 < compareToValue4;
    }


    // areLessThanOrEqualTo
    public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>>
    QuadPredicate<T1, T2, T3, T4> areLessThanOrEqualTo(T1 compareToValue1,
                                                       T2 compareToValue2,
                                                       T3 compareToValue3,
                                                       T4 compareToValue4)
    {
        return (t1, t2, t3, t4) -> t1.compareTo(compareToValue1) <= 0
                                   && t2.compareTo(compareToValue2) <= 0
                                   && t3.compareTo(compareToValue3) <= 0
                                   && t4.compareTo(compareToValue4) <= 0;
    }

    public static IntQuadPredicate areLessThanOrEqualTo(int compareToValue1,
                                                        int compareToValue2,
                                                        int compareToValue3,
                                                        int compareToValue4)
    {
        return (i1, i2, i3, i4) -> i1 <= compareToValue1
                                   && i2 <= compareToValue2
                                   && i3 <= compareToValue3
                                   && i4 <= compareToValue4;
    }

    public static LongQuadPredicate areLessThanOrEqualTo(long compareToValue1,
                                                         long compareToValue2,
                                                         long compareToValue3,
                                                         long compareToValue4)
    {
        return (l1, l2, l3, l4) -> l1 <= compareToValue1
                                   && l2 <= compareToValue2
                                   && l3 <= compareToValue3
                                   && l4 <= compareToValue4;
    }

    public static DoubleQuadPredicate areLessThanOrEqualTo(double compareToValue1,
                                                           double compareToValue2,
                                                           double compareToValue3,
                                                           double compareToValue4,
                                                           double delta)
    {
        return (d1, d2, d3, d4) -> d1 <= compareToValue1
                                   && d2 <= compareToValue2
                                   && d3 <= compareToValue3
                                   && d4 <= compareToValue4;
    }


    // areGreaterThan
    public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>>
    QuadPredicate<T1, T2, T3, T4> areGreaterThan(T1 compareToValue1,
                                                 T2 compareToValue2,
                                                 T3 compareToValue3,
                                                 T4 compareToValue4)
    {
        return (t1, t2, t3, t4) -> t1.compareTo(compareToValue1) > 0
                                   && t2.compareTo(compareToValue2) > 0
                                   && t3.compareTo(compareToValue3) > 0
                                   && t4.compareTo(compareToValue4) > 0;
    }

    public static IntQuadPredicate areGreaterThan(int compareToValue1,
                                                  int compareToValue2,
                                                  int compareToValue3,
                                                  int compareToValue4)
    {
        return (i1, i2, i3, i4) -> i1 > compareToValue1
                                   && i2 > compareToValue2
                                   && i3 > compareToValue3
                                   && i4 > compareToValue4;
    }

    public static LongQuadPredicate areGreaterThan(long compareToValue1,
                                                   long compareToValue2,
                                                   long compareToValue3,
                                                   long compareToValue4)
    {
        return (l1, l2, l3, l4) -> l1 > compareToValue1
                                   && l2 > compareToValue2
                                   && l3 > compareToValue3
                                   && l4 > compareToValue4;
    }

    public static DoubleQuadPredicate areGreaterThan(double compareToValue1,
                                                     double compareToValue2,
                                                     double compareToValue3,
                                                     double compareToValue4,
                                                     double delta)
    {
        return (d1, d2, d3, d4) -> d1 > compareToValue1
                                   && d2 > compareToValue2
                                   && d3 > compareToValue3
                                   && d4 > compareToValue4;
    }


    // areGreaterThanOrEqualTo
    public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>>
    QuadPredicate<T1, T2, T3, T4> areGreaterThanOrEqualTo(T1 compareToValue1,
                                                          T2 compareToValue2,
                                                          T3 compareToValue3,
                                                          T4 compareToValue4)
    {
        return (t1, t2, t3, t4) -> t1.compareTo(compareToValue1) >= 0
                                   && t2.compareTo(compareToValue2) >= 0
                                   && t3.compareTo(compareToValue3) >= 0
                                   && t4.compareTo(compareToValue4) >= 0;
    }

    public static IntQuadPredicate areGreaterThanOrEqualTo(int compareToValue1,
                                                           int compareToValue2,
                                                           int compareToValue3,
                                                           int compareToValue4)
    {
        return (i1, i2, i3, i4) -> i1 >= compareToValue1
                                   && i2 >= compareToValue2
                                   && i3 >= compareToValue3
                                   && i4 >= compareToValue4;
    }

    public static LongQuadPredicate areGreaterThanOrEqualTo(long compareToValue1,
                                                            long compareToValue2,
                                                            long compareToValue3,
                                                            long compareToValue4)
    {
        return (l1, l2, l3, l4) -> l1 >= compareToValue1
                                   && l2 >= compareToValue2
                                   && l3 >= compareToValue3
                                   && l4 >= compareToValue4;
    }

    public static DoubleQuadPredicate areGreaterThanOrEqualTo(double compareToValue1,
                                                              double compareToValue2,
                                                              double compareToValue3,
                                                              double compareToValue4,
                                                              double delta)
    {
        return (d1, d2, d3, d4) -> d1 >= compareToValue1
                                   && d2 >= compareToValue2
                                   && d3 >= compareToValue3
                                   && d4 >= compareToValue4;
    }


    public static boolean areApproximatelyEqual(double d1, double d2, double delta) {
        return d1 >= Math.max(Double.MIN_VALUE, d2 - delta)
               && d1 <= Math.min(Double.MAX_VALUE, d2 + delta);
    }


    @FunctionalInterface
    public interface IntBiPredicate
    {
        boolean test(int a, int b);

        default IntBiPredicate and(IntBiPredicate other) {
            Objects.requireNonNull(other);
            return (a, b) -> test(a, b) && other.test(a, b);
        }

        default IntBiPredicate negate() {
            return (a, b) -> !test(a, b);
        }

        default IntBiPredicate or(IntBiPredicate other) {
            Objects.requireNonNull(other);
            return (a, b) -> test(a, b) || other.test(a, b);
        }
    }

    @FunctionalInterface
    public interface LongBiPredicate
    {
        boolean test(long a, long b);

        default LongBiPredicate and(LongBiPredicate other) {
            Objects.requireNonNull(other);
            return (a, b) -> test(a, b) && other.test(a, b);
        }

        default LongBiPredicate negate() {
            return (a, b) -> !test(a, b);
        }

        default LongBiPredicate or(LongBiPredicate other) {
            Objects.requireNonNull(other);
            return (a, b) -> test(a, b) || other.test(a, b);
        }
    }

    @FunctionalInterface
    public interface DoubleBiPredicate
    {
        boolean test(double a, double b);

        default DoubleBiPredicate and(DoubleBiPredicate other) {
            Objects.requireNonNull(other);
            return (a, b) -> test(a, b) && other.test(a, b);
        }

        default DoubleBiPredicate negate() {
            return (a, b) -> !test(a, b);
        }

        default DoubleBiPredicate or(DoubleBiPredicate other) {
            Objects.requireNonNull(other);
            return (a, b) -> test(a, b) || other.test(a, b);
        }
    }


    @FunctionalInterface
    public interface TriPredicate<T1, T2, T3>
    {
        boolean test(T1 t1, T2 t2, T3 t3);

        default TriPredicate<T1, T2, T3> and(TriPredicate<T1, T2, T3> other) {
            Objects.requireNonNull(other);
            return (a, b, c) -> test(a, b, c) && other.test(a, b, c);
        }

        default TriPredicate<T1, T2, T3> negate() {
            return (a, b, c) -> !test(a, b, c);
        }

        default TriPredicate<T1, T2, T3> or(TriPredicate<T1, T2, T3> other) {
            Objects.requireNonNull(other);
            return (a, b, c) -> test(a, b, c) || other.test(a, b, c);
        }
    }

    @FunctionalInterface
    public interface IntTriPredicate
    {
        boolean test(int a, int b, int c);

        default IntTriPredicate and(IntTriPredicate other) {
            Objects.requireNonNull(other);
            return (a, b, c) -> test(a, b, c) && other.test(a, b, c);
        }

        default IntTriPredicate negate() {
            return (a, b, c) -> !test(a, b, c);
        }

        default IntTriPredicate or(IntTriPredicate other) {
            Objects.requireNonNull(other);
            return (a, b, c) -> test(a, b, c) || other.test(a, b, c);
        }
    }

    @FunctionalInterface
    public interface LongTriPredicate
    {
        boolean test(long a, long b, long c);

        default LongTriPredicate and(LongTriPredicate other) {
            Objects.requireNonNull(other);
            return (a, b, c) -> test(a, b, c) && other.test(a, b, c);
        }

        default LongTriPredicate negate() {
            return (a, b, c) -> !test(a, b, c);
        }

        default LongTriPredicate or(LongTriPredicate other) {
            Objects.requireNonNull(other);
            return (a, b, c) -> test(a, b, c) || other.test(a, b, c);
        }
    }

    @FunctionalInterface
    public interface DoubleTriPredicate
    {
        boolean test(double a, double b, double c);

        default DoubleTriPredicate and(DoubleTriPredicate other) {
            Objects.requireNonNull(other);
            return (a, b, c) -> test(a, b, c) && other.test(a, b, c);
        }

        default DoubleTriPredicate negate() {
            return (a, b, c) -> !test(a, b, c);
        }

        default DoubleTriPredicate or(DoubleTriPredicate other) {
            Objects.requireNonNull(other);
            return (a, b, c) -> test(a, b, c) || other.test(a, b, c);
        }
    }

    @FunctionalInterface
    public interface QuadPredicate<T1, T2, T3, T4>
    {
        boolean test(T1 t1, T2 t2, T3 t3, T4 t4);

        default QuadPredicate<T1, T2, T3, T4> and(QuadPredicate<T1, T2, T3, T4> other) {
            Objects.requireNonNull(other);
            return (a, b, c, d) -> test(a, b, c, d) && other.test(a, b, c, d);
        }

        default QuadPredicate<T1, T2, T3, T4> negate() {
            return (a, b, c, d) -> !test(a, b, c, d);
        }

        default QuadPredicate<T1, T2, T3, T4> or(QuadPredicate<T1, T2, T3, T4> other) {
            Objects.requireNonNull(other);
            return (a, b, c, d) -> test(a, b, c, d) || other.test(a, b, c, d);
        }
    }

    @FunctionalInterface
    public interface IntQuadPredicate
    {
        boolean test(int a, int b, int c, int d);

        default IntQuadPredicate and(IntQuadPredicate other) {
            Objects.requireNonNull(other);
            return (a, b, c, d) -> test(a, b, c, d) && other.test(a, b, c, d);
        }

        default IntQuadPredicate negate() {
            return (a, b, c, d) -> !test(a, b, c, d);
        }

        default IntQuadPredicate or(IntQuadPredicate other) {
            Objects.requireNonNull(other);
            return (a, b, c, d) -> test(a, b, c, d) || other.test(a, b, c, d);
        }
    }

    @FunctionalInterface
    public interface LongQuadPredicate
    {
        boolean test(long a, long b, long c, long d);

        default LongQuadPredicate and(LongQuadPredicate other) {
            Objects.requireNonNull(other);
            return (a, b, c, d) -> test(a, b, c, d) && other.test(a, b, c, d);
        }

        default LongQuadPredicate negate() {
            return (a, b, c, d) -> !test(a, b, c, d);
        }

        default LongQuadPredicate or(LongQuadPredicate other) {
            Objects.requireNonNull(other);
            return (a, b, c, d) -> test(a, b, c, d) || other.test(a, b, c, d);
        }
    }

    @FunctionalInterface
    public interface DoubleQuadPredicate
    {
        boolean test(double a, double b, double c, double d);

        default DoubleQuadPredicate and(DoubleQuadPredicate other) {
            Objects.requireNonNull(other);
            return (a, b, c, d) -> test(a, b, c, d) && other.test(a, b, c, d);
        }

        default DoubleQuadPredicate negate() {
            return (a, b, c, d) -> !test(a, b, c, d);
        }

        default DoubleQuadPredicate or(DoubleQuadPredicate other) {
            Objects.requireNonNull(other);
            return (a, b, c, d) -> test(a, b, c, d) || other.test(a, b, c, d);
        }
    }

}
