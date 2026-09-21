CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(64) NOT NULL,
    CONSTRAINT users_username_unique UNIQUE (username)
);

CREATE TABLE api_keys (
    token VARCHAR(128) PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users (id)
);

-- Dev/test seeds only: plaintext API keys for local learning. Not for production.
INSERT INTO users (id, username) VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'alice'),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'bob');

INSERT INTO api_keys (token, user_id) VALUES
    ('li_alice_dev_key_001', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'),
    ('li_bob_dev_key_002', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb');

ALTER TABLE resources
    ADD COLUMN owner_id UUID REFERENCES users (id);

-- Local learning DBs may have rows from hito 2; attribute them to alice then require owner.
UPDATE resources
SET owner_id = 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'
WHERE owner_id IS NULL;

ALTER TABLE resources
    ALTER COLUMN owner_id SET NOT NULL;
