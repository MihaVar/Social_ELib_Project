package org.mvar.social_elib_project.controller;

import lombok.RequiredArgsConstructor;
import org.mvar.social_elib_project.model.User;
import org.mvar.social_elib_project.payload.request.user.UserChangeUsernameRequest;
import org.mvar.social_elib_project.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    @PutMapping("/me")
    public ResponseEntity<User> changeUsername(
            @RequestBody UserChangeUsernameRequest request
            ) {
        User changedUsername = userService.changeUsername(request.usersname());
        return ResponseEntity.ok(changedUsername);
    }
    @GetMapping()
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}
