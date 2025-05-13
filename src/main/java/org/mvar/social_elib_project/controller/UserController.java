package org.mvar.social_elib_project.controller;

import lombok.RequiredArgsConstructor;
import org.mvar.social_elib_project.model.Item;
import org.mvar.social_elib_project.model.Role;
import org.mvar.social_elib_project.model.User;
import org.mvar.social_elib_project.payload.request.user.AddExpertAccomplishmentRequest;
import org.mvar.social_elib_project.payload.request.user.UserChangeUsernameRequest;
import org.mvar.social_elib_project.service.ItemService;
import org.mvar.social_elib_project.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final ItemService itemService;

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentUser(
            ) {
        userService.deleteCurrentUser();
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/me")
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
    @GetMapping("/{username}")
    public ResponseEntity<Optional<User>> getUser(
            @PathVariable String username
    ) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }
    @GetMapping("/role")
    public ResponseEntity<Role> getUserRole() {
        return ResponseEntity.ok(userService.getUserRole());
    }
    @PutMapping("/{itemId}/favour")
    public ResponseEntity<User> favourItem( @PathVariable long itemId) {
        return ResponseEntity.ok(userService.favourItem(itemId));
    }
    @PatchMapping("/{itemId}/unfavour")
    public ResponseEntity<User> unfavourItem( @PathVariable long itemId) {
        return ResponseEntity.ok(userService.unfavourItem(itemId));
    }
    @GetMapping("/{itemId}/favour-status")
    public ResponseEntity<Boolean> checkFavoriteStatus( @PathVariable long itemId) {
        boolean hasFavoured = userService.checkFavoriteStatus(itemId);
        return ResponseEntity.ok(hasFavoured);
    }
    @GetMapping("/{username}/favourites")
    public ResponseEntity<List<Item>> getUserFavouriteItems(@PathVariable String username) {
        return ResponseEntity.ok(itemService.getFavouredItemsByUser(username));
    }
    @PutMapping("/{username}/add-expert-accomplishments")
    public ResponseEntity<User> addExpertAccomplishments(@RequestBody AddExpertAccomplishmentRequest request, @PathVariable String username) {
        return ResponseEntity.ok(userService.addExpertAccomplishment(request));
    }
    @GetMapping("/{username}/expert-accomplishments")
    public ResponseEntity<Set<String>> addExpertAccomplishments(@PathVariable String username) {
        return ResponseEntity.ok(userService.getExpertAccomplishments(username));
    }
}
