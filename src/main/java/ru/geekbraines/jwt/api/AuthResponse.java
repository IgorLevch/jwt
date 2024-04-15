package ru.geekbraines.jwt.api;

import lombok.Value;

@Value  //это аннотация ломбока, которая генерит геттеры, конструктор 
public class AuthResponse {

    String token;

}
