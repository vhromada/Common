DROP TABLE IF EXISTS account_roles;
DROP TABLE IF EXISTS accounts;
DROP TABLE IF EXISTS roles;

CREATE TABLE roles (
  id        INTEGER      NOT NULL CONSTRAINT roles_pk PRIMARY KEY,
  role_name VARCHAR(200) NOT NULL CONSTRAINT roles_name_ck CHECK (LENGTH(role_name) > 0)
);

CREATE TABLE accounts (
  id       INTEGER      NOT NULL CONSTRAINT accounts_pk PRIMARY KEY,
  uuid     VARCHAR(36)  NOT NULL CONSTRAINT accounts_uuid_ck CHECK (LENGTH(uuid) > 0),
  username VARCHAR(200) NOT NULL CONSTRAINT accounts_username_ck CHECK (LENGTH(username) > 0),
  password VARCHAR(200) NOT NULL CONSTRAINT accounts_password_ck CHECK (LENGTH(password) > 0)
);

CREATE TABLE account_roles (
  account INTEGER CONSTRAINT account_roles_account_fk REFERENCES accounts (id),
  role    INTEGER CONSTRAINT account_roles_role_fk REFERENCES roles (id)
);

DROP SEQUENCE IF EXISTS accounts_sq;
DROP SEQUENCE IF EXISTS roles_sq;

CREATE SEQUENCE accounts_sq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE roles_sq START WITH 1 INCREMENT BY 1;

INSERT INTO roles (id, role_name) VALUES (1, 'ROLE_ADMIN');
INSERT INTO roles (id, role_name) VALUES (2, 'ROLE_USER');

INSERT INTO accounts (id, uuid, username, password) VALUES (1, '08f12e2f-f842-436f-ac0d-b4d1026d74be', 'Account 1 username', 'Account 1 password');
INSERT INTO accounts (id, uuid, username, password) VALUES (2, '1436b587-401e-4183-9982-9e7eaea9d33a', 'Account 2 username', 'Account 2 password');
INSERT INTO accounts (id, uuid, username, password) VALUES (3, 'be63de12-96b7-46fc-943d-a1af577c0e5d', 'Account 3 username', 'Account 3 password');

INSERT INTO account_roles (account, role) VALUES (1, 1);
INSERT INTO account_roles (account, role) VALUES (2, 2);
INSERT INTO account_roles (account, role) VALUES (3, 1);
INSERT INTO account_roles (account, role) VALUES (3, 2);

ALTER SEQUENCE roles_sq RESTART WITH 3;
ALTER SEQUENCE accounts_sq RESTART WITH 4;
