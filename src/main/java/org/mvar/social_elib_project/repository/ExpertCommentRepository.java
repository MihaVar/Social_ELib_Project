package org.mvar.social_elib_project.repository;

import org.mvar.social_elib_project.model.ExpertComment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ExpertCommentRepository extends MongoRepository<ExpertComment, String> {
}
