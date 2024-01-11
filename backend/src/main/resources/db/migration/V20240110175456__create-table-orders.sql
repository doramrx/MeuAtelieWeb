create table orders (
    id uuid default uuid_generate_v4() primary key,
    cost double precision not null,
    type varchar(20) check (type in('Adjust', 'Tailored')),
    due_date timestamp not null,
    created_at timestamp not null,
    delivered_at timestamp,
    id_customer uuid references customers(id)
);