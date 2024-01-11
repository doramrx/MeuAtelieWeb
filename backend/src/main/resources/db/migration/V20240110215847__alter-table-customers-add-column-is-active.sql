alter table customers add column is_active boolean default true;

update customers set is_active = true;

alter table customers alter column is_active set not null;