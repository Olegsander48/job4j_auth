package ru.job4j.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.job4j.validation.Operation;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull(message = "Id must be not null",
            groups = {Operation.OnUpdateWithLogin.class, Operation.OnUpdateWithPassword.class,
                        Operation.OnUpdatePartially.class})
    private Integer id;

    @NotBlank(message = "Login must be not null or empty",
            groups = {Operation.OnUpdateWithLogin.class, Operation.OnCreate.class})
    private String login;

    @NotBlank(message = "Password must be not null or empty",
            groups = {Operation.OnUpdateWithPassword.class, Operation.OnCreate.class})
    private String password;
}
