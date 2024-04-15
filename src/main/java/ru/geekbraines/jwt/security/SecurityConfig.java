package ru.geekbraines.jwt.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration        // вместо WebSecurityAdapter
public class SecurityConfig {


    // мы идем на открытый ресурс за токеном, мы будем этот токен получать и все остальные запросы будем делать с этим токеном
    // вся проверка безопасности сводится к тому, чтобы достать из запроса токен и проверить , что он валидный 
    
    // если работать через форму введения логин-пароль (как на прошлом уроке), то Спринг кладет в Спринг-контекст 
    //данные о пользователе: "JSESSION: account"
    //что вызывает у сервера некое состояние. Мы хотим, чтобы не было состояния
    // поэтому мы сначала будем получать токен (auth-> token)
    // а уже потом будем делать api/resource с токеном 


    // для того, тобы открыть ендпойнт, мы отключаем безопасность на этом ресурсе:
    @Bean 
    public WebSecurityCustomizer webSecurityCustomizer(){
         return new WebSecurityCustomizer() {     // это интерфейс, в котором есть доступ к веб-секьюрити

            @Override  
            public void customize(WebSecurity web) {
                // TODO Auto-generated method stub
              //  throw new UnsupportedOperationException("Unimplemented method 'customize'");
                web.ignoring().requestMatchers("/auth/**");
                // т.е. все, что в контроллере auth и дальше -- на эти запросы безопасность не работает вообще. 
            }   
            
         } ;                               


    }


    @Bean     // это для настройки  Http Security
    public SecurityFilterChain securityFilterChain(SecurityFilter filter, HttpSecurity httpSecurity) throws Exception{
        //   SecurityFilterChain - это и есть цепочка фильтров, о которой мы говорили на прошлом уроке
         return httpSecurity.authorizeHttpRequests()   //  не должно быть перечеркнуто
            .requestMatchers("/api/**")  // все, что после api - пользователь должен быть авторизован 
            .authenticated()
            .and()
            .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class) // это для того, чтобы наш фильтр (SecurityFilter запускался в самом начале)
            .build();
            // если на этом этапе зайдем по адресу http://localhost:8080/api/resource  -- будет 403 ошибка (доступ запрещен), 
            // т.к. нет формы логина 

    }

    // проверяем, что та учетная запись, которую мы получили (AuthRequest request) - она валидная и такой польз-ль существует
    // генерация токена - это уже следующий этап:
    
    @Bean
    public UserDetailsManager/*(по юзернейму загружает юзердетайлс)*/ userDetailsManager(){  // это наш UserDetailsSerice: см.
        // StandardAuthenticationProvider 


        UserDetails user = User.builder()  // делаем своего юзера 
        .username("user")
        .password("pass")
        .authorities("ADMIN","MANAGER")
        .build();

        return new InMemoryUserDetailsManager(user);// эта штука в оперативной памяти хранит инфо о всех юзерах 
        // передаем в конструктор нашего юзера
    }



    // пишем нашего AuthenticationManager(который инжектится в AuthController например)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationProvider... providers){

        return new ProviderManager(providers);// это такой фасад для Authentication провайдеров . А Authentication провайдеры в свою очередь 
        // похожи на  AuthenticationManager 
        // выглядит это так:
        // у нас есть manager, у которого есть список провайдеров:
        // manager [provider1, provider2, provider 3, ... ]
        // когда мы менеджеру посылаем запрос authenticationManager.authenticate(
         //   new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())); 
        //   (это из AuthController), менеджер ищет провайдера , который делает такую авторизацию
        // (в данном случае по username и password). 
        // и делегирует ему свою работу (менеджер и провайдер похожи . Отличаются тем только, что у AuthenticationProvider 
        // есть метод supports()  )    
    }  


    // теперь нужен какой-то конкретный провайдер:
    @Bean
    public AuthenticationProvider authenticationProvider(){

        return new StandardAuthenticationProvider();
    }


}
