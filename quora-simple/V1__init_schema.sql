-- Users
CREATE TABLE IF NOT EXISTS users (
    id         BIGSERIAL PRIMARY KEY,
    uuid       VARCHAR(36)  NOT NULL UNIQUE,
    first_name VARCHAR(50)  NOT NULL,
    last_name  VARCHAR(50)  NOT NULL,
    username   VARCHAR(30)  NOT NULL UNIQUE,
    email      VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    role       VARCHAR(20)  NOT NULL DEFAULT 'nonadmin',
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- Questions
CREATE TABLE IF NOT EXISTS questions (
    id         BIGSERIAL PRIMARY KEY,
    uuid       VARCHAR(36)  NOT NULL UNIQUE,
    content    TEXT         NOT NULL,
    user_id    BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- Answers
CREATE TABLE IF NOT EXISTS answers (
    id          BIGSERIAL PRIMARY KEY,
    uuid        VARCHAR(36) NOT NULL UNIQUE,
    ans         TEXT        NOT NULL,
    question_id BIGINT      NOT NULL REFERENCES questions(id) ON DELETE CASCADE,
    user_id     BIGINT      NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_questions_user_id  ON questions(user_id);
CREATE INDEX IF NOT EXISTS idx_answers_question_id ON answers(question_id);
CREATE INDEX IF NOT EXISTS idx_answers_user_id     ON answers(user_id);
