-- MySQL dump 10.13  Distrib 8.1.0, for macos13.3 (arm64)
--
-- Host: 127.0.0.1    Database: exchange-diary
-- ------------------------------------------------------
-- Server version	11.1.2-MariaDB-1:11.1.2+maria~ubu2204

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `auth_password`
--

DROP TABLE IF EXISTS `auth_password`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `auth_password` (
  `password` varchar(255) NOT NULL,
  `username` varchar(31) NOT NULL,
  `id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_qgl016vovi3k21753sevog6p5` (`username`),
  CONSTRAINT `FKs28qr8e9yffpdciv3k09e95r4` FOREIGN KEY (`id`) REFERENCES `member` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `auth_password`
--

LOCK TABLES `auth_password` WRITE;
/*!40000 ALTER TABLE `auth_password` DISABLE KEYS */;
/*!40000 ALTER TABLE `auth_password` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `auth_social`
--

DROP TABLE IF EXISTS `auth_social`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `auth_social` (
  `oauth_id` varchar(255) NOT NULL,
  `oauth_type` varchar(20) NOT NULL,
  `id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK9en3xyr72k4gd5mqsol94ta3f` FOREIGN KEY (`id`) REFERENCES `member` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `auth_social`
--

LOCK TABLES `auth_social` WRITE;
/*!40000 ALTER TABLE `auth_social` DISABLE KEYS */;
/*!40000 ALTER TABLE `auth_social` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `blacklist`
--

DROP TABLE IF EXISTS `blacklist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `blacklist` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ended_at` datetime NOT NULL,
  `started_at` datetime NOT NULL,
  `type` varchar(20) NOT NULL,
  `member_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK3iajjr87pnejucqth00u4l32s` (`member_id`),
  CONSTRAINT `FK3iajjr87pnejucqth00u4l32s` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `blacklist`
--

LOCK TABLES `blacklist` WRITE;
/*!40000 ALTER TABLE `blacklist` DISABLE KEYS */;
/*!40000 ALTER TABLE `blacklist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `block`
--

DROP TABLE IF EXISTS `block`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `block` (
  `member_id` bigint(20) NOT NULL,
  `target_member_id` bigint(20) NOT NULL,
  `blocked_at` datetime NOT NULL,
  PRIMARY KEY (`member_id`,`target_member_id`),
  KEY `FKq498obgrmn1x4bi5gjx04c103` (`target_member_id`),
  CONSTRAINT `FKq498obgrmn1x4bi5gjx04c103` FOREIGN KEY (`target_member_id`) REFERENCES `member` (`id`),
  CONSTRAINT `FKrafl451lbepvxgwy4qblo84t6` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `block`
--

LOCK TABLES `block` WRITE;
/*!40000 ALTER TABLE `block` DISABLE KEYS */;
/*!40000 ALTER TABLE `block` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bookmark`
--

DROP TABLE IF EXISTS `bookmark`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bookmark` (
  `member_id` bigint(20) NOT NULL,
  `note_id` bigint(20) NOT NULL,
  `created_at` datetime NOT NULL,
  PRIMARY KEY (`member_id`,`note_id`),
  KEY `FK9tqba0qeyy7j2pic2y9ciu9l6` (`note_id`),
  CONSTRAINT `FK9tqba0qeyy7j2pic2y9ciu9l6` FOREIGN KEY (`note_id`) REFERENCES `note` (`id`),
  CONSTRAINT `FKr49x0nvx1grd5b4rsy6icu01` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bookmark`
--

LOCK TABLES `bookmark` WRITE;
/*!40000 ALTER TABLE `bookmark` DISABLE KEYS */;
/*!40000 ALTER TABLE `bookmark` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cover_color`
--

DROP TABLE IF EXISTS `cover_color`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cover_color` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `color_code` varchar(255) NOT NULL,
  `diary_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9dkwq68331wxcx034yquhec3a` (`diary_id`),
  CONSTRAINT `FK9dkwq68331wxcx034yquhec3a` FOREIGN KEY (`diary_id`) REFERENCES `diary` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cover_color`
--

LOCK TABLES `cover_color` WRITE;
/*!40000 ALTER TABLE `cover_color` DISABLE KEYS */;
/*!40000 ALTER TABLE `cover_color` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cover_image`
--

DROP TABLE IF EXISTS `cover_image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cover_image` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `image_url` varchar(255) NOT NULL,
  `diary_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKal84kao9ug5o0s874t04to6uo` (`diary_id`),
  CONSTRAINT `FKal84kao9ug5o0s874t04to6uo` FOREIGN KEY (`diary_id`) REFERENCES `diary` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cover_image`
--

LOCK TABLES `cover_image` WRITE;
/*!40000 ALTER TABLE `cover_image` DISABLE KEYS */;
/*!40000 ALTER TABLE `cover_image` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `device_registry`
--

DROP TABLE IF EXISTS `device_registry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `device_registry` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime NOT NULL,
  `token` varchar(255) NOT NULL,
  `member_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKdbgajh10uj3q4r11yib4mj7d3` (`member_id`),
  CONSTRAINT `FKdbgajh10uj3q4r11yib4mj7d3` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `device_registry`
--

LOCK TABLES `device_registry` WRITE;
/*!40000 ALTER TABLE `device_registry` DISABLE KEYS */;
/*!40000 ALTER TABLE `device_registry` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `diary`
--

DROP TABLE IF EXISTS `diary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `diary` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cover_type` varchar(255) NOT NULL,
  `created_at` datetime NOT NULL,
  `group_name` varchar(15) DEFAULT NULL,
  `title` varchar(31) NOT NULL,
  `updated_at` datetime NOT NULL,
  `master_member_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK6in65y28scq49b8h5cq1sfnri` (`master_member_id`),
  CONSTRAINT `FK6in65y28scq49b8h5cq1sfnri` FOREIGN KEY (`master_member_id`) REFERENCES `member` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `diary`
--

LOCK TABLES `diary` WRITE;
/*!40000 ALTER TABLE `diary` DISABLE KEYS */;
/*!40000 ALTER TABLE `diary` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `follow`
--

DROP TABLE IF EXISTS `follow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `follow` (
  `member_id` bigint(20) NOT NULL,
  `target_member_id` bigint(20) NOT NULL,
  `created_at` datetime NOT NULL,
  PRIMARY KEY (`member_id`,`target_member_id`),
  KEY `FKmo9e1wb2d6g06ftnabotig3kl` (`target_member_id`),
  CONSTRAINT `FKl1xe4y0yo3rojwc3dw2w85idc` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`),
  CONSTRAINT `FKmo9e1wb2d6g06ftnabotig3kl` FOREIGN KEY (`target_member_id`) REFERENCES `member` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `follow`
--

LOCK TABLES `follow` WRITE;
/*!40000 ALTER TABLE `follow` DISABLE KEYS */;
/*!40000 ALTER TABLE `follow` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `like`
--

DROP TABLE IF EXISTS `like`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `like` (
  `member_id` bigint(20) NOT NULL,
  `note_id` bigint(20) NOT NULL,
  `liked_at` datetime NOT NULL,
  PRIMARY KEY (`member_id`,`note_id`),
  KEY `FK2gws9wcuigjmsumnhnfv7u2a9` (`note_id`),
  CONSTRAINT `FK2gws9wcuigjmsumnhnfv7u2a9` FOREIGN KEY (`note_id`) REFERENCES `note` (`id`),
  CONSTRAINT `FKgxdigfme9q6iwx7n622m2bf83` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `like`
--

LOCK TABLES `like` WRITE;
/*!40000 ALTER TABLE `like` DISABLE KEYS */;
/*!40000 ALTER TABLE `like` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `member`
--

DROP TABLE IF EXISTS `member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `member` (
  `login_type` varchar(31) NOT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime NOT NULL,
  `deleted_at` datetime DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `last_logged_in_at` datetime NOT NULL,
  `nickname` varchar(10) NOT NULL,
  `nickname_updated_at` datetime NOT NULL,
  `profile_image_url` varchar(255) DEFAULT NULL,
  `role` varchar(20) NOT NULL,
  `statement` varchar(31) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_mbmcqelty0fbrvxp1q58dn57t` (`email`),
  UNIQUE KEY `UK_hh9kg6jti4n1eoiertn2k6qsc` (`nickname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `member`
--

LOCK TABLES `member` WRITE;
/*!40000 ALTER TABLE `member` DISABLE KEYS */;
/*!40000 ALTER TABLE `member` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `note`
--

DROP TABLE IF EXISTS `note`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `note` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `content` varchar(4095) NOT NULL,
  `created_at` datetime NOT NULL,
  `diary_id` bigint(20) DEFAULT 0,
  `title` varchar(31) NOT NULL,
  `updated_at` datetime NOT NULL,
  `visible_scope` varchar(20) NOT NULL,
  `member_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKtmh6ehybeynj8fkotoo2tehus` (`member_id`),
  CONSTRAINT `FKtmh6ehybeynj8fkotoo2tehus` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `note`
--

LOCK TABLES `note` WRITE;
/*!40000 ALTER TABLE `note` DISABLE KEYS */;
/*!40000 ALTER TABLE `note` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `note_image`
--

DROP TABLE IF EXISTS `note_image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `note_image` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `image_url` varchar(255) NOT NULL,
  `index` int(11) NOT NULL,
  `note_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK3lq6bp747ym1gcot82ty9lr0k` (`note_id`),
  CONSTRAINT `FK3lq6bp747ym1gcot82ty9lr0k` FOREIGN KEY (`note_id`) REFERENCES `note` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `note_image`
--

LOCK TABLES `note_image` WRITE;
/*!40000 ALTER TABLE `note_image` DISABLE KEYS */;
/*!40000 ALTER TABLE `note_image` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `note_read`
--

DROP TABLE IF EXISTS `note_read`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `note_read` (
  `member_id` bigint(20) NOT NULL,
  `note_id` bigint(20) NOT NULL,
  `counts` int(11) NOT NULL,
  `read_at` datetime NOT NULL,
  PRIMARY KEY (`member_id`,`note_id`),
  KEY `FK6349sr5wmesfk26ap9qwk37mp` (`note_id`),
  CONSTRAINT `FK6349sr5wmesfk26ap9qwk37mp` FOREIGN KEY (`note_id`) REFERENCES `note` (`id`),
  CONSTRAINT `FKoxdj2sagid7gn6afymxon699g` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `note_read`
--

LOCK TABLES `note_read` WRITE;
/*!40000 ALTER TABLE `note_read` DISABLE KEYS */;
/*!40000 ALTER TABLE `note_read` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notice`
--

DROP TABLE IF EXISTS `notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notice` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `content` varchar(255) NOT NULL,
  `create_at` datetime NOT NULL,
  `from_id` bigint(20) DEFAULT NULL,
  `member_id` bigint(20) NOT NULL,
  `notice_type` varchar(20) NOT NULL,
  `title` varchar(63) DEFAULT NULL,
  `to_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notice`
--

LOCK TABLES `notice` WRITE;
/*!40000 ALTER TABLE `notice` DISABLE KEYS */;
/*!40000 ALTER TABLE `notice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `registration`
--

DROP TABLE IF EXISTS `registration`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `registration` (
  `diary_id` bigint(20) NOT NULL,
  `member_id` bigint(20) NOT NULL,
  `registered_at` datetime NOT NULL,
  PRIMARY KEY (`diary_id`,`member_id`),
  KEY `FKhd6mebosl771jt1jmwdj0lsu3` (`member_id`),
  CONSTRAINT `FKhd6mebosl771jt1jmwdj0lsu3` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`),
  CONSTRAINT `FKtpk3gn33c0ib9799qqtvleae6` FOREIGN KEY (`diary_id`) REFERENCES `diary` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `registration`
--

LOCK TABLES `registration` WRITE;
/*!40000 ALTER TABLE `registration` DISABLE KEYS */;
/*!40000 ALTER TABLE `registration` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `report`
--

DROP TABLE IF EXISTS `report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `report` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `content` varchar(255) NOT NULL,
  `create_at` datetime NOT NULL,
  `reason` varchar(20) NOT NULL,
  `blacklist_id` bigint(20) DEFAULT NULL,
  `report_member_id` bigint(20) NOT NULL,
  `note_id` bigint(20) DEFAULT NULL,
  `reported_member_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK2j3qcmod8ayv4qundn5estjve` (`blacklist_id`),
  KEY `FKsxahx6u49hk1ylxphvpa4kgue` (`report_member_id`),
  KEY `FKn74evkw5npyysrufnmmlbotq6` (`note_id`),
  KEY `FKpv52voi5pbchl3srmwlonk261` (`reported_member_id`),
  CONSTRAINT `FK2j3qcmod8ayv4qundn5estjve` FOREIGN KEY (`blacklist_id`) REFERENCES `blacklist` (`id`),
  CONSTRAINT `FKn74evkw5npyysrufnmmlbotq6` FOREIGN KEY (`note_id`) REFERENCES `note` (`id`),
  CONSTRAINT `FKpv52voi5pbchl3srmwlonk261` FOREIGN KEY (`reported_member_id`) REFERENCES `member` (`id`),
  CONSTRAINT `FKsxahx6u49hk1ylxphvpa4kgue` FOREIGN KEY (`report_member_id`) REFERENCES `member` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `report`
--

LOCK TABLES `report` WRITE;
/*!40000 ALTER TABLE `report` DISABLE KEYS */;
/*!40000 ALTER TABLE `report` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-09-03 19:14:06

