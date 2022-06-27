create table todos (
    id SERIAL PRIMARY KEY,
    title varchar(255),
    description varchar(255),
    completed bool,
    created_at date
);