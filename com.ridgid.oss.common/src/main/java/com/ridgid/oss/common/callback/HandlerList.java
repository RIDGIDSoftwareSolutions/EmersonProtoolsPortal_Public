package com.ridgid.oss.common.callback;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class HandlerList<T, R> implements List<Handler<T, R>> {

    private final List<Handler<T, R>> handlers = new ArrayList<>();

    public Optional<R> invoke(T t, Predicate<R> returnIf) {
        for (Handler<T, R> handler : handlers) {
            R r = handler.handle(t);
            if (returnIf.test(r)) return Optional.ofNullable(r);
        }
        return Optional.empty();
    }

    public Optional<R> invoke(T t) {
        return handlers
                .stream()
                .map(h -> h.handle(t))
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
    public Iterator<Handler<T, R>> iterator() {
        return handlers.iterator();
    }

    @Override
    public Object[] toArray() {
        return handlers.toArray();
    }

    @SuppressWarnings({"TypeParameterHidesVisibleType", "SuspiciousToArrayCall"})
    @Override
    public <T> T[] toArray(T[] a) {
        return handlers.toArray(a);
    }

    @Override
    public boolean add(Handler<T, R> trHandler) {
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
    public boolean addAll(Collection<? extends Handler<T, R>> c) {
        return handlers.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends Handler<T, R>> c) {
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
    public void replaceAll(UnaryOperator<Handler<T, R>> operator) {
        handlers.replaceAll(operator);
    }

    @Override
    public void sort(Comparator<? super Handler<T, R>> c) {
        handlers.sort(c);
    }

    @Override
    public void clear() {
        handlers.clear();
    }

    @Override
    public Handler<T, R> get(int index) {
        return handlers.get(index);
    }

    @Override
    public Handler<T, R> set(int index, Handler<T, R> element) {
        return handlers.set(index, element);
    }

    @Override
    public void add(int index, Handler<T, R> element) {
        handlers.add(index, element);
    }

    @Override
    public Handler<T, R> remove(int index) {
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
    public ListIterator<Handler<T, R>> listIterator() {
        return handlers.listIterator();
    }

    @Override
    public ListIterator<Handler<T, R>> listIterator(int index) {
        return handlers.listIterator(index);
    }

    @Override
    public List<Handler<T, R>> subList(int fromIndex, int toIndex) {
        return handlers.subList(fromIndex, toIndex);
    }

    @Override
    public Spliterator<Handler<T, R>> spliterator() {
        return handlers.spliterator();
    }

    @Override
    public boolean removeIf(Predicate<? super Handler<T, R>> filter) {
        return handlers.removeIf(filter);
    }

    @Override
    public Stream<Handler<T, R>> stream() {
        return handlers.stream();
    }

    @Override
    public Stream<Handler<T, R>> parallelStream() {
        return handlers.parallelStream();
    }

    @Override
    public void forEach(Consumer<? super Handler<T, R>> action) {
        handlers.forEach(action);
    }
}
