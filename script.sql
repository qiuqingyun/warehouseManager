create database db_warehouse;
use db_warehouse;
create user 'warehouseuser'@'%' identified by 'cEZ28iKH8XrLjRL';
grant all on db_warehouse.* to 'warehouseuser'@'%';
create table owner
(
    id int auto_increment
        primary key,
    name text not null,
    phone_number text null,
    date_registration timestamp not null,
    date_last_change timestamp not null,
    note text null
);
alter table owner
    auto_increment = 48123450;
create table item
(
    uuid varchar(36) not null
        primary key,
    name text not null,
    status text not null,
    owner_id int null,
    date_record timestamp not null,
    date_into timestamp null,
    date_leave timestamp null,
    date_last_change timestamp not null,
    length double default 0 null,
    width double default 0 null,
    height double default 0 null,
    architecture text not null,
    description text null
);
create table role
(
    id int auto_increment,
    name text not null,
    constraint role_id_uindex
        unique (id)
);

alter table role
    add primary key (id);
create table user
(
    id int auto_increment,
    username text not null,
    password text not null,
    role_id int not null,
    account_non_expired tinyint(1) not null,
    account_non_locked tinyint(1) not null,
    credentials_non_expired tinyint(1) not null,
    enabled tinyint(1) not null,
    constraint user_t2_id_uindex
        unique (id)
);

alter table user
    add primary key (id);
