package com.hitachi.library.repository;

import com.hitachi.library.entity.Book;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface BookRepository extends JpaRepository<Book, Long> {
    @Transactional
    @Modifying
    @Query("DELETE FROM Book b WHERE b.author.id = :authorId")
    void deleteByAuthorId(Long authorId);
}
