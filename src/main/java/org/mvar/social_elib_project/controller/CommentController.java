package org.mvar.social_elib_project.controller;

import lombok.RequiredArgsConstructor;
import org.mvar.social_elib_project.model.Comment;
import org.mvar.social_elib_project.model.Item;
import org.mvar.social_elib_project.payload.request.comment.*;
import org.mvar.social_elib_project.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/catalog/{itemId}/")
public class CommentController {
    private final CommentService commentService;
    @PostMapping("/comments/add_comment")
    public ResponseEntity<Comment> addComment(
            @RequestBody AddCommentRequest addCommentRequest, @PathVariable long itemId) {
        return ResponseEntity.ok(commentService.addCommentToItem(addCommentRequest, itemId));
    }
    @DeleteMapping("/comments/delete_comment")
    public ResponseEntity<Void> deleteComment(
            @RequestBody DeleteCommentRequest deleteCommentRequest,
            Principal principal, @PathVariable long itemId) {
        commentService.deleteComment(deleteCommentRequest.id());

        return ResponseEntity.noContent().build();
    }
    @GetMapping("/comments")
    public List<Comment> getComments(
            @PathVariable long itemId) {
        return commentService.getCommentsByItem(itemId);
    }
    @GetMapping("/comments/{commentId}")
    public boolean checkCommentPermission(
            @PathVariable long commentId, @PathVariable long itemId) {
        return commentService.checkUserCommentPermission(commentId);
    }
    @CrossOrigin(origins = "http://localhost:3000")
    @PatchMapping("/comments/{commentId}/update_comment")
    public ResponseEntity<Comment> updateComment(
            @PathVariable long commentId,
            @RequestBody UpdateCommentRequest request,
            @PathVariable String itemId) {
        return ResponseEntity.ok(commentService.updateCommentText(request, commentId));
    }
    @PutMapping("/add_expert_comment")
    public ResponseEntity<Item> addExpertComment(
            @RequestBody AddExpertCommentRequest addExpertCommentRequest, @PathVariable long itemId) {
        return ResponseEntity.ok(commentService.addExpertCommentToItem(addExpertCommentRequest, itemId));
    }
    @DeleteMapping("/delete_expert_comment")
    public ResponseEntity<Void> deleteExpertComment(
            @RequestBody DeleteExpertCommentRequest deleteExpertCommentRequest, @PathVariable long itemId) {
        commentService.deleteExpertComment(deleteExpertCommentRequest.id());
        return ResponseEntity.noContent().build();
    }
}
