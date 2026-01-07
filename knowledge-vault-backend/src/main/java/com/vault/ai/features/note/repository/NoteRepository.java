package com.vault.ai.features.note.repository;

import com.vault.ai.features.auth.model.User;
import com.vault.ai.features.note.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByUser(User user);

    // SECURE KEYWORD SEARCH: Filter by User AND keyword
    @Query("SELECT n FROM Note n WHERE n.user = :user AND " +
            "(LOWER(n.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(n.content) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Note> searchByKeywordAndUser(@Param("query") String query, @Param("user") User user);
}