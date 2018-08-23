-- +goose Up
CREATE TABLE user_locations (
  id      VARCHAR(255) NOT NULL PRIMARY KEY,
  user_id VARCHAR(255) NOT NULL,
  data    json         NOT NULL
);
CREATE INDEX user_locations_user_id ON user_locations USING btree (user_id);

-- +goose Down
DROP TABLE user_locations;
