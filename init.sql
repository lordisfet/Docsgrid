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
VALUES
    ('111-22-3333', 'Admin', '$2a$12$8GwdKtF7e0G8d0pt7nieJuMtx3a3/uPgy6px.Af0.T.lZjI579T5a', 'Owner', 1),
    ('222-11-3333', 'User', '$2a$12$8GwdKtF7e0G8d0pt7nieJuMtx3a3/uPgy6px.Af0.T.lZjI579T5a', 'Analyst', 1),
    ('000-00-0000','Max','$2a$12$s68zxc3699DhkUf4BpQcquEaO2uTy.golNYtCW9V1e.YrHuP5CONG','Java developer',1);

INSERT INTO document_templates(structure, title) VALUES
    ('I, {{name1}} (TIN: {{client1TIN}}), grant {{name2}} (TIN: {{client2TIN}}) the right to use {{resource}}', 'Usage Grant'),
    ('I, {{name1}} (TIN: {{client1TIN}}), hire {{name2}} (TIN: {{client2TIN}}) on the position {{job}}', 'Employment'),
    ('I, {{name1}} (TIN: {{client1TIN}}), lease {{property}} to {{name2}} (TIN: {{client2TIN}})', 'Lease Agreement'),
    ('I, {{name1}} (TIN: {{client1TIN}}), sell {{item}} to {{name2}} (TIN: {{client2TIN}})', 'Sales Agreement'),
    ('I, {{name1}} (TIN: {{client1TIN}}), appoint {{name2}} (TIN: {{client2TIN}}) as my attorney for {{purpose}}', 'Power of Attorney');


INSERT INTO documents(template_id, content) VALUES (1, '{"name1":"Admin","client1TIN":"111-22-3333","name2":"User","client2TIN":"222-11-3333","resource":"BMW X5"}');
INSERT INTO documents(template_id, content) VALUES (1, '{"name1":"User","client1TIN":"222-11-3333","name2":"Admin","client2TIN":"111-22-3333","resource":"Mercedes GLS"}');
INSERT INTO documents(template_id, content) VALUES (2, '{"name1":"Admin","client1TIN":"111-22-3333","name2":"User","client2TIN":"222-11-3333","job":"Analyst"}');

INSERT INTO signatories(document_id, employee_id, sign_status) VALUES (1, 1, TRUE);
INSERT INTO signatories(document_id, employee_id, sign_status) VALUES (1, 2, TRUE);
INSERT INTO signatories(document_id, employee_id, sign_status) VALUES (2, 1, TRUE);
INSERT INTO signatories(document_id, employee_id, sign_status) VALUES (2, 2, FALSE);
INSERT INTO signatories(document_id, employee_id, sign_status) VALUES (3, 1, TRUE);
INSERT INTO signatories(document_id, employee_id, sign_status) VALUES (3, 2, TRUE);
