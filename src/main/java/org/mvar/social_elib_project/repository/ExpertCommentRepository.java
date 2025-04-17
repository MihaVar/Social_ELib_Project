package org.mvar.social_elib_project.repository;

import jakarta.validation.constraints.NotBlank;
import org.mvar.social_elib_project.model.ExpertComment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ExpertCommentRepository extends MongoRepository<ExpertComment, String> {
    Optional<ExpertComment> findExpertCommentById(String id);
    void deleteAllByItemId(@NotBlank(message = "Item cannot be empty") String itemId);
}
