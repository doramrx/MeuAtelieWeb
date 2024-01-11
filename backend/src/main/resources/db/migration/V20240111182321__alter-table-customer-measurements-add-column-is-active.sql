alter table customer_measurements add column is_active boolean default true;

update customer_measurements set is_active = true;

alter table customer_measurements alter column is_active set not null;