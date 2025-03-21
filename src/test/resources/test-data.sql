INSERT INTO users (id, name, email) VALUES (1, 'Jedusor', 'tom.jedusor@thermoweb.org');
ALTER TABLE users ALTER COLUMN id RESTART WITH 2;
INSERT INTO tasks (id, name, status, description, assignee_id) VALUES ('already-existing-task', 'already existing task', 'IN_PROGRESS', 'my task description', 1);
INSERT INTO tasks (id, name, status, description, assignee_id) VALUES ('opened-task', 'already opened task', 'OPEN', 'my task description', null);
INSERT INTO tasks (id, name, status, description, assignee_id) VALUES ('task-to-update', 'un-updated task', 'OPEN', 'my task description', null);