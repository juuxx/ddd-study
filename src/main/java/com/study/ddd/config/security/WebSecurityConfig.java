package com.study.ddd.config.security;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.NullRequestCache;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class WebSecurityConfig {
    public static final String AUTHCOOKIENAME = "AUTH";
    private final DataSource dataSource;

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        UserDetailsService userDetailsService = userDetailsService(dataSource);
        http.securityContext().securityContextRepository(new CookieSecurityContextRepository(userDetailsService));
        http.requestCache().requestCache(new NullRequestCache());

        http
            .authorizeHttpRequests((authz) -> authz
                .requestMatchers("/", "/categories/**", "/products/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin() // login
            .permitAll()
            .successHandler(new CustomAuthSuccessHandler())
            .and()
            .logout() // /login?logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/loggedOut")
            .deleteCookies(AUTHCOOKIENAME)
            .permitAll()
            .and()
            .csrf().disable()
        ;
        return http.build();
    }


    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);
        userDetailsManager.setUsersByUsernameQuery("select member_id, password, 'true' from member where member_id = ?");
        userDetailsManager.setAuthoritiesByUsernameQuery("select member_id, authority from member_authorities where member_id = ?");
        return userDetailsManager;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() throws Exception {
        return (web) -> web.ignoring().requestMatchers("/vendor/**", "/api/**", "/images/**", "/favicon.ico");
    }

}
