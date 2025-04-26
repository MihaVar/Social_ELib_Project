package org.mvar.social_elib_project.repository;

import jakarta.validation.constraints.NotBlank;
import org.mvar.social_elib_project.model.ExpertComment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ExpertCommentRepository extends MongoRepository<ExpertComment, Long> {
    Optional<ExpertComment> findExpertCommentByExpertCommentId(long expertCommentId);
    void deleteAllByItemId(long itemId);

    void deleteByExpertCommentId(@NotBlank(message = "Expert comment id is required") long expertCommentId);
}
