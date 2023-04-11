package com.example.medicalappointments.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
public class AuthenticationTest {

    @Autowired
    private MockMvc mockMvc;

    static Stream<Arguments> validCredentials() {
        return Stream.of(
                arguments("pacient_1", "123456"),
                arguments("doctor_1", "123456")
        );
    }

    static Stream<Arguments> invalidCredentials() {
        return Stream.of(
                arguments("pacient_1", "bad_password"),
                arguments("doctor_1", "bad_password")
        );
    }

    @Test
    @WithAnonymousUser
    void showLoginPage_success() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @ParameterizedTest
    @WithAnonymousUser
    @MethodSource("validCredentials")
    void login_success(String username, String password) throws Exception {
        mockMvc.perform(formLogin("/authUser").user(username).password(password))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @ParameterizedTest
    @MethodSource("invalidCredentials")
    void login_invalidCredentials_failure(String username, String password) throws Exception {
        mockMvc.perform(formLogin("/authUser").user(username).password(password))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));

    }

    @Test
    void logout_success() throws Exception {
        mockMvc.perform(post("/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/index"));
    }

}
