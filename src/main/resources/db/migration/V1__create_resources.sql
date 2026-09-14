CREATE TABLE resources (
    id UUID PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    url VARCHAR(2048) NOT NULL,
    reason VARCHAR(1000),
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT resources_title_not_blank CHECK (char_length(btrim(title)) >= 1),
    CONSTRAINT resources_url_not_blank CHECK (char_length(btrim(url)) >= 1),
    CONSTRAINT resources_status_known CHECK (status IN ('PENDING'))
);
