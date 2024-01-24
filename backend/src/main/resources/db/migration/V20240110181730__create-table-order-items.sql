create table order_items (
    id uuid primary key,
    title varchar(40) not null,
    description varchar(150),
    type varchar(20) check (type in('ADJUST', 'TAILORED')),
    cost double precision,
    id_order uuid references orders(id),
    created_at timestamp not null,
    due_date timestamp not null,
    delivered_at timestamp
);