create table customer_measurements (
    id uuid default uuid_generate_v4() primary key,
    measurement_value double precision not null,
    id_measurement uuid references measurements(id),
    id_order_item uuid references order_items(id)
);