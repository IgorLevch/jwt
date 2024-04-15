package ru.geekbraines.jwt.security;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;


@Component
public class StandardAuthenticationProvider /*implements AuthenticationProvider -- можно через implements, 
но мы сделаем через extends*/ extends AbstractUserDetailsAuthenticationProvider/*
в этом провайдере в методе supports возвращется  return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
142  строка в описании класса
--  т.е. то, что нам нужно в AuthController  в методе AuthResponse: 
 new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));*/{


    @Autowired
    private UserDetailsService userDetailsService;


    //этот метод проверяет пароль:
    // (сюда попадаем уже из метода protected UserDetails retrieveUser с полученным и подтвержденным Юзером )
    @Override
    protected void additionalAuthenticationChecks(UserDetails/* это то, что лежит на выходе метода ниже*/  userDetails,
            UsernamePasswordAuthenticationToken authentication/* это то, что приехало вместе с запросом
            из  AuthController:  
             new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())) 

             и в этом месте нам нужно сравнить пароли у той учетной запси, которая лежит в базе:
              UserDetails  userDetails   и той, которая приехала в запросе: 
              UsernamePasswordAuthenticationToken authentication */) throws AuthenticationException {
                if (!Objects.equals(userDetails.getPassword(), authentication.getCredentials())) {
                    throw new BadCredentialsException("Bad credentials");
                }
        // AuthenticationException  --  это ексепшны, которые могут случиться в момент авторизации, они бывают разные 
        // всегда четко:   UserDetails -  это то, что хранится в сервисе, а  authentication - то , что пришло в запросе          

       // throw new UnsupportedOperationException("Unimplemented method 'additionalAuthenticationChecks'");
    }


    //этот метод отвечает за загрузку юзердитейлс по юзернейму:
    //(из authenticate AuthController попадаем сначала сюда)
    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
       return userDetailsService.loadUserByUsername(username);
       // throw new UnsupportedOperationException("Unimplemented method 'retrieveUser'");
    }




}
