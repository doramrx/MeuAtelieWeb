create table customer_adjustments (
    id uuid primary key,
    adjustment_cost double precision not null,
    id_adjustments uuid references adjustments(id),
    id_order_item uuid references order_items(id)
);