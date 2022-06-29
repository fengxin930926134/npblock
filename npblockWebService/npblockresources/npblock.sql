/*
Navicat MySQL Data Transfer

Source Server         : MySQL
Source Server Version : 50710
Source Host           : localhost:3306
Source Database       : npblock

Target Server Type    : MYSQL
Target Server Version : 50710
File Encoding         : 65001

Date: 2020-04-08 14:12:48
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `classic_record`
-- ----------------------------
DROP TABLE IF EXISTS `classic_record`;
CREATE TABLE `classic_record` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `max_score` int(20) DEFAULT '0' COMMENT '最高游戏积分',
  `upload_count` int(20) DEFAULT '0' COMMENT '上传次数',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of classic_record
-- ----------------------------

-- ----------------------------
-- Table structure for `goods`
-- ----------------------------
DROP TABLE IF EXISTS `goods`;
CREATE TABLE `goods` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `goods_name` varchar(255) DEFAULT NULL COMMENT '商品名称',
  `goods_picture` varchar(255) DEFAULT NULL COMMENT '商品图片',
  `goods_price` int(10) DEFAULT NULL COMMENT '商品价格',
  `goods_type` varchar(255) DEFAULT NULL COMMENT '商品类型',
  `create_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of goods
-- ----------------------------

-- ----------------------------
-- Table structure for `rank_record`
-- ----------------------------
DROP TABLE IF EXISTS `rank_record`;
CREATE TABLE `rank_record` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `max_rank_score` int(20) DEFAULT '1000' COMMENT '排位最大积分',
  `rank_score` int(20) DEFAULT '1000' COMMENT '当前排位积分',
  `rise_in_rank` tinyint(1) DEFAULT '0' COMMENT '是否晋级赛，1是，0否',
  `max_repeated_defeats` int(10) DEFAULT '0' COMMENT '连败',
  `max_win_repeatedly` int(10) DEFAULT '0' COMMENT '连胜',
  `max_rank` int(10) DEFAULT '0' COMMENT '最高排名',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of rank_record
-- ----------------------------

-- ----------------------------
-- Table structure for `rush_record`
-- ----------------------------
DROP TABLE IF EXISTS `rush_record`;
CREATE TABLE `rush_record` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `max_pass` int(5) DEFAULT '0' COMMENT '最高通过关卡',
  `score` int(20) DEFAULT '0' COMMENT '成绩',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of rush_record
-- ----------------------------

-- ----------------------------
-- Table structure for `test`
-- ----------------------------
DROP TABLE IF EXISTS `test`;
CREATE TABLE `test` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of test
-- ----------------------------

-- ----------------------------
-- Table structure for `users`
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `head_sculpture` varchar(255) DEFAULT NULL COMMENT '头像',
  `sex` int(2) DEFAULT NULL COMMENT '性别',
  `phone` varchar(11) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `token` varchar(255) DEFAULT NULL,
  `open_id` varchar(255) DEFAULT NULL,
  `game_name` varchar(255) DEFAULT NULL COMMENT '游戏名',
  `token_time` bigint(20) DEFAULT NULL,
  `classic_id` int(20) DEFAULT NULL COMMENT '经典模式记录id',
  `rush_id` int(20) DEFAULT NULL COMMENT '闯关记录id',
  `rank_id` int(20) DEFAULT NULL COMMENT '排位分记录id',
  `knapsack` varchar(255) DEFAULT NULL COMMENT '背包（商品id,商品id...）',
  `wallet_block` int(20) DEFAULT '0' COMMENT '钱袋 方块币',
  `wallet_jewel` int(20) DEFAULT '0' COMMENT '钱袋 彩钻币',
  `create_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of users
-- ----------------------------

-- ----------------------------
-- Table structure for `user_buy_record`
-- ----------------------------
DROP TABLE IF EXISTS `user_buy_record`;
CREATE TABLE `user_buy_record` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `user_id` int(20) DEFAULT NULL,
  `goods_id` int(20) DEFAULT NULL,
  `already_use` tinyint(1) DEFAULT '0' COMMENT '是否已经使用',
  `create_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_buy_record
-- ----------------------------
