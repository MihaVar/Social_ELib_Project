package org.mvar.social_elib_project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.mvar.social_elib_project.model.Item;
import org.mvar.social_elib_project.payload.request.item.*;
import org.mvar.social_elib_project.service.ItemService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/catalog")
public class ItemController {
    private final ItemService itemService;
    @PostMapping(value = "/add_item", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Item> addItem(
            @RequestPart("data") String dataJson,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestPart(value = "pdf", required = false) MultipartFile pdf

    ) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        AddItemRequest request = objectMapper.readValue(dataJson, AddItemRequest.class);
        return ResponseEntity.ok(itemService.createNewItem(request, image, pdf));
    }


    @DeleteMapping("/{itemId}/delete_item")
    public ResponseEntity<Void> deleteItem(
            @PathVariable long itemId) {
        itemService.deleteItem(itemId);

        return ResponseEntity.noContent().build();
    }
    @GetMapping("/items")
    public ResponseEntity<List<Item>> getAllItems() {
        List<Item> items = itemService.getAllItems();
        return ResponseEntity.ok(items);
    }
    @GetMapping("/category/{categoryName}")
    public ResponseEntity<List<Item>> getItemsByCategory(
            @PathVariable String categoryName
    ) {
        List<Item> items = itemService.getItemsByCategory(categoryName);
        return ResponseEntity.ok(items);
    }
    @GetMapping("/{itemId}")
    public ResponseEntity<Item> getItem(
            @PathVariable long itemId
    ) {
        Optional<Item> item = itemService.getItemByItemId(itemId);
        return item.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @CrossOrigin(origins = "http://localhost:3000")
    @PatchMapping("/{itemId}/update_item")
    public ResponseEntity<Item> updateItem(
            @PathVariable long itemId,
            @RequestPart(required = false) MultipartFile image,  // файл зображення
            @RequestPart(required = false, name="data") String updateItemRequestJson // параметр для JSON даних
    ) throws IOException {
        UpdateItemRequest updateItemRequest = null;

        // Перевірка на null для параметра "data"
        if (updateItemRequestJson != null && !updateItemRequestJson.isEmpty()) {
            // Перетворюємо JSON на об'єкт UpdateItemRequest
            ObjectMapper objectMapper = new ObjectMapper();
            updateItemRequest = objectMapper.readValue(updateItemRequestJson, UpdateItemRequest.class);
        }

        // Оновлюємо предмет через сервіс
        Item updatedItem = itemService.updateItem(updateItemRequest, itemId, image);

        return ResponseEntity.ok(updatedItem);
    }
    @GetMapping("/{itemId}/check_update_permission")
    public ResponseEntity<Boolean> checkUpdateItemPermission(
            @PathVariable long itemId) {
        boolean hasPermission = itemService.checkUserItemPermission(itemId);
        return ResponseEntity.ok(hasPermission);
    }
    @PutMapping("/{itemId}/vote")
    public ResponseEntity<Item> voteItem(
            @PathVariable long itemId,
            @RequestBody VoteRequest voteRequest,
            Principal principal) {
            Item updatedItem = itemService.voteItem(itemId, principal.getName(), voteRequest.vote());
            return ResponseEntity.ok(updatedItem);
    }
    @PatchMapping("/{itemId}/unvote")
    public ResponseEntity<Item> unvoteItem(
            @PathVariable long itemId,
            Principal principal) {
        Item updatedItem = itemService.unvoteItem(itemId, principal.getName());
        return ResponseEntity.ok(updatedItem);
    }
    @GetMapping("/{itemId}/vote-status")
    public ResponseEntity<Boolean> getVoteStatus(@PathVariable Long itemId) {
        boolean hasVoted = itemService.checkIfUserVoted(itemId);
        return ResponseEntity.ok(hasVoted);
    }
    @GetMapping("/items/{username}")
    public ResponseEntity<List<Item>> getItemsByUsername(@PathVariable String username) {
        return ResponseEntity.ok(itemService.getItemsByUser(username));
    }
}
