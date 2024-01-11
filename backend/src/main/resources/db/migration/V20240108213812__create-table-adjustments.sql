create table adjustments (
    id uuid default uuid_generate_v4() primary key,
    name varchar(30) not null,
    cost double precision not null
);