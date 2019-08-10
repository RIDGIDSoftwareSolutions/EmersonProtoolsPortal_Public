package com.ridgid.oss.common.helper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

import static com.ridgid.oss.common.helper.StreamHelpers.distinctBy;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class StreamHelpers_Test {

    private static class Person {
        final String firstName;
        final String lastName;
        final LocalDate dateOfBirth;

        Person(String firstName, String lastName, LocalDate dateOfBirth) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.dateOfBirth = dateOfBirth;
        }

        String getFirstName() {
            return firstName;
        }

        String getLastName() {
            return lastName;
        }

        LocalDate getDateOfBirth() {
            return dateOfBirth;
        }
    }

    private List<Person> testPersons;

    @BeforeEach
    void setup() {
        testPersons = IntStream.range(0, 120)
                .mapToObj(i -> new Person(
                        "FirstName" + i % 4,
                        "LastName" + i % 3,
                        LocalDate.of(2000 + i % 5, 6, 15)))
                .collect(toList());
    }

    @Test
    void when_distinctBy_is_used_in_a_flatmap_of_a_stream_it_passed_only_distinct_elements_of_the_stream_according_to_the_selected_keys() {
        assertEquals(
                4,
                testPersons.stream()
                        .flatMap(
                                distinctBy(
                                        Person::getFirstName)
                        ).count()
        );
        assertEquals(
                3,
                testPersons.stream()
                        .flatMap(
                                distinctBy(
                                        Person::getLastName)
                        ).count()
        );
        assertEquals(
                12,
                testPersons.stream()
                        .flatMap(
                                distinctBy(
                                        Person::getLastName,
                                        Person::getFirstName)
                        ).count()
        );
        assertEquals(
                12,
                testPersons.stream()
                        .flatMap(
                                distinctBy(
                                        Person::getFirstName,
                                        Person::getLastName)
                        ).count()
        );
        assertEquals(
                5,
                testPersons.stream()
                        .flatMap(
                                distinctBy(
                                        Person::getDateOfBirth)
                        ).count()
        );
        assertEquals(
                60,
                testPersons.stream()
                        .flatMap(
                                distinctBy(
                                        Person::getDateOfBirth,
                                        Person::getFirstName,
                                        Person::getLastName
                                )).count()
        );
        assertEquals(
                60,
                testPersons.stream()
                        .flatMap(
                                distinctBy(
                                        Person::getFirstName,
                                        Person::getDateOfBirth,
                                        Person::getLastName
                                )).count()
        );
        assertEquals(
                60,
                testPersons.stream()
                        .flatMap(
                                distinctBy(
                                        Person::getLastName,
                                        Person::getDateOfBirth,
                                        Person::getFirstName
                                )).count()
        );
    }
}