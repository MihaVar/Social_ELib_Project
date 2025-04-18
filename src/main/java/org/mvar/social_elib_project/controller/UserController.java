package org.mvar.social_elib_project.controller;

import lombok.RequiredArgsConstructor;
import org.mvar.social_elib_project.model.User;
import org.mvar.social_elib_project.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentUser(
            ) {
        userService.deleteCurrentUser();
        return ResponseEntity.noContent().build();
    }
    @GetMapping()
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}
