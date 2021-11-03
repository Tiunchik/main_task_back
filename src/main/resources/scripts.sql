create table films
(
    code char(5) not null
        constraint firstkey
            primary key,
    title varchar(40) not null,
    did integer not null,
    date_prod date,
    kind varchar(10),
    len integer not null
);

alter table films owner to psql;

CREATE OR REPLACE FUNCTION films_notify_change() RETURNS TRIGGER AS $$
BEGIN
    PERFORM pg_notify('films_changed','{"tableName":"films_changed","value":'||row_to_json(NEW)::text||'}');
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

create trigger films_table_change
    after insert or update or delete
    on films
    for each row
execute procedure films_notify_change();
