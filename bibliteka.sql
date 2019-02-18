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
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `autor` */

insert  into `autor`(`autorid`,`imeautora`,`prezimeautora`,`datumrodjenja`,`zemljaporekla`,`najznacajnijedelo`) values (3,'Ivo','Andric','1932-11-17','Jugoslavija','Na Drini cuprija'),(4,'Ernest','Hemingvej','1911-05-05','Engleska','Starac i more'),(5,'Dobrica','Cosic','1934-03-09','Jugoslavija','Koreni'),(6,'Migel','Servantes','1606-11-07','Spanija','Don Kihot'),(7,'Vilijem','Sekspir','1499-08-12','Engleska','Romeo i Julija'),(8,'Lav','Tolstoj','1833-06-18','Rusija','Rat i mir'),(9,'Dante','Aligijeri','1232-09-03','Italija','Bozanstvena komedija');

/*Table structure for table `clan` */

DROP TABLE IF EXISTS `clan`;

CREATE TABLE `clan` (
  `jmbg` varchar(20) NOT NULL,
  `ime` varchar(50) DEFAULT NULL,
  `prezime` varchar(50) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `telefon` varchar(50) DEFAULT NULL,
  `datumuclanjenja` date DEFAULT NULL,
  PRIMARY KEY (`jmbg`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `clan` */

insert  into `clan`(`jmbg`,`ime`,`prezime`,`email`,`telefon`,`datumuclanjenja`) values ('1224567890123','Mika','Mikic','mika@gmail.com','0612233222','2011-03-22'),('1224567890787','Janko','Tubic','jank@live.com','0621133589','2011-07-11'),('1234567890123','Pera','Peric','pera@gmail.com','0612235653','2009-12-20'),('1234567891111','Slavko','Savic','savicd@gmail.com','0623435763','2011-03-22'),('22244488812345','Aleksa','Rakic','rakles@gmail.com','0612249125','2015-01-03');

/*Table structure for table `evidencijaoclanu` */

DROP TABLE IF EXISTS `evidencijaoclanu`;

CREATE TABLE `evidencijaoclanu` (
  `publikacijaid` int(11) NOT NULL,
  `jmbg` varchar(20) NOT NULL,
  `datumuzimanjaknjige` date DEFAULT NULL,
  `datumvracanjaknjige` date DEFAULT NULL,
  `napomena` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`publikacijaid`,`jmbg`),
  KEY `clanfk` (`jmbg`),
  CONSTRAINT `clanfk` FOREIGN KEY (`jmbg`) REFERENCES `clan` (`jmbg`),
  CONSTRAINT `publikacijafk` FOREIGN KEY (`publikacijaid`) REFERENCES `publikacija` (`idpublikacije`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `evidencijaoclanu` */

insert  into `evidencijaoclanu`(`publikacijaid`,`jmbg`,`datumuzimanjaknjige`,`datumvracanjaknjige`,`napomena`) values (1,'22244488812345','2016-01-23',NULL,'vraca u roku od mesec dana'),(7,'1234567891111','2011-04-12',NULL,'uzeo knjigu'),(11,'22244488812345','2016-02-12',NULL,'uzeo knjigu'),(13,'1224567890123','2011-12-03',NULL,'uzeo je knjigu za skolu');

/*Table structure for table `nagradna_igra` */

DROP TABLE IF EXISTS `nagradna_igra`;

CREATE TABLE `nagradna_igra` (
  `idigre` int(11) NOT NULL AUTO_INCREMENT,
  `prezimeautora` varchar(255) DEFAULT NULL,
  `godinaizdanja` int(11) DEFAULT NULL,
  `najpoznatijedelo` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`idigre`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;

/*Data for the table `nagradna_igra` */

insert  into `nagradna_igra`(`idigre`,`prezimeautora`,`godinaizdanja`,`najpoznatijedelo`) values (1,'Andric',1956,'Na drini cuprija'),(2,'Servantes',1645,'Don Kihot'),(3,'Sekspir',1531,'Romeo i Julija'),(4,'Tolstoj',1879,'Rat i mir'),(5,'Aligijeri',1276,'Bozanstvena komedija'),(6,'Cosic',1954,'Koreni'),(7,'Hemingvej',1935,'Starac i more');

/*Table structure for table `publikacija` */

DROP TABLE IF EXISTS `publikacija`;

CREATE TABLE `publikacija` (
  `idpublikacije` int(11) NOT NULL AUTO_INCREMENT,
  `nazivpublikacije` varchar(255) DEFAULT NULL,
  `zanrid` int(11) DEFAULT NULL,
  `godinaizdanja` int(11) DEFAULT NULL,
  `status` varchar(255) DEFAULT 'dostupna',
  `autorid` int(11) DEFAULT NULL,
  PRIMARY KEY (`idpublikacije`),
  UNIQUE KEY `nazivpub` (`nazivpublikacije`),
  KEY `autorfk` (`autorid`),
  KEY `zanrfk` (`zanrid`),
  CONSTRAINT `autorfk` FOREIGN KEY (`autorid`) REFERENCES `autor` (`autorid`),
  CONSTRAINT `zanrfk` FOREIGN KEY (`zanrid`) REFERENCES `zanr` (`zanrid`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=latin1;

/*Data for the table `publikacija` */

insert  into `publikacija`(`idpublikacije`,`nazivpublikacije`,`zanrid`,`godinaizdanja`,`status`,`autorid`) values (1,'Na Drini cuprija',1,1959,'nije dostupna',3),(4,'Travnicka hronika',3,1956,'dostupna',3),(6,'Koreni',3,1954,'dostupna',5),(7,'Rat i mir',3,1879,'nije dostupna',8),(8,'Don Kihot',3,1645,'dostupna',6),(11,'Romeo i Julija',1,1531,'nije dostupna',7),(12,'Bozanstvena komedija',1,1276,'dostupna',9),(13,'Starac i more',3,1935,'nije dostupna',4);

/*Table structure for table `zanr` */

DROP TABLE IF EXISTS `zanr`;

CREATE TABLE `zanr` (
  `zanrid` int(11) NOT NULL AUTO_INCREMENT,
  `nazivzanra` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`zanrid`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;

/*Data for the table `zanr` */

insert  into `zanr`(`zanrid`,`nazivzanra`) values (1,'drama'),(2,'istorijski roman'),(3,'avantura'),(4,'pripovetka');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
