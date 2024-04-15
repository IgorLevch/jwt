package ru.geekbraines.jwt.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Jwt;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import ru.geekbraines.jwt.security.JwtProperties;

@Service
public class JwtService {

 //   public static final long EXPIRE_TIME_MILLIS = 1000*60*5;   // 5 minutes: 1000 миллисекунд - это 1 секунда 
  //  public static final String SECRET = "hhfggryyy6wegggfhh";

   @Autowired
   private JwtProperties properties;



    public String generateToken(UserDetails userDetails) {
        
    // Здесь пишем инфо, которую мы хотим, чтобы в токене присутствовала. 

       String username = userDetails.getUsername();                 // из UserDetails мы достаем userName (мы хотим, чтобы он в токене 
                                                                      // у нас был)  
     List<String> authorities =  userDetails.getAuthorities().stream()                          // также нам нужны его права
            .map(GrantedAuthority::getAuthority)                   // GrantedAuthority - просто оборачивает Стринг 
         //   .collect(Collectors.toList());  это можно заменить как ниже:
            .toList();
      //  throw new UnsupportedOperationException("Unimplemented method 'generateToken'");


        // теперь начинаем генерацию нашего токена (самое главное!)

         Map<String, Object> claims = new HashMap<>(Map.of("authority", authorities)); // это для claims   
         return Jwt.builder()
            .setClaims(claims)  // это как раз средняя часть - набор пар ключ - значение (МАПа) с нашими данными
            .setSubject(username)  // это наш владелец 
            .setIssuedAt(new Date())  // это то время, когда этот токен был выдан  -- текущая дата : метод new Date возвращает объект
            // типа  Date c  текущей датой 
            .setExpiration(new Date(System.currentTimeMillis() + properties.getExpireTime().toMillis()/*properties.getExpireTimeMillis()/*EXPIRE_TIME_MILLIS*/)) // это дата, когда токен просрочится 
            .signWith(SignatureAlgorithm.HS256, /*SECRET*/properties.getSecret())  // это указываем, каким образом этот токен зашифровать: 
            //алгоритм RS256 и секретный ключ,который на сайте обозначен как:  «your-256-bit-secret»                                                  
            .compact(); // преобразует объект Jwt просто в String 
    }

   public String getUsername(String value) {
      // достаем из токена юзернейм
      return parse(value).getSubject();  // getSubject, потому что username записывали в Subject


   }

   // вспомогательный метод для public String getUsername(String value):
   private Claims parse(String value){

      return Jwts.parser()
         .setSigningKey(/*SECRET*/properties.getSecret()) // для того, чтобы проверить, что данный токен выдан  мной же , использую ту же самую переменную
         // которую генерили для создания этого токена 
         .parseClaimsJws(value)
         .getBody(); 

   }

   public List<GrantedAuthority> getAuthorities(String value) {
    List<String> authorities =   parse(value).get("authority");
      // это та authority, что мы писали в  Map<String, Object> claims = new HashMap<>(Map.of("authority", authorities)); 
      return authorities.stream()
         .map(SimpleGrantedAuthority::new)  // SimpleGrantedAuthority - обертка над Стрингом. Имплементация Authority
         .map(it -> (GrantedAuthority) it)
         .toList();

   }


}
