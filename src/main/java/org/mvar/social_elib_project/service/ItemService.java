package org.mvar.social_elib_project.service;

import lombok.RequiredArgsConstructor;
import org.mvar.social_elib_project.model.Item;
import org.mvar.social_elib_project.model.User;
import org.mvar.social_elib_project.payload.request.item.AddItemRequest;
import org.mvar.social_elib_project.repository.CommentRepository;
import org.mvar.social_elib_project.repository.ItemRepository;
import org.mvar.social_elib_project.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public Item createNewItem(AddItemRequest addItemRequest) {
        User user = userRepository.findUserByUsername(addItemRequest.user())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + addItemRequest.user()));
        Item item = Item.builder()
                .name(addItemRequest.name())
                .author(addItemRequest.author())
                .description(addItemRequest.description())
                .category(addItemRequest.category())
                .date(addItemRequest.date())
                .pdfLink(addItemRequest.pdfLink())
                .user(user.getUsername())
                .build();
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
