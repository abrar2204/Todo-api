package com.cm.todoapi.todo;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotNull(message = "Provide a title")
    private String title;
    @NotNull(message = "Provide a description")
    private String description;
    @NotNull(message = "Provide a completed status")
    private boolean completed;
    @NotNull(message = "Provide a created date")
    private LocalDate createdAt;
}
