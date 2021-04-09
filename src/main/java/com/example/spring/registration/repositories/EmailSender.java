package com.example.spring.registration.repositories;

public interface EmailSender {
    void send(String to, String email);
}
