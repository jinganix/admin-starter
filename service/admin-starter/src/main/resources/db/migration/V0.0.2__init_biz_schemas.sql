CREATE TABLE IF NOT EXISTS `biz_overview` (
  `id` bigint NOT NULL,
  `month` date NOT NULL,
  `api_get` int NOT NULL,
  `api_post` int NOT NULL,
  `user_created` int NOT NULL,
  `user_deleted` int NOT NULL,
  `role_created` int NOT NULL,
  `role_deleted` int NOT NULL,
  `permission_created` int NOT NULL,
  `permission_deleted` int NOT NULL,
  `created_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updated_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  CONSTRAINT `biz_overview_pk` PRIMARY KEY (`id`),
  CONSTRAINT `biz_overview_month_uk` UNIQUE KEY (`month`)
);
