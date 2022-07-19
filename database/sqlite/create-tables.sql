CREATE TABLE refget_data (
-- has both metadata and sequence information
    id TEXT PRIMARY KEY,
    md5 TEXT,
    trunc512 TEXT,
    length INT,
    sequence TEXT,
    iscircular INT DEFAULT 0 NOT NULL --sqlite3 stores boolean as int. 0(False), 1(True)
);

CREATE TABLE aliases (
    alias_id INTEGER PRIMARY KEY AUTOINCREMENT,
    sequence_id TEXT NOT NULL,
    alias TEXT,
    naming_authority TEXT,
    FOREIGN KEY (sequence_id) REFERENCES reference_sequence(id)
);