alter table order_items add column is_active boolean default true;

update order_items set is_active = true;

alter table order_items alter column is_active set not null;