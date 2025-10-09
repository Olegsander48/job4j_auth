package ru.job4j.service;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.hibernate.ObjectNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.job4j.model.Person;
import ru.job4j.repository.PersonRepository;
import ru.job4j.validation.Operation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SimplePersonService implements PersonService {
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;

    public SimplePersonService(PersonRepository personRepository, PasswordEncoder passwordEncoder, Validator validator) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
    }

    @Override
    public Person save(Person person) {
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        return personRepository.save(person);
    }

    @Override
    public void update(Person person) {
        var personOptional = personRepository.findById(person.getId());
        if (personOptional.isEmpty()) {
            throw new ObjectNotFoundException("Person with id", person.getId());
        }
        if (validator.validate(person, Operation.OnUpdateWithLogin.class).isEmpty()) {
            personOptional.get().setLogin(person.getLogin());
        }
        if (validator.validate(person, Operation.OnUpdateWithPassword.class).isEmpty()) {
            personOptional.get().setPassword(passwordEncoder.encode(person.getPassword()));
        } else {
            throw new ConstraintViolationException("Login and password cannot be null", null);
        }
        personRepository.save(personOptional.get());
    }

    @Override
    public Optional<Person> findById(int id) {
        return personRepository.findById(id);
    }

    @Override
    public void deleteById(int id) {
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
