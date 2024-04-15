package ru.geekbraines.jwt.security;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;


import lombok.Data;

@Data
@ConfigurationProperties(JwtProperties.PREFIX)
public class JwtProperties {


    public static final String PREFIX = "jwt";

    private String secret;
    //private long expireTimeMillis;
    private Duration expireTime;  //  класс Duration описывает собой какой-то интервал времени

}
