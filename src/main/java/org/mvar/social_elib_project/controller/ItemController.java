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
        itemService.deleteItem(deleteItemRequest.id(), principal.getName());

        return ResponseEntity.noContent().build();
    }
    @GetMapping()
    public ResponseEntity<List<Item>> getAllItems() {
        List<Item> items = itemService.getAllItems();
        return ResponseEntity.ok(items);
    }
    @PostMapping("/{id}/vote")
    public ResponseEntity<Item> voteItem(
            @PathVariable String id,
            @RequestBody VoteRequest voteRequest,
            Principal principal) {
            Item updatedItem = itemService.voteItem(id, principal.getName(), voteRequest.vote());
            return ResponseEntity.ok(updatedItem);
    }
    @PostMapping("/{id}/unvote")
    public ResponseEntity<Item> unvoteItem(
            @PathVariable String id,
            Principal principal) {
        Item updatedItem = itemService.unvoteItem(id, principal.getName());
        return ResponseEntity.ok(updatedItem);
    }
}
