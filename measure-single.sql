/*
 Navicat Premium Data Transfer

 Source Server         : local mysql
 Source Server Type    : MySQL
 Source Server Version : 80023
 Source Host           : 192.168.31.69:3306
 Source Schema         : measure-single

 Target Server Type    : MySQL
 Target Server Version : 80023
 File Encoding         : 65001

 Date: 05/05/2021 10:01:51
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_authority
-- ----------------------------
DROP TABLE IF EXISTS `t_authority`;
CREATE TABLE `t_authority`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `authority` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_authority
-- ----------------------------
INSERT INTO `t_authority` VALUES (1, 'ROLE_root');
INSERT INTO `t_authority` VALUES (2, 'ROLE_admin');
INSERT INTO `t_authority` VALUES (3, 'ROLE_manager');

-- ----------------------------
-- Table structure for t_company
-- ----------------------------
DROP TABLE IF EXISTS `t_company`;
CREATE TABLE `t_company`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `company_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `company_bucket` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `t_company_UN`(`company_name`) USING BTREE,
  UNIQUE INDEX `t_company_bucket_UN`(`company_bucket`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_company
-- ----------------------------
INSERT INTO `t_company` VALUES (1, 'root', 'rootSwjtu');
INSERT INTO `t_company` VALUES (3, '测绘单位00', '4s62bz42qtszbdpfvwlqfwquc');
INSERT INTO `t_company` VALUES (5, '测绘单位01', 'test01');

-- ----------------------------
-- Table structure for t_contractor
-- ----------------------------
DROP TABLE IF EXISTS `t_contractor`;
CREATE TABLE `t_contractor`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `company` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UN_id`(`id`) USING BTREE,
  UNIQUE INDEX `UN_name`(`name`, `company`) USING BTREE,
  INDEX `fk_company`(`company`) USING BTREE,
  CONSTRAINT `fk_company` FOREIGN KEY (`company`) REFERENCES `t_company` (`company_name`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_contractor
-- ----------------------------
INSERT INTO `t_contractor` VALUES (1, '承包公司01', '测绘单位00');
INSERT INTO `t_contractor` VALUES (2, '承包公司01', '测绘单位01');
INSERT INTO `t_contractor` VALUES (7, '承包公司02', '测绘单位00');

-- ----------------------------
-- Table structure for t_manager
-- ----------------------------
DROP TABLE IF EXISTS `t_manager`;
CREATE TABLE `t_manager`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `mobile` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `contractor_id` int NOT NULL,
  `project_id` int NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_contractor`(`contractor_id`) USING BTREE,
  INDEX `fk_manager`(`username`) USING BTREE,
  INDEX `fk_project`(`project_id`) USING BTREE,
  CONSTRAINT `fk_contractor` FOREIGN KEY (`contractor_id`) REFERENCES `t_contractor` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_manager` FOREIGN KEY (`username`) REFERENCES `t_user` (`username`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_project` FOREIGN KEY (`project_id`) REFERENCES `t_project` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_manager
-- ----------------------------
INSERT INTO `t_manager` VALUES (1, 'manager01', '15509096060', 1, 1);
INSERT INTO `t_manager` VALUES (2, 'manager02', NULL, 2, 3);
INSERT INTO `t_manager` VALUES (4, 'manager04', NULL, 1, 1);
INSERT INTO `t_manager` VALUES (12, 'manager05', '1', 1, 2);

-- ----------------------------
-- Table structure for t_person
-- ----------------------------
DROP TABLE IF EXISTS `t_person`;
CREATE TABLE `t_person`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `password` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `mobile` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `project_id` int NOT NULL,
  `contractor_id` int NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UN_username`(`username`) USING BTREE,
  INDEX `fk_project_id`(`project_id`) USING BTREE,
  INDEX `fk_contractor_id`(`contractor_id`) USING BTREE,
  CONSTRAINT `fk_contractor_id` FOREIGN KEY (`contractor_id`) REFERENCES `t_contractor` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_project_id` FOREIGN KEY (`project_id`) REFERENCES `t_project` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_person
-- ----------------------------
INSERT INTO `t_person` VALUES (14, 'person02', '000', '123', 1, 1);
INSERT INTO `t_person` VALUES (16, 'person03', '000', '321', 1, 1);
INSERT INTO `t_person` VALUES (18, 'person04', '000', NULL, 2, 1);

-- ----------------------------
-- Table structure for t_project
-- ----------------------------
DROP TABLE IF EXISTS `t_project`;
CREATE TABLE `t_project`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `company_id` int NOT NULL,
  `progress` float UNSIGNED ZEROFILL NULL DEFAULT 000000000000,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UN_id`(`id`) USING BTREE,
  UNIQUE INDEX `UN_name_company`(`name`, `company_id`) USING BTREE,
  INDEX `fk_project_company`(`company_id`) USING BTREE,
  CONSTRAINT `fk_project_company` FOREIGN KEY (`company_id`) REFERENCES `t_company` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_project
-- ----------------------------
INSERT INTO `t_project` VALUES (1, '工程1', 3, 000000000060);
INSERT INTO `t_project` VALUES (2, '工程2', 3, 000000000020);
INSERT INTO `t_project` VALUES (3, '工程1', 5, 000000000050);
INSERT INTO `t_project` VALUES (5, '工程2', 5, 000000000025);

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `password` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `company` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `valid` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username_UN`(`username`) USING BTREE,
  INDEX `t_user_FK`(`company`) USING BTREE,
  CONSTRAINT `t_user_FK` FOREIGN KEY (`company`) REFERENCES `t_company` (`company_name`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO `t_user` VALUES (1, 'rootSwjtu', 'rootSwjtu', 'root', 1);
INSERT INTO `t_user` VALUES (9, 'testperson1', '123456', '测绘单位00', 1);
INSERT INTO `t_user` VALUES (10, 'manager01', '123456', NULL, 1);
INSERT INTO `t_user` VALUES (11, 'manager02', '000', NULL, 1);
INSERT INTO `t_user` VALUES (13, 'manager04', '000', NULL, 1);
INSERT INTO `t_user` VALUES (26, 'manager05', '111', NULL, 1);

-- ----------------------------
-- Table structure for t_user_authority
-- ----------------------------
DROP TABLE IF EXISTS `t_user_authority`;
CREATE TABLE `t_user_authority`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NULL DEFAULT NULL,
  `authority_id` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_user_authority
-- ----------------------------
INSERT INTO `t_user_authority` VALUES (1, 1, 1);
INSERT INTO `t_user_authority` VALUES (3, 9, 2);
INSERT INTO `t_user_authority` VALUES (5, 13, 3);
INSERT INTO `t_user_authority` VALUES (6, 11, 3);
INSERT INTO `t_user_authority` VALUES (16, 24, 3);
INSERT INTO `t_user_authority` VALUES (18, 10, 3);
INSERT INTO `t_user_authority` VALUES (19, 26, 3);

SET FOREIGN_KEY_CHECKS = 1;
