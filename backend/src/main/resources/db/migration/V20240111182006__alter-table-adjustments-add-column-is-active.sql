alter table adjustments add column is_active boolean default true;

update adjustments set is_active = true;

alter table adjustments alter column is_active set not null;