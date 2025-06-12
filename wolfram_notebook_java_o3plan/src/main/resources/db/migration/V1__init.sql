-- Initial database schema for Wolfram Notebook application

CREATE TABLE notebook (
  id         TEXT PRIMARY KEY,
  title      TEXT NOT NULL,
  created    TEXT NOT NULL,
  updated    TEXT NOT NULL
);

CREATE TABLE cell (
  id           TEXT PRIMARY KEY,
  notebook_id  TEXT NOT NULL,
  input        TEXT NOT NULL,
  submitted_at TEXT NOT NULL,
  FOREIGN KEY(notebook_id) REFERENCES notebook(id) ON DELETE CASCADE
);

CREATE TABLE evaluation_result (
  cell_id      TEXT PRIMARY KEY,
  output       TEXT,
  success      INTEGER NOT NULL,
  error_msg    TEXT,
  eval_time_ms INTEGER NOT NULL,
  FOREIGN KEY(cell_id) REFERENCES cell(id) ON DELETE CASCADE
);

-- Indexes for better query performance
CREATE INDEX idx_cell_notebook_id ON cell(notebook_id);
CREATE INDEX idx_cell_submitted_at ON cell(submitted_at);
CREATE INDEX idx_notebook_updated ON notebook(updated);
