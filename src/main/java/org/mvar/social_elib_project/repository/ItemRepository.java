package org.mvar.social_elib_project.repository;

import jakarta.validation.constraints.NotBlank;
import org.mvar.social_elib_project.model.Item;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ItemRepository extends MongoRepository<Item, Long> {
    Optional<Item> findItemByItemId(long itemId);

    void deleteByItemId(long itemId);

    Optional<Item> findItemByPdfLink(@NotBlank(message = "PDF is required") String pdfLink);
}
