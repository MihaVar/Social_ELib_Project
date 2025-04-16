package org.mvar.social_elib_project.repository;

import jakarta.validation.constraints.NotBlank;
import org.mvar.social_elib_project.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findCommentsByItemId(String itemId);
    Optional<Comment> findCommentById(String id);

    void deleteAllByItemId(@NotBlank(message = "Item cannot be empty") String itemId);
}
