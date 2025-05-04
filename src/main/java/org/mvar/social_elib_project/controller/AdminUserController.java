package org.mvar.social_elib_project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mvar.social_elib_project.model.User;
import org.mvar.social_elib_project.payload.request.admin.*;
import org.mvar.social_elib_project.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminUserController {
    private final AdminService adminService;
    @DeleteMapping("/remove_user/{username}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable String username
            ) {
        adminService.deleteUserByAdmin(username);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/remove_item/{itemId}")
    public ResponseEntity<Void> deleteItem(
            @PathVariable long itemId
    ) {
        adminService.deleteItemByAdmin(itemId);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/remove_comment/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable long commentId
    ) {
        adminService.deleteCommentByAdmin(commentId);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/remove_expert_comment/{expertCommentId}")
    public ResponseEntity<Void> deleteExpertComment(
            @PathVariable long expertCommentId
            ) {
        adminService.deleteExpertCommentByAdmin(expertCommentId);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/role")
    public ResponseEntity<User> updateUserRoleByAdmin(
            @Valid
            @RequestBody AdminChangeRoleRequest request) {
        User user = adminService.updateUserRole(request);
        return ResponseEntity.ok(user);
    }
}
