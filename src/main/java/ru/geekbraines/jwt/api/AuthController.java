package ru.geekbraines.jwt.api;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import ru.geekbraines.jwt.service.JwtService;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

// Это для получения токена


    // нам нужно из этого запроса: AuthRequest request  достать юзера и его авторизовать (делаем с пом-ю authenticationManager):
    @Autowired
    private AuthenticationManager authenticationManager;  
    // authenticationManager - это как бы обертка над данными об учетной записи 


    @Autowired
    private JwtService jwtService;


    @PostMapping("/token")
    public AuthResponse token(@RequestBody AuthRequest request){  // запрос POST, потому что есть тело запроса
        log.info("Request from:  {}", request.getUsername());  // убедиться, что ендпойнт работает и что будет  открыт 
        
      

        Authentication authenticate = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));  // это азпрос от authenticationManager

        // мы посылаем запроc (в запросе будет наша учетная запись) на ендпойнт /token и получаем AuthResponse
        // (в респонсе будет токен) 

    UserDetails user = (UserDetails) authenticate.getPrincipal(); // нам нужен не весь authenticate, а только его часть по текущему юзеру

        String token = jwtService.generateToken(user);

        // return new AuthResponse("tokenValue"); было в самом начале до введения генерации токнов

        return new AuthResponse(token);

    }

   // Генерация самого токена . В кчаестве токена будем рассматривать JWT JSon Web Token
   // jwt.io

}
