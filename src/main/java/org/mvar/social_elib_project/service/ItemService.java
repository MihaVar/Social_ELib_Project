package org.mvar.social_elib_project.service;

import lombok.RequiredArgsConstructor;
import org.mvar.social_elib_project.model.Item;
import org.mvar.social_elib_project.model.User;
import org.mvar.social_elib_project.payload.request.item.AddItemRequest;
import org.mvar.social_elib_project.repository.CommentRepository;
import org.mvar.social_elib_project.repository.ExpertCommentRepository;
import org.mvar.social_elib_project.repository.ItemRepository;
import org.mvar.social_elib_project.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ExpertCommentRepository expertCommentRepository;
    private final IdCounterService idCounterService;
    private final List<String> allowedUpdateProperties = Arrays.asList("name", "author", "description", "publishDate", "image", "pdfLink");

    public Item createNewItem(AddItemRequest addItemRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }
        String email = authentication.getName();
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with email not found: " + email));
        Item item = Item.builder()
                .name(addItemRequest.name())
                .author(addItemRequest.author())
                .description(addItemRequest.description())
                .category(addItemRequest.category())
                .publishDate(addItemRequest.publishDate())
                .pdfLink(addItemRequest.pdfLink())
                .user(user.getUsersname())
                .usersWhoVoted(new HashSet<>())
                .build();
        item.setCreationDate(LocalDateTime.now());
        item.setItemId(idCounterService.generateSequence("items_sequence"));
        return itemRepository.save(item);
    }

    public void deleteItem(Long id) {
        Item item = itemRepository.findItemByItemId(id)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + id));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }
        String email = authentication.getName();
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with email not found: " + email));
        if (!item.getUser().equals(user.getUsersname())) {
            throw new IllegalArgumentException("User not authorized to delete item");
        }
        itemRepository.deleteByItemId(id);
        commentRepository.deleteAllByItemId(id);
        expertCommentRepository.deleteAllByItemId(id);
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Optional<Item> getItemByItemId(Long id) {
        return itemRepository.findItemByItemId(id);
    }

    public Item voteItem(Long itemId, String user, int vote) {
        Item item = itemRepository.findItemByItemId(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));
        if (item.getUsersWhoVoted().contains(user)) {
            throw new IllegalStateException("User has already voted for this item");
        }
        if (vote != 1 && vote != -1) {
            throw new IllegalArgumentException("Invalid vote value. Must be 1 or -1");
        }
        item.setRating(item.getRating() + vote);
        item.getUsersWhoVoted().add(user);
        return itemRepository.save(item);
    }

    public Item unvoteItem(Long itemId, String username) {
        Item item = itemRepository.findItemByItemId(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));
        if (!item.getUsersWhoVoted().contains(username)) {
            throw new IllegalStateException("User has not voted for this item");
        }
        item.setRating(item.getRating() - (item.getRating() > 0 ? 1 : (item.getRating() < 0 ? -1 : 0)));
        item.getUsersWhoVoted().remove(username);
        return itemRepository.save(item);
    }
}
