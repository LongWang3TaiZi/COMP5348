/*
 Navicat Premium Dump SQL

 Source Server         : db
 Source Server Type    : PostgreSQL
 Source Server Version : 160002 (160002)
 Source Host           : localhost:5432
 Source Catalog        : delivery
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 160002 (160002)
 File Encoding         : 65001

 Date: 27/10/2024 18:20:21
*/


-- ----------------------------
-- Sequence structure for address_info_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."address_info_seq";
CREATE SEQUENCE "public"."address_info_seq" 
INCREMENT 50
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."address_info_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for customer_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."customer_seq";
CREATE SEQUENCE "public"."customer_seq" 
INCREMENT 50
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."customer_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for delivery_order_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."delivery_order_seq";
CREATE SEQUENCE "public"."delivery_order_seq" 
INCREMENT 50
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."delivery_order_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for harvest_info_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."harvest_info_seq";
CREATE SEQUENCE "public"."harvest_info_seq" 
INCREMENT 50
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."harvest_info_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for item_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."item_seq";
CREATE SEQUENCE "public"."item_seq" 
INCREMENT 50
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."item_seq" OWNER TO "postgres";

-- ----------------------------
-- Table structure for delivery_order
-- ----------------------------
DROP TABLE IF EXISTS "public"."delivery_order";
CREATE TABLE "public"."delivery_order" (
  "id" int8 NOT NULL,
  "creation_time" timestamp(6) NOT NULL,
  "delivery_status" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "email" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "from_address" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "product_name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "quantity" int4 NOT NULL,
  "to_address" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "update_time" timestamp(6) NOT NULL,
  "user_name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "version" int4 NOT NULL,
  "webhook_url" varchar(255) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "public"."delivery_order" OWNER TO "postgres";

-- ----------------------------
-- Records of delivery_order
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."address_info_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."customer_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."delivery_order_seq"', 1401, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."harvest_info_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."item_seq"', 1, false);

-- ----------------------------
-- Checks structure for table delivery_order
-- ----------------------------
ALTER TABLE "public"."delivery_order" ADD CONSTRAINT "delivery_order_delivery_status_check" CHECK (delivery_status::text = ANY (ARRAY['CREATED'::character varying, 'PICKED_UP'::character varying, 'DELIVERING'::character varying, 'DELIVERED'::character varying, 'LOST'::character varying]::text[]));

-- ----------------------------
-- Primary Key structure for table delivery_order
-- ----------------------------
ALTER TABLE "public"."delivery_order" ADD CONSTRAINT "delivery_order_pkey" PRIMARY KEY ("id");
