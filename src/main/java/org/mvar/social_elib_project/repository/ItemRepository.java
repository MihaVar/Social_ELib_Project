package org.mvar.social_elib_project.repository;

import org.mvar.social_elib_project.model.Item;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ItemRepository extends MongoRepository<Item, String> {
    Optional<Item> findItemById(String id);
}
