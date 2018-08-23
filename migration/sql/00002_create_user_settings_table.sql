-- +goose Up
CREATE TABLE user_settings (
  user_id   VARCHAR(255) NOT NULL PRIMARY KEY,
  api_token VARCHAR(255) NOT NULL UNIQUE
);

-- +goose Down
DROP TABLE user_settings;
