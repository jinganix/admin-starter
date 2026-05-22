CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` bigint NOT NULL,
  `nickname` varchar(20) DEFAULT NULL,
  `status` tinyint NOT NULL DEFAULT '0',
  `created_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updated_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  CONSTRAINT `sys_user_pk` PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `sys_user_token` (
  `id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `refresh_token` varchar(50) DEFAULT NULL,
  `created_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updated_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  CONSTRAINT `sys_user_token_pk` PRIMARY KEY (`id`),
  CONSTRAINT `sys_user_token_refresh_token_uk` UNIQUE KEY (`refresh_token`)
);

CREATE TABLE IF NOT EXISTS `sys_user_credential` (
  `id` bigint NOT NULL,
  `username` varchar(30) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL,
  `created_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updated_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  CONSTRAINT `sys_user_credential_pk` PRIMARY KEY (`id`),
  CONSTRAINT `sys_user_credential_username_uk` UNIQUE KEY (`username`)
);

CREATE TABLE IF NOT EXISTS `sys_role` (
  `id` bigint NOT NULL,
  `code` varchar(50) NOT NULL,
  `name` varchar(50) NOT NULL,
  `description` varchar(200) NULL,
  `status` tinyint NOT NULL DEFAULT '0',
  `created_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updated_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  CONSTRAINT `sys_role_pk` PRIMARY KEY (`id`),
  CONSTRAINT `sys_role_code_uk` UNIQUE KEY (`code`)
);

CREATE TABLE IF NOT EXISTS `sys_user_role` (
  `id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  `created_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updated_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  CONSTRAINT `sys_user_role_pk` PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `sys_permission` (
  `id` bigint NOT NULL,
  `type` tinyint NOT NULL DEFAULT '0',
  `code` varchar(50) NOT NULL,
  `name` varchar(50) NOT NULL,
  `description` varchar(200) NULL,
  `status` tinyint NOT NULL DEFAULT '0',
  `created_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updated_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  CONSTRAINT `sys_permission_pk` PRIMARY KEY (`id`),
  CONSTRAINT `sys_permission_code_uk` UNIQUE KEY (`code`)
);

CREATE TABLE IF NOT EXISTS `sys_role_permission` (
  `id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  `permission_id` bigint NOT NULL,
  `created_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updated_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  CONSTRAINT `sys_role_permission_pk` PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `sys_audit` (
  `id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `method` varchar(10) NOT NULL,
  `path` varchar(200) NOT NULL,
  `params` text NULL,
  `created_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updated_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  CONSTRAINT `sys_audit_pk` PRIMARY KEY (`id`)
);
CREATE INDEX `sys_audit_user_id_k` ON `sys_audit` (`user_id`);
