package ru.job4j.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.job4j.model.Person;

import java.util.List;
import java.util.Optional;

public interface PersonService extends UserDetailsService {
    Person save(Person person);

    void update(Person person);

    Optional<Person> findById(int id);

    void deleteById(int id);

    List<Person> findAll();
}
