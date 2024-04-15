package ru.geekbraines.jwt.security;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.geekbraines.jwt.service.JwtService;

@Component
public class SecurityFilter extends OncePerRequestFilter{

    @Autowired
    private JwtService jwtService;



    // Токен мы будет вставлять в заголовок, который называется 
    // Authorization: Bearer XXX  . Где ХХХ - это значения нашего токена. 

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                FilterChain filterChain) throws ServletException, IOException {
        // тут есть доступ к запросу-ответу, к цепочке фильтров, о которой говорили на прошлом уроке
           
    
    //в этом месте нужно из запроса (request) выдернуть токен, посмотреть на него (что он валидный, не просроченный)
    // и авторизовать пользователя в контекст: 

       String authorizationHeaderValue = request.getHeader(HttpHeaders.AUTHORIZATION); // из запроса достаем http- заголовок,
       // в котором содержится токен
       if (authorizationHeaderValue != null && authorizationHeaderValue.startsWith("Bearer ")) {
        // если ДА, то мы имеем дело с запросом, в котором есть токен 
        // и этот токен нужно достать и провалидировать 

        // вычленяем сам токен из запроса:
        String bearerTokenValue = authorizationHeaderValue.substring(7); // это мы отрезали слово Bearer 
        

        // Декодирую его (достаю юзернейм и роли):
        String username = jwtService.getUsername(bearerTokenValue);
        List<GrantedAuthority> authorities = jwtService.getAuthorities(bearerTokenValue);

       // Проверяю, что если их там нет, то записываю пользователя в контекст:
       if (Objects.nonNull(username) && Objects.isNull(SecurityContextHolder.getContext().getAuthentication())) {
            SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, null, authorities)
            );
       } 
       }             
    
    //    SecurityContextHolder.getContext().setAuthentication(
    //     new UsernamePasswordAuthenticationToken(username, null, authorities)
    // );        --- это делаем только если пользователь еще не авторизован. Если авторизован, то его не нужно заново авторизовывать



        // цепочка, которая состоит из таких фильтров:  OncePerRequestFilter
        // у каждого фильтра есть возможность прервать исполнение запроса. 
        // чтобы исполнение запроса прервать, нужно этот метод не вызывать (т.е. не вызывать следующий фильтр цепочки)
        // и наоборот, чтобы цепочка не оборвалась, нужно этот метод вызвать :
       filterChain.doFilter(request, response);
    
    }

}
