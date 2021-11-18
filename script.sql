create database db_example;
create user 'springuser'@'%' identified by 'ThePassword';
grant all on db_example.* to 'springuser'@'%';
create table owner
(
    id                int auto_increment
        primary key,
    name              text      not null,
    phone_number      text      null,
    date_registration timestamp not null
);
alter table owner
    auto_increment = 48123450;
create table item
(
    uuid         varchar(36) not null
        primary key,
    name         text        not null,
    owner_id     int         null,
    status       text        not null,
    date_into    timestamp   null,
    date_leave   timestamp   null,
    date_record  timestamp   not null,
    length       double DEFAULT '0',
    width        double DEFAULT '0',
    height       double DEFAULT '0',
    architecture text        NOT NULL,
    description  text
);
