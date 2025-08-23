--liquibase formatted sql

-----------------------------------------------------------------
-- 001: Base schema - Users
-----------------------------------------------------------------
--changeset lucian:001_create_app_user_table
CREATE TABLE IF NOT EXISTS app_user (
    id          UUID PRIMARY KEY,
    username    VARCHAR(255) NOT NULL UNIQUE,
    is_active   BOOLEAN NOT NULL DEFAULT TRUE
);

-----------------------------------------------------------------
-- 002: Base schema - Subject + ENUM type
-----------------------------------------------------------------
--changeset lucian:002_create_side_enum
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'side') THEN
        CREATE TYPE side AS ENUM ('FRONT', 'BACK', 'ANY');
    END IF;
END $$;

--changeset lucian:002_create_subject_table
CREATE TABLE IF NOT EXISTS subject (
    id                  BIGSERIAL PRIMARY KEY,
    name                VARCHAR(100) NOT NULL UNIQUE,
    front_label         VARCHAR(255),
    back_label          VARCHAR(255),
    default_side        side DEFAULT 'FRONT',
    display_deck_names  BOOLEAN DEFAULT FALSE,
    user_id             UUID NOT NULL REFERENCES app_user(id) ON DELETE CASCADE
);

-----------------------------------------------------------------
-- 003: Base schema - Card
-----------------------------------------------------------------
--changeset lucian:003_create_card_table
CREATE TABLE IF NOT EXISTS card (
    id          BIGSERIAL PRIMARY KEY,
    front       VARCHAR(255) NOT NULL,
    back        VARCHAR(255) NOT NULL,
    hint_front  VARCHAR(255),
    hint_back   VARCHAR(255),
    subject_id  BIGINT NOT NULL REFERENCES subject(id),
    user_id     UUID NOT NULL REFERENCES app_user(id) ON DELETE CASCADE
);

-----------------------------------------------------------------
-- 004: Base schema - Deck + Join Table
-----------------------------------------------------------------
--changeset lucian:004_create_deck_and_carddeck_tables
CREATE TABLE IF NOT EXISTS deck (
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(40) NOT NULL,
    subject_id BIGINT NOT NULL REFERENCES subject(id),
    user_id    UUID NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    CONSTRAINT uq_deck_subject_name UNIQUE (subject_id, name)
);

CREATE TABLE IF NOT EXISTS cardDeck (
    deckId BIGINT NOT NULL REFERENCES deck(id),
    cardId BIGINT NOT NULL REFERENCES card(id),
    PRIMARY KEY (deckId, cardId)
);

-----------------------------------------------------------------
-- 005: Base schema - Card History
-----------------------------------------------------------------
--changeset lucian:005_create_card_history_table
CREATE TABLE IF NOT EXISTS card_history (
    id           BIGSERIAL PRIMARY KEY,
    card_id      BIGINT NOT NULL REFERENCES card(id),
    avg_rating   DOUBLE PRECISION,
    view_count   INTEGER,
    last_viewed  TIMESTAMP,
    last_rating  INTEGER,
    subject_id   BIGINT NOT NULL REFERENCES subject(id),
    user_id      UUID NOT NULL REFERENCES app_user(id) ON DELETE CASCADE
);

-----------------------------------------------------------------
-- End of schema.sql
-----------------------------------------------------------------
