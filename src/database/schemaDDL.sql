create table if not exists public.companies
(
    id           serial
    primary key,
    company_name varchar(255) not null
    unique
    );

alter table public.companies
    owner to postgres;

create table if not exists public.employees
(
    id            serial
    primary key,
    tin           varchar(11)  not null
    unique
    constraint tin_format_check
    check ((tin)::text ~ '^[0-9]{3}-[0-9]{2}-[0-9]{4}$'::text),
    full_name     varchar(255) not null,
    password_hash varchar(255) not null,
    job           varchar(255) not null,
    company_id    integer
    references public.companies
    on delete cascade
    );

alter table public.employees
    owner to postgres;

create table if not exists public.document_templates
(
    id        serial
    primary key,
    title     varchar(255)
    unique,
    structure text
    );

alter table public.document_templates
    owner to postgres;

create table if not exists public.documents
(
    id      serial
    primary key,
    content json not null
);

alter table public.documents
    owner to postgres;

