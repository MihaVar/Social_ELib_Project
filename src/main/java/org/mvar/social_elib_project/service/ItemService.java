package org.mvar.social_elib_project.service;

import lombok.RequiredArgsConstructor;
import org.mvar.social_elib_project.model.Item;
import org.mvar.social_elib_project.model.User;
import org.mvar.social_elib_project.payload.request.ItemRequest;
import org.mvar.social_elib_project.repository.ItemRepository;
import org.mvar.social_elib_project.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public Item findItemById(int id) {return itemRepository.findItemById(id).orElse(null);}

    public Item createNewItem(ItemRequest itemRequest) {
        User user = userRepository.findUserByUsername(itemRequest.user())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + itemRequest.user()));
        Item item = Item.builder()
                .name(itemRequest.name())
                .author(itemRequest.author())
                .description(itemRequest.description())
                .category(itemRequest.category())
                .date(itemRequest.date())
                .pdfLink(itemRequest.pdfLink())
                .user(user)
                .build();
        return itemRepository.save(item);
    }
}
