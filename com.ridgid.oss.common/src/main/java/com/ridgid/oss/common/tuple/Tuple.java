package com.ridgid.oss.common.tuple;

import com.ridgid.oss.common.helper.ComparisonHelpers;
import com.ridgid.oss.common.tuple.Tuple.Implementation.Tuple0Impl;
import com.ridgid.oss.common.tuple.Tuple.Implementation.Tuple1Impl;
import com.ridgid.oss.common.tuple.Tuple.Implementation.Tuple2Impl;
import com.ridgid.oss.common.tuple.Tuple.Implementation.Tuple3Impl;
import com.ridgid.oss.common.tuple.Tuple.Implementation.Tuple4Impl;
import com.ridgid.oss.common.tuple.Tuple.Implementation.Tuple5Impl;
import com.ridgid.oss.common.tuple.Tuple.Implementation.Tuple6Impl;

import java.util.Objects;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public interface Tuple extends Comparable<Tuple>
{
    byte getNumberOfElements();

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    default Class<?> getType(int elementNumber) {
        return getTypes().skip(elementNumber)
                         .findFirst()
                         .get();
    }

    Stream<Class<?>> getTypes();

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    default Comparable<?> getValue(int elementNumber) {
        return getValues().skip(elementNumber)
                          .findFirst()
                          .get();
    }

    Stream<Comparable<?>> getValues();

    default int compareTo(Tuple o) {
        return ComparisonHelpers.comparingNullsLast(getValues(), o.getValues());
    }

    interface Tuple0 extends Tuple
    {
        @Override
        default byte getNumberOfElements() {return 0;}

        @Override
        default Stream<Class<?>> getTypes() {return Stream.empty();}

        @Override
        default Stream<Comparable<?>> getValues() {
            return Stream.empty();
        }
    }

    interface Tuple1<T> extends Tuple
    {
        @Override
        default byte getNumberOfElements() { return 1;}

        @SuppressWarnings("unchecked")
        default T getFirst() {
            return (T) getValue(0);
        }
    }

    interface Tuple2<T1, T2> extends Tuple1<T1>
    {
        @Override
        default byte getNumberOfElements() { return 2;}

        @SuppressWarnings("unchecked")
        default T2 getSecond() {
            return (T2) getValue(1);
        }
    }

    interface Tuple3<T1, T2, T3> extends Tuple2<T1, T2>
    {
        @Override
        default byte getNumberOfElements() { return 3;}

        @SuppressWarnings("unchecked")
        default T3 getThird() {
            return (T3) getValue(2);
        }
    }

    interface Tuple4<T1, T2, T3, T4> extends Tuple3<T1, T2, T3>
    {
        @Override
        default byte getNumberOfElements() { return 4;}

        @SuppressWarnings("unchecked")
        default T4 getForth() {
            return (T4) getValue(3);
        }
    }

    interface Tuple5<T1, T2, T3, T4, T5> extends Tuple4<T1, T2, T3, T4>
    {
        @Override
        default byte getNumberOfElements() { return 5;}

        @SuppressWarnings("unchecked")
        default T5 getFifth() {
            return (T5) getValue(4);
        }
    }

    interface Tuple6<T1, T2, T3, T4, T5, T6> extends Tuple5<T1, T2, T3, T4, T5>
    {
        @Override
        default byte getNumberOfElements() { return 6;}

        @SuppressWarnings("unchecked")
        default T6 getSixth() {
            return (T6) getValue(5);
        }
    }

    static Tuple0 of() {
        return new Tuple0Impl();
    }

    static <T extends Comparable<?>> Tuple1 of(T t) {
        return new Tuple1Impl<>(t);
    }

    static <T1 extends Comparable<?>, T2 extends Comparable<?>>
    Tuple2 of(T1 t1, T2 t2) {
        return new Tuple2Impl<>(t1, t2);
    }

    static <T1 extends Comparable<?>, T2 extends Comparable<?>, T3 extends Comparable<?>>
    Tuple3 of(T1 t1, T2 t2, T3 t3) {
        return new Tuple3Impl<>(t1, t2, t3);
    }

    static <T1 extends Comparable<?>, T2 extends Comparable<?>, T3 extends Comparable<?>, T4 extends Comparable<?>>
    Tuple4 of(T1 t1, T2 t2, T3 t3, T4 t4) {
        return new Tuple4Impl<>(t1, t2, t3, t4);
    }

    static <T1 extends Comparable<?>, T2 extends Comparable<?>, T3 extends Comparable<?>, T4 extends Comparable<?>, T5 extends Comparable<?>>
    Tuple5 of(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
        return new Tuple5Impl<>(t1, t2, t3, t4, t5);
    }

    static <T1 extends Comparable<?>, T2 extends Comparable<?>, T3 extends Comparable<?>, T4 extends Comparable<?>, T5 extends Comparable<?>, T6 extends Comparable<?>>
    Tuple5 of(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6) {
        return new Tuple6Impl<>(t1, t2, t3, t4, t5, t6);
    }

    final class Implementation
    {
        private Implementation() {}

        static class Tuple0Impl implements Tuple0
        {
            @Override
            public int hashCode() {
                return 764839172;
            }

            @Override
            public boolean equals(Object obj) {
                return obj instanceof Tuple0Impl;
            }

            @Override
            public String toString() {
                return "()";
            }

            @Override
            public int compareTo(Tuple o) {
                return 0;
            }
        }

        static class Tuple1Impl<T extends Comparable<?>> implements Tuple1<T>
        {
            private T value;

            Tuple1Impl(T value) {
                this.value = value;
            }

            @Override
            public Stream<Class<?>> getTypes() {
                return Stream.of(value.getClass());
            }

            @Override
            public Stream<Comparable<?>> getValues() {
                return Stream.of(value);
            }

            @Override
            public T getFirst() {
                return value;
            }

            @Override
            public boolean equals(Object o) {
                if ( this == o ) return true;
                if ( o == null || getClass() != o.getClass() ) return false;
                Tuple1Impl<?> tuple1 = (Tuple1Impl<?>) o;
                return Objects.equals(value, tuple1.value);
            }

            @Override
            public int hashCode() {
                return Objects.hash(value);
            }

            @Override
            public String toString() {
                return "(" + value + ')';
            }
        }

        static class Tuple2Impl<T1 extends Comparable<?>, T2 extends Comparable<?>>
            implements Tuple2<T1, T2>
        {
            private T1 value1;
            private T2 value2;

            Tuple2Impl(T1 value1, T2 value2) {
                this.value1 = value1;
                this.value2 = value2;
            }

            @Override
            public Stream<Class<?>> getTypes() {
                return Stream.of(value1.getClass(),
                                 value2.getClass());
            }

            @Override
            public Stream<Comparable<?>> getValues() {
                return Stream.of(value1,
                                 value2);
            }

            @Override
            public T1 getFirst() {
                return value1;
            }

            @Override
            public T2 getSecond() {
                return value2;
            }

            @Override
            public boolean equals(Object o) {
                if ( this == o ) return true;
                if ( o == null || getClass() != o.getClass() ) return false;
                Tuple2Impl<?, ?> tuple2 = (Tuple2Impl<?, ?>) o;
                return Objects.equals(value1, tuple2.value1) &&
                       Objects.equals(value2, tuple2.value2);
            }

            @Override
            public int hashCode() {
                return Objects.hash(value1, value2);
            }

            @Override
            public String toString() {
                return "(" + value1 +
                       "," + value2 +
                       ')';
            }
        }

        static class Tuple3Impl<T1 extends Comparable<?>, T2 extends Comparable<?>, T3 extends Comparable<?>>
            implements Tuple3<T1, T2, T3>
        {
            private T1 value1;
            private T2 value2;
            private T3 value3;

            Tuple3Impl(T1 value1,
                       T2 value2,
                       T3 value3)
            {
                this.value1 = value1;
                this.value2 = value2;
                this.value3 = value3;
            }

            @Override
            public Stream<Class<?>> getTypes() {
                return Stream.of(value1.getClass(),
                                 value2.getClass(),
                                 value3.getClass());
            }

            @Override
            public Stream<Comparable<?>> getValues() {
                return Stream.of(value1,
                                 value2,
                                 value3);
            }

            @Override
            public T1 getFirst() {
                return value1;
            }

            @Override
            public T2 getSecond() {
                return value2;
            }

            @Override
            public T3 getThird() {
                return value3;
            }

            @Override
            public boolean equals(Object o) {
                if ( this == o ) return true;
                if ( o == null || getClass() != o.getClass() ) return false;
                Tuple3Impl<?, ?, ?> tuple3 = (Tuple3Impl<?, ?, ?>) o;
                return Objects.equals(value1, tuple3.value1) &&
                       Objects.equals(value2, tuple3.value2) &&
                       Objects.equals(value3, tuple3.value3);
            }

            @Override
            public int hashCode() {
                return Objects.hash(value1, value2, value3);
            }

            @Override
            public String toString() {
                return "(" + value1 +
                       "," + value2 +
                       "," + value3 +
                       ')';
            }
        }

        static class Tuple4Impl<T1 extends Comparable<?>, T2 extends Comparable<?>, T3 extends Comparable<?>, T4 extends Comparable<?>>
            implements Tuple4<T1, T2, T3, T4>
        {
            private T1 value1;
            private T2 value2;
            private T3 value3;
            private T4 value4;

            Tuple4Impl(T1 value1,
                       T2 value2,
                       T3 value3,
                       T4 value4)
            {
                this.value1 = value1;
                this.value2 = value2;
                this.value3 = value3;
                this.value4 = value4;
            }

            @Override
            public Stream<Class<?>> getTypes() {
                return Stream.of(value1.getClass(),
                                 value2.getClass(),
                                 value3.getClass(),
                                 value4.getClass());
            }

            @Override
            public Stream<Comparable<?>> getValues() {
                return Stream.of(value1,
                                 value2,
                                 value3,
                                 value4);
            }

            @Override
            public T1 getFirst() {
                return value1;
            }

            @Override
            public T2 getSecond() {
                return value2;
            }

            @Override
            public T3 getThird() {
                return value3;
            }

            @Override
            public T4 getForth() {
                return value4;
            }

            @Override
            public boolean equals(Object o) {
                if ( this == o ) return true;
                if ( o == null || getClass() != o.getClass() ) return false;
                Tuple4Impl<?, ?, ?, ?> tuple3 = (Tuple4Impl<?, ?, ?, ?>) o;
                return Objects.equals(value1, tuple3.value1) &&
                       Objects.equals(value2, tuple3.value2) &&
                       Objects.equals(value3, tuple3.value3) &&
                       Objects.equals(value4, tuple3.value4);
            }

            @Override
            public int hashCode() {
                return Objects.hash(value1, value2, value3, value4);
            }

            @Override
            public String toString() {
                return "(" + value1 +
                       "," + value2 +
                       "," + value3 +
                       "," + value4 +
                       ')';
            }
        }

        static class Tuple5Impl<T1 extends Comparable<?>, T2 extends Comparable<?>, T3 extends Comparable<?>, T4 extends Comparable<?>, T5 extends Comparable<?>>
            implements Tuple5<T1, T2, T3, T4, T5>
        {
            private T1 value1;
            private T2 value2;
            private T3 value3;
            private T4 value4;
            private T5 value5;

            Tuple5Impl(T1 value1,
                       T2 value2,
                       T3 value3,
                       T4 value4,
                       T5 value5)
            {
                this.value1 = value1;
                this.value2 = value2;
                this.value3 = value3;
                this.value4 = value4;
                this.value5 = value5;
            }

            @Override
            public Stream<Class<?>> getTypes() {
                return Stream.of(value1.getClass(),
                                 value2.getClass(),
                                 value3.getClass(),
                                 value4.getClass(),
                                 value5.getClass());
            }

            @Override
            public Stream<Comparable<?>> getValues() {
                return Stream.of(value1,
                                 value2,
                                 value3,
                                 value4,
                                 value5);
            }

            @Override
            public T1 getFirst() {
                return value1;
            }

            @Override
            public T2 getSecond() {
                return value2;
            }

            @Override
            public T3 getThird() {
                return value3;
            }

            @Override
            public T4 getForth() {
                return value4;
            }

            @Override
            public T5 getFifth() {
                return value5;
            }

            @Override
            public boolean equals(Object o) {
                if ( this == o ) return true;
                if ( o == null || getClass() != o.getClass() ) return false;
                Tuple5Impl<?, ?, ?, ?, ?> tuple3 = (Tuple5Impl<?, ?, ?, ?, ?>) o;
                return Objects.equals(value1, tuple3.value1) &&
                       Objects.equals(value2, tuple3.value2) &&
                       Objects.equals(value3, tuple3.value3) &&
                       Objects.equals(value4, tuple3.value4) &&
                       Objects.equals(value5, tuple3.value5);
            }

            @Override
            public int hashCode() {
                return Objects.hash(value1, value2, value3, value4, value5);
            }

            @Override
            public String toString() {
                return "(" + value1 +
                       "," + value2 +
                       "," + value3 +
                       "," + value4 +
                       "," + value5 +
                       ')';
            }
        }

        static class Tuple6Impl<T1 extends Comparable<?>, T2 extends Comparable<?>, T3 extends Comparable<?>, T4 extends Comparable<?>, T5 extends Comparable<?>, T6 extends Comparable<?>>
            implements Tuple6<T1, T2, T3, T4, T5, T6>
        {
            private T1 value1;
            private T2 value2;
            private T3 value3;
            private T4 value4;
            private T5 value5;
            private T6 value6;

            Tuple6Impl(T1 value1,
                       T2 value2,
                       T3 value3,
                       T4 value4,
                       T5 value5,
                       T6 value6)
            {
                this.value1 = value1;
                this.value2 = value2;
                this.value3 = value3;
                this.value4 = value4;
                this.value5 = value5;
                this.value6 = value6;
            }

            @Override
            public Stream<Class<?>> getTypes() {
                return Stream.of(value1.getClass(),
                                 value2.getClass(),
                                 value3.getClass(),
                                 value4.getClass(),
                                 value5.getClass(),
                                 value6.getClass());
            }

            @Override
            public Stream<Comparable<?>> getValues() {
                return Stream.of(value1,
                                 value2,
                                 value3,
                                 value4,
                                 value5,
                                 value6);
            }

            @Override
            public T1 getFirst() {
                return value1;
            }

            @Override
            public T2 getSecond() {
                return value2;
            }

            @Override
            public T3 getThird() {
                return value3;
            }

            @Override
            public T4 getForth() {
                return value4;
            }

            @Override
            public T5 getFifth() {
                return value5;
            }

            @Override
            public T6 getSixth() {
                return value6;
            }

            @Override
            public boolean equals(Object o) {
                if ( this == o ) return true;
                if ( o == null || getClass() != o.getClass() ) return false;
                Tuple6Impl<?, ?, ?, ?, ?, ?> tuple3 = (Tuple6Impl<?, ?, ?, ?, ?, ?>) o;
                return Objects.equals(value1, tuple3.value1) &&
                       Objects.equals(value2, tuple3.value2) &&
                       Objects.equals(value3, tuple3.value3) &&
                       Objects.equals(value4, tuple3.value4) &&
                       Objects.equals(value5, tuple3.value5) &&
                       Objects.equals(value6, tuple3.value6);
            }

            @Override
            public int hashCode() {
                return Objects.hash(value1, value2, value3, value4, value5, value6);
            }

            @Override
            public String toString() {
                return "(" + value1 +
                       "," + value2 +
                       "," + value3 +
                       "," + value4 +
                       "," + value5 +
                       "," + value6 +
                       ')';
            }
        }
    }
}
