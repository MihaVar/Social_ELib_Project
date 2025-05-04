package org.mvar.social_elib_project.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.mvar.social_elib_project.model.*;
import org.mvar.social_elib_project.payload.request.comment.*;
import org.mvar.social_elib_project.repository.*;
import org.mvar.social_elib_project.service.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentServiceTests {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private IdCounterService idCounterService;

    @InjectMocks
    private CommentService commentService;

    private User user;
    private Item item;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setUsersname("testUser");

        item = new Item();
        item.setItemId(1L);
        item.setExpertComment(new HashSet<>());
    }

    @Test
    void addCommentToItem_shouldSaveComment_whenUserAuthenticated() {
        AddCommentRequest request = new AddCommentRequest("Test comment");

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("email@test.com");

        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(itemRepository.findItemByItemId(1L)).thenReturn(Optional.of(item));
        when(userRepository.findUserByEmail("email@test.com")).thenReturn(Optional.of(user));
        when(idCounterService.generateSequence("comments_sequence")).thenReturn(123L);

        Comment expected = Comment.builder().text("Test comment").itemId(1L).user("testUser").build();
        expected.setCommentId(123L);

        when(commentRepository.save(any(Comment.class))).thenReturn(expected);

        Comment result = commentService.addCommentToItem(request, 1L);

        assertEquals("testUser", result.getUser());
        assertEquals("Test comment", result.getText());
        assertEquals(123L, result.getCommentId());
    }

    @Test
    void deleteComment_shouldDeleteComment_whenUserIsOwner() {
        Comment comment = new Comment();
        comment.setCommentId(1L);
        comment.setUser("testUser");

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("email@test.com");

        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(commentRepository.findCommentByCommentId(1L)).thenReturn(Optional.of(comment));
        when(userRepository.findUserByEmail("email@test.com")).thenReturn(Optional.of(user));

        doNothing().when(commentRepository).deleteByCommentId(1L);

        commentService.deleteComment(1L);

        verify(commentRepository, times(1)).deleteByCommentId(1L);
    }

    @Test
    void deleteComment_shouldThrowException_whenUserIsNotOwner() {
        Comment comment = new Comment();
        comment.setCommentId(1L);
        comment.setUser("anotherUser");

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("email@test.com");

        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(commentRepository.findCommentByCommentId(1L)).thenReturn(Optional.of(comment));
        when(userRepository.findUserByEmail("email@test.com")).thenReturn(Optional.of(user));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> commentService.deleteComment(1L));

        assertEquals("User not authorized to delete comment", thrown.getMessage());
    }

    @Test
    void checkUserCommentPermission_shouldReturnTrue_whenUserIsOwner() {
        Comment comment = new Comment();
        comment.setCommentId(1L);
        comment.setUser("testUser");

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("email@test.com");

        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(commentRepository.findCommentByCommentId(1L)).thenReturn(Optional.of(comment));
        when(userRepository.findUserByEmail("email@test.com")).thenReturn(Optional.of(user));

        assertTrue(commentService.checkUserCommentPermission(1L));
    }

    @Test
    void checkUserCommentPermission_shouldReturnFalse_whenUserIsNotOwner() {
        Comment comment = new Comment();
        comment.setCommentId(1L);
        comment.setUser("anotherUser");

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("email@test.com");

        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(commentRepository.findCommentByCommentId(1L)).thenReturn(Optional.of(comment));
        when(userRepository.findUserByEmail("email@test.com")).thenReturn(Optional.of(user));

        assertFalse(commentService.checkUserCommentPermission(1L));
    }

    @Test
    void updateCommentText_shouldUpdateCommentText_whenUserIsOwner() {
        UpdateCommentRequest request = new UpdateCommentRequest("Updated comment text");

        Comment comment = new Comment();
        comment.setCommentId(1L);
        comment.setUser("testUser");
        comment.setText("Old comment text");

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("email@test.com");

        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(commentRepository.findCommentByCommentId(1L)).thenReturn(Optional.of(comment));
        when(userRepository.findUserByEmail("email@test.com")).thenReturn(Optional.of(user));

        Comment updatedComment = new Comment();
        updatedComment.setCommentId(1L);
        updatedComment.setUser("testUser");
        updatedComment.setText("Updated comment text");

        when(commentRepository.save(any(Comment.class))).thenReturn(updatedComment);

        Comment result = commentService.updateCommentText(request, 1L);

        assertEquals("Updated comment text", result.getText());
    }

    @Test
    void updateCommentText_shouldThrowException_whenUserIsNotOwner() {
        UpdateCommentRequest request = new UpdateCommentRequest("Updated comment text");

        Comment comment = new Comment();
        comment.setCommentId(1L);
        comment.setUser("anotherUser");
        comment.setText("Old comment text");

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("email@test.com");

        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(commentRepository.findCommentByCommentId(1L)).thenReturn(Optional.of(comment));
        when(userRepository.findUserByEmail("email@test.com")).thenReturn(Optional.of(user));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> commentService.updateCommentText(request, 1L));

        assertEquals("User not authorized to perform action", thrown.getMessage());
    }

    @Test
    void addExpertCommentToItem_shouldAddExpertComment_whenUserIsExpert() {
        AddExpertCommentRequest request = new AddExpertCommentRequest("Expert comment");
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(Role.EXPERT.name()));

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("email@test.com");
        doReturn(authorities).when(authentication).getAuthorities();  // Return a Set

        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(itemRepository.findItemByItemId(1L)).thenReturn(Optional.of(item));
        when(userRepository.findUserByEmail("email@test.com")).thenReturn(Optional.of(user));
        when(idCounterService.generateSequence("expert_comments_sequence")).thenReturn(456L);

        ExpertComment expertComment = new ExpertComment();
        expertComment.setExpertCommentId(456L);
        expertComment.setText("Expert comment");
        expertComment.setUser("testUser");

        item.getExpertComment().add(expertComment);

        when(itemRepository.save(item)).thenReturn(item);

        Item result = commentService.addExpertCommentToItem(request, 1L);

        assertEquals(2, result.getExpertComment().size());
        assertEquals("Expert comment", result.getExpertComment().iterator().next().getText());
    }


    @Test
    void addExpertCommentToItem_shouldThrowException_whenUserIsNotExpert() {
        AddExpertCommentRequest request = new AddExpertCommentRequest("Expert comment");
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(Role.USER.name()));  // Non-expert user

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("email@test.com");
        doReturn(authorities).when(authentication).getAuthorities();

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Mock the itemRepository to return an item (so that item is not found)
        when(itemRepository.findItemByItemId(1L)).thenReturn(Optional.of(item));  // Mock item to be found

        // Assert that an IllegalArgumentException is thrown for non-expert users
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> commentService.addExpertCommentToItem(request, 1L));

        assertEquals("User does not have permission to add expert comment", thrown.getMessage());
    }


    @Test
    void deleteExpertComment_shouldDeleteExpertComment_whenUserIsOwner() {
        ExpertComment expertComment = new ExpertComment();
        expertComment.setExpertCommentId(1L);
        expertComment.setUser("testUser");

        item.getExpertComment().add(expertComment);

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("email@test.com");

        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(itemRepository.findAll()).thenReturn(Collections.singletonList(item));
        when(userRepository.findUserByEmail("email@test.com")).thenReturn(Optional.of(user));

        // Mock save method to return the item after "deleting" the comment
        when(itemRepository.save(item)).thenReturn(item);

        // Call the service method
        commentService.deleteExpertComment(1L);

        // Assert that the comment has been removed
        assertTrue(item.getExpertComment().isEmpty());
    }


    @Test
    void deleteExpertComment_shouldThrowException_whenUserIsNotOwner() {
        ExpertComment expertComment = new ExpertComment();
        expertComment.setExpertCommentId(1L);
        expertComment.setUser("anotherUser");

        item.getExpertComment().add(expertComment);

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("email@test.com");

        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(itemRepository.findAll()).thenReturn(Collections.singletonList(item));
        when(userRepository.findUserByEmail("email@test.com")).thenReturn(Optional.of(user));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> commentService.deleteExpertComment(1L));

        assertEquals("User not authorized to delete this comment", thrown.getMessage());
    }
}

