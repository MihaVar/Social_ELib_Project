package org.mvar.social_elib_project.repository;

import jakarta.validation.constraints.NotBlank;
import org.mvar.social_elib_project.model.Item;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ItemRepository extends MongoRepository<Item, Long> {
    Optional<Item> findItemByItemId(long itemId);

    void deleteByItemId(long itemId);

    Optional<Item> findItemByMaterialLink(@NotBlank(message = "Material link is required") String materialLink);

    List<Item> findItemsByCategory(String category);

    List<Item> findItemsByUser(@NotBlank(message = "User is required") String user);

    List<Item> findItemsByItemId(Iterable<Long> favouredItemIds);

    List<Item> findAllByItemIdIn(Collection<Long> itemIds);
}
