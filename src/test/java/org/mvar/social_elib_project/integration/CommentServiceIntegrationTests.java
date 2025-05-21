package org.mvar.social_elib_project.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mvar.social_elib_project.model.*;
import org.mvar.social_elib_project.payload.request.comment.AddCommentRequest;
import org.mvar.social_elib_project.payload.request.comment.AddExpertCommentRequest;
import org.mvar.social_elib_project.payload.request.comment.UpdateCommentRequest;
import org.mvar.social_elib_project.repository.CommentRepository;
import org.mvar.social_elib_project.repository.ItemRepository;
import org.mvar.social_elib_project.repository.UserRepository;
import org.mvar.social_elib_project.service.CommentService;
import org.mvar.social_elib_project.service.IdCounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "file:./.env")
@ActiveProfiles("test")
class CommentServiceIntegrationTests {

    @Autowired
    private CommentService commentService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private IdCounterService idCounterService;

    private User testUser;
    private Item testItem;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();

        testUser = User.builder()
                .email("test@example.com")
                .usersname("testuser")
                .role(Role.USER)
                .build();
        userRepository.save(testUser);

        testItem = Item.builder()
                .itemId(idCounterService.generateSequence("items_sequence"))
                .name("Test Book")
                .user(testUser.getUsersname())
                .expertComment(new java.util.HashSet<>())
                .usersWhoVoted(new java.util.HashSet<>())
                .build();
        itemRepository.save(testItem);

        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(testUser.getEmail(), null, "ROLE_USER")
        );
    }

    @Test
    void shouldAddCommentToItem() {
        AddCommentRequest request = new AddCommentRequest("Very useful material!");

        Comment comment = commentService.addCommentToItem(request, testItem.getItemId());

        assertNotNull(comment);
        assertEquals("Very useful material!", comment.getText());
        assertEquals(testUser.getUsersname(), comment.getUser());
        assertEquals(testItem.getItemId(), comment.getItemId());
    }

    @Test
    void shouldUpdateComment() {
        Comment comment = commentService.addCommentToItem(new AddCommentRequest("Old comment"), testItem.getItemId());

        UpdateCommentRequest updateRequest = new UpdateCommentRequest("Updated text");
        Comment updated = commentService.updateCommentText(updateRequest, comment.getCommentId());

        assertEquals("Updated text", updated.getText());
    }

    @Test
    void shouldDeleteComment() {
        Comment comment = commentService.addCommentToItem(new AddCommentRequest("To be deleted"), testItem.getItemId());

        commentService.deleteComment(comment.getCommentId());

        assertFalse(commentRepository.findCommentByCommentId(comment.getCommentId()).isPresent());
    }

    @Test
    void shouldReturnCommentsByItemId() {
        commentService.addCommentToItem(new AddCommentRequest("First comment"), testItem.getItemId());
        commentService.addCommentToItem(new AddCommentRequest("Second comment"), testItem.getItemId());

        List<Comment> comments = commentService.getCommentsByItem(testItem.getItemId());

        assertEquals(2, comments.size());
    }

    @Test
    void shouldCheckUserCommentPermission() {
        Comment comment = commentService.addCommentToItem(new AddCommentRequest("My comment"), testItem.getItemId());

        boolean hasPermission = commentService.checkUserCommentPermission(comment.getCommentId());

        assertTrue(hasPermission);
    }
}

@SpringBootTest
@TestPropertySource(locations = "file:./.env")
@ActiveProfiles("test")
class ExpertCommentIntegrationTests {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private IdCounterService idCounterService;

    private Item testItem;

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll();
        userRepository.deleteAll();

        User expertUser = User.builder()
                .email("expert@example.com")
                .usersname("expertuser")
                .role(Role.EXPERT)
                .build();
        userRepository.save(expertUser);

        testItem = Item.builder()
                .itemId(idCounterService.generateSequence("items_sequence"))
                .name("Test Book")
                .user("someone")
                .expertComment(new java.util.HashSet<>())
                .usersWhoVoted(new java.util.HashSet<>())
                .build();
        itemRepository.save(testItem);

        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(expertUser.getEmail(), null, "EXPERT")
        );
    }

    @Test
    void shouldAddExpertCommentToItem() {
        AddExpertCommentRequest request = new AddExpertCommentRequest("Expert insight");
        Item itemWithComment = commentService.addExpertCommentToItem(request, testItem.getItemId());

        assertNotNull(itemWithComment);
        assertEquals(1, itemWithComment.getExpertComment().size());

        ExpertComment comment = itemWithComment.getExpertComment().iterator().next();
        assertEquals("Expert insight", comment.getText());
        assertEquals("expertuser", comment.getUser());
    }

    @Test
    void shouldUpdateExpertCommentText() {
        // Add comment
        AddExpertCommentRequest request = new AddExpertCommentRequest("Original");
        Item item = commentService.addExpertCommentToItem(request, testItem.getItemId());
        ExpertComment comment = item.getExpertComment().iterator().next();

        // Update
        commentService.updateExpertCommentText(new UpdateCommentRequest("Updated"), comment.getExpertCommentId());

        Item updatedItem = itemRepository.findItemByItemId(testItem.getItemId()).orElseThrow();
        Optional<ExpertComment> updatedComment = updatedItem.getExpertComment().stream()
                .filter(c -> c.getExpertCommentId() == comment.getExpertCommentId())
                .findFirst();

        assertTrue(updatedComment.isPresent());
        assertEquals("Updated", updatedComment.get().getText());
    }

    @Test
    void shouldDeleteExpertComment() {
        // Add comment
        AddExpertCommentRequest request = new AddExpertCommentRequest("To delete");
        Item item = commentService.addExpertCommentToItem(request, testItem.getItemId());
        ExpertComment comment = item.getExpertComment().iterator().next();

        // Delete
        commentService.deleteExpertComment(comment.getExpertCommentId());

        Item itemAfterDelete = itemRepository.findItemByItemId(testItem.getItemId()).orElseThrow();
        boolean stillExists = itemAfterDelete.getExpertComment().stream()
                .anyMatch(c -> c.getExpertCommentId() == comment.getExpertCommentId());

        assertFalse(stillExists);
    }

    @Test
    void shouldCheckExpertCommentPermission() {
        // Add comment
        AddExpertCommentRequest request = new AddExpertCommentRequest("Check me");
        Item item = commentService.addExpertCommentToItem(request, testItem.getItemId());
        ExpertComment comment = item.getExpertComment().iterator().next();

        boolean hasPermission = commentService.checkExpertCommentPermission(comment.getExpertCommentId());

        assertTrue(hasPermission);
    }

    @Test
    void shouldReturnExpertCommentsByItemId() {
        commentService.addExpertCommentToItem(new AddExpertCommentRequest("One"), testItem.getItemId());
        commentService.addExpertCommentToItem(new AddExpertCommentRequest("Two"), testItem.getItemId());

        Set<ExpertComment> expertComments = commentService.getExpertCommentsByItemId(testItem.getItemId());

        assertEquals(2, expertComments.size());
    }
}

