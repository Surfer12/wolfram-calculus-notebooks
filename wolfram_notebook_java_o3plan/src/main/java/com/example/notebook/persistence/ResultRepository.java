package com.example.notebook.persistence;

import com.example.notebook.model.EvaluationResult;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ResultRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    private final RowMapper<EvaluationResult> resultRowMapper = (rs, rowNum) -> new EvaluationResult(
        UUID.fromString(rs.getString("cell_id")),
        rs.getString("output"),
        rs.getBoolean("success"),
        rs.getString("error_msg"),
        Duration.ofMillis(rs.getLong("eval_time_ms"))
    );
    
    public ResultRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public EvaluationResult insert(EvaluationResult result) {
        jdbcTemplate.update(
            "INSERT INTO evaluation_result (cell_id, output, success, error_msg, eval_time_ms) VALUES (?, ?, ?, ?, ?)",
            result.cellId().toString(),
            result.output(),
            result.success(),
            result.errorMessage(),
            result.evalTime().toMillis()
        );
        return result;
    }
    
    public Optional<EvaluationResult> findByCellId(UUID cellId) {
        List<EvaluationResult> results = jdbcTemplate.query(
            "SELECT * FROM evaluation_result WHERE cell_id = ?",
            resultRowMapper,
            cellId.toString()
        );
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    public List<EvaluationResult> findByNotebookId(UUID notebookId) {
        return jdbcTemplate.query(
            """
            SELECT er.* FROM evaluation_result er
            JOIN cell c ON er.cell_id = c.id
            WHERE c.notebook_id = ?
            ORDER BY c.submitted_at ASC
            """,
            resultRowMapper,
            notebookId.toString()
        );
    }
    
    public void deleteByCellId(UUID cellId) {
        jdbcTemplate.update("DELETE FROM evaluation_result WHERE cell_id = ?", cellId.toString());
    }
}
