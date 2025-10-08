package ru.job4j.service;

import org.hibernate.ObjectNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.job4j.model.Person;
import ru.job4j.repository.PersonRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SimplePersonService implements PersonService {
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    public SimplePersonService(PersonRepository personRepository, PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Person save(Person person) {
        if (isCredentialInvalid(person.getLogin())) {
            throw new IllegalArgumentException("Login cannot be null or empty");
        }
        if (isCredentialInvalid(person.getPassword())) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        return personRepository.save(person);
    }

    @Override
    public void update(Person person) {
        var personOptional = personRepository.findById(person.getId());
        if (personOptional.isEmpty()) {
            throw new ObjectNotFoundException("Person with id", person.getId());
        }
        if (!isCredentialInvalid(person.getLogin()) && isCredentialInvalid(person.getPassword())) {
            personOptional.get().setLogin(person.getLogin());
        } else if (isCredentialInvalid(person.getLogin()) && !isCredentialInvalid(person.getPassword())) {
            personOptional.get().setPassword(passwordEncoder.encode(person.getPassword()));
        } else {
            if (isCredentialInvalid(person.getLogin())) {
                throw new IllegalArgumentException("Login cannot be null or empty");
            }
            if (isCredentialInvalid(person.getPassword())) {
                throw new IllegalArgumentException("Password cannot be null or empty");
            }
        }
        personRepository.save(personOptional.get());
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

    private boolean isCredentialInvalid(String credential) {
        return credential == null || credential.isBlank();
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
