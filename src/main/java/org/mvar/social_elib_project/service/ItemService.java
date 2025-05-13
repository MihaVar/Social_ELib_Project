package org.mvar.social_elib_project.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mvar.social_elib_project.model.Item;
import org.mvar.social_elib_project.model.User;
import org.mvar.social_elib_project.payload.request.item.AddItemRequest;
import org.mvar.social_elib_project.payload.request.item.UpdateItemRequest;
import org.mvar.social_elib_project.repository.CommentRepository;
import org.mvar.social_elib_project.repository.ItemRepository;
import org.mvar.social_elib_project.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final IdCounterService idCounterService;
    private final ImageService imageService;

    public Item createNewItem(AddItemRequest request, MultipartFile image) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }
        String email = authentication.getName();
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with email " + email + " not found"));

        if (itemRepository.findItemByMaterialLink(request.materialLink()).isPresent()) {
            throw new IllegalArgumentException("Material with the same PDF link already exists");
        }
        String imageUrl = (image != null && !image.isEmpty()) ? imageService.saveImage(image) : null;
        Item item = Item.builder()
                .name(request.name())
                .author(request.author())
                .description(request.description())
                .category(request.category())
                .publishDate(request.publishDate())
                .materialLink(request.materialLink())
                .image(imageUrl)
                .user(user.getUsersname())
                .usersWhoVoted(new HashSet<>())
                .expertComment(new HashSet<>())
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
            throw new IllegalArgumentException("User not authorized to perform action");
        }
        itemRepository.deleteByItemId(id);
        commentRepository.deleteAllByItemId(id);
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Optional<Item> getItemByItemId(Long id) {
        return itemRepository.findItemByItemId(id);
    }

    public List<Item> getItemsByUser(String username) { return itemRepository.findItemsByUser(username); }

    public List<Item> getFavouredItemsByUser(String username) {
        User user = userRepository.findUserByUsersname(username)
                .orElseThrow(() -> new IllegalArgumentException("User with username not found: " + username));
        Set<Long> favouredItemIds = user.getFavouredItems();
        System.out.println(favouredItemIds);
        if (favouredItemIds == null || favouredItemIds.isEmpty()) {
            return Collections.emptyList();
        }
        System.out.println(itemRepository.findItemsByItemId(favouredItemIds));
        return itemRepository.findAllByItemIdIn(favouredItemIds);
    }

    public List<Item> getItemsByCategory(String category) {
        return itemRepository.findItemsByCategory(category);
    }

    @Transactional
    public Item updateItem(UpdateItemRequest updateItemRequest, long itemId, MultipartFile image) throws IOException {
        Item item = itemRepository.findItemByItemId(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        if (updateItemRequest != null) {
            if (updateItemRequest.name() != null) {
                item.setName(updateItemRequest.name());
            }
            if (updateItemRequest.author() != null) {
                item.setAuthor(updateItemRequest.author());
            }
            if (updateItemRequest.description() != null) {
                item.setDescription(updateItemRequest.description());
            }
            if (updateItemRequest.category() != null) {
                item.setCategory(updateItemRequest.category());
            }
            if (updateItemRequest.publishDate() != null) {
                item.setPublishDate(updateItemRequest.publishDate());
            }
            if (updateItemRequest.materialLink() != null) {
                item.setMaterialLink(updateItemRequest.materialLink());
            }
        }
        if (image != null && !image.isEmpty()) {
            String imageUrl = imageService.saveImage(image);
            item.setImage(imageUrl);
        }

        return itemRepository.save(item);
    }



    public boolean checkUserItemPermission(Long itemId) {
        Item item = itemRepository.findItemByItemId(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }
        String email = authentication.getName();
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with email not found: " + email));
        return item.getUser().equals(user.getUsersname());
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
        item.setRating(item.getRating() - (Integer.compare(item.getRating(), 0)));
        item.getUsersWhoVoted().remove(username);
        return itemRepository.save(item);
    }

    public boolean checkIfUserVoted(Long itemId) {
        Item item = itemRepository.findItemByItemId(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }
        String email = authentication.getName();
        // Перевіряємо, чи містить набір голосуючих користувачів ім'я користувача
        return item.getUsersWhoVoted().contains(email);
    }
}

