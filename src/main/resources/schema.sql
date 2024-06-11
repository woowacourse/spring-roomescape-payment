CREATE TABLE IF NOT EXISTS `Payment`
(
    `id` bigint AUTO_INCREMENT PRIMARY KEY,
    `reservation_id`   bigint NOT NULL,
    `payment_key` varchar(200) NULL,
    `order_id` varchar(64) NULL,
    `amount` decimal(8, 2) NULL,
    `created_at` timestamp NULL,
    `updated_at` timestamp NULL,
    `deleted_at` timestamp NULL
);

CREATE TABLE IF NOT EXISTS `Member`
(
    `id` bigint AUTO_INCREMENT PRIMARY KEY,
    `name`  varchar(10) NULL,
    `email` varchar(24) NULL,
    `password` varchar(30) NULL,
    `role` varchar(10) NULL,
    `created_at` timestamp NULL,
    `updated_at` timestamp NULL,
    `deleted_at` timestamp NULL
);

CREATE TABLE IF NOT EXISTS `Theme`
(
    `id` bigint AUTO_INCREMENT PRIMARY KEY,
    `name`  varchar(30) NULL,
    `description` varchar(200) NULL,
    `thumbnail` varchar(300) NULL,
    `created_at` timestamp NULL,
    `updated_at` timestamp NULL,
    `deleted_at` timestamp NULL
);

CREATE TABLE IF NOT EXISTS `Reservation_Time`
(
    `id` bigint AUTO_INCREMENT PRIMARY KEY,
    `start_at` time NULL,
    `created_at` timestamp NULL,
    `updated_at` timestamp NULL,
    `deleted_at` timestamp NULL
);

CREATE TABLE IF NOT EXISTS `Reservation_Waiting`
(
    `id` bigint AUTO_INCREMENT PRIMARY KEY,
    `member_id`   bigint NOT NULL,
    `date` date NULL,
    `time_id`   bigint NOT NULL,
    `theme_id`   bigint NOT NULL,
    `denied_at` datetime NULL,
    `created_at` timestamp NULL,
    `updated_at` timestamp NULL,
    `deleted_at` timestamp NULL
);

CREATE TABLE IF NOT EXISTS `Reservation`
(
    `id` bigint AUTO_INCREMENT PRIMARY KEY,
    `member_id`   bigint NOT NULL,
    `date` date NULL,
    `time_id`   bigint NOT NULL,
    `theme_id`   bigint NOT NULL,
    `created_at` timestamp NULL,
    `updated_at` timestamp NULL,
    `deleted_at` timestamp NULL
);
