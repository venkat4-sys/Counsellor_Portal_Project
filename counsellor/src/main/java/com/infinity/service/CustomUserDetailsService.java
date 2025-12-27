package com.infinity.service;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.infinity.repository.CounsellorRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

   @Autowired
    private CounsellorRepository counsellorRepo;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        Map<String, Object> c = counsellorRepo.findUserByEmail(email);

        if (c == null || c.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        return new User(
                (String) c.get("email"),
                (String) c.get("password"),
                new ArrayList<>()
        );
    }
}
