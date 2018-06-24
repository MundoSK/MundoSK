package com.pie.tlatoani.Util.Collections;

import com.google.common.collect.ImmutableList;
import com.pie.tlatoani.Core.Static.Logging;
import com.pie.tlatoani.Core.Static.Utilities;

import java.util.*;

/**
 * Created by Tlatoani on 12/8/17.
 */
public class ImmutableGroupedList<E, G> extends AbstractList<E> {
    private final ImmutableList<E> list;
    private final ImmutableList<GroupIdentifier<G>> groupIDs;

    public final Comparator<G> keyComparator;

    private ImmutableGroupedList(ImmutableList<E> list, ImmutableList<GroupIdentifier<G>> groupIDs, Comparator<G> keyComparator) {
        super();
        this.list = list;
        this.groupIDs = groupIDs;
        this.keyComparator = keyComparator;
    }

    @Override
    public E get(int index) {
        return list.get(index);
    }

    @Override
    public int size() {
        return list.size();
    }

    public List<E> getGroup(G g) {
        return Utilities
                .binarySearchList(groupIDs, g, (gVal, id) -> keyComparator.compare(gVal, id.key))
                .map(Group::new)
                .orElse(null);
    }

    public List<List<E>> getAllGroups() {
        return new GroupList();
    }

    public List<G> getGroupKeys() {
        return new GroupKeyList();
    }

    private static class GroupIdentifier<G> {
        public final G key;
        public final int start; //inclusive
        public final int end; //exclusive

        GroupIdentifier(G key, int start, int end) {
            this.key = key;
            this.start = start;
            this.end = end;
        }
    }

    private class Group extends AbstractList<E> {
        private final GroupIdentifier<G> identifier;

        Group(GroupIdentifier<G> identifier) {
            this.identifier = identifier;
        }

        @Override
        public E get(int index) {
            if (index < 0 || index >= size()) {
                throw new IndexOutOfBoundsException();
            }
            return list.get(index + identifier.start);
        }

        @Override
        public int size() {
            return identifier.end - identifier.start;
        }
    }

    private class GroupList extends AbstractList<List<E>> {

        @Override
        public List<E> get(int index) {
            return new Group(groupIDs.get(index));
        }

        @Override
        public int size() {
            return groupIDs.size();
        }
    }

    private class GroupKeyList extends AbstractList<G> {

        @Override
        public G get(int index) {
            return groupIDs.get(index).key;
        }

        @Override
        public int size() {
            return groupIDs.size();
        }
    }

    public static class OrderedBuilder<E, G> {
        public final Comparator<E> elemComparator;
        public final Comparator<G> keyComparator;

        private final TreeMap<G, Set<E>> setMap;

        public OrderedBuilder(Comparator<E> elemComparator, Comparator<G> keyComparator) {
            this.elemComparator = elemComparator;
            this.keyComparator = keyComparator;
            this.setMap = new TreeMap<G, Set<E>>(keyComparator);
        }

        public void add(G key, E elem) {
            Set<E> set = setMap.computeIfAbsent(key, __ -> new TreeSet<E>(elemComparator));
            set.add(elem);
        }

        public ImmutableGroupedList<E, G> build() {
            Logging.debug(this, "Building an ImmutableGroupedList");
            ImmutableList.Builder<E> listBuilder = ImmutableList.builder();
            ImmutableList.Builder<GroupIdentifier<G>> groupIDsBuilder = ImmutableList.builder();
            int prevElems = 0;
            for (Map.Entry<G, Set<E>> entry : setMap.entrySet()) {
                Logging.debug(this, "Adding an entry: " + entry);
                listBuilder.addAll(entry.getValue());
                groupIDsBuilder.add(new GroupIdentifier<G>(entry.getKey(), prevElems, prevElems += entry.getValue().size()));
            }
            return new ImmutableGroupedList<E, G>(listBuilder.build(), groupIDsBuilder.build(), keyComparator);
        }
    }
}
