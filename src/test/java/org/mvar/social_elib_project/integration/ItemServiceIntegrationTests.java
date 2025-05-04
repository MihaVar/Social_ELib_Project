package org.mvar.social_elib_project.integration;

import org.junit.jupiter.api.*;
import org.mvar.social_elib_project.model.Item;
import org.mvar.social_elib_project.model.Role;
import org.mvar.social_elib_project.model.User;
import org.mvar.social_elib_project.payload.request.item.AddItemRequest;
import org.mvar.social_elib_project.repository.ItemRepository;
import org.mvar.social_elib_project.repository.UserRepository;
import org.mvar.social_elib_project.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ItemServiceIntegrationTests {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private static final String TEST_EMAIL = "user@example.com";
    private static final String USERNAME = "testuser";

    private User testUser;

    @BeforeEach
    void setup() {
        // Clean DB
        itemRepository.deleteAll();
        userRepository.deleteAll();

        testUser = User.builder()
                .email(TEST_EMAIL)
                .usersname(USERNAME)
                .password("pass")
                .role(Role.USER)
                .build();

        userRepository.save(testUser);

        // Set authentication context
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(TEST_EMAIL, null, "ROLE_USER")
        );
    }

    @Test
    @Order(1)
    void testCreateNewItem() throws Exception {
        AddItemRequest request = new AddItemRequest(
                "Test Book",
                "Author",
                "Some Description",
                "Science",
                "LocalDate.of(2023, 1, 1),",
                "https://material-link.com",
                null
        );

        MockMultipartFile image = new MockMultipartFile(
                "image", "test.jpg", "image/jpeg", "fake-image-content".getBytes());

        Item item = itemService.createNewItem(request, image);

        assertNotNull(item);
        assertEquals("Test Book", item.getName());
        assertEquals(USERNAME, item.getUser());
    }

    @Test
    @Order(2)
    void testVoteItem() {
        Item item = createAndSaveItem("Voting Test");
        Item votedItem = itemService.voteItem(item.getItemId(), USERNAME, 1);

        assertEquals(1, votedItem.getRating());
        assertTrue(votedItem.getUsersWhoVoted().contains(USERNAME));
    }

    @Test
    @Order(3)
    void testUnvoteItem() {
        Item item = createAndSaveItem("Unvote Test");
        itemService.voteItem(item.getItemId(), USERNAME, 1);

        Item updated = itemService.unvoteItem(item.getItemId(), USERNAME);
        assertEquals(0, updated.getRating());
        assertFalse(updated.getUsersWhoVoted().contains(USERNAME));
    }

    @Test
    @Order(4)
    void testDeleteItem() {
        Item item = createAndSaveItem("Delete Test");

        itemService.deleteItem(item.getItemId());

        Optional<Item> deleted = itemRepository.findItemByItemId(item.getItemId());
        assertTrue(deleted.isEmpty());
    }

    private Item createAndSaveItem(String name) {
        Item item = Item.builder()
                .name(name)
                .user(USERNAME)
                .materialLink("link-" + name)
                .category("test")
                .usersWhoVoted(new java.util.HashSet<>())
                .rating(0)
                .itemId(System.currentTimeMillis()) // Simplified ID
                .build();

        return itemRepository.save(item);
    }
}

