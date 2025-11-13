package com.security.auth.controller;

import com.security.auth.model.dto.UserWithListRole;
import com.security.auth.model.request.UserCreate;
import com.security.auth.model.request.UserUpdate;
import com.security.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Метод для работы с данными пользователя",
        description = "С помощью этого метода добавляются/редактируются/обновляются/удаляются данные пользователей")
public class UserController {

    UserService userService;

    @Operation(
            summary = "Регистрация нового пользователя через админ-панель",
            description = "Создаёт нового пользователя на основании данных, переданных администратором.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "DTO с данными для создания пользователя",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserCreate.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "username": "new_user",
                                      "email": "user@example.com",
                                      "password": "securePass123",
                                      "role": "MANAGER"
                                    }
                                    """)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пользователь успешно создан"),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса"),
                    @ApiResponse(responseCode = "401", description = "Неавторизован"),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
                    @ApiResponse(responseCode = "500", description = "Ошибка сервера")
            }
    )
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserCreate userCreate) {
        userService.createUser(userCreate);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Обновление данных пользователя",
            description = "Позволяет администратору изменить данные существующего пользователя.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "DTO с изменяемыми данными пользователя",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserUpdate.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "username": "existing_user",
                                      "email": "updated@example.com",
                                      "role": "ADMIN",
                                      "active": true
                                    }
                                    """)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Данные пользователя обновлены"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса")
            }
    )
    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody UserUpdate user) {
        userService.updateUser(user);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Получение списка пользователей",
            description = "Возвращает всех пользователей с их ролями. Доступно только администраторам.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Список пользователей с ролями",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(allOf = UserWithListRole.class),
                                    examples = @ExampleObject(value = """
                                            [
                                              {"username": "admin", "email": "admin@example.com", "role": "ADMIN"},
                                              {"username": "user1", "email": "user1@example.com", "role": "USER"}
                                            ]
                                            """)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Неавторизован"),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав")
            }
    )
    @GetMapping
    public ResponseEntity<?> getUsers() {
        return ResponseEntity.ok(userService.getAllUsersWithRole());
    }

    @Operation(
            summary = "Удаление пользователя",
            description = "Удаляет пользователя по имени.",
            parameters = {
                    @Parameter(
                            name = "username",
                            description = "Имя пользователя, которого нужно удалить",
                            required = true,
                            example = "old_user"
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пользователь удалён"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
                    @ApiResponse(responseCode = "400", description = "Некорректный запрос")
            }
    )
    @DeleteMapping
    public ResponseEntity<?> deleteUser(@RequestParam String username) {
        userService.deleteUser(username);
        return ResponseEntity.ok().build();
    }
}
