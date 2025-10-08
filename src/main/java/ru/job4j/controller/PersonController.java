package ru.job4j.controller;

import org.hibernate.ObjectNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.job4j.model.Person;
import ru.job4j.service.PersonService;
import java.util.List;

@RestController
@RequestMapping("/person")
public class PersonController {
    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping("/")
    public List<Person> findAll() {
        return personService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable int id) {
        return personService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ObjectNotFoundException("Person with id", id));
    }

    @PostMapping("/")
    public ResponseEntity<Person> create(@RequestBody Person person) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(personService.save(person));
    }

    @PutMapping("/")
    public ResponseEntity<String> update(@RequestBody Person person) {
        personService.update(person);
        return ResponseEntity.ok("updated successfully");
    }

    @PatchMapping("/")
    public ResponseEntity<String> updateFields(@RequestBody Person person) {
        personService.update(person);
        return ResponseEntity.ok("updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable int id) {
        personService.deleteById(id);
        return ResponseEntity.ok("Deleted successfully");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleException() {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("Person already exists");
    }
}
