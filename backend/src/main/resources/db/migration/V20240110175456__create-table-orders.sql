create table orders (
    id uuid primary key,
    cost double precision not null,
    type varchar(20) check (type in('ADJUST', 'TAILORED')),
    due_date timestamp not null,
    created_at timestamp not null,
    delivered_at timestamp,
    id_customer uuid references customers(id)
);