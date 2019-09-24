package com.ridgid.oss.common.tuple;

import java.util.Objects;
import java.util.function.BiFunction;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class Tuple3<T1, T2, T3>
    implements Comparable<Tuple3<T1, T2, T3>> {

    public final T1 left;
    public final T2 right;

    public final BiFunction<T1, T1, Integer> leftComparer;
    public final BiFunction<T2, T2, Integer> rightComparer;

    public Tuple3(T1 left, T2 right) {
        Objects.requireNonNull(left, "Left value must be non-null");
        Objects.requireNonNull(left, "Right value must be non-null");
        this.left = left;
        this.right = right;
        this.leftComparer = makeComparer(left);
        this.rightComparer = makeComparer(right);
    }

    @SuppressWarnings("unchecked")
    private <T> BiFunction<T, T, Integer> makeComparer(T element) {
        return element instanceof Comparable<?>
                ? (o1, o2) -> ((Comparable<T>) o1).compareTo(o2)
                : (o1, o2) -> o1.toString().compareTo(o2.toString());
    }

    public final T1 getLeft() {
        return left;
    }

    public final T2 getRight() {
        return right;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple3<?, ?>)) return false;
        Tuple3<T1, T2> pair = (Tuple3<T1, T2>) o;
        return compareTo(pair) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public String toString() {
        return "Pair{" +
                "left=" + left +
                ", right=" + right +
                '}';
    }

    @Override
    public int compareTo(Tuple3<T1, T2> o) {
        int rv = leftComparer.apply(left, o.left);
        if (rv != 0) return rv;
        return rightComparer.apply(right, o.right);
    }

    public static <L, R> Tuple3<L, R> of(L l, R r) {
        return new Tuple3<>(l, r);
    }
}
