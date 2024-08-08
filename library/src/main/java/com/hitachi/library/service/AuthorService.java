package com.hitachi.library.service;

import com.hitachi.library.entity.Author;
import com.hitachi.library.exception.ResourceNotFoundException;
import com.hitachi.library.repository.AuthorRepository;
import com.hitachi.library.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    @Autowired
    public AuthorService(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    public Author getAuthorById(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id " + id));
    }

    public Author createAuthor(Author author) {
        return authorRepository.save(author);
    }

    public Author updateAuthor(Long id, Author authorDetails) {
        Author author = getAuthorById(id);
        author.setName(authorDetails.getName());
        author.setBiography(authorDetails.getBiography());
        author.setDob(authorDetails.getDob());
        return authorRepository.save(author);
    }

    public void deleteAuthor(Long id) {
        bookRepository.deleteByAuthorId(id);
        Author author = getAuthorById(id);
        authorRepository.delete(author);
    }
}
