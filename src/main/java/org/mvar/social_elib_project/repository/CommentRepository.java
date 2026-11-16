package org.mvar.social_elib_project.repository;

import jakarta.validation.constraints.NotBlank;
import org.mvar.social_elib_project.model.Comment;
import org.mvar.social_elib_project.service.AverageScoreProjection;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends MongoRepository<Comment, Long> {
    List<Comment> findCommentsByItemId(long itemId);
    Optional<Comment> findCommentByCommentId(long commentId);
    void deleteAllByItemId(long itemId);

    void deleteByCommentId(@NotBlank(message = "Comment text cannot be empty") long commentId);

    @Aggregation(pipeline = {
            "{ '$match': { 'itemId': ?0 } }",
            "{ '$group': { '_id': null, 'averageScore': { '$avg': '$sentiment' } } }"
    })
    AverageScoreProjection getAverageSentimentScoreByItemId(long itemId);
}
