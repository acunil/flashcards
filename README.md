# flashcards

Flash cards app for subject revision

## Curl commands

Get all cards:

```bash
curl http://localhost:8080/api/cards
```

Add a new card:

```bashbash
curl -X POST http://localhost:8080/api/cards -H "Content-Type: application/json" -d '{"front":"nett","back":"nice"}'
```

Upload a CSV file:

```bash
curl -X POST http://localhost:8080/api/upload \
  -H "Content-Type: multipart/form-data" \
  -F "file=@/path/to/your/file.csv"
```

> 🔁 Replace `/path/to/your/file.csv` with the actual path to your CSV file.

## Stored Procedure

This is already active in the Neon database.

```sql
CREATE PROCEDURE record_card_rating(
  IN p_card_id BIGINT,
  IN p_rating INT
)
LANGUAGE plpgsql
AS $$
BEGIN
  UPDATE card_history
  SET
    view_count  = view_count + 1,
    last_viewed = NOW(),
    last_rating = p_rating,
    avg_rating  = ((avg_rating * (view_count::numeric)) + p_rating)
                  / (view_count + 1)
  WHERE card_id = p_card_id;

  IF NOT FOUND THEN
    INSERT INTO card_history(card_id, avg_rating, view_count, last_viewed, last_rating)
    VALUES (p_card_id, p_rating, 1, NOW(), p_rating);
  END IF;
END;
$$;
```

To view the stored procedure, you can use the following command in the Neon database:

```sql
\df+ record_card_vote
```
