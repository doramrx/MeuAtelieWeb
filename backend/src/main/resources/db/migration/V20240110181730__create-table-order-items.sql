create table order_items (
    id uuid primary key,
    title varchar(40) not null,
    description varchar(150),
    type varchar(20) check (type in('ADJUST', 'TAILORED')),
    cost double precision not null,
    id_order uuid references orders(id)
);