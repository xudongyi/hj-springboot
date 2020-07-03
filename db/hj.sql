/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50730
 Source Host           : localhost:3306
 Source Schema         : hj

 Target Server Type    : MySQL
 Target Server Version : 50730
 File Encoding         : 65001

 Date: 03/07/2020 17:02:53
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for air_current_overproof
-- ----------------------------
DROP TABLE IF EXISTS `air_current_overproof`;
CREATE TABLE `air_current_overproof`  (
  `ID` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `SOURCE_ID` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `MN` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `CODE` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `VALUE` double NULL DEFAULT NULL,
  `STANDARD_VALUE` double NULL DEFAULT NULL,
  `STATUS` int(11) NULL DEFAULT NULL,
  `DATA_TIME` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `MN`(`MN`) USING BTREE,
  INDEX `DATA_TIME`(`DATA_TIME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for air_current_tr_2007
-- ----------------------------
DROP TABLE IF EXISTS `air_current_tr_2007`;
CREATE TABLE `air_current_tr_2007`  (
  `ID` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `DATA_TIME` datetime(0) NULL DEFAULT NULL,
  `CREATE_TIME` datetime(0) NULL DEFAULT NULL,
  `MN` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `STATE` int(11) NULL DEFAULT NULL,
  `A00000_RTD` double NULL DEFAULT NULL,
  `A00000_STATE` int(11) NULL DEFAULT NULL,
  `A00000_ZSRTD` double NULL DEFAULT NULL,
  `A00000_ZSSTATE` int(11) NULL DEFAULT NULL,
  `A00000_FLAG` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `MN`(`MN`) USING BTREE,
  INDEX `DATA_TIME`(`DATA_TIME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for air_current_tr_2008
-- ----------------------------
DROP TABLE IF EXISTS `air_current_tr_2008`;
CREATE TABLE `air_current_tr_2008`  (
  `ID` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `DATA_TIME` datetime(0) NULL DEFAULT NULL,
  `CREATE_TIME` datetime(0) NULL DEFAULT NULL,
  `MN` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `STATE` int(11) NULL DEFAULT NULL,
  `A00000_RTD` double NULL DEFAULT NULL,
  `A00000_STATE` int(11) NULL DEFAULT NULL,
  `A00000_ZSRTD` double NULL DEFAULT NULL,
  `A00000_ZSSTATE` int(11) NULL DEFAULT NULL,
  `A00000_FLAG` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `MN`(`MN`) USING BTREE,
  INDEX `DATA_TIME`(`DATA_TIME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for air_day
-- ----------------------------
DROP TABLE IF EXISTS `air_day`;
CREATE TABLE `air_day`  (
  `ID` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `DATA_TIME` datetime(0) NULL DEFAULT NULL,
  `CREATE_TIME` datetime(0) NULL DEFAULT NULL,
  `MN` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `STATE` int(11) NULL DEFAULT NULL,
  `A00000_MIN` double NULL DEFAULT NULL,
  `A00000_MIN_STATE` int(11) NULL DEFAULT NULL,
  `A00000_ZSMIN` double NULL DEFAULT NULL,
  `A00000_ZSMIN_STATE` int(11) NULL DEFAULT NULL,
  `A00000_MAX` double NULL DEFAULT NULL,
  `A00000_MAX_STATE` int(11) NULL DEFAULT NULL,
  `A00000_ZSMAX` double NULL DEFAULT NULL,
  `A00000_ZSMAX_STATE` int(11) NULL DEFAULT NULL,
  `A00000_AVG` double NULL DEFAULT NULL,
  `A00000_AVG_STATE` int(11) NULL DEFAULT NULL,
  `A00000_ZSAVG` double NULL DEFAULT NULL,
  `A00000_ZSAVG_STATE` int(11) NULL DEFAULT NULL,
  `A00000_COU` double NULL DEFAULT NULL,
  `A00000_COU_STATE` int(11) NULL DEFAULT NULL,
  `A00000_ZSCOU` double NULL DEFAULT NULL,
  `A00000_ZSCOU_STATE` int(11) NULL DEFAULT NULL,
  `A00000_FLAG` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `MN`(`MN`) USING BTREE,
  INDEX `DATA_TIME`(`DATA_TIME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for air_hour
-- ----------------------------
DROP TABLE IF EXISTS `air_hour`;
CREATE TABLE `air_hour`  (
  `ID` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `DATA_TIME` datetime(0) NULL DEFAULT NULL,
  `CREATE_TIME` datetime(0) NULL DEFAULT NULL,
  `MN` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `STATE` int(11) NULL DEFAULT NULL,
  `A00000_MIN` double NULL DEFAULT NULL,
  `A00000_MIN_STATE` int(11) NULL DEFAULT NULL,
  `A00000_ZSMIN` double NULL DEFAULT NULL,
  `A00000_ZSMIN_STATE` int(11) NULL DEFAULT NULL,
  `A00000_MAX` double NULL DEFAULT NULL,
  `A00000_MAX_STATE` int(11) NULL DEFAULT NULL,
  `A00000_ZSMAX` double NULL DEFAULT NULL,
  `A00000_ZSMAX_STATE` int(11) NULL DEFAULT NULL,
  `A00000_AVG` double NULL DEFAULT NULL,
  `A00000_AVG_STATE` int(11) NULL DEFAULT NULL,
  `A00000_ZSAVG` double NULL DEFAULT NULL,
  `A00000_ZSAVG_STATE` int(11) NULL DEFAULT NULL,
  `A00000_COU` double NULL DEFAULT NULL,
  `A00000_COU_STATE` int(11) NULL DEFAULT NULL,
  `A00000_ZSCOU` double NULL DEFAULT NULL,
  `A00000_ZSCOU_STATE` int(11) NULL DEFAULT NULL,
  `A00000_FLAG` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `MN`(`MN`) USING BTREE,
  INDEX `DATA_TIME`(`DATA_TIME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for air_incinerator_current
-- ----------------------------
DROP TABLE IF EXISTS `air_incinerator_current`;
CREATE TABLE `air_incinerator_current`  (
  `ID` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `DATA_TIME` datetime(0) NULL DEFAULT NULL,
  `CREATE_TIME` datetime(0) NULL DEFAULT NULL,
  `MN` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `STATE` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `MN`(`MN`) USING BTREE,
  INDEX `DATA_TIME`(`DATA_TIME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for air_minute_2007
-- ----------------------------
DROP TABLE IF EXISTS `air_minute_2007`;
CREATE TABLE `air_minute_2007`  (
  `ID` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `DATA_TIME` datetime(0) NULL DEFAULT NULL,
  `CREATE_TIME` datetime(0) NULL DEFAULT NULL,
  `MN` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `STATE` int(11) NULL DEFAULT NULL,
  `A00000_MIN` double NULL DEFAULT NULL,
  `A00000_MIN_STATE` int(11) NULL DEFAULT NULL,
  `A00000_ZSMIN` double NULL DEFAULT NULL,
  `A00000_ZSMIN_STATE` int(11) NULL DEFAULT NULL,
  `A00000_MAX` double NULL DEFAULT NULL,
  `A00000_MAX_STATE` int(11) NULL DEFAULT NULL,
  `A00000_ZSMAX` double NULL DEFAULT NULL,
  `A00000_ZSMAX_STATE` int(11) NULL DEFAULT NULL,
  `A00000_AVG` double NULL DEFAULT NULL,
  `A00000_AVG_STATE` int(11) NULL DEFAULT NULL,
  `A00000_ZSAVG` double NULL DEFAULT NULL,
  `A00000_ZSAVG_STATE` int(11) NULL DEFAULT NULL,
  `A00000_COU` double NULL DEFAULT NULL,
  `A00000_COU_STATE` int(11) NULL DEFAULT NULL,
  `A00000_ZSCOU` double NULL DEFAULT NULL,
  `A00000_ZSCOU_STATE` int(11) NULL DEFAULT NULL,
  `A00000_FLAG` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `MN`(`MN`) USING BTREE,
  INDEX `DATA_TIME`(`DATA_TIME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for air_minute_2008
-- ----------------------------
DROP TABLE IF EXISTS `air_minute_2008`;
CREATE TABLE `air_minute_2008`  (
  `ID` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `DATA_TIME` datetime(0) NULL DEFAULT NULL,
  `CREATE_TIME` datetime(0) NULL DEFAULT NULL,
  `MN` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `STATE` int(11) NULL DEFAULT NULL,
  `A00000_MIN` double NULL DEFAULT NULL,
  `A00000_MIN_STATE` int(11) NULL DEFAULT NULL,
  `A00000_ZSMIN` double NULL DEFAULT NULL,
  `A00000_ZSMIN_STATE` int(11) NULL DEFAULT NULL,
  `A00000_MAX` double NULL DEFAULT NULL,
  `A00000_MAX_STATE` int(11) NULL DEFAULT NULL,
  `A00000_ZSMAX` double NULL DEFAULT NULL,
  `A00000_ZSMAX_STATE` int(11) NULL DEFAULT NULL,
  `A00000_AVG` double NULL DEFAULT NULL,
  `A00000_AVG_STATE` int(11) NULL DEFAULT NULL,
  `A00000_ZSAVG` double NULL DEFAULT NULL,
  `A00000_ZSAVG_STATE` int(11) NULL DEFAULT NULL,
  `A00000_COU` double NULL DEFAULT NULL,
  `A00000_COU_STATE` int(11) NULL DEFAULT NULL,
  `A00000_ZSCOU` double NULL DEFAULT NULL,
  `A00000_ZSCOU_STATE` int(11) NULL DEFAULT NULL,
  `A00000_FLAG` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `MN`(`MN`) USING BTREE,
  INDEX `DATA_TIME`(`DATA_TIME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for air_month
-- ----------------------------
DROP TABLE IF EXISTS `air_month`;
CREATE TABLE `air_month`  (
  `ID` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `DATA_TIME` datetime(0) NULL DEFAULT NULL,
  `CREATE_TIME` datetime(0) NULL DEFAULT NULL,
  `STATIC_TIME` datetime(0) NULL DEFAULT NULL,
  `TIMES` int(11) NULL DEFAULT NULL,
  `MN` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `A00000_MIN` double NULL DEFAULT NULL,
  `A00000_MIN_STATE` int(11) NULL DEFAULT NULL,
  `A00000_ZSMIN` double NULL DEFAULT NULL,
  `A00000_ZSMIN_STATE` int(11) NULL DEFAULT NULL,
  `A00000_MAX` double NULL DEFAULT NULL,
  `A00000_MAX_STATE` int(11) NULL DEFAULT NULL,
  `A00000_ZSMAX` double NULL DEFAULT NULL,
  `A00000_ZSMAX_STATE` int(11) NULL DEFAULT NULL,
  `A00000_AVG` double NULL DEFAULT NULL,
  `A00000_AVG_STATE` int(11) NULL DEFAULT NULL,
  `A00000_ZSAVG` double NULL DEFAULT NULL,
  `A00000_ZSAVG_STATE` int(11) NULL DEFAULT NULL,
  `A00000_COU` double NULL DEFAULT NULL,
  `A00000_COU_STATE` int(11) NULL DEFAULT NULL,
  `A00000_ZSCOU` double NULL DEFAULT NULL,
  `A00000_ZSCOU_STATE` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `MN`(`MN`) USING BTREE,
  INDEX `DATA_TIME`(`DATA_TIME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for air_offline
-- ----------------------------
DROP TABLE IF EXISTS `air_offline`;
CREATE TABLE `air_offline`  (
  `ID` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `MN` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `START_TIME` datetime(0) NULL DEFAULT NULL,
  `END_TIME` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `MN`(`MN`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for air_year
-- ----------------------------
DROP TABLE IF EXISTS `air_year`;
CREATE TABLE `air_year`  (
  `ID` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `DATA_TIME` datetime(0) NULL DEFAULT NULL,
  `CREATE_TIME` datetime(0) NULL DEFAULT NULL,
  `STATIC_TIME` datetime(0) NULL DEFAULT NULL,
  `TIMES` int(11) NULL DEFAULT NULL,
  `MN` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `A00000_MIN` double NULL DEFAULT NULL,
  `A00000_MIN_STATE` int(11) NULL DEFAULT NULL,
  `A00000_ZSMIN` double NULL DEFAULT NULL,
  `A00000_ZSMIN_STATE` int(11) NULL DEFAULT NULL,
  `A00000_MAX` double NULL DEFAULT NULL,
  `A00000_MAX_STATE` int(11) NULL DEFAULT NULL,
  `A00000_ZSMAX` double NULL DEFAULT NULL,
  `A00000_ZSMAX_STATE` int(11) NULL DEFAULT NULL,
  `A00000_AVG` double NULL DEFAULT NULL,
  `A00000_AVG_STATE` int(11) NULL DEFAULT NULL,
  `A00000_ZSAVG` double NULL DEFAULT NULL,
  `A00000_ZSAVG_STATE` int(11) NULL DEFAULT NULL,
  `A00000_COU` double NULL DEFAULT NULL,
  `A00000_COU_STATE` int(11) NULL DEFAULT NULL,
  `A00000_ZSCOU` double NULL DEFAULT NULL,
  `A00000_ZSCOU_STATE` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `MN`(`MN`) USING BTREE,
  INDEX `DATA_TIME`(`DATA_TIME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for airq_aqi
-- ----------------------------
DROP TABLE IF EXISTS `airq_aqi`;
CREATE TABLE `airq_aqi`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `factor_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `l_value` double(10, 0) NULL DEFAULT NULL,
  `h_value` double(10, 0) NULL DEFAULT NULL,
  `li_aqi` double(10, 0) NULL DEFAULT NULL,
  `hi_aqi` double(10, 0) NULL DEFAULT NULL,
  `type` int(1) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of airq_aqi
-- ----------------------------
INSERT INTO `airq_aqi` VALUES ('1', 'A21026', 0, 50, 0, 50, 24);
INSERT INTO `airq_aqi` VALUES ('10', 'A21026', 501, 650, 101, 150, 1);
INSERT INTO `airq_aqi` VALUES ('11', 'A21026', 651, 800, 151, 200, 1);
INSERT INTO `airq_aqi` VALUES ('12', 'A21026', 801, 1600, 201, 300, 1);
INSERT INTO `airq_aqi` VALUES ('15', 'A21004', 0, 40, 0, 50, 24);
INSERT INTO `airq_aqi` VALUES ('16', 'A21004', 41, 80, 51, 100, 24);
INSERT INTO `airq_aqi` VALUES ('17', 'A21004', 81, 180, 101, 150, 24);
INSERT INTO `airq_aqi` VALUES ('18', 'A21004', 181, 280, 151, 200, 24);
INSERT INTO `airq_aqi` VALUES ('19', 'A21004', 281, 565, 201, 300, 24);
INSERT INTO `airq_aqi` VALUES ('2', 'A21026', 51, 150, 51, 100, 24);
INSERT INTO `airq_aqi` VALUES ('20', 'A21004', 566, 750, 301, 400, 24);
INSERT INTO `airq_aqi` VALUES ('21', 'A21004', 751, 940, 401, 500, 24);
INSERT INTO `airq_aqi` VALUES ('22', 'A21004', 0, 100, 0, 50, 1);
INSERT INTO `airq_aqi` VALUES ('23', 'A21004', 101, 200, 51, 100, 1);
INSERT INTO `airq_aqi` VALUES ('24', 'A21004', 201, 700, 101, 150, 1);
INSERT INTO `airq_aqi` VALUES ('25', 'A21004', 701, 1200, 151, 200, 1);
INSERT INTO `airq_aqi` VALUES ('26', 'A21004', 2010, 2340, 201, 300, 1);
INSERT INTO `airq_aqi` VALUES ('27', 'A21004', 2341, 3090, 301, 400, 1);
INSERT INTO `airq_aqi` VALUES ('28', 'A21004', 3019, 3840, 401, 500, 1);
INSERT INTO `airq_aqi` VALUES ('29', 'A34002', 0, 50, 0, 50, 24);
INSERT INTO `airq_aqi` VALUES ('3', 'A21026', 151, 475, 101, 150, 24);
INSERT INTO `airq_aqi` VALUES ('30', 'A34002', 51, 150, 51, 100, 24);
INSERT INTO `airq_aqi` VALUES ('31', 'A34002', 151, 250, 101, 150, 24);
INSERT INTO `airq_aqi` VALUES ('32', 'A34002', 251, 350, 151, 200, 24);
INSERT INTO `airq_aqi` VALUES ('33', 'A34002', 351, 420, 201, 300, 24);
INSERT INTO `airq_aqi` VALUES ('34', 'A34002', 421, 500, 301, 400, 24);
INSERT INTO `airq_aqi` VALUES ('35', 'A34002', 501, 600, 401, 500, 24);
INSERT INTO `airq_aqi` VALUES ('36', 'A34004', 0, 35, 0, 50, 24);
INSERT INTO `airq_aqi` VALUES ('37', 'A34004', 36, 75, 51, 100, 24);
INSERT INTO `airq_aqi` VALUES ('38', 'A34004', 76, 115, 101, 150, 24);
INSERT INTO `airq_aqi` VALUES ('39', 'A34004', 116, 150, 151, 200, 24);
INSERT INTO `airq_aqi` VALUES ('4', 'A21026', 476, 800, 151, 200, 24);
INSERT INTO `airq_aqi` VALUES ('40', 'A34004', 151, 250, 201, 300, 24);
INSERT INTO `airq_aqi` VALUES ('41', 'A34004', 251, 350, 301, 400, 24);
INSERT INTO `airq_aqi` VALUES ('42', 'A34004', 351, 500, 401, 500, 24);
INSERT INTO `airq_aqi` VALUES ('43', 'A21005', 0, 2, 0, 50, 24);
INSERT INTO `airq_aqi` VALUES ('44', 'A21005', 2, 4, 50, 100, 24);
INSERT INTO `airq_aqi` VALUES ('45', 'A21005', 4, 14, 100, 150, 24);
INSERT INTO `airq_aqi` VALUES ('46', 'A21005', 14, 24, 150, 200, 24);
INSERT INTO `airq_aqi` VALUES ('47', 'A21005', 24, 36, 200, 300, 24);
INSERT INTO `airq_aqi` VALUES ('48', 'A21005', 36, 48, 300, 400, 24);
INSERT INTO `airq_aqi` VALUES ('49', 'A21005', 48, 60, 400, 500, 24);
INSERT INTO `airq_aqi` VALUES ('5', 'A21026', 801, 1600, 201, 300, 24);
INSERT INTO `airq_aqi` VALUES ('50', 'A21005', 0, 5, 0, 50, 1);
INSERT INTO `airq_aqi` VALUES ('51', 'A21005', 5, 10, 50, 100, 1);
INSERT INTO `airq_aqi` VALUES ('52', 'A21005', 10, 35, 100, 150, 1);
INSERT INTO `airq_aqi` VALUES ('53', 'A21005', 35, 60, 150, 200, 1);
INSERT INTO `airq_aqi` VALUES ('54', 'A21005', 60, 90, 200, 300, 1);
INSERT INTO `airq_aqi` VALUES ('55', 'A21005', 90, 120, 300, 400, 1);
INSERT INTO `airq_aqi` VALUES ('56', 'A21005', 120, 150, 400, 500, 1);
INSERT INTO `airq_aqi` VALUES ('57', 'A05024', 0, 160, 0, 50, 1);
INSERT INTO `airq_aqi` VALUES ('58', 'A05024', 160, 200, 50, 100, 1);
INSERT INTO `airq_aqi` VALUES ('59', 'A05024', 200, 300, 100, 150, 1);
INSERT INTO `airq_aqi` VALUES ('6', 'A21026', 1601, 2100, 301, 400, 24);
INSERT INTO `airq_aqi` VALUES ('60', 'A05024', 300, 400, 150, 200, 1);
INSERT INTO `airq_aqi` VALUES ('61', 'A05024', 400, 800, 200, 300, 1);
INSERT INTO `airq_aqi` VALUES ('62', 'A05024', 800, 1000, 300, 400, 1);
INSERT INTO `airq_aqi` VALUES ('63', 'A05024', 1000, 1200, 400, 500, 1);
INSERT INTO `airq_aqi` VALUES ('64', 'A05024', 0, 100, 0, 50, 8);
INSERT INTO `airq_aqi` VALUES ('65', 'A05024', 100, 160, 50, 100, 8);
INSERT INTO `airq_aqi` VALUES ('66', 'A05024', 160, 215, 100, 150, 8);
INSERT INTO `airq_aqi` VALUES ('67', 'A05024', 215, 265, 150, 200, 8);
INSERT INTO `airq_aqi` VALUES ('68', 'A05024', 265, 800, 200, 300, 8);
INSERT INTO `airq_aqi` VALUES ('7', 'A21026', 2010, 2620, 401, 500, 24);
INSERT INTO `airq_aqi` VALUES ('8', 'A21026', 0, 150, 0, 50, 1);
INSERT INTO `airq_aqi` VALUES ('9', 'A21026', 151, 500, 51, 100, 1);

-- ----------------------------
-- Table structure for airq_day
-- ----------------------------
DROP TABLE IF EXISTS `airq_day`;
CREATE TABLE `airq_day`  (
  `ID` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `DATA_TIME` datetime(0) NULL DEFAULT NULL,
  `CREATE_TIME` datetime(0) NULL DEFAULT NULL,
  `MN` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `STATE` int(11) NULL DEFAULT NULL,
  `LEVEL` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `FIRST_CODE` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `AQI` double NULL DEFAULT NULL,
  `A21004_AVG` double NULL DEFAULT NULL,
  `A21004_IAQI` double NULL DEFAULT NULL,
  `A01007_AVG` double NULL DEFAULT NULL,
  `A01007_IAQI` double NULL DEFAULT NULL,
  `A21026_AVG` double NULL DEFAULT NULL,
  `A21026_IAQI` double NULL DEFAULT NULL,
  `A21003_AVG` double NULL DEFAULT NULL,
  `A21003_IAQI` double NULL DEFAULT NULL,
  `A21002_AVG` double NULL DEFAULT NULL,
  `A21002_IAQI` double NULL DEFAULT NULL,
  `A01002_AVG` double NULL DEFAULT NULL,
  `A01002_IAQI` double NULL DEFAULT NULL,
  `A01001_AVG` double NULL DEFAULT NULL,
  `A01001_IAQI` double NULL DEFAULT NULL,
  `A21005_AVG` double NULL DEFAULT NULL,
  `A21005_IAQI` double NULL DEFAULT NULL,
  `A0502401_AVG` double NULL DEFAULT NULL,
  `A0502401_IAQI` double NULL DEFAULT NULL,
  `A3400201_AVG` double NULL DEFAULT NULL,
  `A3400201_IAQI` double NULL DEFAULT NULL,
  `A0502408_AVG` double NULL DEFAULT NULL,
  `A0502408_IAQI` double NULL DEFAULT NULL,
  `A3400424_AVG` double NULL DEFAULT NULL,
  `A3400424_IAQI` double NULL DEFAULT NULL,
  `A3400224_AVG` double NULL DEFAULT NULL,
  `A3400224_IAQI` double NULL DEFAULT NULL,
  `A3400401_AVG` double NULL DEFAULT NULL,
  `A3400401_IAQI` double NULL DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `MN`(`MN`) USING BTREE,
  INDEX `DATA_TIME`(`DATA_TIME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for airq_hour
-- ----------------------------
DROP TABLE IF EXISTS `airq_hour`;
CREATE TABLE `airq_hour`  (
  `ID` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `DATA_TIME` datetime(0) NULL DEFAULT NULL,
  `CREATE_TIME` datetime(0) NULL DEFAULT NULL,
  `MN` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `STATE` int(11) NULL DEFAULT NULL,
  `LEVEL` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `FIRST_CODE` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `AQI` double NULL DEFAULT NULL,
  `A21004_AVG` double NULL DEFAULT NULL,
  `A21004_IAQI` double NULL DEFAULT NULL,
  `A01007_AVG` double NULL DEFAULT NULL,
  `A01007_IAQI` double NULL DEFAULT NULL,
  `A21026_AVG` double NULL DEFAULT NULL,
  `A21026_IAQI` double NULL DEFAULT NULL,
  `A21003_AVG` double NULL DEFAULT NULL,
  `A21003_IAQI` double NULL DEFAULT NULL,
  `A21002_AVG` double NULL DEFAULT NULL,
  `A21002_IAQI` double NULL DEFAULT NULL,
  `A01002_AVG` double NULL DEFAULT NULL,
  `A01002_IAQI` double NULL DEFAULT NULL,
  `A01001_AVG` double NULL DEFAULT NULL,
  `A01001_IAQI` double NULL DEFAULT NULL,
  `A21005_AVG` double NULL DEFAULT NULL,
  `A21005_IAQI` double NULL DEFAULT NULL,
  `A0502401_AVG` double NULL DEFAULT NULL,
  `A0502401_IAQI` double NULL DEFAULT NULL,
  `A3400201_AVG` double NULL DEFAULT NULL,
  `A3400201_IAQI` double NULL DEFAULT NULL,
  `A0502408_AVG` double NULL DEFAULT NULL,
  `A0502408_IAQI` double NULL DEFAULT NULL,
  `A3400424_AVG` double NULL DEFAULT NULL,
  `A3400424_IAQI` double NULL DEFAULT NULL,
  `A3400224_AVG` double NULL DEFAULT NULL,
  `A3400224_IAQI` double NULL DEFAULT NULL,
  `A3400401_AVG` double NULL DEFAULT NULL,
  `A3400401_IAQI` double NULL DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `MN`(`MN`) USING BTREE,
  INDEX `DATA_TIME`(`DATA_TIME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of airq_hour
-- ----------------------------
INSERT INTO `airq_hour` VALUES ('451d70257e1140ac9d29dec721e058ed', '2020-07-03 09:00:00', '2020-07-03 14:22:20', 'AQ130133000016', 0, '2', 'A3400424', 96, 155.6, 78, 6, NULL, 279.77, 69, 43.71, NULL, 89.78, NULL, 86.8, NULL, 16, NULL, 8.14, 81, 40.97, 13, 53.18, NULL, 0, 0, 72, 96, 79.33, 65, 72.04, NULL);

-- ----------------------------
-- Table structure for airq_level
-- ----------------------------
DROP TABLE IF EXISTS `airq_level`;
CREATE TABLE `airq_level`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `level` int(1) NULL DEFAULT NULL COMMENT '级别',
  `level_grade` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '评分 优，良，轻度污染，中度污染，重度污染，严重污染，爆表',
  `aqi_l` double(10, 0) NULL DEFAULT NULL COMMENT '空气质量AQI低值',
  `aqi_h` double(10, 0) NULL DEFAULT NULL COMMENT '空气质量AQI高值',
  `level_content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '对健康影响情况',
  `level_rgb` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '颜色rgb值',
  `advice` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '建议采取的措施',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of airq_level
-- ----------------------------
INSERT INTO `airq_level` VALUES ('1', 1, '优', 0, 50, '空气质量令人满意', NULL, '各类人群可');
INSERT INTO `airq_level` VALUES ('2', 2, '良', 51, 100, '可接受', NULL, '各类人群可');
INSERT INTO `airq_level` VALUES ('3', 3, '轻度污染', 101, 150, '可接受', NULL, '各类人群可');
INSERT INTO `airq_level` VALUES ('4', 4, '中度污染', 151, 200, '可接受', NULL, '各类人群可');
INSERT INTO `airq_level` VALUES ('5', 5, '重度污染', 201, 300, '可接受', NULL, '各类人群可');
INSERT INTO `airq_level` VALUES ('6', 6, '严重污染', 301, 600, '可接受', NULL, '各类人群可');
INSERT INTO `airq_level` VALUES ('7', 7, '爆表', 601, 2000, '可接受', NULL, '各类人群可');

-- ----------------------------
-- Table structure for airq_month
-- ----------------------------
DROP TABLE IF EXISTS `airq_month`;
CREATE TABLE `airq_month`  (
  `ID` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `MONTH` char(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `CREATE_TIME` datetime(0) NULL DEFAULT NULL,
  `MN` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `FINE_DAYS` int(11) NULL DEFAULT NULL,
  `TOTAL_I` double NULL DEFAULT NULL,
  `A21026_AVG` double NULL DEFAULT NULL,
  `A21004_AVG` double NULL DEFAULT NULL,
  `A34002_AVG` double NULL DEFAULT NULL,
  `A34004_AVG` double NULL DEFAULT NULL,
  `A21005_95` double NULL DEFAULT NULL,
  `A05024_90` double NULL DEFAULT NULL,
  `A21026_S` double NULL DEFAULT NULL,
  `A21004_S` double NULL DEFAULT NULL,
  `A34002_S` double NULL DEFAULT NULL,
  `A34004_S` double NULL DEFAULT NULL,
  `A21005_S` double NULL DEFAULT NULL,
  `A05024_S` double NULL DEFAULT NULL,
  `A21026_I` double NULL DEFAULT NULL,
  `A21004_I` double NULL DEFAULT NULL,
  `A34002_I` double NULL DEFAULT NULL,
  `A34004_I` double NULL DEFAULT NULL,
  `A21005_I` double NULL DEFAULT NULL,
  `A05024_I` double NULL DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `MN`(`MN`) USING BTREE,
  INDEX `MONTH`(`MONTH`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for com_schedule
-- ----------------------------
DROP TABLE IF EXISTS `com_schedule`;
CREATE TABLE `com_schedule`  (
  `ID` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `DATA_TIME` datetime(0) NULL DEFAULT NULL,
  `CREATE_TIME` datetime(0) NULL DEFAULT NULL,
  `STATIC_TIME` datetime(0) NULL DEFAULT NULL,
  `COMPANY_ID` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `MN` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `W00000_DAY_BEGIN` double NULL DEFAULT NULL,
  `W00000_MONTH_BEGIN` double NULL DEFAULT NULL,
  `W00000_YEAR_BEGIN` double NULL DEFAULT NULL,
  `W00000_DAY_COU` double NULL DEFAULT NULL,
  `W00000_MONTH_COU` double NULL DEFAULT NULL,
  `W00000_YEAR_COU` double NULL DEFAULT NULL,
  `W00000_DAY_PROCESS` double NULL DEFAULT NULL,
  `W00000_MONTH_PROCESS` double NULL DEFAULT NULL,
  `W00000_YEAR_PROCESS` double NULL DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `STATIC_TIME`(`STATIC_TIME`) USING BTREE,
  INDEX `MN`(`MN`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for com_schedule_air
-- ----------------------------
DROP TABLE IF EXISTS `com_schedule_air`;
CREATE TABLE `com_schedule_air`  (
  `ID` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `DATA_TIME` datetime(0) NULL DEFAULT NULL,
  `CREATE_TIME` datetime(0) NULL DEFAULT NULL,
  `STATIC_TIME` datetime(0) NULL DEFAULT NULL,
  `COMPANY_ID` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `MN` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `A00000_DAY_COU` double NULL DEFAULT NULL,
  `A00000_MONTH_COU` double NULL DEFAULT NULL,
  `A00000_YEAR_COU` double NULL DEFAULT NULL,
  `A00000_DAY_PROCESS` double NULL DEFAULT NULL,
  `A00000_MONTH_PROCESS` double NULL DEFAULT NULL,
  `A00000_YEAR_PROCESS` double NULL DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `STATIC_TIME`(`STATIC_TIME`) USING BTREE,
  INDEX `MN`(`MN`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for com_schedule_voc
-- ----------------------------
DROP TABLE IF EXISTS `com_schedule_voc`;
CREATE TABLE `com_schedule_voc`  (
  `ID` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `DATA_TIME` datetime(0) NULL DEFAULT NULL,
  `CREATE_TIME` datetime(0) NULL DEFAULT NULL,
  `STATIC_TIME` datetime(0) NULL DEFAULT NULL,
  `COMPANY_ID` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `MN` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `STATIC_TIME`(`STATIC_TIME`) USING BTREE,
  INDEX `MN`(`MN`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for device_state
-- ----------------------------
DROP TABLE IF EXISTS `device_state`;
CREATE TABLE `device_state`  (
  `ID` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `MN` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `DATA_TIME` datetime(0) NULL DEFAULT NULL,
  `CREATE_TIME` datetime(0) NULL DEFAULT NULL,
  `END_TIME` datetime(0) NULL DEFAULT NULL,
  `SAMPLE_TIME` datetime(0) NULL DEFAULT NULL,
  `CODE` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `STATE` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `MN`(`MN`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mon_repair_apply
-- ----------------------------
DROP TABLE IF EXISTS `mon_repair_apply`;
CREATE TABLE `mon_repair_apply`  (
  `ID` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `MONITOR_ID` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `MN` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `INSTRUCTION` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `BEGIN_TIME` datetime(0) NOT NULL,
  `END_TIME` datetime(0) NOT NULL,
  `OPER_USER` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `EXAM_USER` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `EXAM_RES` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `STATUS` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for noise_current_tr_2007
-- ----------------------------
DROP TABLE IF EXISTS `noise_current_tr_2007`;
CREATE TABLE `noise_current_tr_2007`  (
  `ID` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `DATA_TIME` datetime(0) NULL DEFAULT NULL,
  `CREATE_TIME` datetime(0) NULL DEFAULT NULL,
  `MN` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `STATE` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `MN`(`MN`) USING BTREE,
  INDEX `DATA_TIME`(`DATA_TIME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for noise_current_tr_2008
-- ----------------------------
DROP TABLE IF EXISTS `noise_current_tr_2008`;
CREATE TABLE `noise_current_tr_2008`  (
  `ID` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `DATA_TIME` datetime(0) NULL DEFAULT NULL,
  `CREATE_TIME` datetime(0) NULL DEFAULT NULL,
  `MN` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `STATE` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `MN`(`MN`) USING BTREE,
  INDEX `DATA_TIME`(`DATA_TIME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for noise_day
-- ----------------------------
DROP TABLE IF EXISTS `noise_day`;
CREATE TABLE `noise_day`  (
  `ID` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `DATA_TIME` datetime(0) NULL DEFAULT NULL,
  `CREATE_TIME` datetime(0) NULL DEFAULT NULL,
  `MN` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `STATE` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `MN`(`MN`) USING BTREE,
  INDEX `DATA_TIME`(`DATA_TIME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for noise_hour
-- ----------------------------
DROP TABLE IF EXISTS `noise_hour`;
CREATE TABLE `noise_hour`  (
  `ID` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `DATA_TIME` datetime(0) NULL DEFAULT NULL,
  `CREATE_TIME` datetime(0) NULL DEFAULT NULL,
  `MN` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `STATE` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `MN`(`MN`) USING BTREE,
  INDEX `DATA_TIME`(`DATA_TIME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for noise_minute_2007
-- ----------------------------
DROP TABLE IF EXISTS `noise_minute_2007`;
CREATE TABLE `noise_minute_2007`  (
  `ID` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `DATA_TIME` datetime(0) NULL DEFAULT NULL,
  `CREATE_TIME` datetime(0) NULL DEFAULT NULL,
  `MN` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `STATE` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `MN`(`MN`) USING BTREE,
  INDEX `DATA_TIME`(`DATA_TIME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for noise_minute_2008
-- ----------------------------
DROP TABLE IF EXISTS `noise_minute_2008`;
CREATE TABLE `noise_minute_2008`  (
  `ID` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `DATA_TIME` datetime(0) NULL DEFAULT NULL,
  `CREATE_TIME` datetime(0) NULL DEFAULT NULL,
  `MN` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `STATE` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `MN`(`MN`) USING BTREE,
  INDEX `DATA_TIME`(`DATA_TIME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for overproof_period
-- ----------------------------
DROP TABLE IF EXISTS `overproof_period`;
CREATE TABLE `overproof_period`  (
  `ID` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `MONITOR_TYPE` int(11) NULL DEFAULT NULL,
  `MN` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `FACTOR_CODE` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `BEGIN_TIME` datetime(0) NULL DEFAULT NULL,
  `END_TIME` datetime(0) NULL DEFAULT NULL,
  `OVER_TIME` double NULL DEFAULT NULL,
  `MAX_VALUE` double NULL DEFAULT NULL,
  `ST_VALUE` double NULL DEFAULT NULL,
  `MULTIPLE_VALUE` double NULL DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `MN`(`MN`) USING BTREE,
  INDEX `FACTOR_CODE`(`FACTOR_CODE`) USING BTREE,
  INDEX `END_TIME`(`END_TIME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for rate_device
-- ----------------------------
DROP TABLE IF EXISTS `rate_device`;
CREATE TABLE `rate_device`  (
  `ID` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `MONITOR_ID` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `MONITOR_NAME` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `MN` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `MONITOR_TYPE` int(11) NULL DEFAULT NULL,
  `CODE` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `NAME` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `ERROR` int(11) NULL DEFAULT NULL,
  `NORMAL` int(11) NULL DEFAULT NULL,
  `PERCENT` double NULL DEFAULT NULL,
  `DATA_TIME` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `MN`(`MN`) USING BTREE,
  INDEX `MONITOR_ID`(`MONITOR_ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for rate_online
-- ----------------------------
DROP TABLE IF EXISTS `rate_online`;
CREATE TABLE `rate_online`  (
  `ID` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `MONITOR_ID` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `MONITOR_NAME` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `MN` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `MONITOR_TYPE` int(11) NULL DEFAULT NULL,
  `DATA_TIME` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `ONLINE_TIME` int(11) NULL DEFAULT NULL,
  `OFFLINE_TIME` int(11) NULL DEFAULT NULL,
  `STOP_TIME` int(11) NULL DEFAULT NULL,
  `ONLINE_RATE` double NULL DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `MN`(`MN`) USING BTREE,
  INDEX `DATA_TIME`(`DATA_TIME`) USING BTREE,
  INDEX `MONITOR_ID`(`MONITOR_ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for rate_overproof
-- ----------------------------
DROP TABLE IF EXISTS `rate_overproof`;
CREATE TABLE `rate_overproof`  (
  `ID` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `MONITOR_ID` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `MONITOR_NAME` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `MN` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `MONITOR_TYPE` int(11) NULL DEFAULT NULL,
  `CODE` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `NAME` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `COUNT` int(11) NULL DEFAULT NULL,
  `TOTAL` int(11) NULL DEFAULT NULL,
  `PERCENT` double NULL DEFAULT NULL,
  `DATA_TIME` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `MN`(`MN`) USING BTREE,
  INDEX `DATA_TIME`(`DATA_TIME`) USING BTREE,
  INDEX `MONITOR_ID`(`MONITOR_ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for rate_upload
-- ----------------------------
DROP TABLE IF EXISTS `rate_upload`;
CREATE TABLE `rate_upload`  (
  `ID` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `MONITOR_ID` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `MONITOR_NAME` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `MN` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `MONITOR_TYPE` int(11) NULL DEFAULT NULL,
  `COUNT` int(11) NULL DEFAULT NULL,
  `TOTAL` int(11) NULL DEFAULT NULL,
  `PERCENT` double NULL DEFAULT NULL,
  `DATA_TIME` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `MN`(`MN`) USING BTREE,
  INDEX `DATA_TIME`(`DATA_TIME`) USING BTREE,
  INDEX `MONITOR_ID`(`MONITOR_ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for rate_valid
-- ----------------------------
DROP TABLE IF EXISTS `rate_valid`;
CREATE TABLE `rate_valid`  (
  `ID` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `MONITOR_ID` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `MONITOR_NAME` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `MN` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `MONITOR_TYPE` int(11) NULL DEFAULT NULL,
  `COUNT` int(11) NULL DEFAULT NULL,
  `TOTAL` int(11) NULL DEFAULT NULL,
  `PERCENT` double NULL DEFAULT NULL,
  `DATA_TIME` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `MN`(`MN`) USING BTREE,
  INDEX `DATA_TIME`(`DATA_TIME`) USING BTREE,
  INDEX `MONITOR_ID`(`MONITOR_ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for site_monitor_point
-- ----------------------------
DROP TABLE IF EXISTS `site_monitor_point`;
CREATE TABLE `site_monitor_point`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `company_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '所属企业',
  `mn` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '站点名称',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '站点类型 数据字典：污染因子类型：废水，废气，VOCs，空气质量，地表水，土壤，地下水，放射源，噪声，电气',
  `point_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '站点编号',
  `point_level` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '站点级别 国控 省控 市控',
  `is_net` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否联网',
  `state` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '站点状态',
  `area` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '所属区域',
  `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `data_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据状态',
  `device_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备状态',
  `online_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '在线状态',
  `valve_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '阀门状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of site_monit