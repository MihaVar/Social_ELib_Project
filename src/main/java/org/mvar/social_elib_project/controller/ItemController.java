package org.mvar.social_elib_project.controller;

import lombok.RequiredArgsConstructor;
import org.mvar.social_elib_project.model.Item;
import org.mvar.social_elib_project.payload.request.ItemRequest;
import org.mvar.social_elib_project.service.ItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/catalog")
public class ItemController {
    private final ItemService itemService;
    @PostMapping("/add_item")
    public ResponseEntity<Item> addItem(
            @RequestBody ItemRequest itemRequest
    ) {
        return ResponseEntity.ok(itemService.createNewItem(itemRequest));
    }
}
