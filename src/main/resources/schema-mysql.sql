CREATE TABLE IF NOT EXISTS `quizzz` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(100) DEFAULT NULL,
  `description` varchar(500) DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `is_published` tinyint DEFAULT '0',
  PRIMARY KEY (`id`)
);
ALTER TABLE `quiz_1141121`.`quizzz` 
CHANGE COLUMN `title` `title` VARCHAR(200) NOT NULL ,
CHANGE COLUMN `description` `description` VARCHAR(500) NOT NULL ,
CHANGE COLUMN `start_date` `start_date` DATE NOT NULL ,
CHANGE COLUMN `end_date` `end_date` DATE NOT NULL ;

