package com.example.medicalappointments.configuration;

import com.example.medicalappointments.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final UserService userService;

    public static final String ADMIN = "ADMIN";
    public static final String DOCTOR = "DOCTOR";
    public static final String PATIENT = "PATIENT";
    public static final String ROLE_ADMIN = "ROLE_" + ADMIN;
    public static final String ROLE_DOCTOR = "ROLE_" + DOCTOR;
    public static final String ROLE_PATIENT = "ROLE_" + PATIENT;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/h2-console/login.do*").permitAll()

                .antMatchers("/departments/new", "/departments/{^[0-9]+}/edit").hasRole(ADMIN)
                .antMatchers("/departments/{^[0-9]+}/delete").hasRole(ADMIN)
                .antMatchers("/departments/{^[0-9]+}").permitAll()
                .antMatchers("/departments").permitAll()

                .antMatchers("/patients/new", "/patients/{^[0-9]+}/edit").hasRole(ADMIN)
                .antMatchers("/patients/{^[0-9]+}/delete").hasRole(ADMIN)
                .antMatchers("/patients/{^[0-9]+}").hasAnyRole(ADMIN, DOCTOR)
                .antMatchers("/patients").hasAnyRole(ADMIN, DOCTOR)

                .antMatchers("/doctors/{^[0-9]+}/edit").hasAnyRole(DOCTOR, ADMIN)
                .antMatchers("/doctors/my-profile").hasRole(DOCTOR)
                .antMatchers("/doctors/{^[0-9]+}/delete").hasRole(ADMIN)
                .antMatchers("/doctors/{^[0-9]+}").permitAll()
                .antMatchers("/doctors").permitAll()

                .antMatchers("/medications/new", "/medications/{^[0-9]+}/edit").hasRole(ADMIN)
                .antMatchers("/medications/{^[0-9]+}/delete").hasRole(ADMIN)
                .antMatchers("/medications/{^[0-9]+}").permitAll()
                .antMatchers("/medications").permitAll()

                .antMatchers("/consults/{^[0-9]+}/edit").hasAnyRole(DOCTOR, ADMIN)
                .antMatchers("/consults/{^[0-9]+}/delete").hasRole(ADMIN)
                .antMatchers("/consults/new").hasAnyRole(DOCTOR, ADMIN)
                .antMatchers("/consults/{^[0-9]+}").hasAnyRole(DOCTOR, ADMIN)
                .antMatchers("/consults/my-consults").hasRole(DOCTOR)
                .antMatchers("/consults").hasAnyRole(DOCTOR, ADMIN)

                .antMatchers("/**/bootstrap/**").permitAll()

                .and()

                .headers().frameOptions().disable()
                .and()
                .csrf().disable()

                .formLogin().loginPage("/login")
                .loginProcessingUrl("/authUser")
                .permitAll()
                .and()
                .logout().permitAll()
                .logoutSuccessUrl("/index")
                .invalidateHttpSession(true)
                .and()
                .exceptionHandling().accessDeniedPage("/access-denied");
    }
}

