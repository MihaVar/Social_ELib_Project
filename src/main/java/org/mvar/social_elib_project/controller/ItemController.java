package org.mvar.social_elib_project.controller;

import lombok.RequiredArgsConstructor;
import org.mvar.social_elib_project.model.Item;
import org.mvar.social_elib_project.payload.request.item.DeleteItemRequest;
import org.mvar.social_elib_project.payload.request.item.AddItemRequest;
import org.mvar.social_elib_project.payload.request.item.VoteRequest;
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
    @GetMapping("/{itemId}")
    public ResponseEntity<Item> getItem(
            @PathVariable long itemId
    ) {
        Optional<Item> item = itemService.getItemByItemId(itemId);
        return item.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @PostMapping("/{itemId}/vote")
    public ResponseEntity<Item> voteItem(
            @PathVariable long itemId,
            @RequestBody VoteRequest voteRequest,
            Principal principal) {
            Item updatedItem = itemService.voteItem(itemId, principal.getName(), voteRequest.vote());
            return ResponseEntity.ok(updatedItem);
    }
    @PostMapping("/{itemId}/unvote")
    public ResponseEntity<Item> unvoteItem(
            @PathVariable long itemId,
            Principal principal) {
        Item updatedItem = itemService.unvoteItem(itemId, principal.getName());
        return ResponseEntity.ok(updatedItem);
    }
}
