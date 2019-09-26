package com.ridgid.oss.common.collection;

import com.ridgid.oss.common.collection.ComparableList.Implementation.ComparableListImpl;
import com.ridgid.oss.common.helper.ComparisonHelpers;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public interface ComparableList<T extends Comparable<? super T>>
    extends List<T>,
            Comparable<List<T>>
{
    @SuppressWarnings({"unused"})
    @Override
    default int compareTo(List<T> list) {
        return ComparisonHelpers.comparingNullsLast(this, list);
    }

    @SuppressWarnings("unchecked")
    static <T extends Comparable<? super T>> ComparableList<T> from(List<T> list) {
        return new ComparableListImpl(list);
    }

    @SuppressWarnings("unused")
    class Implementation
    {
        static class ComparableListImpl<T extends Comparable<? super T>> implements ComparableList<T>
        {
            private final List<T> list;

            ComparableListImpl(List<T> list) {
                this.list = list;
            }

            @Override
            public int size() {return list.size();}

            @Override
            public boolean isEmpty() {return list.isEmpty();}

            @Override
            public boolean contains(Object o) {return list.contains(o);}

            @Override
            public Iterator<T> iterator() {return list.iterator();}

            @Override
            public Object[] toArray() {return list.toArray();}

            @SuppressWarnings("SuspiciousToArrayCall")
            @Override
            public <T1> T1[] toArray(T1[] a) {return list.toArray(a);}

            @Override
            public boolean add(T t) {return list.add(t);}

            @Override
            public boolean remove(Object o) {return list.remove(o);}

            @Override
            public boolean containsAll(Collection<?> c) {return list.containsAll(c);}

            @Override
            public boolean addAll(Collection<? extends T> c) {return list.addAll(c);}

            @Override
            public boolean addAll(int index, Collection<? extends T> c) {return list.addAll(index, c);}

            @Override
            public boolean removeAll(Collection<?> c) {return list.removeAll(c);}

            @Override
            public boolean retainAll(Collection<?> c) {return list.retainAll(c);}

            @Override
            public void replaceAll(UnaryOperator<T> operator) {list.replaceAll(operator);}

            @Override
            public void sort(Comparator<? super T> c) {list.sort(c);}

            @Override
            public void clear() {list.clear();}

            @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
            @Override
            public boolean equals(Object o) {return list.equals(o);}

            @Override
            public int hashCode() {return list.hashCode();}

            @Override
            public T get(int index) {return list.get(index);}

            @Override
            public T set(int index, T element) {return list.set(index, element);}

            @Override
            public void add(int index, T element) {list.add(index, element);}

            @Override
            public T remove(int index) {return list.remove(index);}

            @Override
            public int indexOf(Object o) {return list.indexOf(o);}

            @Override
            public int lastIndexOf(Object o) {return list.lastIndexOf(o);}

            @Override
            public ListIterator<T> listIterator() {return list.listIterator();}

            @Override
            public ListIterator<T> listIterator(int index) {return list.listIterator(index);}

            @Override
            public List<T> subList(int fromIndex, int toIndex) {return list.subList(fromIndex, toIndex);}

            @Override
            public Spliterator<T> spliterator() {return list.spliterator();}

            @Override
            public boolean removeIf(Predicate<? super T> filter) {return list.removeIf(filter);}

            @Override
            public Stream<T> stream() {return list.stream();}

            @Override
            public Stream<T> parallelStream() {return list.parallelStream();}

            @Override
            public void forEach(Consumer<? super T> action) {list.forEach(action);}

        }
    }


}
