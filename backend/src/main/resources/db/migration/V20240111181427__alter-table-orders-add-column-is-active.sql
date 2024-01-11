alter table orders add column is_active boolean default true;

update orders set is_active = true;

alter table orders alter column is_active set not null;