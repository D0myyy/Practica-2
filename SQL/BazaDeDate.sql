-- ===============================
-- 1. Creare baza de date
-- ===============================
CREATE DATABASE Rezervare_bilete_avion;
GO

USE Rezervare_bilete_avion;
GO

-- ===============================
-- 2. Aeroporturi
-- ===============================
CREATE TABLE Aeroporturi (
    IdAeroport INT IDENTITY PRIMARY KEY,
    Nume VARCHAR(100),
    Oras VARCHAR(50),
    Tara VARCHAR(50),
    CodIATA CHAR(3) UNIQUE -- ex: KIV, OTP
);
GO

-- ===============================
-- 3. Avioane
-- ===============================
CREATE TABLE Avioane (
    IdAvion INT IDENTITY PRIMARY KEY,
    Model VARCHAR(50),
    Capacitate INT
);
GO

-- ===============================
-- 4. Locuri (in avion)
-- ===============================
CREATE TABLE Locuri (
    IdLoc INT IDENTITY PRIMARY KEY,
    IdAvion INT,
    NumarLoc VARCHAR(5), -- ex: 12A
    Clasa VARCHAR(20),   -- Economy / Business
    CONSTRAINT FK_Locuri_Avion FOREIGN KEY (IdAvion)
        REFERENCES Avioane(IdAvion)
);
GO

-- ===============================
-- 5. Zboruri
-- ===============================
CREATE TABLE Zboruri (
    IdZbor INT IDENTITY PRIMARY KEY,
    NumarZbor VARCHAR(10),
    IdAeroportPlecare INT,
    IdAeroportSosire INT,
    IdAvion INT,
    DataPlecare DATETIME,
    DataSosire DATETIME,
    CONSTRAINT FK_Zbor_Plecare FOREIGN KEY (IdAeroportPlecare)
        REFERENCES Aeroporturi(IdAeroport),
    CONSTRAINT FK_Zbor_Sosire FOREIGN KEY (IdAeroportSosire)
        REFERENCES Aeroporturi(IdAeroport),
    CONSTRAINT FK_Zbor_Avion FOREIGN KEY (IdAvion)
        REFERENCES Avioane(IdAvion)
);
GO

-- ===============================
-- 6. Pasageri
-- ===============================
CREATE TABLE Pasageri (
    IdPasager INT IDENTITY PRIMARY KEY,
    Nume VARCHAR(50),
    Prenume VARCHAR(50),
    Email VARCHAR(100),
    Telefon VARCHAR(20)
);
GO

-- ===============================
-- 7. Rezervari
-- ===============================
CREATE TABLE Rezervari (
    IdRezervare INT IDENTITY PRIMARY KEY,
    CodRezervare VARCHAR(20) UNIQUE,
    DataRezervare DATETIME DEFAULT GETDATE()
);
GO

-- ===============================
-- 8. Bilete
-- ===============================
CREATE TABLE Bilete (
    IdBilet INT IDENTITY PRIMARY KEY,
    IdRezervare INT,
    IdPasager INT,
    IdZbor INT,
    IdLoc INT,
    Pret DECIMAL(10,2),
    Status VARCHAR(20) DEFAULT 'Confirmat', -- Confirmat / Anulat
    CONSTRAINT FK_Bilet_Rezervare FOREIGN KEY (IdRezervare)
        REFERENCES Rezervari(IdRezervare),
    CONSTRAINT FK_Bilet_Pasager FOREIGN KEY (IdPasager)
        REFERENCES Pasageri(IdPasager),
    CONSTRAINT FK_Bilet_Zbor FOREIGN KEY (IdZbor)
        REFERENCES Zboruri(IdZbor),
    CONSTRAINT FK_Bilet_Loc FOREIGN KEY (IdLoc)
        REFERENCES Locuri(IdLoc)
);
GO

-- ===============================
-- 9. Plati
-- ===============================
CREATE TABLE Plati (
    IdPlata INT IDENTITY PRIMARY KEY,
    IdRezervare INT,
    Suma DECIMAL(10,2),
    Metoda VARCHAR(20), -- Card / Cash
    Status VARCHAR(20), -- Platit / Refuzat
    DataPlata DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_Plata_Rezervare FOREIGN KEY (IdRezervare)
        REFERENCES Rezervari(IdRezervare)
);
GO

-- ===============================
-- 10. Inserari
-- ===============================
INSERT INTO Aeroporturi (Nume, Oras, Tara, CodIATA) VALUES
    ('Aeroportul International Chisinau', 'Chisinau', 'Moldova', 'KIV'),
    ('Aeroportul International Marculesti', 'Marculesti', 'Moldova', 'MRV'),
    ('Aeroportul International Balti', 'Balti', 'Moldova', 'BZY'),
    ('Aeroportul International Iasi', 'Iasi', 'Romania', 'IAS'),
    ('Aeroportul Henri Coanda', 'Bucuresti', 'Romania', 'OTP'),
    ('Aeroportul Baneasa', 'Bucuresti', 'Romania', 'BBU'),
    ('Aeroportul Cluj Avram Iancu', 'Cluj-Napoca', 'Romania', 'CLJ'),
    ('Aeroportul Timisoara Traian Vuia', 'Timisoara', 'Romania', 'TSR'),
    ('Aeroportul Sibiu International', 'Sibiu', 'Romania', 'SBZ'),
    ('Aeroportul Bacau International', 'Bacau', 'Romania', 'BCM'),
    ('Aeroportul Suceava Stefan cel Mare', 'Suceava', 'Romania', 'SCV'),

    ('Aeroportul Istanbul', 'Istanbul', 'Turcia', 'IST'),
    ('Aeroportul Sabiha Gokcen', 'Istanbul', 'Turcia', 'SAW'),
    ('Aeroportul Ankara Esenboga', 'Ankara', 'Turcia', 'ESB'),

    ('Aeroportul Luton', 'Londra', 'UK', 'LTN'),
    ('Aeroportul Heathrow', 'Londra', 'UK', 'LHR'),
    ('Aeroportul Gatwick', 'Londra', 'UK', 'LGW'),
    ('Aeroportul Manchester', 'Manchester', 'UK', 'MAN'),

    ('Aeroportul Bergamo', 'Milano', 'Italia', 'BGY'),
    ('Aeroportul Malpensa', 'Milano', 'Italia', 'MXP'),
    ('Aeroportul Fiumicino', 'Roma', 'Italia', 'FCO'),
    ('Aeroportul Ciampino', 'Roma', 'Italia', 'CIA'),
    ('Aeroportul Napoli Capodichino', 'Napoli', 'Italia', 'NAP'),

    ('Aeroportul Barcelona El Prat', 'Barcelona', 'Spania', 'BCN'),
    ('Aeroportul Madrid Barajas', 'Madrid', 'Spania', 'MAD'),
    ('Aeroportul Valencia', 'Valencia', 'Spania', 'VLC'),
    ('Aeroportul Malaga', 'Malaga', 'Spania', 'AGP'),

    ('Aeroportul Charles de Gaulle', 'Paris', 'Franta', 'CDG'),
    ('Aeroportul Orly', 'Paris', 'Franta', 'ORY'),
    ('Aeroportul Nice Cote dAzur', 'Nice', 'Franta', 'NCE'),
    ('Aeroportul Lyon', 'Lyon', 'Franta', 'LYS'),

    ('Aeroportul Frankfurt', 'Frankfurt', 'Germania', 'FRA'),
    ('Aeroportul Munchen', 'Munchen', 'Germania', 'MUC'),
    ('Aeroportul Berlin Brandenburg', 'Berlin', 'Germania', 'BER'),
    ('Aeroportul Dusseldorf', 'Dusseldorf', 'Germania', 'DUS'),

    ('Aeroportul Amsterdam Schiphol', 'Amsterdam', 'Olanda', 'AMS'),
    ('Aeroportul Eindhoven', 'Eindhoven', 'Olanda', 'EIN'),

    ('Aeroportul Zurich', 'Zurich', 'Elvetia', 'ZRH'),
    ('Aeroportul Geneva', 'Geneva', 'Elvetia', 'GVA'),

    ('Aeroportul Vienna International', 'Viena', 'Austria', 'VIE'),
    ('Aeroportul Salzburg', 'Salzburg', 'Austria', 'SZG'),

    ('Aeroportul Athens International', 'Atena', 'Grecia', 'ATH'),
    ('Aeroportul Thessaloniki', 'Thessaloniki', 'Grecia', 'SKG'),

    ('Aeroportul Warsaw Chopin', 'Varsovia', 'Polonia', 'WAW'),
    ('Aeroportul Krakow', 'Krakow', 'Polonia', 'KRK'),

    ('Aeroportul Budapest Ferenc Liszt', 'Budapesta', 'Ungaria', 'BUD'),
    ('Aeroportul Sofia', 'Sofia', 'Bulgaria', 'SOF'),

    ('Aeroportul Dubai International', 'Dubai', 'UAE', 'DXB'),
    ('Aeroportul Doha Hamad', 'Doha', 'Qatar', 'DOH');
GO

INSERT INTO Avioane (Model, Capacitate) VALUES
    ('Airbus A320', 180),
    ('Boeing 737', 160),
    ('Airbus A321', 220),
    ('ATR 72', 70),
    ('Embraer E190', 100),
    ('Boeing 737 MAX 8', 178),
    ('Airbus A319', 150),
    ('ATR 42', 48),
    ('Airbus A220-100', 130),
    ('Boeing 787-8 Dreamliner', 250),

    ('Boeing 787-9 Dreamliner', 290),
    ('Boeing 777-300ER', 396),
    ('Boeing 777-200', 314),
    ('Boeing 767-300', 218),
    ('Boeing 757-200', 200),

    ('Airbus A330-200', 247),
    ('Airbus A330-300', 277),
    ('Airbus A340-300', 295),
    ('Airbus A350-900', 300),
    ('Airbus A350-1000', 350),

    ('Airbus A380', 500),
    ('Embraer E170', 80),
    ('Embraer E175', 88),
    ('Embraer E195', 120),
    ('Embraer E195-E2', 132),

    ('Bombardier CRJ700', 70),
    ('Bombardier CRJ900', 90),
    ('Bombardier CRJ1000', 104),

    ('Sukhoi Superjet 100', 98),
    ('COMAC C919', 158),

    ('ATR 42-600', 50),
    ('ATR 72-600', 78),

    ('McDonnell Douglas MD-80', 172),
    ('McDonnell Douglas MD-90', 153),

    ('McDonnell Douglas MD-11', 293),

    ('Airbus A310', 280),
    ('Airbus A300', 266),

    ('Boeing 717', 110),
    ('Boeing 727', 189),
    ('Boeing 737-800', 189),
    ('Boeing 737-900', 215),

    ('Airbus A318', 132),

    ('Fokker 70', 80),
    ('Fokker 100', 107),

    ('Dash 8 Q400', 82),

    ('Antonov An-148', 68),
    ('Antonov An-158', 99),

    ('Ilyushin Il-96', 262),
    ('Tupolev Tu-204', 210);
GO

-- INSERT INTO Locuri (IdAvion, NumarLoc, Clasa)
DECLARE @i INT = 1;

WHILE @i <= 10
BEGIN
    DECLARE @seat INT = 1;

    WHILE @seat <= 15
    BEGIN
        INSERT INTO Locuri (IdAvion, NumarLoc, Clasa)
        VALUES
        (@i, CONCAT(@seat, 'A'), 'Economy'),
        (@i, CONCAT(@seat, 'B'), 'Business'),
        (@i, CONCAT(@seat, 'C'), 'Economy');

        SET @seat += 1;
    END

    SET @i += 1;
END
GO

-- INSERT INTO Zboruri
DECLARE @i INT = 1;

WHILE @i <= 50
BEGIN
    INSERT INTO Zboruri (
        NumarZbor,
        IdAeroportPlecare,
        IdAeroportSosire,
        IdAvion,
        DataPlecare,
        DataSosire
    )
    VALUES (
        CONCAT('MD', 100 + @i),
        1, -- Chisinau
        (ABS(CHECKSUM(NEWID())) % 6) + 2,
        (ABS(CHECKSUM(NEWID())) % 10) + 1,
        DATEADD(DAY, @i, GETDATE()),
        DATEADD(HOUR, 2, DATEADD(DAY, @i, GETDATE()))
    );

    SET @i += 1;
END
GO

INSERT INTO Pasageri (Nume, Prenume, Email, Telefon) VALUES
	('Achirus','Constantin','achirus@mail.com','060000001'),
	('Batitchi','Nicolae','batitchi@mail.com','060000002'),
	('Besliu','Dan','besliu@mail.com','060000003'),
	('Bragari','Eduard','bragari@mail.com','060000004'),
	('Caraman','Pavel','caraman@mail.com','060000005'),
	('Ciutac','Adriana','ciutac@mail.com','060000006'),
	('Cirlan','Ion','cirlan@mail.com','060000007'),
	('Comanac','Patricia','comanac@mail.com','060000008'),
	('Corciu','Alexandru','corciu@mail.com','060000009'),
	('Cretu','Sergiu','cretu@mail.com','060000010'),
	('Gaina','Vladislav','gaina@mail.com','060000011'),
	('Gojan','Marin','gojan@mail.com','060000012'),
	('Grigorita','Salomeia','grigorita@mail.com','060000013'),
	('Istrati','Oxana','istrati@mail.com','060000014'),
	('Jumiga','Virineea','jumiga@mail.com','060000015'),
	('Macari','Nicolae','macari@mail.com','060000016'),
	('Medinschi','Vladislav','medinschi@mail.com','060000017'),
	('Miron','Dragos','miron@mail.com','060000018'),
	('Mironova','Ecaterina','mironova@mail.com','060000019'),
	('Munteanu','Alexandrina','munteanu@mail.com','060000020'),
	('Nedelco','Maxim','nedelco@mail.com','060000021'),
	('Nicolai','Petru','nicolai@mail.com','060000022'),
	('Nitrean','Remos','nitrean@mail.com','060000023'),
	('Orletchi','Bogdan','orletchi@mail.com','060000024'),
	('Pascaru','Gabriel','pascaru@mail.com','060000025'),
	('Poclitar','Artemie','poclitar@mail.com','060000026'),
	('Postoronca','Sergiu','postoronca@mail.com','060000027'),
	('Prijilevschi','Daniel','prijilevschi@mail.com','060000028'),
	('Roman','Madalina','roman@mail.com','060000029'),
	('Sava','Damian','sava@mail.com','060000030'),
	('Stepan','Gabriela','stepan@mail.com','060000031'),
	('Stici','Denis','stici@mail.com','060000032'),
	('Tilipet','Dumitru','tilipet@mail.com','060000033'),
	('Bogza','Daniel','bogza@mail.com','060000034'),
	('Botnari','Daniel','botnari@mail.com','060000035'),
	('Cazacu','Mihaela','cazacu@mail.com','060000036'),
	('Ceban','Vladislav','ceban@mail.com','060000037'),
	('Chiochiu','Andrei','chiochiu@mail.com','060000038'),
	('Ciumac','Andreea','ciumac@mail.com','060000039'),
	('Ciupac','Oleg','ciupac@mail.com','060000040'),
	('Cozma','Ion','cozma@mail.com','060000041'),
	('Cusnir','Alex','cusnir@mail.com','060000042'),
	('Enachi','Liviu','enachi@mail.com','060000043'),
	('Gavrilas','Nicolae','gavrilas@mail.com','060000044'),
	('Gorceac','Ionalea','gorceac@mail.com','060000045'),
	('Ivanciuc','Vadim','ivanciuc@mail.com','060000046'),
	('Jelihovschi','Iulian','jelihovschi@mail.com','060000047'),
	('Midrigan','Nichita','midrigan@mail.com','060000048'),
	('Moscalu','Victoria','moscalu@mail.com','060000049'),
	('Negara','Andrian','negara@mail.com','060000050');
GO

-- INSERT INTO Rezerari
DECLARE @i INT = 1;

WHILE @i <= 50
BEGIN
    INSERT INTO Rezervari (CodRezervare)
    VALUES (CONCAT('RES', 1000 + @i));

    SET @i += 1;
END
GO

-- INSERT INTO Bilete
DECLARE @i INT = 1;

WHILE @i <= 50
BEGIN
    INSERT INTO Bilete (
        IdRezervare,
        IdPasager,
        IdZbor,
        IdLoc,
        Pret
    )
    VALUES (
        @i,
        (ABS(CHECKSUM(NEWID())) % 50) + 1,
        (ABS(CHECKSUM(NEWID())) % 50) + 1,
        (ABS(CHECKSUM(NEWID())) % 150) + 1,
        (ABS(CHECKSUM(NEWID())) % 200) + 50
    );

    SET @i += 1;
END
GO

-- INSERT INTO Plati
DECLARE @i INT = 1;

WHILE @i <= 50
BEGIN
    INSERT INTO Plati (IdRezervare, Suma, Metoda, Status)
    VALUES (
        @i,
        (ABS(CHECKSUM(NEWID())) % 200) + 50,
        CASE WHEN @i % 2 = 0 THEN 'Card' ELSE 'Cash' END,
        'Platit'
    );

    SET @i += 1;
END
GO

