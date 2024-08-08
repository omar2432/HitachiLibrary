package com.hitachi.library.service;

import com.hitachi.library.entity.Author;
import com.hitachi.library.exception.ResourceNotFoundException;
import com.hitachi.library.repository.AuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorService authorService;

    private Author author;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        author = new Author();
        author.setName("Author Name");
    }

    @Test
    void testGetAllAuthors() {
        authorService.getAllAuthors();
        verify(authorRepository, times(1)).findAll();
    }

    @Test
    void testGetAuthorById_Success() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

        Author foundAuthor = authorService.getAuthorById(1L);

        assertNotNull(foundAuthor);
        assertEquals("Author Name", foundAuthor.getName());
    }

    @Test
    void testGetAuthorById_NotFound() {
        when(authorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authorService.getAuthorById(1L));
    }

    @Test
    void testCreateAuthor() {
        when(authorRepository.save(author)).thenReturn(author);

        Author createdAuthor = authorService.createAuthor(author);

        assertNotNull(createdAuthor);
        assertEquals("Author Name", createdAuthor.getName());
    }

    @Test
    void testUpdateAuthor() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(authorRepository.save(author)).thenReturn(author);

        Author updatedAuthor = authorService.updateAuthor(1L, author);

        assertNotNull(updatedAuthor);
        assertEquals("Author Name", updatedAuthor.getName());
    }

    @Test
    void testDeleteAuthor() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

        authorService.deleteAuthor(1L);

        verify(authorRepository, times(1)).delete(author);
    }
}
