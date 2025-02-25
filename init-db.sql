-- Adminer 4.8.1 PostgreSQL 17.0 (Debian 17.0-1.pgdg120+1) dump

\connect "ccs-tech-db";

CREATE TABLE "public"."account" (
    "id" integer GENERATED BY DEFAULT AS IDENTITY(SEQUENCE NAME account_seq START WITH 1),
    "iban" character varying(25) NOT NULL,
    "balance" numeric(10,2) NOT NULL,
    CONSTRAINT "account_pkey" PRIMARY KEY ("id")
) WITH (oids = false);

INSERT INTO "account" ("id", "iban", "balance") VALUES
(1,	'RABO111',	20000.00),
(2,	'RABO222',	20000.00);

CREATE TABLE "public"."account_transaction" (
    "id" integer GENERATED BY DEFAULT AS IDENTITY(SEQUENCE NAME acc_trx_seq START WITH 1),
    "origin_account_number" character varying(26) NOT NULL,
    "target_account_number" character varying(26),
    "amount" numeric(10,2) NOT NULL,
    "percentage" double precision NOT NULL,
    "type" character varying(50) NOT NULL,
    "card_type" character varying(25) NOT NULL,
    "date" timestamp NOT NULL,
    CONSTRAINT "account_transaction_pkey" PRIMARY KEY ("id")
) WITH (oids = false);


CREATE TABLE "public"."card" (
    "id" integer GENERATED BY DEFAULT AS IDENTITY(SEQUENCE NAME card_seq START WITH 1),
    "number" integer NOT NULL,
    "iban" character varying(25) NOT NULL,
    "type" character varying(30),
    "user_id" integer NOT NULL,
    "status" character varying(25) NOT NULL,
    CONSTRAINT "card_pkey" PRIMARY KEY ("id")
) WITH (oids = false);

INSERT INTO "card" ("id", "number", "iban", "type", "user_id", "status") VALUES
(1,	11111,	'RABO111',	'DEBIT_CARD',	1,	'ACTIVE'),
(2,	22222,	'RABO222',	'CREDIT_CARD',	2,	'ACTIVE');


CREATE TABLE "public"."databasechangelog" (
    "id" character varying(255) NOT NULL,
    "author" character varying(255) NOT NULL,
    "filename" character varying(255) NOT NULL,
    "dateexecuted" timestamp NOT NULL,
    "orderexecuted" integer NOT NULL,
    "exectype" character varying(10) NOT NULL,
    "md5sum" character varying(35),
    "description" character varying(255),
    "comments" character varying(255),
    "tag" character varying(255),
    "liquibase" character varying(20),
    "contexts" character varying(255),
    "labels" character varying(255),
    "deployment_id" character varying(10)
) WITH (oids = false);


CREATE TABLE "public"."databasechangeloglock" (
    "id" integer NOT NULL,
    "locked" boolean NOT NULL,
    "lockgranted" timestamp,
    "lockedby" character varying(255),
    CONSTRAINT "databasechangeloglock_pkey" PRIMARY KEY ("id")
) WITH (oids = false);

CREATE TABLE "public"."users" (
    "id" integer GENERATED BY DEFAULT AS IDENTITY(SEQUENCE NAME user_seq START WITH 1),
    "username" character varying(50) NOT NULL,
    "name" character varying(50) NOT NULL,
    "lastname" character varying(50) NOT NULL,
    "password" character varying(500) NOT NULL,
    CONSTRAINT "users_pkey" PRIMARY KEY ("id")
) WITH (oids = false);


-- 2025-02-25 10:36:21.868798+00