create table customers (
    id uuid default uuid_generate_v4() primary key,
    name varchar(100) not null,
    email varchar(100) not null,
    phone varchar(11)
);