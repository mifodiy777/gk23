package ru.kircoop.gk23;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.kircoop.gk23.service.MyAuthenticationProvider;

@SpringBootApplication
@EnableWebSecurity
public class Gk23Application extends WebSecurityConfigurerAdapter {

    @Autowired
    private MyAuthenticationProvider myProvider;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    public static void main(String[] args) {
        SpringApplication.run(Gk23Application.class, args);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String[] resources = new String[]{
                "/css/**", "/fonts/**", "/images/**", "/js/**"
        };
        http.csrf().disable()
                .cors().disable()
                .httpBasic().disable()
                .authenticationProvider(myProvider)
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .defaultSuccessUrl("/start", true)
                .and()
                .authorizeRequests()
                .antMatchers(resources).permitAll()
                .anyRequest().authenticated()
                .and()
                .logout()
                .logoutSuccessUrl("/login.html");
    }

}
