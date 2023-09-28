package com.socialshop.backend.authservice.services.impl;

import com.socialshop.backend.authservice.repositiories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthUserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var credential = userRepository.findUserByEmail(email);
        if (credential.isEmpty()) {
            throw new UsernameNotFoundException("Email not found");
        }
        return credential.get();
    }
}
