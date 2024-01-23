create sequence orders_order_number_seq
increment 1
start 1;

create table orders (
    id uuid primary key,
    order_number int default nextval('orders_order_number_seq'),
    created_at timestamp not null,
    updated_at timestamp,
    finished_at timestamp,
    id_customer uuid references customers(id)
);

alter sequence orders_order_number_seq
owned by orders.order_number;