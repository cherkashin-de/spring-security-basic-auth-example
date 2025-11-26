package com.security.auth.controller;

import com.security.auth.model.request.UserCreate;
import com.security.auth.service.RoleService;
import com.security.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "Публичные методы")
public class PublicController {

    RoleService roleService;
    UserService userService;

    @Operation(
            summary = "Регистрация нового пользователя",
            description = "Создаёт нового пользователя. Используется при самостоятельной регистрации через клиентское приложение.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "DTO с данными для создания нового пользователя",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован"),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные регистрации"),
                    @ApiResponse(responseCode = "409", description = "Пользователь с таким логином или email уже существует"),
                    @ApiResponse(responseCode = "500", description = "Ошибка сервера")
            }
    )
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserCreate userCreate) {
        userService.createUser(userCreate);
        userService.createUserInformation(userCreate);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
            summary = "Получить список ролей",
            description = "Возвращает доступные роли пользователей.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Список ролей получен успешно",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = String[].class)
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "Ошибка сервера")
            }
    )
    @GetMapping("/roles")
    public ResponseEntity<?> getRoles() {
        return new ResponseEntity<>(roleService.getRoles(), HttpStatus.OK);
    }

}
