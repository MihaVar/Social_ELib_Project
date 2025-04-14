package org.mvar.social_elib_project.repository;

import org.mvar.social_elib_project.model.Token;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.Optional;

public interface TokenRepository extends MongoRepository<Token, String> {
    boolean existsByJwt(String jwt);
    void deleteTokenByExpireDate(Date expireDate);
    Optional<Token> findByJwt(String jwt);
}
