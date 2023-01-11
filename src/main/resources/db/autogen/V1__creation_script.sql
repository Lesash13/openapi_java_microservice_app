create sequence people_seq start 1 increment 1;

create table people
(
    id                      int8 not null,
    created_timestamp       timestamp,
    last_modified_timestamp timestamp,
    version                 int4,
    birthday                date,
    firstname               varchar(255),
    lastname                varchar(255),
    home_id                 int8,
    status                  varchar(255),
    parent_id               int8,
    primary key (id)
);

create table people_owned_houses
(
    people_id int8 not null,
    home_id   int8
);

create table people_people
(
    people_id    int8 not null,
    relatives_id int8 not null
);
create index peo_par_id_idx on people (parent_id);
create index peo_fir_las_idx on people (firstname, lastname);

alter table people
    add constraint peo_las_fir_bir_uq unique (lastname, firstname, birthday);

alter table people
    add constraint peo_par_id_fk
        foreign key (parent_id)
            references people;

alter table people_owned_houses
    add constraint peo_own_hou_peo_id_fk
        foreign key (people_id)
            references people;

alter table people_people
    add constraint peo_rel_id_fk
        foreign key (relatives_id)
            references people;

alter table people_people
    add constraint peo_peo_id_fk
        foreign key (people_id)
            references people;
