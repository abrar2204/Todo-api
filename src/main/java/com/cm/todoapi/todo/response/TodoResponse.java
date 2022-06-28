package com.cm.todoapi.todo.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class TodoResponse {
    Object success;
    Object error;
}
