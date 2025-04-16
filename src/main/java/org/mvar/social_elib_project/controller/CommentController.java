package org.mvar.social_elib_project.controller;

import lombok.RequiredArgsConstructor;
import org.mvar.social_elib_project.model.Comment;
import org.mvar.social_elib_project.payload.request.AddCommentRequest;
import org.mvar.social_elib_project.payload.request.DeleteCommentRequest;
import org.mvar.social_elib_project.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/catalog/{id}/")
public class CommentController {
    private final CommentService commentService;
    @PostMapping("/comments")
    public ResponseEntity<Comment> addComment(
            @RequestBody AddCommentRequest addCommentRequest, @PathVariable String id) {
        return ResponseEntity.ok(commentService.addCommentToItem(addCommentRequest));
    }
    @DeleteMapping("/comments")
    public ResponseEntity<Void> deleteComment(
            @RequestBody DeleteCommentRequest deleteCommentRequest,
            Principal principal, @PathVariable String id) {
        commentService.deleteComment(deleteCommentRequest.id(), principal.getName());

        return ResponseEntity.noContent().build();
    }
    @GetMapping("/comments")
    public List<Comment> getComments(
            @PathVariable String id) {
        return commentService.getCommentsByItem(id);
    }
}
