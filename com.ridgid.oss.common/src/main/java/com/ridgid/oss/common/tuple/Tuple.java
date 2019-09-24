package com.ridgid.oss.common.tuple;

import com.ridgid.oss.common.tuple.Tuple.Implementation.Tuple0Impl;
import com.ridgid.oss.common.tuple.Tuple.Implementation.Tuple1Impl;
import com.ridgid.oss.common.tuple.Tuple.Implementation.Tuple2Impl;
import com.ridgid.oss.common.tuple.Tuple.Implementation.Tuple3Impl;
import com.ridgid.oss.common.tuple.Tuple.Implementation.Tuple4Impl;
import com.ridgid.oss.common.tuple.Tuple.Implementation.Tuple5Impl;

import java.util.stream.Stream;

@SuppressWarnings("unused")
public interface Tuple
{
    byte getNumberOfElements();

   @SuppressWarnings("OptionalGetWithoutIsPresent")
   default Class<?> getType(int elementNumber){
       return getTypes().skip(elementNumber)
                        .findFirst()
                        .get();
   }

    Stream<Class<?>> getTypes();

   @SuppressWarnings("OptionalGetWithoutIsPresent")
   default Object getValue(int elementNumber){
       return getValues().skip(elementNumber)
                         .findFirst()
                         .get();
   }

   Stream<Object> getValues();


   interface Tuple0 extends Tuple
   {
       @Override
       default byte getNumberOfElements() {return 0;}

       @Override
       default Stream<Class<?>> getTypes() {return Stream.empty();}

       @Override
       default Stream<Object> getValues() {return Stream.empty();}
   }

   interface Tuple1<T> extends Tuple {
       @Override
       default byte getNumberOfElements() {return 1;}

      @SuppressWarnings("unchecked")
      default T getFirst(){
           return (T)getValue(0);
       }
   }

    interface Tuple2<T1, T2> extends Tuple1<T1> {
        @Override
        default byte getNumberOfElements() {return 2;}

        @SuppressWarnings("unchecked")
        default T2 getSecond(){
           return (T2)getValue(1);
        }
    }

    interface Tuple3<T1, T2, T3> extends Tuple2<T1, T2> {
        @Override
        default byte getNumberOfElements() {return 3;}

        @SuppressWarnings("unchecked")
        default T3 getThird(){
            return (T3)getValue(2);
        }
    }

    interface Tuple4<T1, T2, T3, T4> extends Tuple3<T1, T2, T3> {
        @Override
        default byte getNumberOfElements() {return 4;}

        @SuppressWarnings("unchecked")
        default T4 getFourth(){
            return (T4)getValue(3);
        }
    }

    interface Tuple5<T1, T2, T3, T4, T5> extends Tuple4<T1, T2, T3, T4> {
        @Override
        default byte getNumberOfElements() {return 5;}

        @SuppressWarnings("unchecked")
        default T5 getFifth(){
            return (T5)getValue(4);
        }
    }

    interface Tuple6<T1, T2, T3, T4, T5, T6> extends Tuple5<T1, T2, T3, T4, T5> {
        @Override
        default byte getNumberOfElements() {return 6;}

        @SuppressWarnings("unchecked")
        default T6 getSixth(){
            return (T6)getValue(5);
        }
    }

    static Tuple0 of(){
       return new Tuple0Impl();
    }

    static <T>Tuple1 of(T t){
        return new Tuple1Impl<T>(t);
    }

    static <T1, T2>Tuple2 of(T1 t1, T2 t2){
        return new Tuple2Impl<T1, T2>(t1, t2);
    }

    static <T1, T2, T3>Tuple3 of(T1 t1, T2 t2, T3 t3){
        return new Tuple3Impl<T1, T2, T3>(t1, t2, t3);
    }

    static <T1, T2, T3, T4>Tuple4 of(T1 t1, T2 t2, T3 t3, T4 t4){
        return new Tuple4Impl<T1, T2, T3, T4>(t1, t2, t3, t4);
    }

    static <T1, T2, T3, T4, T5>Tuple5 of(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5){
        return new Tuple5Impl<T1, T2, T3, T4, T5>(t1, t2, t3, t4, t5);
    }

    final class Implementation
    {
        private Implementation() {}

        static class Tuple0Impl implements Tuple0
        {
        }

        static class Tuple1Impl<T> implements Tuple1<T>
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
            public Stream<Object> getValues() {
                return Stream.of(value);
            }

            @Override
            public T getFirst() {
                return value;
            }
        }

        static class Tuple2Impl<T1, T2> implements Tuple2<T1, T2>
        {
            private T1 value;
            private T2 value2;

            public Tuple2Impl(T1 value, T2 value2) {
                this.value  = value;
                this.value2 = value2;
            }

            @Override
            public Stream<Class<?>> getTypes() {
                return Stream.of(value2.getClass());
            }

            @Override
            public Stream<Object> getValues() {
                return Stream.of(value2);
            }

            @Override
            public T1 getFirst() {
                return value;
            }

            @Override
            public T2 getSecond() {
                return value2;
            }
        }

        static class Tuple3Impl<T1, T2, T3> implements Tuple3<T1, T2, T3>{

            private T1 value1;
            private T2 value2;
            private T3 value3;

            public Tuple3Impl(T1 value1, T2 value2, T3 value3) {
                this.value1 = value1;
                this.value2 = value2;
                this.value3 = value3;
            }

            @Override
            public Stream<Class<?>> getTypes() {
                return Stream.of(value3.getClass());
            }

            @Override
            public Stream<Object> getValues() {
                return Stream.of(value3);
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
        }

        @SuppressWarnings("WeakerAccess")
        static class Tuple4Impl<T1, T2, T3, T4> implements Tuple4<T1, T2, T3, T4>{

            private T1 value1;
            private T2 value2;
            private T3 value3;
            private T4 value4;

            public Tuple4Impl(T1 value1, T2 value2, T3 value3, T4 value4) {
                this.value1 = value1;
                this.value2 = value2;
                this.value3 = value3;
                this.value4 = value4;
            }

            @Override
            public Stream<Class<?>> getTypes() {
                return Stream.of(value3.getClass());
            }

            @Override
            public Stream<Object> getValues() {
                return Stream.of(value3);
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
            public T4 getFourth() {
                return value4;
            }
        }

        @SuppressWarnings("WeakerAccess")
        static class Tuple5Impl<T1, T2, T3, T4, T5> implements Tuple5<T1, T2, T3, T4, T5>{

            private T1 value1;
            private T2 value2;
            private T3 value3;
            private T4 value4;
            private T5 value5;

            public Tuple5Impl(T1 value1, T2 value2, T3 value3, T4 value4, T5 value5) {
                this.value1 = value1;
                this.value2 = value2;
                this.value3 = value3;
                this.value4 = value4;
                this.value5 = value5;
            }

            @Override
            public Stream<Class<?>> getTypes() {
                return Stream.of(value3.getClass());
            }

            @Override
            public Stream<Object> getValues() {
                return Stream.of(value3);
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
            public T4 getFourth() {
                return value4;
            }

            @Override
            public T5 getFifth() {
                return value5;
            }
        }
    }
}

