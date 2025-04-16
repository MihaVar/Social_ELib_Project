package org.mvar.social_elib_project.service;

import lombok.RequiredArgsConstructor;
import org.mvar.social_elib_project.model.Comment;
import org.mvar.social_elib_project.model.Item;
import org.mvar.social_elib_project.model.User;
import org.mvar.social_elib_project.payload.request.AddCommentRequest;
import org.mvar.social_elib_project.repository.CommentRepository;
import org.mvar.social_elib_project.repository.ItemRepository;
import org.mvar.social_elib_project.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public Comment addCommentToItem(AddCommentRequest addCommentRequest) {
        Item item = itemRepository.findById(addCommentRequest.itemId())
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + addCommentRequest.itemId()));

        User user = userRepository.findUserByUsername(addCommentRequest.user())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + addCommentRequest.user()));
        Comment comment = Comment.builder()
                .text(addCommentRequest.text())
                .date(addCommentRequest.date())
                .itemId(item.getId())
                .user(user.getUsername())
                .build();
        return commentRepository.save(comment);
    }

    public void deleteComment(String id, String user) {
        Comment comment = commentRepository.findCommentById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found: " + id));
        if (!comment.getUser().equals(user)) {
            throw new IllegalArgumentException("User not authorized to delete comment");
        }
        commentRepository.deleteById(id);
    }

    public List<Comment> getCommentsByItem(String itemId) {
        return commentRepository.findCommentsByItemId(itemId);
    }
}
