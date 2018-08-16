/*
SQLyog Ultimate v10.00 Beta1
MySQL - 5.5.5-10.1.28-MariaDB : Database - bibliteka
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`bibliteka` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `bibliteka`;

/*Table structure for table `autor` */

DROP TABLE IF EXISTS `autor`;

CREATE TABLE `autor` (
  `autorid` int(11) NOT NULL AUTO_INCREMENT,
  `imeautora` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `prezimeautora` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `datumrodjenja` date DEFAULT NULL,
  `zemljaporekla` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `najznacajnijedelo` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`autorid`),
  UNIQUE KEY `prezime` (`prezimeautora`,`imeautora`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `autor` */

insert  into `autor`(`autorid`,`imeautora`,`prezimeautora`,`datumrodjenja`,`zemljaporekla`,`najznacajnijedelo`) values (3,'Ivo','Andric','1932-11-17','Jugoslavija','Na Drini cuprija'),(4,'Ernest','Hemingvej','1911-05-07','Engleska','Starac i more'),(5,'Dobrica','Cosic','1934-03-09','Jugoslavija','Koreni');

/*Table structure for table `publikacija` */

DROP TABLE IF EXISTS `publikacija`;

CREATE TABLE `publikacija` (
  `idpublikacije` int(11) NOT NULL AUTO_INCREMENT,
  `nazivpublikacije` varchar(255) DEFAULT NULL,
  `tippublikacije` varchar(255) DEFAULT NULL,
  `godinaizdanja` int(11) DEFAULT NULL,
  `brojprimeraka` int(11) DEFAULT NULL,
  `autorid` int(11) DEFAULT NULL,
  PRIMARY KEY (`idpublikacije`),
  UNIQUE KEY `nazivpub` (`nazivpublikacije`),
  KEY `autorfk` (`autorid`),
  CONSTRAINT `autorfk` FOREIGN KEY (`autorid`) REFERENCES `autor` (`autorid`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=latin1;

/*Data for the table `publikacija` */

insert  into `publikacija`(`idpublikacije`,`nazivpublikacije`,`tippublikacije`,`godinaizdanja`,`brojprimeraka`,`autorid`) values (1,'Most','drama',1943,27,3),(2,'Prokleta avlija','drama',1956,44,3),(3,'Starac i more','drama',1935,100,4),(4,'Koreni','drama',1954,33,5);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
