CREATE OR REPLACE FUNCTION films_notify_change() RETURNS TRIGGER AS $$
BEGIN
    PERFORM pg_notify('films_changed',row_to_json(NEW)::text);
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER films_table_change
    AFTER INSERT OR UPDATE OR DELETE ON films
    FOR EACH ROW EXECUTE PROCEDURE films_notify_change();
