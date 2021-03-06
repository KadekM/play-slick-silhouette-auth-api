# --- !Ups

create table "users" ("uuid" UUID NOT NULL PRIMARY KEY,"email" VARCHAR NOT NULL,"firstName" VARCHAR NOT NULL,"lastName" VARCHAR NOT NULL,"state" VARCHAR NOT NULL);
create table "userTokens" ("token" VARCHAR NOT NULL PRIMARY KEY,"users_uuid" UUID NOT NULL,"expiresOn" TIMESTAMP NOT NULL,"tokenAction" VARCHAR NOT NULL);
alter table "userTokens" add constraint "fk_users_uuid" foreign key("users_uuid") references "users"("uuid") on update NO ACTION on delete NO ACTION;
create table "loginInfos" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"users_uuid" UUID NOT NULL,"providerId" VARCHAR NOT NULL,"providerKey" VARCHAR NOT NULL);
alter table "loginInfos" add constraint "fk_users_uuid" foreign key("users_uuid") references "users"("uuid") on update NO ACTION on delete NO ACTION;
create table "passwordInfos" ("loginInfos_id" BIGINT NOT NULL,"hasher" VARCHAR NOT NULL,"password" VARCHAR NOT NULL,"salt" VARCHAR);
alter table "passwordInfos" add constraint "fk_logininfos_id" foreign key("loginInfos_id") references "loginInfos"("id") on update NO ACTION on delete NO ACTION;
create table "permissions" ("name" VARCHAR NOT NULL PRIMARY KEY);
create table "permissionsToUsers" ("permissions_name" VARCHAR NOT NULL,"users_uuid" UUID NOT NULL);
alter table "permissionsToUsers" add constraint "fk_permissions" foreign key("permissions_name") references "permissions"("name") on update NO ACTION on delete NO ACTION;
alter table "permissionsToUsers" add constraint "fk_users_uuid" foreign key("users_uuid") references "users"("uuid") on update NO ACTION on delete NO ACTION;
create unique index "permission_to_user_idx" on "permissionsToUsers" ("permissions_name","users_uuid");

insert into permissions(name) values ('AccessAdmin');
insert into permissions(name) values ('AccessBar');

# --- !Downs
alter table "userTokens" drop constraint "fk_users_uuid";
alter table "loginInfos" drop constraint "fk_users_uuid";
alter table "passwordInfos" drop constraint "fk_logininfos_id";
alter table "permissionsToUsers" drop constraint "fk_permissions";
alter table "permissionsToUsers" drop constraint "fk_users_uuid";

delete from permissions;

drop table "users";
drop table "userTokens";
drop table "loginInfos";
drop table "passwordInfos";
drop table "permissions";
drop table "permissionsToUsers";
