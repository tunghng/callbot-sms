package com.example.callbotsms.security.service;


import com.example.callbotsms.model.AppUser;
import com.example.callbotsms.model.UserCredential;
import com.example.callbotsms.repository.AppUserRepository;
import com.example.callbotsms.repository.UserCredentialsRepository;
import com.example.callbotsms.security.model.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class SecurityUserService implements UserDetailsService {

    @Autowired
    AppUserRepository appUserRepository;

    @Autowired
    UserCredentialsRepository userCredentialsRepository;

    @Override
    @Transactional
    public SecurityUser loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser user = appUserRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User with email [" + email + "] not found");
        }
        UserCredential userCredential = userCredentialsRepository.findByUserId(user.getId()).get();
        return new SecurityUser(user, userCredential);
    }
}
