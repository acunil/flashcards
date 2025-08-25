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


# Database Migrations
Once a new database is created, you must replace the login details saved locally in your .env file.

Run the following command to create the database tables:
```bash
./liquibase-update.sh
```

In order to have multiple users, you must also create new users in the database.

The liquibase update script will run the migrations and create the tables.

However, new users will not be able to access or edit the database until they are granted permission.

Use pgAdmin to grant permissions to the new user.

1. Connect to the database in pgAdmin using details provided on the database portal
2. Run the following sql commands to grant access to the new user:

```sql
GRANT USAGE ON SCHEMA public TO newuser1;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO newuser1;
GRANT USAGE ON ALL SEQUENCES IN SCHEMA public TO newuser1;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO newuser1;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT USAGE ON SEQUENCES TO newuser1;
```

> #### IMPORTANT - Remember to import the stored procedure into the new database!
 

## Stored Procedure

Run this in the database to create the stored procedure. 
(Preferable to liquibase as liquibase has trouble reading the SQL for some reason)
```sql
CREATE OR REPLACE PROCEDURE record_card_rating(
  IN p_card_id BIGINT,
  IN p_rating  INT
)
LANGUAGE plpgsql
AS $$
DECLARE
  v_user_id UUID;
BEGIN
  -- Try to update existing history
  UPDATE card_history
  SET
    view_count  = view_count + 1,
    last_viewed = NOW(),
    last_rating = p_rating,
    avg_rating  = ROUND(((avg_rating * (view_count::numeric)) + p_rating) / (view_count + 1), 1)
  WHERE card_id = p_card_id;

  -- If no existing history row, fetch the user_id for this card
  IF NOT FOUND THEN
    SELECT user_id
    INTO v_user_id
    FROM card
    WHERE id = p_card_id;

    IF v_user_id IS NULL THEN
      RAISE EXCEPTION
        'Cannot create card_history for card_id % — no matching card or user_id is NULL',
        p_card_id
        USING ERRCODE = '23502';
    END IF;

    INSERT INTO card_history(card_id, user_id, avg_rating, view_count, last_viewed, last_rating)
    VALUES (p_card_id, v_user_id, ROUND(p_rating::numeric, 1), 1, NOW(), p_rating);
  END IF;
END;
$$;

```

To view the stored procedure, you can use the following command in the database query tool:

```sql
\df+ record_card_rating
```
