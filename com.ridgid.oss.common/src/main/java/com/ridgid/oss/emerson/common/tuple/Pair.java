package com.ridgid.oss.emerson.common.tuple;

import java.util.Objects;
import java.util.function.BiFunction;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class Pair<L, R> implements Comparable<Pair<L, R>> {

    public final L left;
    public final R right;

    public final BiFunction<L, L, Integer> leftComparer;
    public final BiFunction<R, R, Integer> rightComparer;

    public Pair(L left, R right) {
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

    public final L getLeft() {
        return left;
    }

    public final R getRight() {
        return right;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair<?, ?>)) return false;
        Pair<L, R> pair = (Pair<L, R>) o;
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
    public int compareTo(Pair<L, R> o) {
        int rv = leftComparer.apply(left, o.left);
        if (rv != 0) return rv;
        return rightComparer.apply(right, o.right);
    }

    public static <L, R> Pair<L, R> of(L l, R r) {
        return new Pair<>(l, r);
    }
}
