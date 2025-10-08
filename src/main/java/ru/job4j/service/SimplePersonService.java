package ru.job4j.service;

import org.hibernate.ObjectNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.job4j.model.Person;
import ru.job4j.repository.PersonRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SimplePersonService implements PersonService {
    private final PersonRepository personRepository;

    public SimplePersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public Person save(Person person) {
        if (person.getLogin() == null || person.getLogin().isBlank()) {
            throw new IllegalArgumentException("Login cannot be null or empty");
        }
        if (person.getPassword() == null || person.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return personRepository.save(person);
    }

    @Override
    public void update(Person person) {
        if (!personRepository.existsById(person.getId())) {
            throw new ObjectNotFoundException("Person with id", person.getId());
        }
        if (person.getLogin() == null || person.getLogin().isBlank()) {
            throw new IllegalArgumentException("Login cannot be null or empty");
        }
        if (person.getPassword() == null || person.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        personRepository.save(person);
    }

    @Override
    public Optional<Person> findById(int id) {
        return personRepository.findById(id);
    }

    @Override
    public void deleteById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Id cannot be negative or zero");
        }
        if (!personRepository.existsById(id)) {
            throw new ObjectNotFoundException("Person with id", id);
        }
        personRepository.deleteById(id);
    }

    @Override
    public List<Person> findAll() {
        List<Person> persons = new ArrayList<>();
        personRepository.findAll().forEach(persons::add);
        return persons;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Person person = personRepository.findPersonByLogin(username);
        if (person == null) {
            throw new UsernameNotFoundException(username);
        }
        return new User(person.getLogin(), person.getPassword(), new ArrayList<>());
    }
}
