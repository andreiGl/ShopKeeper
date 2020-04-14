package com.miwdemo.shop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.inMemoryAuthentication()
                .withUser("user").password("{noop}password").roles("USER")
                .and()
                .withUser("admin").password("{noop}password").roles("USER", "ADMIN");

    }

    // Secure the endpoins with HTTP Basic authentication
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                //HTTP Basic authentication
                .httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/items").hasRole("USER")
                .antMatchers(HttpMethod.GET, "/items/**").hasRole("USER")
                .antMatchers(HttpMethod.GET, "/itemsDetailed").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/items").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/buy/**").hasRole("USER")
                .antMatchers(HttpMethod.PUT, "/items/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/items/**").hasRole("ADMIN")
                .and()
                .csrf().disable()
                .formLogin().disable();
    }

}