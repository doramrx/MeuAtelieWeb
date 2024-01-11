alter table measurements add column is_active boolean default true;

update measurements set is_active = true;

alter table measurements alter column is_active set not null;