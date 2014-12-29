-- Create a new "paste" table and index.
CREATE TABLE pastes (
id           TEXT      PRIMARY KEY,
filename     TEXT,
created      TIMESTAMP,
expires      TIMESTAMP,
content      TEXT,
content_type TEXT);

CREATE INDEX pastes_index (
id
expires);
