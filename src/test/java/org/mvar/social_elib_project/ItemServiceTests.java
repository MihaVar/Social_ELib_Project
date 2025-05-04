package org.mvar.social_elib_project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mvar.social_elib_project.model.Item;
import org.mvar.social_elib_project.model.User;
import org.mvar.social_elib_project.payload.request.item.AddItemRequest;
import org.mvar.social_elib_project.payload.request.item.UpdateItemRequest;
import org.mvar.social_elib_project.repository.CommentRepository;
import org.mvar.social_elib_project.repository.ItemRepository;
import org.mvar.social_elib_project.repository.UserRepository;
import org.mvar.social_elib_project.service.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemServiceTests {

    @Mock private ItemRepository itemRepository;
    @Mock private UserRepository userRepository;
    @Mock private CommentRepository commentRepository;
    @Mock private IdCounterService idCounterService;
    @Mock private ImageService imageService;

    @InjectMocks private ItemService itemService;

    private final String email = "user@example.com";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
    }

    private void mockAuth(String email) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(email, null, List.of())
        );
    }

    @Test
    void createNewItem_shouldCreateSuccessfully() throws IOException {
        mockAuth(email);
        User user = new User();
        user.setEmail(email);
        user.setUsersname("user1");

        AddItemRequest request = new AddItemRequest("Title", "Author", "Desc", "Category", "PublishDate", "link", null);
        MockMultipartFile image = new MockMultipartFile("image", new byte[0]);

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));
        when(itemRepository.findItemByMaterialLink("link")).thenReturn(Optional.empty());
        when(idCounterService.generateSequence("items_sequence")).thenReturn(1L);
        when(itemRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Item item = itemService.createNewItem(request, image);
        assertEquals("Title", item.getName());
    }

    @Test
    void deleteItem_shouldDeleteIfOwner() {
        mockAuth(email);
        User user = new User();
        user.setEmail(email);
        user.setUsersname("owner");

        Item item = new Item();
        item.setItemId(1L);
        item.setUser("owner");

        when(itemRepository.findItemByItemId(1L)).thenReturn(Optional.of(item));
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

        itemService.deleteItem(1L);

        verify(itemRepository).deleteByItemId(1L);
        verify(commentRepository).deleteAllByItemId(1L);
    }

    @Test
    void getAllItems_shouldReturnAll() {
        List<Item> items = List.of(new Item(), new Item());
        when(itemRepository.findAll()).thenReturn(items);
        assertEquals(2, itemService.getAllItems().size());
    }

    @Test
    void getItemById_shouldReturnOptional() {
        Item item = new Item();
        when(itemRepository.findItemByItemId(1L)).thenReturn(Optional.of(item));
        assertTrue(itemService.getItemByItemId(1L).isPresent());
    }

    @Test
    void updateItem_shouldUpdateFields() throws IOException {
        Item item = new Item();
        item.setItemId(1L);
        UpdateItemRequest update = new UpdateItemRequest("Baskerville Dog", "Arthur Conan-Doyle", "Sherlock Holmes", "Literature", "Date", "link");

        when(itemRepository.findItemByItemId(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Item updated = itemService.updateItem(update, 1L, null);
        assertEquals("Baskerville Dog", updated.getName());
    }

    @Test
    void voteItem_shouldIncreaseRatingAndAddUser() {
        Item item = new Item();
        item.setRating(0);
        item.setUsersWhoVoted(new HashSet<>());

        when(itemRepository.findItemByItemId(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Item result = itemService.voteItem(1L, "user1", 1);
        assertEquals(1, result.getRating());
        assertTrue(result.getUsersWhoVoted().contains("user1"));
    }

    @Test
    void unvoteItem_shouldDecreaseRating() {
        Item item = new Item();
        item.setRating(1);
        item.setUsersWhoVoted(new HashSet<>(Set.of("user1")));

        when(itemRepository.findItemByItemId(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Item result = itemService.unvoteItem(1L, "user1");
        assertFalse(result.getUsersWhoVoted().contains("user1"));
    }

    @Test
    void checkUserItemPermission_shouldReturnTrueIfOwner() {
        mockAuth(email);
        User user = new User();
        user.setEmail(email);
        user.setUsersname("user1");

        Item item = new Item();
        item.setUser("user1");

        when(itemRepository.findItemByItemId(1L)).thenReturn(Optional.of(item));
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

        assertTrue(itemService.checkUserItemPermission(1L));
    }

    @Test
    void checkIfUserVoted_shouldReturnTrueIfVoted() {
        mockAuth(email);
        Item item = new Item();
        item.setUsersWhoVoted(Set.of(email));

        when(itemRepository.findItemByItemId(1L)).thenReturn(Optional.of(item));

        assertTrue(itemService.checkIfUserVoted(1L));
    }

    @Test
    void getItemsByUser_shouldReturnList() {
        when(itemRepository.findItemsByUser("user1"))
                .thenReturn(List.of(new Item(), new Item()));
        assertEquals(2, itemService.getItemsByUser("user1").size());
    }

    @Test
    void getItemsByCategory_shouldReturnList() {
        when(itemRepository.findItemsByCategory("fiction"))
                .thenReturn(List.of(new Item()));
        assertEquals(1, itemService.getItemsByCategory("fiction").size());
    }
}