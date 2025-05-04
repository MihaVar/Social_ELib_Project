package org.mvar.social_elib_project.repository;

import org.mvar.social_elib_project.model.ExpertComment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ExpertCommentRepository extends MongoRepository<ExpertComment, Long> {
    Optional<ExpertComment> findExpertCommentByExpertCommentId(long expertCommentId);
    void deleteAllByItemId(long itemId);
}
