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