create table orders (
    id uuid primary key,
    order_number SERIAL,
    due_date timestamp not null,
    created_at timestamp not null,
    delivered_at timestamp,
    id_customer uuid references customers(id)
);