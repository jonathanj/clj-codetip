-- Delete all pastes whose expiration date is later than :now.
DELETE FROM pastes WHERE (expires <= :now);
