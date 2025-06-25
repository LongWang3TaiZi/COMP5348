/*
 Navicat Premium Dump SQL

 Source Server         : db
 Source Server Type    : PostgreSQL
 Source Server Version : 160002 (160002)
 Source Host           : localhost:5432
 Source Catalog        : bank
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 160002 (160002)
 File Encoding         : 65001

 Date: 27/10/2024 18:20:01
*/


-- ----------------------------
-- Sequence structure for transaction_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."transaction_seq";
CREATE SEQUENCE "public"."transaction_seq" 
INCREMENT 50
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."transaction_seq" OWNER TO "postgres";

-- ----------------------------
-- Table structure for account
-- ----------------------------
DROP TABLE IF EXISTS "public"."account";
CREATE TABLE "public"."account" (
  "account_number" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "balance" float8 NOT NULL,
  "email" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "modify_time" timestamp(6),
  "username" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "version" int4 NOT NULL
)
;
ALTER TABLE "public"."account" OWNER TO "postgres";

-- ----------------------------
-- Records of account
-- ----------------------------
BEGIN;
INSERT INTO "public"."account" ("account_number", "balance", "email", "modify_time", "username", "version") VALUES ('17296848984629657', 1000, 'admin@admin.com', '2024-10-27 18:19:18.570784', 'admin', 1);
INSERT INTO "public"."account" ("account_number", "balance", "email", "modify_time", "username", "version") VALUES ('17300132590858740', 1000, '123@123.com', '2024-10-27 18:19:24.640926', 'customer', 1);
COMMIT;

-- ----------------------------
-- Table structure for transaction
-- ----------------------------
DROP TABLE IF EXISTS "public"."transaction";
CREATE TABLE "public"."transaction" (
  "id" int8 NOT NULL,
  "amount" float8 NOT NULL,
  "from_account" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "from_account_email" varchar(255) COLLATE "pg_catalog"."default",
  "modify_time" timestamp(6),
  "original_transaction_id" int8 NOT NULL,
  "status" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "to_account" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "transaction_type" varchar(255) COLLATE "pg_catalog"."default" NOT NULL
)
;
ALTER TABLE "public"."transaction" OWNER TO "postgres";

-- ----------------------------
-- Records of transaction
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."transaction_seq"', 1, false);

-- ----------------------------
-- Primary Key structure for table account
-- ----------------------------
ALTER TABLE "public"."account" ADD CONSTRAINT "account_pkey" PRIMARY KEY ("account_number");

-- ----------------------------
-- Checks structure for table transaction
-- ----------------------------
ALTER TABLE "public"."transaction" ADD CONSTRAINT "transaction_status_check" CHECK (status::text = ANY (ARRAY['Accepted'::character varying, 'Failed'::character varying, 'Refunded'::character varying, 'InsufficientBalance'::character varying]::text[]));
ALTER TABLE "public"."transaction" ADD CONSTRAINT "transaction_transaction_type_check" CHECK (transaction_type::text = ANY (ARRAY['Payment'::character varying, 'Refund'::character varying, 'ChargeBack'::character varying]::text[]));

-- ----------------------------
-- Primary Key structure for table transaction
-- ----------------------------
ALTER TABLE "public"."transaction" ADD CONSTRAINT "transaction_pkey" PRIMARY KEY ("id");
