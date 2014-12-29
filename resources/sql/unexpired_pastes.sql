-- Retrieve all unexpired pastes.
SELECT * FROM pastes WHERE (expires > :now);
