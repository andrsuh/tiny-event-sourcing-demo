CREATE SCHEMA IF NOT EXISTS projection;

SET SEARCH_PATH TO projection;

CREATE TABLE IF NOT EXISTS "user"
(
 id            BIGSERIAL PRIMARY KEY,
 user_id       TEXT NOT NULL,
 login         TEXT NOT NULL,
 password      TEXT NOT NULL,
 created_at    BIGINT NOT NULL,
 UNIQUE(user_id),
 UNIQUE(login)
);

CREATE TABLE IF NOT EXISTS project
(
  id            BIGSERIAL PRIMARY KEY,
  project_id    TEXT NOT NULL,
  title         TEXT NOT NULL,
  created_at    BIGINT NOT NULL,
  creator_id    BIGINT REFERENCES "user",
  UNIQUE(project_id)
);

CREATE TABLE IF NOT EXISTS project_member
(
  id            BIGSERIAL PRIMARY KEY,
  user_id       BIGINT REFERENCES "user",
  project_id    BIGINT REFERENCES project,
  created_at    BIGINT NOT NULL,
  UNIQUE(user_id, project_id)
);

CREATE TABLE IF NOT EXISTS task_status
(
  id            BIGSERIAL PRIMARY KEY,
  project_id    BIGINT REFERENCES project,
  name          TEXT NOT NULL,
  created_at    BIGINT NOT NULL,
  UNIQUE(name, project_id)
);

CREATE TABLE IF NOT EXISTS task
(
  id                BIGSERIAL PRIMARY KEY,
  task_id           TEXT NOT NULL,
  project_id        BIGINT REFERENCES project,
  creator_id        BIGINT REFERENCES "user",
  task_status_id    BIGINT REFERENCES task_status,
  created_at        BIGINT NOT NULL,
  UNIQUE(task_id)
);

CREATE INDEX task_task_task_status_id ON task(task_status_id);

CREATE TABLE IF NOT EXISTS task_executor
(
  id            BIGSERIAL PRIMARY KEY,
  task_id       BIGINT REFERENCES task,
  user_id       BIGINT REFERENCES "user",
  created_at    BIGINT NOT NULL,
  UNIQUE(user_id, task_id)
);
