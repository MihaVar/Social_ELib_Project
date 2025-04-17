package org.mvar.social_elib_project.service;

import lombok.RequiredArgsConstructor;
import org.mvar.social_elib_project.model.Item;
import org.mvar.social_elib_project.model.User;
import org.mvar.social_elib_project.payload.request.item.AddItemRequest;
import org.mvar.social_elib_project.repository.CommentRepository;
import org.mvar.social_elib_project.repository.ItemRepository;
import org.mvar.social_elib_project.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;

    public Item createNewItem(AddItemRequest addItemRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }
        String user = authentication.getName();
        Item item = Item.builder()
                .name(addItemRequest.name())
                .author(addItemRequest.author())
                .description(addItemRequest.description())
                .category(addItemRequest.category())
                .publishDate(addItemRequest.publishDate())
                .pdfLink(addItemRequest.pdfLink())
                .user(user)
                .usersWhoVoted(new HashSet<>())
                .build();
        item.setCreationDate(LocalDateTime.now());
        return itemRepository.save(item);
    }

    public void deleteItem(String id, String user) {
        Item item = itemRepository.findItemById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + id));
        if (!item.getUser().equals(user)) {
            throw new IllegalArgumentException("User not authorized to delete item");
        }
        itemRepository.deleteById(id);
        commentRepository.deleteAllByItemId(id);
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }
}
