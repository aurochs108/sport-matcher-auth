--liquibase formatted sql

--changeset dawidzubrowski:001-2026-04-27-004911-init-auth-schema
create table users (
    id uuid primary key,
    email varchar(255) not null unique,
    created_at timestamptz not null
);

create table user_credentials (
    id uuid primary key,
    user_id uuid not null,
    provider varchar(255) not null,
    password_hash varchar(255),
    constraint fk_user_credentials_user
        foreign key (user_id) references users(id),
    constraint uq_user_credentials_user_provider
        unique (user_id, provider)
);

create table refresh_tokens (
    id uuid primary key,
    token_hash varchar(255) not null unique,
    user_id uuid not null,
    device_id varchar(255) not null,
    expires_at timestamptz not null,
    created_at timestamptz not null,
    is_revoked boolean not null default false,
    constraint fk_refresh_tokens_user
        foreign key (user_id) references users(id)
);

create index idx_refresh_tokens_user_id on refresh_tokens(user_id);
create index idx_refresh_tokens_expires_at on refresh_tokens(expires_at);
