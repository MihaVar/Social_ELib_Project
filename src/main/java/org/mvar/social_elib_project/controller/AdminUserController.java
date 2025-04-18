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
    @DeleteMapping("/remove_user")
    public ResponseEntity<Void> deleteUser(
            @RequestBody AdminDeleteUserRequest request
            ) {
        adminService.deleteUserByAdmin(request.user());
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/remove_item")
    public ResponseEntity<Void> deleteItem(
            @RequestBody AdminDeleteItemRequest request
    ) {
        adminService.deleteItemByAdmin(request.itemId());
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/remove_comment")
    public ResponseEntity<Void> deleteComment(
            @RequestBody AdminDeleteCommentRequest request
    ) {
        adminService.deleteCommentByAdmin(request.commentId());
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/remove_expert_comment")
    public ResponseEntity<Void> deleteExpertComment(
            @RequestBody AdminDeleteExpertCommentRequest request
            ) {
        adminService.deleteExpertCommentByAdmin(request.expertCommentId());
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
