DROP TABLE IF EXISTS signatories;
DROP TABLE IF EXISTS documents;
DROP TABLE IF EXISTS employees;
DROP TABLE IF EXISTS companies;
DROP TABLE IF EXISTS document_templates;


CREATE TABLE companies (
    id             SERIAL PRIMARY KEY,
    company_name   TEXT    NOT NULL UNIQUE
);

CREATE TABLE employees (
    id             SERIAL PRIMARY KEY,
    tin            TEXT    NOT NULL UNIQUE,
    full_name      TEXT    NOT NULL,
    password_hash  TEXT    NOT NULL,
    job            TEXT    NOT NULL,
    company_id     INTEGER -- can be null
        REFERENCES companies(id)
        ON DELETE RESTRICT
);

CREATE TABLE document_templates (
    id             SERIAL PRIMARY KEY,
    structure      TEXT    NOT NULL,
    title          TEXT    NOT NULL
);

CREATE TABLE documents (
    id             SERIAL PRIMARY KEY,
    template_id    INTEGER NOT NULL
        REFERENCES document_templates(id)
        ON DELETE RESTRICT,
    content        TEXT    NOT NULL
);

CREATE TABLE signatories (
    document_id    INTEGER NOT NULL
        REFERENCES documents(id)
        ON DELETE CASCADE,
    employee_id    INTEGER NOT NULL
        REFERENCES employees(id)
        ON DELETE RESTRICT,
    sign_status    BOOLEAN NOT NULL,
    PRIMARY KEY (document_id, employee_id)
);

INSERT INTO companies(company_name) VALUES ('SSU');
INSERT INTO companies(company_name) VALUES ('without company');

INSERT INTO employees(tin, full_name, password_hash, job, company_id)
VALUES ('111-22-3333', 'Admin', '$2a$12$0hehvRZgk9/t8BKxaNAvWuAAs.q67WlsmJ79gN6mUNEogmv7DZfe2', 'Owner', 1);

INSERT INTO employees(tin, full_name, password_hash, job, company_id)
VALUES ('222-11-3333', 'User', '$2a$12$0hehvRZgk9/t8BKxaNAvWuAAs.q67WlsmJ79gN6mUNEogmv7DZfe2', 'Analyst', 1);

INSERT INTO document_templates(structure, title) VALUES ('I, {{name1}}, hire person {{name2}} on the position {{job}}', 'Employment');
INSERT INTO document_templates(structure, title) VALUES ('I, {{name1}}, sell {{sell_item}} to {{name2}} for {{amount}} USD', 'Sell Contract (USD)');
INSERT INTO document_templates(structure, title) VALUES ('I, {{name1}}, authorize the use of {{share_item}} to {{name2}}', 'Sharing');

INSERT INTO documents(template_id, content) VALUES (1, '{"name1":"Admin","name2":"User","job":"Analyst"}');
INSERT INTO documents(template_id, content) VALUES (2, '{"name1":"User","name2":"Admin","sell_item":"BMW X5","amount":"20000"}');

INSERT INTO signatories(document_id, employee_id, sign_status) VALUES (1, 1, TRUE);
INSERT INTO signatories(document_id, employee_id, sign_status) VALUES (1, 2, TRUE);
INSERT INTO signatories(document_id, employee_id, sign_status) VALUES (2, 1, TRUE);
INSERT INTO signatories(document_id, employee_id, sign_status) VALUES (2, 2, FALSE);
