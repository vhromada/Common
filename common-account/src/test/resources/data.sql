INSERT INTO roles (id, role_name) VALUES (1, 'ROLE_ADMIN');
INSERT INTO roles (id, role_name) VALUES (2, 'ROLE_USER');

INSERT INTO accounts (id, username, password) VALUES (1, 'Account 1 username', 'Account 1 password');
INSERT INTO accounts (id, username, password) VALUES (2, 'Account 2 username', 'Account 2 password');
INSERT INTO accounts (id, username, password) VALUES (3, 'Account 3 username', 'Account 3 password');

INSERT INTO account_roles (account, role) VALUES (1, 1);
INSERT INTO account_roles (account, role) VALUES (2, 2);
INSERT INTO account_roles (account, role) VALUES (3, 1);
INSERT INTO account_roles (account, role) VALUES (3, 2);

ALTER SEQUENCE roles_sq RESTART WITH 3;
ALTER SEQUENCE accounts_sq RESTART WITH 4;
