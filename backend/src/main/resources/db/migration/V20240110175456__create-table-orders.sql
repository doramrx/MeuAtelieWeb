create table orders (
    id uuid primary key,
    order_number SERIAL,
    created_at timestamp not null,
    updated_at timestamp,
    finished_at timestamp,
    id_customer uuid references customers(id)
);