create table measurements (
    id uuid default uuid_generate_v4() primary key,
    measurement varchar(40) not null
);