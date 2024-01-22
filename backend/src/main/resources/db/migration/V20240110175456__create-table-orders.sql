create table orders (
    id uuid primary key,
    order_number serial not null,
    cost double precision not null,
    due_date timestamp not null,
    created_at timestamp not null,
    delivered_at timestamp,
    id_customer uuid references customers(id)
);