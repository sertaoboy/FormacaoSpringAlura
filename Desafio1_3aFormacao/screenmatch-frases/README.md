# Resolucao
- Apos criar o banco de dados, configuramos o `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost/screenmatch_frases
spring.datasource.username=postgres
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver
hibernate.dialect=org.hibernate.dialect.HSQLDialect

spring.jpa.hibernate.ddl-auto=update

spring.jpa.show-sql=true
spring.jpa.format-sql=true
```
- Criacao de pacotes:
```text
screenmatch_frases
            ├── config
                  └── CorsConfiguration.java
            ├── controller
                  └── FraseController.java
            ├── dto
                  └── FraseDTO.java
            ├── model
                  └── Frase.java
            ├── repository
                  └── FraseRepository.java
            ├── ScreenmatchFrasesApplication.java
            └── service
                  └── FraseService.java
```
- Classes:
> CorsConfiguration
```java
package br.com.alura.screenmatch_frases.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://127.0.0.1:5500")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "TRACE", "CONNECT");
    }
}
```
