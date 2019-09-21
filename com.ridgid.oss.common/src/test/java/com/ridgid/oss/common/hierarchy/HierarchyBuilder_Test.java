package com.ridgid.oss.common.hierarchy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.ridgid.oss.common.hierarchy.HierarchyProcessor.Traversal.BREADTH_FIRST;
import static com.ridgid.oss.common.hierarchy.HierarchyProcessor.Traversal.DEPTH_FIRST;
import static com.ridgid.oss.common.hierarchy.HierarchyProcessor.from;
import static com.ridgid.oss.common.hierarchy.Node.viewAs;
import static com.ridgid.oss.common.hierarchy.VisitStatus.OK_CONTINUE;
import static com.ridgid.oss.common.hierarchy.VisitStatus.SKIP_CURRENT_AND_REMAINING_SIBLINGS;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
class HierarchyBuilder_Test
{

    private Person samplePerson;

    @BeforeEach
    void setup() {
        samplePerson
            = new Person("John Smith",
                         new Person("Jane Doe",
                                    new Person("John Smith"),
                                    Arrays.asList(new Pet("Spot",
                                                          new Food[]{
                                                              new Food("Kibble", 3),
                                                              new Food("Milk-Bone", 1)
                                                          }))),
                         null,
                         new Person("Friend 1"),
                         new Person("Friend 2",
                                    new Person("Spouse of Friend 2")));
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
            .include(Person::getSpouse)
            .buildProcessor();
        assertNotNull(h);
    }

    @Test
    void root_can_create_a_complex_hierarchy() {
        HierarchyProcessor<Person> h
            = from(Person.class)
            .includeStream(Person::getFriends,
                           f -> f.include(Person::getSpouse))
            .buildProcessor();
        assertNotNull(h);
    }

    @Test
    void can_traverse_a_complex_hierarchy() {
        HierarchyProcessor<Person> h
            = from(Person.class)
            .include(Person::getSpouse,
                     s -> s
                         .includeCollection(
                             Person::getPets,
                             p -> p.includeArray(Pet::getFavoriteFoods)
                                           )
                    )
            .includeStream(Person::getFriends,
                           friends -> friends
                               .onVisit
                                   (
                                       check -> check
                                           .afterAll((p, ps) -> OK_CONTINUE)
                                   )
                               .include(Person::getSpouse))
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
                "Milk-Bone",
                "Spouse of Friend 2"
                         ),
            names,
            () -> "Breadth-First Visited List does not match expected list:\n"
                  + String.join(",", names)
                  + "\n"
                            );

        h = from(Person.class)
            .include(Person::getSpouse,
                     s -> s
                         .includeCollection(
                             Person::getPets,
                             p -> p
                                 .includeArray(
                                     Pet::getFavoriteFoods,
                                     viewAs(Name.class)
                                              )
                                           )
                    )
            .includeStream(Person::getFriends,
                           friends -> friends.onVisit
                               (
                                   check -> check
                                       .afterAll((p, ps) -> OK_CONTINUE)
                                       .beforeAllChildren((p1, p2) -> SKIP_CURRENT_AND_REMAINING_SIBLINGS)
                               )
                                             .include(Person::getSpouse,
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

    @Test
    void can_traverse_a_complex_hierarchy_breadthfirst_and_invokeafterchildren_visitor_properly()
    {
        HierarchyProcessor<Person> h
            = from(Person.class)
            .include(Person::getSpouse,
                     spouse -> spouse.includeCollection(Person::getPets,
                                                        eachPet -> eachPet.includeCollection(Pet::getJobs)
                                                                          .includeArray(Pet::getFavoriteFoods))
                                     .includeStream(Person::getFriends)
                                     .includeCollection(Person::getJobs)
                                     .includeArray(Person::getFavoriteFoods))
            .includeCollection(Person::getPets,
                               eachPet -> eachPet.includeCollection(Pet::getJobs)
                                                 .includeArray(Pet::getFavoriteFoods))
            .includeStream(Person::getFriends,
                           friend -> friend.includeCollection(Person::getPets,
                                                              eachPet -> eachPet.includeCollection(Pet::getJobs)
                                                                                .includeArray(Pet::getFavoriteFoods))
                                           .includeCollection(Person::getJobs)
                                           .includeArray(Person::getFavoriteFoods))
            .includeCollection(Person::getJobs)
            .includeArray(Person::getFavoriteFoods)
            .buildProcessor();
        assertNotNull(h);

        Person p = makePerson("", 1, 1, 3);

        List<String> namesVisited              = new ArrayList<>();
        List<String> namesVisitedAfterChildren = new ArrayList<>();
        h.visit(p,
                addToNamesFound(namesVisited),
                addToNamesFound(namesVisitedAfterChildren),
                BREADTH_FIRST);

        assertIterableEquals(
            Arrays.asList
                (
                    "Person 1",                 // Person
                    "Person 1-1",               //  - Spouse
                    "Pet 1-1",                  //  - Pets
                    "Pet 1-2",
                    "Pet 1-3",
                    "Person 1-2",               //  - Friends
                    "Person 1-3",
                    "Person 1-4",
                    "Job 1-1",                  //  - Jobs
                    "Job 1-2",
                    "Food 1-1",                 //  - Favorite Foods
                    "Food 1-2",
                    "Food 1-3",
                    "Food 1-4",
                    "Food 1-5",
                    "Pet 1-1-1",                //    - Spouse Pets
                    "Pet 1-1-2",
                    "Pet 1-1-3",
                    "Person 1-1-2",             //    - Spouse Friends
                    "Person 1-1-3",
                    "Person 1-1-4",
                    "Job 1-1-1",                //    - Spouse Jobs
                    "Job 1-1-2",
                    "Food 1-1-1",               //    - Spouse Favorite Foods
                    "Food 1-1-2",
                    "Food 1-1-3",
                    "Food 1-1-4",
                    "Food 1-1-5",
                    "Job 1-1-1-1",              //      - Spouse Pet 1 Jobs
                    "Job 1-1-1-2",
                    "Food 1-1-1-1",             //      - Spouse Pet 1 Favorite Foods
                    "Food 1-1-1-2",
                    "Food 1-1-1-3",
                    "Food 1-1-1-4",
                    "Food 1-1-1-5",
                    "Job 1-1-2-1",              //      - Spouse Pet 2 Jobs
                    "Job 1-1-2-2",
                    "Food 1-1-2-1",             //      - Spouse Pet 2 Favorite Foods
                    "Food 1-1-2-2",
                    "Food 1-1-2-3",
                    "Food 1-1-2-4",
                    "Food 1-1-2-5",
                    "Job 1-1-3-1",              //      - Spouse Pet 3 Jobs
                    "Job 1-1-3-2",
                    "Food 1-1-3-1",             //      - Spouse Pet 3 Favorite Foods
                    "Food 1-1-3-2",
                    "Food 1-1-3-3",
                    "Food 1-1-3-4",
                    "Food 1-1-3-5",
                    "Job 1-1-1",                //      - Pet 1 Jobs
                    "Job 1-1-2",
                    "Food 1-1-1",               //      - Pet 1 Favorite Foods
                    "Food 1-1-2",
                    "Food 1-1-3",
                    "Food 1-1-4",
                    "Food 1-1-5",
                    "Job 1-2-1",                //      - Pet 2 Jobs
                    "Job 1-2-2",
                    "Food 1-2-1",               //      - Pet 2 Favorite Foods
                    "Food 1-2-2",
                    "Food 1-2-3",
                    "Food 1-2-4",
                    "Food 1-2-5",
                    "Job 1-3-1",                //      - Pet 3 Jobs
                    "Job 1-3-2",
                    "Food 1-3-1",               //      - Pet 3 Favorite Foods
                    "Food 1-3-2",
                    "Food 1-3-3",
                    "Food 1-3-4",
                    "Food 1-3-5",
                    "Pet 1-2-1",               //      - Friend 1, Pets
                    "Pet 1-2-2",
                    "Pet 1-2-3",
                    "Job 1-2-1",               //      - Friend 1, Jobs
                    "Job 1-2-2",
                    "Food 1-2-1",              //      - Friend 1, Foods
                    "Food 1-2-2",
                    "Food 1-2-3",
                    "Food 1-2-4",
                    "Food 1-2-5",
                    "Job 1-2-1-1",             //        - Friend 1, Pet1, Jobs
                    "Job 1-2-1-2",
                    "Food 1-2-1-1",            //        - Friend 1, Pet1, Foods
                    "Food 1-2-1-2",
                    "Food 1-2-1-3",
                    "Food 1-2-1-4",
                    "Food 1-2-1-5",
                    "Job 1-2-2-1",             //        - Friend 1, Pet2, Jobs
                    "Job 1-2-2-2",
                    "Food 1-2-2-1",            //        - Friend 1, Pet2, Foods
                    "Food 1-2-2-2",
                    "Food 1-2-2-3",
                    "Food 1-2-2-4",
                    "Food 1-2-2-5",
                    "Job 1-2-3-1",             //        - Friend 1, Pet3, Jobs
                    "Job 1-2-3-2",
                    "Food 1-2-3-1",            //        - Friend 1, Pet3, Foods
                    "Food 1-2-3-2",
                    "Food 1-2-3-3",
                    "Food 1-2-3-4",
                    "Food 1-2-3-5",
                    "Pet 1-3-1",               //      - Friend 2, Pets
                    "Pet 1-3-2",
                    "Pet 1-3-3",
                    "Job 1-3-1",               //      - Friend 2, Jobs
                    "Job 1-3-2",
                    "Food 1-3-1",              //      - Friend 2, Foods
                    "Food 1-3-2",
                    "Food 1-3-3",
                    "Food 1-3-4",
                    "Food 1-3-5",
                    "Job 1-3-1-1",             //        - Friend 2, Pet1, Jobs
                    "Job 1-3-1-2",
                    "Food 1-3-1-1",            //        - Friend 2, Pet1, Foods
                    "Food 1-3-1-2",
                    "Food 1-3-1-3",
                    "Food 1-3-1-4",
                    "Food 1-3-1-5",
                    "Job 1-3-2-1",             //        - Friend 2, Pet2, Jobs
                    "Job 1-3-2-2",
                    "Food 1-3-2-1",            //        - Friend 2, Pet2, Foods
                    "Food 1-3-2-2",
                    "Food 1-3-2-3",
                    "Food 1-3-2-4",
                    "Food 1-3-2-5",
                    "Job 1-3-3-1",             //        - Friend 2, Pet3, Jobs
                    "Job 1-3-3-2",
                    "Food 1-3-3-1",            //        - Friend 2, Pet3, Foods
                    "Food 1-3-3-2",
                    "Food 1-3-3-3",
                    "Food 1-3-3-4",
                    "Food 1-3-3-5",
                    "Pet 1-4-1",               //      - Friend 3, Pets
                    "Pet 1-4-2",
                    "Pet 1-4-3",
                    "Job 1-4-1",               //      - Friend 3, Jobs
                    "Job 1-4-2",
                    "Food 1-4-1",              //      - Friend 3, Foods
                    "Food 1-4-2",
                    "Food 1-4-3",
                    "Food 1-4-4",
                    "Food 1-4-5",
                    "Job 1-4-1-1",             //        - Friend 3, Pet1, Jobs
                    "Job 1-4-1-2",
                    "Food 1-4-1-1",            //        - Friend 3, Pet1, Foods
                    "Food 1-4-1-2",
                    "Food 1-4-1-3",
                    "Food 1-4-1-4",
                    "Food 1-4-1-5",
                    "Job 1-4-2-1",             //        - Friend 3, Pet2, Jobs
                    "Job 1-4-2-2",
                    "Food 1-4-2-1",            //        - Friend 3, Pet2, Foods
                    "Food 1-4-2-2",
                    "Food 1-4-2-3",
                    "Food 1-4-2-4",
                    "Food 1-4-2-5",
                    "Job 1-4-3-1",             //        - Friend 3, Pet3, Jobs
                    "Job 1-4-3-2",
                    "Food 1-4-3-1",            //        - Friend 3, Pet3, Foods
                    "Food 1-4-3-2",
                    "Food 1-4-3-3",
                    "Food 1-4-3-4",
                    "Food 1-4-3-5"
                ),
            namesVisited,
            () -> "Breadth-First Visited List does not match expected list:\n"
                  + String.join(",", namesVisited)
                  + "\n");

        assertIterableEquals(
            Arrays.asList
                (
                    "Job 1-1-1-1",              //      - Spouse Pet 1 Jobs
                    "Job 1-1-1-2",
                    "Food 1-1-1-1",             //      - Spouse Pet 1 Favorite Foods
                    "Food 1-1-1-2",
                    "Food 1-1-1-3",
                    "Food 1-1-1-4",
                    "Food 1-1-1-5",
                    "Pet 1-1-1",                //    - Spouse Pet 1
                    "Job 1-1-2-1",              //      - Spouse Pet 2 Jobs
                    "Job 1-1-2-2",
                    "Food 1-1-2-1",             //      - Spouse Pet 2 Favorite Foods
                    "Food 1-1-2-2",
                    "Food 1-1-2-3",
                    "Food 1-1-2-4",
                    "Food 1-1-2-5",
                    "Pet 1-1-2",                //    - Spouse Pet 2
                    "Job 1-1-3-1",              //      - Spouse Pet 3 Jobs
                    "Job 1-1-3-2",
                    "Food 1-1-3-1",             //      - Spouse Pet 3 Favorite Foods
                    "Food 1-1-3-2",
                    "Food 1-1-3-3",
                    "Food 1-1-3-4",
                    "Food 1-1-3-5",
                    "Pet 1-1-3",                //    - Spouse Pet 3
                    "Person 1-1-2",             //    - Spouse Friends
                    "Person 1-1-3",
                    "Person 1-1-4",
                    "Job 1-1-1",                //    - Spouse Jobs
                    "Job 1-1-2",
                    "Food 1-1-1",               //    - Spouse Favorite Foods
                    "Food 1-1-2",
                    "Food 1-1-3",
                    "Food 1-1-4",
                    "Food 1-1-5",
                    "Person 1-1",               //  - Spouse
                    "Job 1-1-1",                //      - Pet 1 Jobs
                    "Job 1-1-2",
                    "Food 1-1-1",               //      - Pet 1 Favorite Foods
                    "Food 1-1-2",
                    "Food 1-1-3",
                    "Food 1-1-4",
                    "Food 1-1-5",
                    "Pet 1-1",                  //  - Pet 1
                    "Job 1-2-1",                //      - Pet 2 Jobs
                    "Job 1-2-2",
                    "Food 1-2-1",               //      - Pet 2 Favorite Foods
                    "Food 1-2-2",
                    "Food 1-2-3",
                    "Food 1-2-4",
                    "Food 1-2-5",
                    "Pet 1-2",                  //  - Pet 2
                    "Job 1-3-1",                //      - Pet 3 Jobs
                    "Job 1-3-2",
                    "Food 1-3-1",               //      - Pet 3 Favorite Foods
                    "Food 1-3-2",
                    "Food 1-3-3",
                    "Food 1-3-4",
                    "Food 1-3-5",
                    "Pet 1-3",                  //  - Pet 3
                    "Job 1-2-1-1",              //        - Friend 1, Pet1, Jobs
                    "Job 1-2-1-2",
                    "Food 1-2-1-1",             //        - Friend 1, Pet1, Foods
                    "Food 1-2-1-2",
                    "Food 1-2-1-3",
                    "Food 1-2-1-4",
                    "Food 1-2-1-5",
                    "Pet 1-2-1",               //      - Friend 1, Pet 1
                    "Job 1-2-2-1",             //        - Friend 1, Pet2, Jobs
                    "Job 1-2-2-2",
                    "Food 1-2-2-1",            //        - Friend 1, Pet2, Foods
                    "Food 1-2-2-2",
                    "Food 1-2-2-3",
                    "Food 1-2-2-4",
                    "Food 1-2-2-5",
                    "Pet 1-2-2",               //      - Friend 1, Pet 2
                    "Job 1-2-3-1",             //        - Friend 1, Pet3, Jobs
                    "Job 1-2-3-2",
                    "Food 1-2-3-1",            //        - Friend 1, Pet3, Foods
                    "Food 1-2-3-2",
                    "Food 1-2-3-3",
                    "Food 1-2-3-4",
                    "Food 1-2-3-5",
                    "Pet 1-2-3",               //      - Friend 1, Pet 3
                    "Job 1-2-1",               //      - Friend 1, Jobs
                    "Job 1-2-2",
                    "Food 1-2-1",              //      - Friend 1, Foods
                    "Food 1-2-2",
                    "Food 1-2-3",
                    "Food 1-2-4",
                    "Food 1-2-5",
                    "Person 1-2",               //  - Friend 1
                    "Job 1-3-1-1",              //        - Friend 2, Pet1, Jobs
                    "Job 1-3-1-2",
                    "Food 1-3-1-1",             //        - Friend 2, Pet1, Foods
                    "Food 1-3-1-2",
                    "Food 1-3-1-3",
                    "Food 1-3-1-4",
                    "Food 1-3-1-5",
                    "Pet 1-3-1",                //      - Friend 2, Pet 1
                    "Job 1-3-2-1",              //        - Friend 2, Pet2, Jobs
                    "Job 1-3-2-2",
                    "Food 1-3-2-1",             //        - Friend 2, Pet2, Foods
                    "Food 1-3-2-2",
                    "Food 1-3-2-3",
                    "Food 1-3-2-4",
                    "Food 1-3-2-5",
                    "Pet 1-3-2",                //      - Friend 2, Pet 2
                    "Job 1-3-3-1",              //        - Friend 2, Pet3, Jobs
                    "Job 1-3-3-2",
                    "Food 1-3-3-1",             //        - Friend 2, Pet3, Foods
                    "Food 1-3-3-2",
                    "Food 1-3-3-3",
                    "Food 1-3-3-4",
                    "Food 1-3-3-5",
                    "Pet 1-3-3",                //      - Friend 2, Pet 3
                    "Job 1-3-1",                //      - Friend 2, Jobs
                    "Job 1-3-2",
                    "Food 1-3-1",               //      - Friend 2, Foods
                    "Food 1-3-2",
                    "Food 1-3-3",
                    "Food 1-3-4",
                    "Food 1-3-5",
                    "Person 1-3",               //  - Friend 2
                    "Job 1-4-1-1",              //        - Friend 3, Pet1, Jobs
                    "Job 1-4-1-2",
                    "Food 1-4-1-1",             //        - Friend 3, Pet1, Foods
                    "Food 1-4-1-2",
                    "Food 1-4-1-3",
                    "Food 1-4-1-4",
                    "Food 1-4-1-5",
                    "Pet 1-4-1",                //      - Friend 3, Pet 1
                    "Job 1-4-2-1",              //        - Friend 3, Pet2, Jobs
                    "Job 1-4-2-2",
                    "Food 1-4-2-1",             //        - Friend 3, Pet2, Foods
                    "Food 1-4-2-2",
                    "Food 1-4-2-3",
                    "Food 1-4-2-4",
                    "Food 1-4-2-5",
                    "Pet 1-4-2",                //      - Friend 3, Pet 2
                    "Job 1-4-3-1",              //        - Friend 3, Pet3, Jobs
                    "Job 1-4-3-2",
                    "Food 1-4-3-1",             //        - Friend 3, Pet3, Foods
                    "Food 1-4-3-2",
                    "Food 1-4-3-3",
                    "Food 1-4-3-4",
                    "Food 1-4-3-5",
                    "Pet 1-4-3",                //      - Friend 3, Pet 3
                    "Job 1-4-1",                //      - Friend 3, Jobs
                    "Job 1-4-2",
                    "Food 1-4-1",               //      - Friend 3, Foods
                    "Food 1-4-2",
                    "Food 1-4-3",
                    "Food 1-4-4",
                    "Food 1-4-5",
                    "Person 1-4",               //  - Friend 3
                    "Job 1-1",                  //  - Jobs
                    "Job 1-2",
                    "Food 1-1",                 //  - Favorite Foods
                    "Food 1-2",
                    "Food 1-3",
                    "Food 1-4",
                    "Food 1-5",
                    "Person 1"                  // Person
                ),
            namesVisitedAfterChildren,
            () -> "Breadth-First Visited AFTER CHILDREN List does not match expected list:\n"
                  + String.join(",", namesVisitedAfterChildren)
                  + "\n");
    }

    private Person makePerson(String path, int idx, int level, int maxLevel) {
        if ( level > maxLevel ) return null;
        path  = path + (path.isEmpty() ? "" : "-") + idx;
        level = level + 1;
        return new Person("Person " + path,
                          makePerson(path, 1, level, maxLevel),
                          makePets(path, level),
                          makeJobs(path, level),
                          makeFoods(path, level),
                          makePerson(path, 2, level, maxLevel),
                          makePerson(path, 3, level, maxLevel),
                          makePerson(path, 4, level, maxLevel));
    }

    private List<Pet> makePets(String path, int level) {
        return IntStream.range(1, 4)
                        .mapToObj(i -> path + "-" + i)
                        .map(p -> new Pet("Pet " + p,
                                          makeFoods(p, level + 1),
                                          makeJobs(p, level + 1)))
                        .collect(toList());
    }

    @SuppressWarnings("unused")
    private List<Job> makeJobs(String path, int level) {
        return IntStream.range(1, 3)
                        .mapToObj(i -> path + "-" + i)
                        .map(p -> new Job("Job " + p))
                        .collect(toList());
    }

    private Food[] makeFoods(String path, int level) {
        return IntStream.range(1, 6)
                        .mapToObj(i -> path + "-" + i)
                        .map(p -> new Food("Food " + p, level))
                        .toArray(Food[]::new);
    }

    private GeneralVisitHandler addToNamesFound(List<String> names) {
        return (p, c) -> {
            if ( c instanceof Name )
                names.add(((Name) c).getName());
            return OK_CONTINUE;
        };
    }

    @SuppressWarnings("unused")
    private interface Name
    {
        String getName();
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    private static class Person implements Name
    {
        private String    name;
        private Person    spouse;
        private List<Pet> pets;
        private Person[]  friends;
        private List<Job> jobs;
        private Food[]    favoriteFoods;

        public Person(String name) {
            this.name = name;
        }

        public Person(String name, Person spouse) {
            this.name   = name;
            this.spouse = spouse;
        }

        public Person(String name,
                      Person spouse,
                      List<Pet> pets,
                      Person... friends)
        {
            this.name    = name;
            this.spouse  = spouse;
            this.pets    = pets;
            this.friends = friends;
        }

        public Person(String name,
                      Person spouse,
                      List<Pet> pets,
                      List<Job> jobs,
                      Food[] favoriteFoods,
                      Person... friends)
        {
            this.name          = name;
            this.spouse        = spouse;
            this.pets          = pets;
            this.jobs          = jobs;
            this.favoriteFoods = favoriteFoods;
            this.friends       = friends;
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

        public List<Job> getJobs() {
            return jobs;
        }

        public Food[] getFavoriteFoods() { return favoriteFoods; }

        public Stream<Person> getFriends() {
            return friends == null ? Stream.empty() : Arrays.stream(friends);
        }
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    private static class Job implements Name
    {
        private String name;

        public Job(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    private static class Pet implements Name
    {
        String name;
        Food[] favoriteFoods;
        private List<Job> jobs;

        public Pet(String name) {
            this.name = name;
        }

        public Pet(String name,
                   List<Job> jobs)
        {
            this.name = name;
            this.jobs = jobs;
        }

        public Pet(String name,
                   Food[] favoriteFoods)
        {
            this.name          = name;
            this.favoriteFoods = favoriteFoods;
        }

        public Pet(String name,
                   Food[] favoriteFoods,
                   List<Job> jobs)
        {
            this.name          = name;
            this.favoriteFoods = favoriteFoods;
            this.jobs          = jobs;
        }

        @SuppressWarnings("unused")
        public String getName() {
            return name;
        }

        public Food[] getFavoriteFoods() {
            return favoriteFoods;
        }

        public List<Job> getJobs() {
            return jobs;
        }
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    private static class Food implements Name
    {
        String name;
        int    servingSizeOz;

        public Food(String name) {
            this.name = name;
        }

        public Food(String name, int servingSizeOz) {
            this.name          = name;
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
