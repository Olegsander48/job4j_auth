package ru.job4j.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.hibernate.ObjectNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.job4j.model.Person;
import ru.job4j.service.PersonService;
import ru.job4j.validation.Operation;

import java.util.List;

@RestController
@RequestMapping("/person")
@Validated
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
    public ResponseEntity<Person> findById(@PathVariable
                                               @Min(value = 1, message = "Id cannot be negative or zero") int id) {
        return personService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ObjectNotFoundException("Person with id", id));
    }

    @PostMapping("/")
    @Validated({Operation.OnCreate.class})
    public ResponseEntity<Person> create(@Valid @RequestBody Person person) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(personService.save(person));
    }

    @PutMapping("/")
    @Validated({Operation.OnUpdateWithLogin.class, Operation.OnUpdateWithPassword.class})
    public ResponseEntity<String> update(@RequestBody @Valid Person person) {
        personService.update(person);
        return ResponseEntity.ok("updated successfully");
    }

    @PatchMapping("/")
    @Validated({Operation.OnUpdatePartially.class})
    public ResponseEntity<String> updatePartially(@RequestBody @Valid Person person) {
        personService.update(person);
        return ResponseEntity.ok("updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable
                                             @Min(value = 1, message = "Id cannot be negative or zero") int id) {
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
