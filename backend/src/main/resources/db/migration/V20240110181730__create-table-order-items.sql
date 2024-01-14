create table order_items (
    id uuid primary key,
    title varchar(40) not null,
    description varchar(150),
    id_order uuid references orders(id)
);