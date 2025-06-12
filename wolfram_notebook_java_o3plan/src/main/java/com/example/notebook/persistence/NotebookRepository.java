package com.example.notebook.persistence;

import com.example.notebook.model.Notebook;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class NotebookRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    private final RowMapper<Notebook> notebookRowMapper = (rs, rowNum) -> new Notebook(
        UUID.fromString(rs.getString("id")),
        rs.getString("title"),
        Instant.parse(rs.getString("created")),
        Instant.parse(rs.getString("updated"))
    );
    
    public NotebookRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public Notebook insert(Notebook notebook) {
        jdbcTemplate.update(
            "INSERT INTO notebook (id, title, created, updated) VALUES (?, ?, ?, ?)",
            notebook.id().toString(),
            notebook.title(),
            notebook.created().toString(),
            notebook.updated().toString()
        );
        return notebook;
    }
    
    public Optional<Notebook> findById(UUID id) {
        List<Notebook> results = jdbcTemplate.query(
            "SELECT * FROM notebook WHERE id = ?",
            notebookRowMapper,
            id.toString()
        );
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    public List<Notebook> findAll() {
        return jdbcTemplate.query("SELECT * FROM notebook ORDER BY updated DESC", notebookRowMapper);
    }
    
    public void update(Notebook notebook) {
        jdbcTemplate.update(
            "UPDATE notebook SET title = ?, updated = ? WHERE id = ?",
            notebook.title(),
            notebook.updated().toString(),
            notebook.id().toString()
        );
    }
    
    public void deleteById(UUID id) {
        jdbcTemplate.update("DELETE FROM notebook WHERE id = ?", id.toString());
    }
}
