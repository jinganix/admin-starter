CREATE TABLE IF NOT EXISTS `admin_audit` (
  `id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `method` varchar(10) NOT NULL,
  `path` varchar(200) NOT NULL,
  `params` text NULL,
  `created_at` timestamp(3) NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updated_at` timestamp(3) NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  CONSTRAINT `admin_audit_pk` PRIMARY KEY (`id`)
);
CREATE INDEX `admin_audit_user_id_k` ON `admin_audit` (`user_id`);

CREATE TABLE IF NOT EXISTS `admin_permission` (
  `id` bigint NOT NULL,
  `type` tinyint NOT NULL DEFAULT '0',
  `code` varchar(50) NOT NULL,
  `name` varchar(50) NOT NULL,
  `description` varchar(200) NULL,
  `status` tinyint NOT NULL DEFAULT '0',
  `created_at` timestamp(3) NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updated_at` timestamp(3) NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  CONSTRAINT `admin_permission_pk` PRIMARY KEY (`id`),
  CONSTRAINT `admin_permission_code_uk` UNIQUE KEY (`code`)
);

CREATE TABLE IF NOT EXISTS `admin_role` (
  `id` bigint NOT NULL,
  `code` varchar(50) NOT NULL,
  `name` varchar(50) NOT NULL,
  `description` varchar(200) NULL,
  `status` tinyint NOT NULL DEFAULT '0',
  `created_at` timestamp(3) NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updated_at` timestamp(3) NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  CONSTRAINT `admin_role_pk` PRIMARY KEY (`id`),
  CONSTRAINT `admin_role_code_uk` UNIQUE KEY (`code`)
);

CREATE TABLE IF NOT EXISTS `admin_role_permission` (
  `id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  `permission_id` bigint NOT NULL,
  `created_at` timestamp(3) NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updated_at` timestamp(3) NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  CONSTRAINT `admin_role_permission_pk` PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `admin_user` (
  `id` bigint NOT NULL,
  `nickname` varchar(20) DEFAULT NULL,
  `status` tinyint NOT NULL DEFAULT '0',
  `created_at` timestamp(3) NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updated_at` timestamp(3) NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  CONSTRAINT `admin_user_pk` PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `admin_user_identity` (
  `id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `provider` tinyint NOT NULL DEFAULT 0,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NULL,
  `verified` tinyint NOT NULL DEFAULT 0,
  `created_at` timestamp(3) NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updated_at` timestamp(3) NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  CONSTRAINT `admin_user_identity_pk` PRIMARY KEY (`id`),
  CONSTRAINT `admin_user_identity_username_provider_uk` UNIQUE KEY (`username`, `provider`)
);
CREATE INDEX `admin_user_identity_user_id_cover_k` ON `admin_user_identity` (`user_id`, `provider`, `username`, `verified`);

CREATE TABLE IF NOT EXISTS `admin_user_role` (
  `id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  `created_at` timestamp(3) NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updated_at` timestamp(3) NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  CONSTRAINT `admin_user_role_pk` PRIMARY KEY (`id`)
);
