// File: src/main/java/com/example/cinema/config/DataInitializer.java
package com.example.cinema.config;

import com.example.cinema.entity.Role;
import com.example.cinema.entity.RoleName;
import com.example.cinema.entity.User;
import com.example.cinema.repository.RoleRepository;
import com.example.cinema.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(RoleRepository roleRepo,
                                      UserRepository userRepo,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. Создаём роли, если их нет
            for (RoleName rn : RoleName.values()) {
                roleRepo.findByName(rn).orElseGet(() -> {
                    Role r = new Role();
                    r.setName(rn);
                    return roleRepo.save(r);
                });
            }

            // 2. Создаём дефолтного сотрудника admin / admin123, если его нет
            String adminUsername = "admin";
            if (userRepo.findByUsername(adminUsername).isEmpty()) {
                User admin = new User();
                admin.setUsername(adminUsername);
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setEmail("admin@cinema.local");
                admin.setFirstName("Админ");
                admin.setLastName("Сотрудник");
                // роль EMPLOYEE
                Role empRole = roleRepo.findByName(RoleName.EMPLOYEE)
                        .orElseThrow();
                admin.setRoles(Collections.singleton(empRole));
                userRepo.save(admin);
                System.out.println("Created default admin user: admin / admin123");
            }
        };
    }
}
