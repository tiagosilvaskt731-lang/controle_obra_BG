package com.senac.controle_obra;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Desabilita a proteção CSRF para liberar os botões de formulário (POST)
                .csrf(csrf -> csrf.disable())
                // Exige que todas as páginas e requisições passem pelo login por senha
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                // Ativa o formulário de login padrão do Spring Security
                .formLogin(withDefaults())
                .httpBasic(withDefaults());

        return http.build();
    }
}