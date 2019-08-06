package com.ridgid.oss.common.hierarchy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.ridgid.oss.common.hierarchy.HierarchyProcessor.Traversal.BREADTH_FIRST;
import static com.ridgid.oss.common.hierarchy.HierarchyProcessor.Traversal.DEPTH_FIRST;
import static com.ridgid.oss.common.hierarchy.HierarchyProcessor.from;
import static com.ridgid.oss.common.hierarchy.Node.viewAs;
import static com.ridgid.oss.common.hierarchy.VisitStatus.OK_CONTINUE;
import static com.ridgid.oss.common.hierarchy.VisitStatus.SKIP_CURRENT_AND_REMAINING_SIBLINGS;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
class HierarchyBuilder_Test {

    private Person samplePerson;

    @BeforeEach
    void setup() {
        samplePerson = new Person
                ("John Smith",
                        new Person
                                ("Jane Doe",
                                        new Person("John Smith"),
                                        Arrays.asList(
                                                new Pet("Spot",
                                                        new Food[]{
                                                                new Food("Kibble", 3),
                                                                new Food("Milk-Bone", 1)
                                                        }
                                                )
                                        )
                                ),
                        null,
                        new Person("Friend 1"),
                        new Person
                                ("Friend 2",
                                        new Person("Spouse of Friend 2")
                                )
                );
    }

    @Test
    void root_can_create_with_no_children() {
        HierarchyProcessor<Person> h
                = from(Person.class)
                .buildProcessor();
        assertNotNull(h);
    }

    @Test
    void root_can_create_with_a_singular_child() {
        HierarchyProcessor<Person> h
                = from(Person.class)
                .individual(Person::getSpouse)
                .buildProcessor();
        assertNotNull(h);
    }

    @Test
    void root_can_create_a_complex_hierarchy() {
        HierarchyProcessor<Person> h
                = from(Person.class)
                .stream(Person::getFriends,
                        f -> f.individual(Person::getSpouse))
                .buildProcessor();
        assertNotNull(h);
    }

    @Test
    void can_traverse_a_complex_hierarchy() {
        HierarchyProcessor<Person> h
                = from(Person.class)
                .individual(Person::getSpouse,
                        s -> s
                                .collection(
                                        Person::getPets,
                                        p -> p.array(Pet::getFavoriteFoods)
                                )
                )
                .stream(Person::getFriends,
                        friends -> friends
                                .onVisit
                                        (
                                                check -> check
                                                        .afterAll((p, ps) -> OK_CONTINUE)
                                        )
                                .individual(Person::getSpouse))
                .buildProcessor();
        assertNotNull(h);

        List<String> names = new ArrayList<>();
        h.visit(samplePerson, addToNamesFound(names), DEPTH_FIRST);

        assertIterableEquals(
                Arrays.asList(
                        "John Smith",
                        "Jane Doe",
                        "Spot",
                        "Kibble",
                        "Milk-Bone",
                        "Friend 1",
                        "Friend 2",
                        "Spouse of Friend 2"
                ),
                names,
                () -> "Depth-First Visited List does not match expected list:\n"
                        + String.join(",", names)
                        + "\n"
        );

        names.clear();
        h.visit(samplePerson, addToNamesFound(names), BREADTH_FIRST);

        assertIterableEquals(
                Arrays.asList(
                        "John Smith",
                        "Jane Doe",
                        "Friend 1",
                        "Friend 2",
                        "Spot",
                        "Kibble",
                        "Milk-Bone"
                ),
                names,
                () -> "Breadth-First Visited List does not match expected list:\n"
                        + String.join(",", names)
                        + "\n"
        );

        h = from(Person.class)
                .individual(Person::getSpouse,
                        s -> s
                                .collection(
                                        Person::getPets,
                                        p -> p
                                                .array(
                                                        Pet::getFavoriteFoods,
                                                        viewAs(Name.class)
                                                )
                                )
                )
                .stream(Person::getFriends,
                        friends -> friends.onVisit
                                (
                                        check -> check
                                                .afterAll((p, ps) -> OK_CONTINUE)
                                                .beforeAllChildren((p1, p2) -> SKIP_CURRENT_AND_REMAINING_SIBLINGS)
                                )
                                .individual(Person::getSpouse,
                                        viewAs(Name.class)))
                .buildProcessor();
        assertNotNull(h);

        names.clear();
        h.visit(samplePerson, addToNamesFound(names), DEPTH_FIRST);

        assertIterableEquals(
                Arrays.asList(
                        "John Smith",
                        "Jane Doe",
                        "Spot",
                        "Kibble",
                        "Milk-Bone",
                        "Friend 1"
                ),
                names,
                () -> "Depth-First Visited List does not match expected list:\n"
                        + String.join(",", names)
                        + "\n"
        );

    }

    private GeneralVisitHandler addToNamesFound(List<String> names) {
        return (p, c) -> {
            if (c instanceof Name)
                names.add(((Name) c).getName());
            return OK_CONTINUE;
        };
    }

    @SuppressWarnings("unused")
    private interface Name {
        String getName();
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    private static class Person implements Name {
        private String name;
        private Person spouse;
        private List<Pet> pets;
        private Person[] friends;

        public Person(String name) {
            this.name = name;
        }

        public Person(String name, Person spouse) {
            this.name = name;
            this.spouse = spouse;
        }

        public Person(String name,
                      Person spouse,
                      List<Pet> pets,
                      Person... friends) {
            this.name = name;
            this.spouse = spouse;
            this.pets = pets;
            this.friends = friends;
        }

        public String getName() {
            return name;
        }

        public Person getSpouse() {
            return spouse;
        }

        public List<Pet> getPets() {
            return pets;
        }

        public Stream<Person> getFriends() {
            return friends == null ? Stream.empty() : Arrays.stream(friends);
        }
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    private static class Pet implements Name {
        String name;
        Food[] favoriteFoods;

        public Pet(String name) {
            this.name = name;
        }

        public Pet(String name,
                   Food[] favoriteFoods) {
            this.name = name;
            this.favoriteFoods = favoriteFoods;
        }

        @SuppressWarnings("unused")
        public String getName() {
            return name;
        }

        public Food[] getFavoriteFoods() {
            return favoriteFoods;
        }
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    private static class Food implements Name {
        String name;
        int servingSizeOz;

        public Food(String name) {
            this.name = name;
        }

        public Food(String name, int servingSizeOz) {
            this.name = name;
            this.servingSizeOz = servingSizeOz;
        }

        public String getName() {
            return name;
        }

        public int getServingSizeOz() {
            return servingSizeOz;
        }
    }

}