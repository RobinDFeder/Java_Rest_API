package com.example.demo.security;

import com.example.demo.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JsonFileUserDetailsService implements UserDetailsService {

    private final List<User> users;

    public JsonFileUserDetailsService() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        users = mapper.readValue(new ClassPathResource("users.json").getInputStream(),
                new TypeReference<List<User>>(){});
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return users.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .map(this::mapToUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private UserDetails mapToUserDetails(User user) {
        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities);
    }
}