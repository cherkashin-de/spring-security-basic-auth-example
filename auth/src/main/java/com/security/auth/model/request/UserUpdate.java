package com.security.auth.model.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.sql.Timestamp;

@Schema(description = "DTO для обновления данных пользователя через админ-панель")
public record UserUpdate(
        @Schema(description = "Имя пользователя", example = "jdoe")
        String username,

        @Schema(description = "Полное имя пользователя", example = "John Doe")
        String fullName,

        @Schema(description = "Дата рождения в формате timestamp", example = "1990-05-21T00:00:00Z")
        Timestamp birthDay,

        @Schema(description = "Размер зарплаты", example = "75000.50")
        Double salary
) implements Serializable {
}
