package org.mvar.social_elib_project.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mvar.social_elib_project.model.*;
import org.mvar.social_elib_project.payload.request.comment.AddCommentRequest;
import org.mvar.social_elib_project.payload.request.comment.AddExpertCommentRequest;
import org.mvar.social_elib_project.payload.request.comment.UpdateCommentRequest;
import org.mvar.social_elib_project.repository.CommentRepository;
import org.mvar.social_elib_project.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;
    private final IdCounterService idCounterService;
    private final AuthService authService;

    public Comment addCommentToItem(AddCommentRequest addCommentRequest, long itemId) {
        Item item = itemRepository.findItemByItemId(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));
        User user = authService.getAuthenticatedUser();
        Comment comment = Comment.builder()
                .text(addCommentRequest.text())
                .itemId(item.getItemId())
                .user(user.getUsersname())
                .build();
        comment.setDate(LocalDateTime.now());
        comment.setCommentId(idCounterService.generateSequence("comments_sequence"));
        return commentRepository.save(comment);
    }

    public void deleteComment(long id) {
        Comment comment = commentRepository.findCommentByCommentId(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found: " + id));
        User user = authService.getAuthenticatedUser();
        if (!comment.getUser().equals(user.getUsersname())) {
            throw new IllegalArgumentException("User not authorized to delete comment");
        }
        commentRepository.deleteByCommentId(id);
    }

    public boolean checkUserCommentPermission(Long itemId) {
        Comment comment = commentRepository.findCommentByCommentId(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found: " + itemId));
        User user = authService.getAuthenticatedUser();
        return comment.getUser().equals(user.getUsersname());
    }

    @Transactional
    public Comment updateCommentText(UpdateCommentRequest request, Long id) {
        Comment comment = commentRepository.findCommentByCommentId(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found: " + id));
        User user = authService.getAuthenticatedUser();
        if (!comment.getUser().equals(user.getUsersname())) {
            throw new IllegalArgumentException("User not authorized to perform action");
        }
        comment.setText(request.text());
        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByItem(long itemId) {
        return commentRepository.findCommentsByItemId(itemId);
    }

    public Item addExpertCommentToItem(AddExpertCommentRequest addExpertCommentRequest, long itemId) {
        Item item = itemRepository.findItemByItemId(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));
        User user = authService.getAuthenticatedUser();
        authService.checkExpertRole();
        if(!authService.checkExpertRole()) {
            throw new IllegalArgumentException("User not authorized to add expert comment");
        }
        ExpertComment expertComment = ExpertComment.builder()
                .text(addExpertCommentRequest.text())
                .itemId(item.getItemId())
                .user(user.getUsersname())
                .creationDate(LocalDateTime.now())
                .build();
        item.getExpertComment().add(expertComment);
        expertComment.setExpertCommentId(idCounterService.generateSequence("expert_comments_sequence"));
        return itemRepository.save(item);
    }

    @Transactional
    public void deleteExpertComment(long expertCommentId) {
        User user = authService.getAuthenticatedUser();
        authService.checkExpertRole();
        if(!authService.checkExpertRole()) {
            throw new IllegalArgumentException("User not authorized to add expert comment");
        }
        Optional<Item> itemOptional = itemRepository.findAll().stream()
                .filter(item -> item.getExpertComment().stream()
                        .anyMatch(comment -> comment.getExpertCommentId() == expertCommentId))
                .findFirst();
        if (itemOptional.isEmpty()) {
            throw new IllegalArgumentException("ExpertComment not found in any item: " + expertCommentId);
        }

        Item item = itemOptional.get();

        ExpertComment commentToDelete = item.getExpertComment().stream()
                .filter(comment -> comment.getExpertCommentId() == expertCommentId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("ExpertComment not found: " + expertCommentId));

        if (!commentToDelete.getUser().equals(user.getUsersname())) {
            throw new IllegalArgumentException("User not authorized to delete this comment");
        }
        item.getExpertComment().remove(commentToDelete);
        itemRepository.save(item);
    }


    public boolean checkExpertCommentPermission(Long expertCommentId) {
        User user = authService.getAuthenticatedUser();
        Optional<Item> itemOptional = itemRepository.findAll().stream()
                .filter(item -> item.getExpertComment().stream()
                        .anyMatch(comment -> comment.getExpertCommentId() == expertCommentId))
                .findFirst();
        if (itemOptional.isEmpty()) {
            throw new IllegalArgumentException("Expert comment not found: " + expertCommentId);
        }
        Item item = itemOptional.get();
        return item.getExpertComment().stream()
                .filter(comment -> comment.getExpertCommentId() == expertCommentId)
                .anyMatch(comment -> comment.getUser().equals(user.getUsersname()));
    }


    @Transactional
    public void updateExpertCommentText(UpdateCommentRequest request, Long expertCommentId) {
        User user = authService.getAuthenticatedUser();
        Optional<Item> itemOptional = itemRepository.findAll().stream()
                .filter(item -> item.getExpertComment().stream()
                        .anyMatch(comment -> comment.getExpertCommentId() == expertCommentId))
                .findFirst();

        if (itemOptional.isEmpty()) {
            throw new IllegalArgumentException("ExpertComment not found in any item: " + expertCommentId);
        }
        Item item = itemOptional.get();
        ExpertComment commentToUpdate = item.getExpertComment().stream()
                .filter(comment -> comment.getExpertCommentId() == expertCommentId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("ExpertComment not found: " + expertCommentId));
        if (!commentToUpdate.getUser().equals(user.getUsersname())) {
            throw new IllegalArgumentException("User not authorized to perform action");
        }
        commentToUpdate.setText(request.text());
        itemRepository.save(item);
    }


    public Set<ExpertComment> getExpertCommentsByItemId(long itemId) {
        Item item = itemRepository.findItemByItemId(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));
        return item.getExpertComment();
    }
}
