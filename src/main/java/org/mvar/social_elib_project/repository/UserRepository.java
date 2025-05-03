package org.mvar.social_elib_project.repository;

import jakarta.validation.constraints.NotBlank;
import org.mvar.social_elib_project.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findUserByUsersname(String username);
    Optional<User> findUserByEmail(String email);

    List<User> getUserByUsersname(@NotBlank(message = "Username is required") String usersname);
}
