package org.mvar.social_elib_project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mvar.social_elib_project.model.Item;
import org.mvar.social_elib_project.model.User;
import org.mvar.social_elib_project.payload.request.item.AddItemRequest;
import org.mvar.social_elib_project.repository.CommentRepository;
import org.mvar.social_elib_project.repository.ExpertCommentRepository;
import org.mvar.social_elib_project.repository.ItemRepository;
import org.mvar.social_elib_project.repository.UserRepository;
import org.mvar.social_elib_project.service.IdCounterService;
import org.mvar.social_elib_project.service.ItemService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemServiceTests {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ExpertCommentRepository expertCommentRepository;
    @Mock
    private IdCounterService idCounterService;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private ItemService itemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void createNewItem_success() {
        AddItemRequest addItemRequest = new AddItemRequest("Book Name", "Author", "Desc", "Category", "2022-02-02", "link.pdf");
        User user = new User();
        user.setEmail("test@example.com");
        user.setUsersname("TestUser");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findUserByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(itemRepository.findItemByPdfLink("link.pdf")).thenReturn(Optional.empty());
        when(idCounterService.generateSequence("items_sequence")).thenReturn(1L);
        when(itemRepository.save(any(Item.class))).thenAnswer(i -> i.getArguments()[0]);

        Item createdItem = itemService.createNewItem(addItemRequest);

        assertNotNull(createdItem);
        assertEquals("Book Name", createdItem.getName());
        assertEquals("TestUser", createdItem.getUser());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void createNewItem_duplicatePdfLink_throwsException() {
        AddItemRequest addItemRequest = new AddItemRequest("Book Name", "Author", "Desc", "Category",  "2022-02-02", "link.pdf");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(itemRepository.findItemByPdfLink("link.pdf")).thenReturn(Optional.of(new Item()));

        assertThrows(IllegalArgumentException.class, () -> itemService.createNewItem(addItemRequest));
    }

    @Test
    void deleteItem_success() {
        Item item = new Item();
        item.setItemId(1L);
        item.setUser("TestUser");

        User user = new User();
        user.setUsersname("TestUser");

        when(itemRepository.findItemByItemId(1L)).thenReturn(Optional.of(item));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findUserByEmail("test@example.com")).thenReturn(Optional.of(user));

        itemService.deleteItem(1L);

        verify(itemRepository).deleteByItemId(1L);
        verify(commentRepository).deleteAllByItemId(1L);
        verify(expertCommentRepository).deleteAllByItemId(1L);
    }

    @Test
    void deleteItem_notAuthorized_throwsException() {
        Item item = new Item();
        item.setItemId(1L);
        item.setUser("AnotherUser");

        User user = new User();
        user.setUsersname("TestUser");

        when(itemRepository.findItemByItemId(1L)).thenReturn(Optional.of(item));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findUserByEmail("test@example.com")).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> itemService.deleteItem(1L));
    }

    @Test
    void voteItem_success() {
        Item item = new Item();
        item.setItemId(1L);
        item.setUsersWhoVoted(new HashSet<>());
        item.setRating(0);

        when(itemRepository.findItemByItemId(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenAnswer(i -> i.getArguments()[0]);

        Item votedItem = itemService.voteItem(1L, "TestUser", 1);

        assertEquals(1, votedItem.getRating());
        assertTrue(votedItem.getUsersWhoVoted().contains("TestUser"));
    }

    @Test
    void voteItem_alreadyVoted_throwsException() {
        Item item = new Item();
        item.setItemId(1L);
        item.setUsersWhoVoted(new HashSet<>(Collections.singletonList("TestUser")));

        when(itemRepository.findItemByItemId(1L)).thenReturn(Optional.of(item));

        assertThrows(IllegalStateException.class, () -> itemService.voteItem(1L, "TestUser", 1));
    }

    @Test
    void unvoteItem_success() {
        Item item = new Item();
        item.setItemId(1L);
        item.setUsersWhoVoted(new HashSet<>(Collections.singletonList("TestUser")));
        item.setRating(1);

        when(itemRepository.findItemByItemId(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenAnswer(i -> i.getArguments()[0]);

        Item unvotedItem = itemService.unvoteItem(1L, "TestUser");

        assertEquals(0, unvotedItem.getRating());
        assertFalse(unvotedItem.getUsersWhoVoted().contains("TestUser"));
    }

    @Test
    void unvoteItem_notVoted_throwsException() {
        Item item = new Item();
        item.setItemId(1L);
        item.setUsersWhoVoted(new HashSet<>());

        when(itemRepository.findItemByItemId(1L)).thenReturn(Optional.of(item));

        assertThrows(IllegalStateException.class, () -> itemService.unvoteItem(1L, "TestUser"));
    }
}

