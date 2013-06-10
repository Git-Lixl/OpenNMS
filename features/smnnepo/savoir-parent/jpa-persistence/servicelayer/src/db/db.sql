@funcs@

-- SQL for DS begins here

create table pais (
    id integer not null primary key,
    payload @clob@,
    end_date timestamp@timestamp_constraint@,
    subscription_type varchar(10),
    temp_queue varchar(128),
    creation_time timestamp@timestamp_constraint@,
    last_connect_time timestamp@timestamp_constraint@,
    last_disconnect_time timestamp@timestamp_constraint@,
    deletion_time timestamp@timestamp_constraint@
);


create table seq_table (
    id_name varchar(50) not null primary key,
    id_value integer
);


insert into seq_table(id_name, id_value) values ('pais', 100);
