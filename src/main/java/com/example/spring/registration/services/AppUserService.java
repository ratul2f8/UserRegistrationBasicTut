package com.example.spring.registration.services;

import com.example.spring.registration.entities.AppUser;
import com.example.spring.registration.entities.ConfirmationToken;
import com.example.spring.registration.repositories.AppUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {
    private final AppUserRepository appUserRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        String ERROR_MESSAGE = "User with email: %s not found";
        return appUserRepository.findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(ERROR_MESSAGE, email)));
    }

    @Transactional
    public String signUpUser(AppUser appUser){
        Optional<AppUser> existingUser = appUserRepository.findUserByEmail(appUser.getEmail());
        if(existingUser.isPresent()){
            System.out.println("Existing User: " + existingUser.get().getEmail()
            +" enabled: " + existingUser.get().getEnabled());
        }
        if(existingUser.isPresent() && existingUser.get().getEnabled() ){
            throw new IllegalStateException("Email is already taken and confirmed!");
        }
        //method if the user signed up before but haven't confirmed email address
        if(existingUser.isPresent() && !existingUser.get().getEnabled()){
            String token = UUID.randomUUID().toString();
            ConfirmationToken confirmationToken = new ConfirmationToken(
                    token,
                    LocalDateTime.now(),
                    LocalDateTime.now().plusMinutes(15),
                    existingUser.get()
            );
            confirmationTokenService.saveConfirmationToken(confirmationToken);
            return token;
        }
        String encodedPassword = bCryptPasswordEncoder.encode(appUser.getPassword());
        appUser.setPassword(encodedPassword);
        appUserRepository.save(appUser);
        //TO DO: Sent confirmation token
        //create a token
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                appUser);
        confirmationTokenService.saveConfirmationToken(confirmationToken);
        return token;
    }
    public int enableAppUser(String email){
        return appUserRepository.enableAppUser(email);
    }
}
