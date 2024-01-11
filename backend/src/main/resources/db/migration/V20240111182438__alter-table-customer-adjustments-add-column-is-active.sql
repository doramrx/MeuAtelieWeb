alter table customer_adjustments add column is_active boolean default true;

update customer_adjustments set is_active = true;

alter table customer_adjustments alter column is_active set not null;