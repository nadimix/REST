DROP DATABASE IF EXISTS music;
CREATE DATABASE music;
GRANT ALL PRIVILEGES ON music.* to 'music'@'localhost';


USE music;
DROP TABLE IF EXISTS user;
CREATE TABLE user (
id int(11) NOT NULL AUTO_INCREMENT,
username varchar(32) NOT NULL,
password varchar(35) NOT NULL,
email varchar(128) NOT NULL,
name varchar(64) NOT NULL,
PRIMARY KEY (id),
UNIQUE KEY username (username)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;



DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `roles` (
  `roleid` int(11) NOT NULL AUTO_INCREMENT,
  `user_role` varchar(32) NOT NULL,
  PRIMARY KEY (`roleid`),
  UNIQUE KEY `user_role` (`user_role`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (2,'admin'),(1,'registered');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;
--
-- Table structure for table `user_roles`
--

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `user_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_roles` (
  `userid` int(11) NOT NULL,
  `roleid` int(11) NOT NULL,
  `username` varchar(32) NOT NULL,
  `user_role` varchar(32) NOT NULL,
  PRIMARY KEY (`userid`,`roleid`),
  KEY `roleid` (`roleid`),
  CONSTRAINT `user_roles_ibfk_1` FOREIGN KEY (`userid`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `user_roles_ibfk_2` FOREIGN KEY (`roleid`) REFERENCES `roles` (`roleid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_roles`
--

LOCK TABLES `user_roles` WRITE;
/*!40000 ALTER TABLE `user_roles` DISABLE KEYS */;
INSERT INTO `user_roles` VALUES (1,1,'Rog5','registered'),(2,1,'Andrea95','registered'),(3,1,'Nadimm','registered'),(4,1,'Anaannaa','registered'),(5,2,'admin','admin');
/*!40000 ALTER TABLE `user_roles` ENABLE KEYS */;
UNLOCK TABLES;


DROP TABLE IF EXISTS kind;
CREATE TABLE kind (
  id int(11) NOT NULL AUTO_INCREMENT,
  name varchar(50) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY name (name)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS genre;
CREATE TABLE genre (
  id int(11) NOT NULL AUTO_INCREMENT,
  name varchar(50) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY name (name)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS artist;
CREATE TABLE artist (
  id int(11) NOT NULL AUTO_INCREMENT,
  name varchar(50) NOT NULL,
  idgenre1 int(11) NOT NULL,
  idgenre2 int(11) NULL,
  info varchar(150),
  PRIMARY KEY (id),
  UNIQUE KEY name (name)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


DROP TABLE IF EXISTS event;
CREATE TABLE event (
  id int(11) NOT NULL AUTO_INCREMENT,
  idkind int(11) NOT NULL,
  artist varchar(50) NOT NULL,
  date datetime DEFAULT NULL,
  place varchar(128) NULL,
  city varchar(50) NULL,
  country varchar(50) NOT NULL,
  info varchar(150),
  insertdate datetime NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (idkind) REFERENCES kind(id) ON UPDATE CASCADE ON DELETE CASCADE,
  FOREIGN KEY (artist) REFERENCES artist(name) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS follow;
CREATE TABLE follow (
  id int(11) NOT NULL AUTO_INCREMENT,
  iduser int(11) NOT NULL,
  idartist int(11) NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (iduser) REFERENCES user(id) ON UPDATE CASCADE ON DELETE CASCADE,
  FOREIGN KEY (idartist) REFERENCES artist(id) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS assist;
CREATE TABLE assist (
  id int(11) NOT NULL AUTO_INCREMENT,
  iduser int(11) NOT NULL,
  idevent int(11) NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (iduser) REFERENCES user(id) ON UPDATE CASCADE ON DELETE CASCADE,
  FOREIGN KEY (idevent) REFERENCES event(id) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO user VALUES (NULL,"Rog5", MD5("test"), "rog@rog.com", "Roger");
INSERT INTO user VALUES (NULL,"Andrea95", MD5("test"), "andrea@and.com", "Andrea");
INSERT INTO user VALUES (NULL,"Nadimm", MD5("test"), "nadim@nad.com", "Nadim");
INSERT INTO user VALUES (NULL,"Anaannaa", MD5("test"), "anna@anna.com", "Anna");
INSERT INTO user VALUES (NULL,"admin", MD5("admin"), "administrador@admin.com", "Administrador");

INSERT INTO artist VALUES (NULL, "Daft Punk", 4, NULL, "Más humanos que nunca");
INSERT INTO artist VALUES (NULL, "Robbie Williams", 2, 1, "Hace gritas a las chicas");
INSERT INTO artist VALUES (NULL, "David Bisbal", 2, NULL, "rizitos");
INSERT INTO artist VALUES (NULL, "The XX", 4, NULL, "Al escuchar el tema intro uno piensa en Person of Interest");
INSERT INTO artist VALUES (NULL, "Florence", 4, 1, "Grupo imprescindible");

INSERT INTO kind VALUES (NULL, "Concert");
INSERT INTO kind VALUES (NULL, "Studio Album Release");
INSERT INTO kind VALUES (NULL, "Videoclip Release");

INSERT INTO genre VALUES (NULL, "Rock");
INSERT INTO genre VALUES (NULL, "Pop");
INSERT INTO genre VALUES (NULL, "Classic");
INSERT INTO genre VALUES (NULL, "Electronic");

INSERT INTO event VALUES (NULL, 1, "Florence", "2013-09-20 22:00:00", "Palau Sant Jordi", "Barcelona", "Catalunya", "Awesome!", NOW());
INSERT INTO event VALUES (NULL, 1, "Florence", "2013-09-21 22:00:00", "Palau Joventut", "Badalona", "Catalunya", "Awesome++!", NOW());
INSERT INTO event VALUES (NULL, 1, "Daft Punk", "2013-09-21 22:45:00", "Palau Joventut", "Badalona", "Catalunya", "Awesome++!", NOW());


INSERT INTO follow VALUES (NULL, 1, 1);
INSERT INTO follow VALUES (NULL, 1, 2);
INSERT INTO follow VALUES (NULL, 2, 3);
INSERT INTO follow VALUES (NULL, 3, 4);
INSERT INTO follow VALUES (NULL, 4, 1);
INSERT INTO follow VALUES (NULL, 4, 4);


/* Another: working

INSERT INTO artist VALUES (NULL, 'Florence', 4, NULL, 'Grupo imprescindible');

SELECT * FROM artist;

SELECT * FROM artist WHERE name='Florence';

DELETE FROM artist WHERE name ='Florence';

UPDATE artist SET info='Vocalist kissed by fire' WHERE name='Florence';

INSERT INTO event VALUES (NULL, 1, 'Florence', '2013-09-20 22:00:00', 'Palau Sant Jordi', 'Barcelona', 'Catalunya', 'Va a ser inolvidable', NOW());

UPDATE event SET date='2014-09-20 22:00:00', place='Palau Joventut', city='Badalona', country='Catalunya', info='new Location' WHERE artist='Florence';

SELECT * FROM event WHERE id = 1 AND artist = 'Florence';

SELECT * FROM event WHERE artist='Florence';

SELECT * FROM event WHERE artist='Florence' AND city='Badalona';

DELETE FROM event WHERE id=2 and artist='Florence';

*/









