create schema rule_engine;

create table if not exists rule_engine.person
(
    id             uuid not null
    constraint person_pk
    primary key,
    national_id     varchar,
    name           varchar,
    surname        varchar,
    address        varchar,
    email          varchar,
    amount         bigint,
    payment_method varchar,
    payment_date   date
);

create table if not exists rule_engine.company
(
    id   uuid not null
    constraint company_pk
    primary key,
    name varchar
);


create table if not exists rule_engine.car
(
    id        uuid                 not null
    constraint car_pk
    primary key,
    name      varchar,
    code      varchar,
    color     varchar,
    price     bigint,
    company   uuid
    constraint car_company_id_fk
    references rule_engine.company,
    fare      bigint,
    available boolean default true not null
);


create table rule_engine.contract
(
    id             uuid not null
        constraint contract_pk
            primary key,
    start_date     date,
    end_date       date,
    owner          uuid
        constraint contract_person_id_fk
            references rule_engine.person,
    car            uuid
        constraint contract_car_id_fk
            references rule_engine.car,
    days           bigint,
    payment_method varchar,
    payment_date   varchar,
    bail_amount    integer
);

create table rule_engine.rule(
                                 id uuid primary key ,
                                 rule_type varchar(50),
                                rule_name varchar(50),
                                rule_content varchar(600)
)

