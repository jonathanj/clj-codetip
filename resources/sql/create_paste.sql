-- Create a new paste entry.
INSERT INTO pastes (id, filename, created, expires, content, content_type)
VALUES (
    :id,
    :filename,
    :created,
    :expires,
    :content,
    :content_type);
