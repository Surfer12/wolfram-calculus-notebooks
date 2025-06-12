package com.example.notebook.persistence;

import com.example.notebook.model.Cell;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public class CellRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    private final RowMapper<Cell> cellRowMapper = (rs, rowNum) -> new Cell(
        UUID.fromString(rs.getString("id")),
        UUID.fromString(rs.getString("notebook_id")),
        rs.getString("input"),
        Instant.parse(rs.getString("submitted_at"))
    );
    
    public CellRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public Cell insert(Cell cell) {
        jdbcTemplate.update(
            "INSERT INTO cell (id, notebook_id, input, submitted_at) VALUES (?, ?, ?, ?)",
            cell.id().toString(),
            cell.notebookId().toString(),
            cell.input(),
            cell.submittedAt().toString()
        );
        return cell;
    }
    
    public List<Cell> findByNotebookId(UUID notebookId) {
        return jdbcTemplate.query(
            "SELECT * FROM cell WHERE notebook_id = ? ORDER BY submitted_at ASC",
            cellRowMapper,
            notebookId.toString()
        );
    }
    
    public void deleteByNotebookId(UUID notebookId) {
        jdbcTemplate.update("DELETE FROM cell WHERE notebook_id = ?", notebookId.toString());
    }
}
