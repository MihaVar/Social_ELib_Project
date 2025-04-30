package org.mvar.social_elib_project.controller;

import lombok.RequiredArgsConstructor;
import org.mvar.social_elib_project.model.Item;
import org.mvar.social_elib_project.payload.request.item.*;
import org.mvar.social_elib_project.service.ItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/catalog")
public class ItemController {
    private final ItemService itemService;
    @PostMapping("/add_item")
    public ResponseEntity<Item> addItem(
            @RequestBody AddItemRequest addItemRequest
    ) {
        return ResponseEntity.ok(itemService.createNewItem(addItemRequest));
    }
    @DeleteMapping("/delete_item")
    public ResponseEntity<Void> deleteItem(
            @RequestBody DeleteItemRequest deleteItemRequest,
            Principal principal) {
        itemService.deleteItem(deleteItemRequest.id());

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
            @RequestBody UpdateItemRequest updateItemRequest
    ) {
        return ResponseEntity.ok(itemService.updateItemField(updateItemRequest, itemId));
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
}
