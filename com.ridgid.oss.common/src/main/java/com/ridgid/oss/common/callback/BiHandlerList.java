package com.ridgid.oss.common.callback;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class BiHandlerList<T1, T2, R> implements List<BiHandler<T1, T2, R>> {

    private final List<BiHandler<T1, T2, R>> handlers = new ArrayList<>();

    public Optional<R> invoke(T1 t1, T2 t2, Predicate<R> returnIf) {
        for (BiHandler<T1, T2, R> handler : handlers) {
            R r = handler.handle(t1, t2);
            try {
                if (returnIf.test(r)) return Optional.ofNullable(r);
            } catch (Exception ex) {
                throw new RuntimeException(
                        "Predicate Test through Exception for: "
                                + t1 + ", " + t2
                                + " and mapped value from visit handler "
                                + handler.getClass().getEnclosingClass()
                                + "::" + handler.getClass().getEnclosingMethod()
                                + " value = "
                                + (r == null ? "(nul)" : r),
                        ex);
            }
        }
        return Optional.empty();
    }

    public Optional<R> invoke(T1 t1, T2 t2) {
        return handlers
                .stream()
                .map(h -> h.handle(t1, t2))
                .reduce((a, b) -> b);
    }

    @Override
    public int size() {
        return handlers.size();
    }

    @Override
    public boolean isEmpty() {
        return handlers.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return handlers.contains(o);
    }

    @Override
    public Iterator<BiHandler<T1, T2, R>> iterator() {
        return handlers.iterator();
    }

    @Override
    public Object[] toArray() {
        return handlers.toArray();
    }

    @SuppressWarnings({"SuspiciousToArrayCall"})
    @Override
    public <T> T[] toArray(T[] a) {
        return handlers.toArray(a);
    }

    @Override
    public boolean add(BiHandler<T1, T2, R> trHandler) {
        return handlers.add(trHandler);
    }

    @Override
    public boolean remove(Object o) {
        return handlers.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return handlers.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends BiHandler<T1, T2, R>> c) {
        return handlers.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends BiHandler<T1, T2, R>> c) {
        return handlers.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return handlers.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return handlers.retainAll(c);
    }

    @Override
    public void replaceAll(UnaryOperator<BiHandler<T1, T2, R>> operator) {
        handlers.replaceAll(operator);
    }

    @Override
    public void sort(Comparator<? super BiHandler<T1, T2, R>> c) {
        handlers.sort(c);
    }

    @Override
    public void clear() {
        handlers.clear();
    }

    @Override
    public BiHandler<T1, T2, R> get(int index) {
        return handlers.get(index);
    }

    @Override
    public BiHandler<T1, T2, R> set(int index, BiHandler<T1, T2, R> element) {
        return handlers.set(index, element);
    }

    @Override
    public void add(int index, BiHandler<T1, T2, R> element) {
        handlers.add(index, element);
    }

    @Override
    public BiHandler<T1, T2, R> remove(int index) {
        return handlers.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return handlers.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return handlers.lastIndexOf(o);
    }

    @Override
    public ListIterator<BiHandler<T1, T2, R>> listIterator() {
        return handlers.listIterator();
    }

    @Override
    public ListIterator<BiHandler<T1, T2, R>> listIterator(int index) {
        return handlers.listIterator(index);
    }

    @Override
    public List<BiHandler<T1, T2, R>> subList(int fromIndex, int toIndex) {
        return handlers.subList(fromIndex, toIndex);
    }

    @Override
    public Spliterator<BiHandler<T1, T2, R>> spliterator() {
        return handlers.spliterator();
    }

    @Override
    public boolean removeIf(Predicate<? super BiHandler<T1, T2, R>> filter) {
        return handlers.removeIf(filter);
    }

    @Override
    public Stream<BiHandler<T1, T2, R>> stream() {
        return handlers.stream();
    }

    @Override
    public Stream<BiHandler<T1, T2, R>> parallelStream() {
        return handlers.parallelStream();
    }

    @Override
    public void forEach(Consumer<? super BiHandler<T1, T2, R>> action) {
        handlers.forEach(action);
    }
}
