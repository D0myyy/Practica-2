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

-- =====================================================================
-- INSERARI SUPLIMENTARE
-- Baza de date: Rezervare_bilete_avion
-- =====================================================================

USE Rezervare_bilete_avion;
GO

-- =====================================================================
-- 1. AEROPORTURI (30 noi, ID-uri: 50-79)
-- CodIATA CHAR(3) UNIQUE -- ex: KIV, OTP
-- =====================================================================
INSERT INTO Aeroporturi (Nume, Oras, Tara, CodIATA) VALUES
('Aeroportul Brussels International',      'Bruxelles',        'Belgia',      'BRU'),
('Aeroportul Charleroi',                   'Charleroi',        'Belgia',      'CRL'),
('Aeroportul Lisabona Humberto Delgado',   'Lisabona',         'Portugalia',  'LIS'),
('Aeroportul Porto Francisco Sa Carneiro', 'Porto',            'Portugalia',  'OPO'),
('Aeroportul Faro International',          'Faro',             'Portugalia',  'FAO'),
('Aeroportul Praga Vaclav Havel',          'Praga',            'Cehia',       'PRG'),
('Aeroportul Stockholm Arlanda',           'Stockholm',        'Suedia',      'ARN'),
('Aeroportul Goteborg Landvetter',         'Goteborg',         'Suedia',      'GOT'),
('Aeroportul Helsinki Vantaa',             'Helsinki',         'Finlanda',    'HEL'),
('Aeroportul Copenhaga Kastrup',           'Copenhaga',        'Danemarca',   'CPH'),
('Aeroportul Oslo Gardermoen',             'Oslo',             'Norvegia',    'OSL'),
('Aeroportul Riga International',          'Riga',             'Letonia',     'RIX'),
('Aeroportul Tallinn Lennart Meri',        'Tallinn',          'Estonia',     'TLL'),
('Aeroportul Vilnius International',       'Vilnius',          'Lituania',    'VNO'),
('Aeroportul Kyiv Boryspil',              'Kyiv',             'Ucraina',     'KBP'),
('Aeroportul Moscova Sheremetyevo',        'Moscova',          'Rusia',       'SVO'),
('Aeroportul Sankt Petersburg Pulkovo',    'Sankt Petersburg', 'Rusia',       'LED'),
('Aeroportul Tel Aviv Ben Gurion',         'Tel Aviv',         'Israel',      'TLV'),
('Aeroportul Cairo International',         'Cairo',            'Egipt',       'CAI'),
('Aeroportul Tunis Carthage',              'Tunis',            'Tunisia',     'TUN'),
('Aeroportul Casablanca Mohammed V',       'Casablanca',       'Maroc',       'CMN'),
('Aeroportul New York JFK',                'New York',         'SUA',         'JFK'),
('Aeroportul Los Angeles International',   'Los Angeles',      'SUA',         'LAX'),
('Aeroportul Toronto Pearson',             'Toronto',          'Canada',      'YYZ'),
('Aeroportul Montreal Trudeau',            'Montreal',         'Canada',      'YUL'),
('Aeroportul Singapore Changi',            'Singapore',        'Singapore',   'SIN'),
('Aeroportul Bangkok Suvarnabhumi',        'Bangkok',          'Tailanda',    'BKK'),
('Aeroportul Tokyo Narita',                'Tokyo',            'Japonia',     'NRT'),
('Aeroportul Dublin',                      'Dublin',           'Irlanda',     'DUB'),
('Aeroportul Edinburgh',                   'Edinburgh',        'UK',          'EDI');
GO

-- =====================================================================
-- 2. AVIOANE (20 noi, ID-uri: 50-69)
-- =====================================================================
INSERT INTO Avioane (Model, Capacitate) VALUES
('Boeing 737-700',           140),
('Airbus A321neo',           240),
('Airbus A320neo',           194),
('Boeing 737 MAX 9',         193),
('Boeing 787-10 Dreamliner', 330),
('Airbus A380-800',          555),
('Boeing 777X',              426),
('Embraer E190-E2',          114),
('ATR 72-500',                74),
('Bombardier Q300',           56),
('Boeing 737-600',           110),
('Airbus A321XLR',           244),
('Boeing 767-400',           245),
('Boeing 777-300',           368),
('Airbus A330-900neo',       287),
('Airbus A350-900ULR',       301),
('Embraer E175-E2',           80),
('Bombardier CRJ200',         50),
('COMAC ARJ21',               90),
('Cessna 208 Caravan',        14);
GO

-- =====================================================================
-- 3. LOCURI (pentru avioane 11-30)
-- Clasa VARCHAR(20) -- Economy / Business
-- 20 avioane x 20 randuri x 5 locuri = 2000 locuri (ID-uri: 451-2450)
-- =====================================================================
DECLARE @av INT = 11;
DECLARE @s  INT;

WHILE @av <= 30
BEGIN
    SET @s = 1;
    WHILE @s <= 20
    BEGIN
        INSERT INTO Locuri (IdAvion, NumarLoc, Clasa) VALUES
            (@av, CONCAT(@s, 'A'), 'Economy'),
            (@av, CONCAT(@s, 'B'), 'Economy'),
            (@av, CONCAT(@s, 'C'), 'Business'),
            (@av, CONCAT(@s, 'D'), 'Economy'),
            (@av, CONCAT(@s, 'E'), 'Business');
        SET @s += 1;
    END
    SET @av += 1;
END
GO

-- =====================================================================
-- 4. ZBORURI (100 noi, ID-uri: 51-150)
-- Rute variate intre aeroporturi nationale si internationale
--
-- Referinta rapida ID aeroporturi adaugate:
--   50=BRU  51=CRL  52=LIS  53=OPO  54=FAO  55=PRG
--   56=ARN  57=GOT  58=HEL  59=CPH  60=OSL  61=RIX
--   62=TLL  63=VNO  64=KBP  65=SVO  66=LED  67=TLV
--   68=CAI  69=TUN  70=CMN  71=JFK  72=LAX  73=YYZ
--   74=YUL  75=SIN  76=BKK  77=NRT  78=DUB  79=EDI
--
-- Aeroporturi originale relevante:
--   1=KIV  5=OTP  12=IST  16=LHR  19=BGY  24=BCN
--   28=CDG  32=FRA  36=AMS  40=VIE  42=ATH  46=BUD  47=SOF
-- =====================================================================

-- ---- De la Chisinau (KIV, ID=1) ----
INSERT INTO Zboruri (NumarZbor, IdAeroportPlecare, IdAeroportSosire, IdAvion, DataPlecare, DataSosire) VALUES
('MD201',  1,  5,  1, '2025-07-01 06:00', '2025-07-01 07:30'),  -- KIV -> OTP
('MD202',  1, 12,  2, '2025-07-01 09:00', '2025-07-01 12:00'),  -- KIV -> IST
('MD203',  1, 28,  3, '2025-07-02 07:30', '2025-07-02 10:30'),  -- KIV -> CDG
('MD204',  1, 32,  4, '2025-07-02 11:00', '2025-07-02 13:30'),  -- KIV -> FRA
('MD205',  1, 36,  5, '2025-07-03 06:00', '2025-07-03 09:00'),  -- KIV -> AMS
('MD206',  1, 16,  6, '2025-07-03 10:00', '2025-07-03 13:00'),  -- KIV -> LHR
('MD207',  1, 40,  7, '2025-07-04 07:00', '2025-07-04 09:00'),  -- KIV -> VIE
('MD208',  1, 47,  8, '2025-07-04 12:00', '2025-07-04 14:30'),  -- KIV -> SOF
('MD209',  1, 19,  9, '2025-07-05 06:30', '2025-07-05 09:30'),  -- KIV -> BGY
('MD210',  1, 50, 10, '2025-07-05 08:00', '2025-07-05 10:30');  -- KIV -> BRU
GO

-- ---- De la Bucuresti (OTP, ID=5) ----
INSERT INTO Zboruri (NumarZbor, IdAeroportPlecare, IdAeroportSosire, IdAvion, DataPlecare, DataSosire) VALUES
('RO301',  5,  1,  1, '2025-07-06 06:00', '2025-07-06 07:30'),  -- OTP -> KIV
('RO302',  5, 12,  2, '2025-07-06 10:00', '2025-07-06 13:00'),  -- OTP -> IST
('RO303',  5, 32,  3, '2025-07-07 08:00', '2025-07-07 11:00'),  -- OTP -> FRA
('RO304',  5, 28,  4, '2025-07-07 12:00', '2025-07-07 15:00'),  -- OTP -> CDG
('RO305',  5, 16,  5, '2025-07-08 07:00', '2025-07-08 10:00'),  -- OTP -> LHR
('RO306',  5, 40,  6, '2025-07-08 11:00', '2025-07-08 13:00'),  -- OTP -> VIE
('RO307',  5, 36,  7, '2025-07-09 08:00', '2025-07-09 11:00'),  -- OTP -> AMS
('RO308',  5, 24,  8, '2025-07-09 12:00', '2025-07-09 14:30'),  -- OTP -> BCN
('RO309',  5, 50,  9, '2025-07-10 06:00', '2025-07-10 08:30'),  -- OTP -> BRU
('RO310',  5, 55, 10, '2025-07-10 11:00', '2025-07-10 13:00');  -- OTP -> PRG
GO

-- ---- De la Istanbul (IST, ID=12) ----
INSERT INTO Zboruri (NumarZbor, IdAeroportPlecare, IdAeroportSosire, IdAvion, DataPlecare, DataSosire) VALUES
('TK401', 12,  1, 11, '2025-07-11 07:00', '2025-07-11 10:00'),  -- IST -> KIV
('TK402', 12,  5, 12, '2025-07-11 09:00', '2025-07-11 11:30'),  -- IST -> OTP
('TK403', 12, 32, 13, '2025-07-12 06:00', '2025-07-12 09:00'),  -- IST -> FRA
('TK404', 12, 28, 14, '2025-07-12 10:00', '2025-07-12 13:00'),  -- IST -> CDG
('TK405', 12, 16, 15, '2025-07-13 08:00', '2025-07-13 11:00'),  -- IST -> LHR
('TK406', 12, 67, 16, '2025-07-13 12:00', '2025-07-13 14:30'),  -- IST -> TLV
('TK407', 12, 68, 17, '2025-07-14 07:00', '2025-07-14 10:00'),  -- IST -> CAI
('TK408', 12, 36, 18, '2025-07-14 11:00', '2025-07-14 14:00'),  -- IST -> AMS
('TK409', 12, 40, 19, '2025-07-15 09:00', '2025-07-15 11:30'),  -- IST -> VIE
('TK410', 12, 42, 20, '2025-07-15 13:00', '2025-07-15 14:30');  -- IST -> ATH
GO

-- ---- De la Frankfurt (FRA, ID=32) ----
INSERT INTO Zboruri (NumarZbor, IdAeroportPlecare, IdAeroportSosire, IdAvion, DataPlecare, DataSosire) VALUES
('LH501', 32,  1,  1, '2025-07-16 06:00', '2025-07-16 08:30'),  -- FRA -> KIV
('LH502', 32,  5,  2, '2025-07-16 10:00', '2025-07-16 12:00'),  -- FRA -> OTP
('LH503', 32, 12,  3, '2025-07-17 08:00', '2025-07-17 11:00'),  -- FRA -> IST
('LH504', 32, 28,  4, '2025-07-17 12:00', '2025-07-17 14:00'),  -- FRA -> CDG
('LH505', 32, 36,  5, '2025-07-18 07:00', '2025-07-18 08:30'),  -- FRA -> AMS
('LH506', 32, 40,  6, '2025-07-18 11:00', '2025-07-18 12:30'),  -- FRA -> VIE
('LH507', 32, 67,  7, '2025-07-19 09:00', '2025-07-19 14:00'),  -- FRA -> TLV
('LH508', 32, 71,  8, '2025-07-19 13:00', '2025-07-20 03:00'),  -- FRA -> JFK (transatlantic)
('LH509', 32, 50,  9, '2025-07-20 06:00', '2025-07-20 08:00'),  -- FRA -> BRU
('LH510', 32, 55, 10, '2025-07-20 10:00', '2025-07-20 12:00');  -- FRA -> PRG
GO

-- ---- De la Paris CDG (ID=28) ----
INSERT INTO Zboruri (NumarZbor, IdAeroportPlecare, IdAeroportSosire, IdAvion, DataPlecare, DataSosire) VALUES
('AF601', 28,  1, 11, '2025-07-21 07:00', '2025-07-21 10:00'),  -- CDG -> KIV
('AF602', 28,  5, 12, '2025-07-21 09:00', '2025-07-21 12:00'),  -- CDG -> OTP
('AF603', 28, 12, 13, '2025-07-22 08:00', '2025-07-22 11:00'),  -- CDG -> IST
('AF604', 28, 32, 14, '2025-07-22 12:00', '2025-07-22 14:00'),  -- CDG -> FRA
('AF605', 28, 36, 15, '2025-07-23 06:00', '2025-07-23 08:00'),  -- CDG -> AMS
('AF606', 28, 16, 16, '2025-07-23 10:00', '2025-07-23 11:30'),  -- CDG -> LHR
('AF607', 28, 40, 17, '2025-07-24 08:00', '2025-07-24 10:00'),  -- CDG -> VIE
('AF608', 28, 24, 18, '2025-07-24 12:00', '2025-07-24 14:30'),  -- CDG -> BCN
('AF609', 28, 50, 19, '2025-07-25 07:00', '2025-07-25 09:00'),  -- CDG -> BRU
('AF610', 28, 55, 20, '2025-07-25 11:00', '2025-07-25 13:00');  -- CDG -> PRG
GO

-- ---- De la Amsterdam (AMS, ID=36) ----
INSERT INTO Zboruri (NumarZbor, IdAeroportPlecare, IdAeroportSosire, IdAvion, DataPlecare, DataSosire) VALUES
('KL701', 36,  1,  1, '2025-07-26 06:00', '2025-07-26 09:00'),  -- AMS -> KIV
('KL702', 36,  5,  2, '2025-07-26 10:00', '2025-07-26 13:00'),  -- AMS -> OTP
('KL703', 36, 12,  3, '2025-07-27 08:00', '2025-07-27 12:00'),  -- AMS -> IST
('KL704', 36, 32,  4, '2025-07-27 12:00', '2025-07-27 14:00'),  -- AMS -> FRA
('KL705', 36, 28,  5, '2025-07-28 07:00', '2025-07-28 09:00'),  -- AMS -> CDG
('KL706', 36, 16,  6, '2025-07-28 11:00', '2025-07-28 12:30'),  -- AMS -> LHR
('KL707', 36, 67,  7, '2025-07-29 08:00', '2025-07-29 13:00'),  -- AMS -> TLV
('KL708', 36, 50,  8, '2025-07-29 12:00', '2025-07-29 14:00'),  -- AMS -> BRU
('KL709', 36, 55,  9, '2025-07-30 06:00', '2025-07-30 08:00'),  -- AMS -> PRG
('KL710', 36, 47, 10, '2025-07-30 10:00', '2025-07-30 11:30');  -- AMS -> SOF
GO

-- ---- De la Londra Heathrow (LHR, ID=16) ----
INSERT INTO Zboruri (NumarZbor, IdAeroportPlecare, IdAeroportSosire, IdAvion, DataPlecare, DataSosire) VALUES
('BA801', 16,  1, 11, '2025-07-31 07:00', '2025-07-31 11:00'),  -- LHR -> KIV
('BA802', 16,  5, 12, '2025-07-31 09:00', '2025-07-31 12:30'),  -- LHR -> OTP
('BA803', 16, 12, 13, '2025-08-01 08:00', '2025-08-01 12:00'),  -- LHR -> IST
('BA804', 16, 32, 14, '2025-08-01 12:00', '2025-08-01 14:30'),  -- LHR -> FRA
('BA805', 16, 28, 15, '2025-08-02 07:00', '2025-08-02 09:00'),  -- LHR -> CDG
('BA806', 16, 36, 16, '2025-08-02 10:00', '2025-08-02 12:00'),  -- LHR -> AMS
('BA807', 16, 40, 17, '2025-08-03 06:00', '2025-08-03 08:30'),  -- LHR -> VIE
('BA808', 16, 67, 18, '2025-08-03 11:00', '2025-08-03 16:00'),  -- LHR -> TLV
('BA809', 16, 71, 19, '2025-08-04 09:00', '2025-08-04 19:00'),  -- LHR -> JFK
('BA810', 16, 55, 20, '2025-08-04 14:00', '2025-08-04 16:00');  -- LHR -> PRG
GO

-- ---- De la Viena (VIE, ID=40) ----
INSERT INTO Zboruri (NumarZbor, IdAeroportPlecare, IdAeroportSosire, IdAvion, DataPlecare, DataSosire) VALUES
('OS901', 40,  1,  1, '2025-08-05 06:00', '2025-08-05 08:00'),  -- VIE -> KIV
('OS902', 40,  5,  2, '2025-08-05 09:00', '2025-08-05 11:00'),  -- VIE -> OTP
('OS903', 40, 12,  3, '2025-08-06 07:00', '2025-08-06 10:00'),  -- VIE -> IST
('OS904', 40, 32,  4, '2025-08-06 11:00', '2025-08-06 13:00'),  -- VIE -> FRA
('OS905', 40, 28,  5, '2025-08-07 08:00', '2025-08-07 10:30'),  -- VIE -> CDG
('OS906', 40, 36,  6, '2025-08-07 12:00', '2025-08-07 14:30'),  -- VIE -> AMS
('OS907', 40, 16,  7, '2025-08-08 07:00', '2025-08-08 10:00'),  -- VIE -> LHR
('OS908', 40, 50,  8, '2025-08-08 11:00', '2025-08-08 13:00'),  -- VIE -> BRU
('OS909', 40, 55,  9, '2025-08-09 06:00', '2025-08-09 08:00'),  -- VIE -> PRG
('OS910', 40, 67, 10, '2025-08-09 10:00', '2025-08-09 13:30');  -- VIE -> TLV
GO

-- ---- De la Atena (ATH, ID=42) ----
INSERT INTO Zboruri (NumarZbor, IdAeroportPlecare, IdAeroportSosire, IdAvion, DataPlecare, DataSosire) VALUES
('A3801', 42,  1,  1, '2025-08-10 07:00', '2025-08-10 10:00'),  -- ATH -> KIV
('A3802', 42,  5,  2, '2025-08-10 09:00', '2025-08-10 11:30'),  -- ATH -> OTP
('A3803', 42, 12,  3, '2025-08-11 08:00', '2025-08-11 10:00'),  -- ATH -> IST
('A3804', 42, 32,  4, '2025-08-11 12:00', '2025-08-11 15:30'),  -- ATH -> FRA
('A3805', 42, 28,  5, '2025-08-12 07:00', '2025-08-12 10:30'),  -- ATH -> CDG
('A3806', 42, 36,  6, '2025-08-12 11:00', '2025-08-12 15:00'),  -- ATH -> AMS
('A3807', 42, 67,  7, '2025-08-13 09:00', '2025-08-13 12:00'),  -- ATH -> TLV
('A3808', 42, 40,  8, '2025-08-13 13:00', '2025-08-13 15:00'),  -- ATH -> VIE
('A3809', 42, 50,  9, '2025-08-14 07:00', '2025-08-14 10:30'),  -- ATH -> BRU
('A3810', 42, 55, 10, '2025-08-14 11:00', '2025-08-14 13:30');  -- ATH -> PRG
GO

-- ---- De la Budapesta (BUD, ID=46) ----
INSERT INTO Zboruri (NumarZbor, IdAeroportPlecare, IdAeroportSosire, IdAvion, DataPlecare, DataSosire) VALUES
('MA901', 46,  1,  1, '2025-08-15 07:00', '2025-08-15 09:30'),  -- BUD -> KIV
('MA902', 46,  5,  2, '2025-08-15 09:00', '2025-08-15 11:00'),  -- BUD -> OTP
('MA903', 46, 12,  3, '2025-08-16 08:00', '2025-08-16 11:00'),  -- BUD -> IST
('MA904', 46, 32,  4, '2025-08-16 12:00', '2025-08-16 14:00'),  -- BUD -> FRA
('MA905', 46, 28,  5, '2025-08-17 07:00', '2025-08-17 09:30'),  -- BUD -> CDG
('MA906', 46, 36,  6, '2025-08-17 11:00', '2025-08-17 13:30'),  -- BUD -> AMS
('MA907', 46, 16,  7, '2025-08-18 08:00', '2025-08-18 10:30'),  -- BUD -> LHR
('MA908', 46, 40,  8, '2025-08-18 12:00', '2025-08-18 13:30'),  -- BUD -> VIE
('MA909', 46, 50,  9, '2025-08-19 07:00', '2025-08-19 09:30'),  -- BUD -> BRU
('MA910', 46, 55, 10, '2025-08-19 11:00', '2025-08-19 13:00');  -- BUD -> PRG
GO

-- =====================================================================
-- 5. PASAGERI (153 noi, ID-uri: 51-203)
-- =====================================================================
INSERT INTO Pasageri (Nume, Prenume, Email, Telefon) VALUES

-- ---- Lista 1 (29 pasageri) ----
('Arion',        'Andrian',           'arion.andrian@mail.com',          '069100001'),
('Babara',       'Elvis',             'babara.elvis@mail.com',           '069100002'),
('Babcinetchi',  'Mihail',            'babcinetchi.mihail@mail.com',     '069100003'),
('Barburas',     'Mirela',            'barburas.mirela@mail.com',        '069100004'),
('Bodrug',       'Valeria',           'bodrug.valeria@mail.com',         '069100005'),
('Botnari',      'Cristina',          'botnari.cristina@mail.com',       '069100006'),
('Botnaru',      'Ion',               'botnaru.ion@mail.com',            '069100007'),
('Cervatiuc',    'Maxim',             'cervatiuc.maxim@mail.com',        '069100008'),
('Cotofana',     'Tudor',             'cotofana.tudor@mail.com',         '069100009'),
('Fedco',        'Madalina',          'fedco.madalina@mail.com',         '069100010'),
('Galben',       'Andrei',            'galben.andrei@mail.com',          '069100011'),
('Halinga',      'Nicoleta',          'halinga.nicoleta@mail.com',       '069100012'),
('Harbuzaru',    'Mihaela',           'harbuzaru.mihaela@mail.com',      '069100013'),
('Karpinskaia',  'Aliona',            'karpinskaia.aliona@mail.com',     '069100014'),
('Lupu',         'Daniel',            'lupu.daniel@mail.com',            '069100015'),
('Marusciac',    'Valeria',           'marusciac.valeria@mail.com',      '069100016'),
('Molache',      'Dan',               'molache.dan@mail.com',            '069100017'),
('Ostap',        'Stefan',            'ostap.stefan@mail.com',           '069100018'),
('Ostap',        'Victor',            'ostap.victor@mail.com',           '069100019'),
('Padure',       'Valeria',           'padure.valeria@mail.com',         '069100020'),
('Petrovschi',   'Alexandra',         'petrovschi.alexandra@mail.com',   '069100021'),
('Plesca',       'Maxim',             'plesca.maxim@mail.com',           '069100022'),
('Popovici',     'Cristian',          'popovici.cristian@mail.com',      '069100023'),
('Primac',       'Dorin',             'primac.dorin@mail.com',           '069100024'),
('Rosu',         'Radic',             'rosu.radic@mail.com',             '069100025'),
('Timuta',       'Alexandru',         'timuta.alexandru@mail.com',       '069100026'),
('Tulea',        'Artiom',            'tulea.artiom@mail.com',           '069100027'),
('Tulea',        'Artur',             'tulea.artur@mail.com',            '069100028'),
('Velescu',      'Vladimir',          'velescu.vladimir@mail.com',       '069100029'),

-- ---- Lista 2 (28 pasageri) ----
('Andronic',     'Patricia-Gabriela', 'andronic.patricia@mail.com',      '069200001'),
('Avram',        'Robert',            'avram.robert@mail.com',           '069200002'),
('Balanuta',     'Vlada',             'balanuta.vlada@mail.com',         '069200003'),
('Barbos',       'Dan',               'barbos.dan@mail.com',             '069200004'),
('Bota',         'Alexandru',         'bota.alexandru@mail.com',         '069200005'),
('Carp',         'Alexei',            'carp.alexei@mail.com',            '069200006'),
('Certan',       'Daniel',            'certan.daniel@mail.com',          '069200007'),
('Chilaru',      'Cristian-Ion',      'chilaru.cristian@mail.com',       '069200008'),
('Cogilniceanu', 'Bianca',            'cogilniceanu.bianca@mail.com',    '069200009'),
('Costin',       'Roman',             'costin.roman@mail.com',           '069200010'),
('Cozma',        'Nicolai',           'cozma.nicolai@mail.com',          '069200011'),
('Erina',        'Iulia',             'erina.iulia@mail.com',            '069200012'),
('Gheruha',      'Maxim',             'gheruha.maxim@mail.com',          '069200013'),
('Grama',        'Victoria',          'grama.victoria@mail.com',         '069200014'),
('Grigoriev',    'Gabriela',          'grigoriev.gabriela@mail.com',     '069200015'),
('Gugulan',      'Alexandr',          'gugulan.alexandr@mail.com',       '069200016'),
('Iacovenco',    'David',             'iacovenco.david@mail.com',        '069200017'),
('Ispas',        'Ioan',              'ispas.ioan@mail.com',             '069200018'),
('Lazari',       'Dumitru',           'lazari.dumitru@mail.com',         '069200019'),
('Palade',       'Radu-Tudor',        'palade.radu@mail.com',            '069200020'),
('Pesterean',    'Artiom',            'pesterean.artiom@mail.com',       '069200021'),
('Petcu',        'Adrian',            'petcu.adrian@mail.com',           '069200022'),
('Pinzari',      'Adrian',            'pinzari.adrian@mail.com',         '069200023'),
('Railean',      'Lia',               'railean.lia@mail.com',            '069200024'),
('Rotaru',       'Laviniu',           'rotaru.laviniu@mail.com',         '069200025'),
('Sandu',        'Maxim',             'sandu.maxim@mail.com',            '069200026'),
('Sahov',        'Daniil',            'sahov.daniil@mail.com',           '069200027'),
('Ursu',         'Cristian',          'ursu.cristian@mail.com',          '069200028'),

-- ---- Lista 3 (34 pasageri) ----
('Bezerdic',     'Marina',            'bezerdic.marina@mail.com',        '069300001'),
('Bodiu',        'Alexandra',         'bodiu.alexandra@mail.com',        '069300002'),
('Botnari',      'Marina',            'botnari.marina@mail.com',         '069300003'),
('Burdujan',     'Dumitrita',         'burdujan.dumitrita@mail.com',     '069300004'),
('Buzilo',       'Sabrina',           'buzilo.sabrina@mail.com',         '069300005'),
('Carasevici',   'Maxim',             'carasevici.maxim@mail.com',       '069300006'),
('Cealenco',     'Daniela',           'cealenco.daniela@mail.com',       '069300007'),
('Ciorba',       'Cristi',            'ciorba.cristi@mail.com',          '069300008'),
('Coada',        'Cristina',          'coada.cristina@mail.com',         '069300009'),
('Doga',         'Mihaela',           'doga.mihaela@mail.com',           '069300010'),
('Ermurache',    'Dana',              'ermurache.dana@mail.com',         '069300011'),
('Filimon',      'Iuliana',           'filimon.iuliana@mail.com',        '069300012'),
('Garaz',        'Gabriela',          'garaz.gabriela@mail.com',         '069300013'),
('Grozinschi',   'Alina',             'grozinschi.alina@mail.com',       '069300014'),
('Guranda',      'Vladislav',         'guranda.vladislav@mail.com',      '069300015'),
('Jian',         'Daniela',           'jian.daniela@mail.com',           '069300016'),
('Josan',        'Ana',               'josan.ana@mail.com',              '069300017'),
('Leanca',       'Mihaela',           'leanca.mihaela@mail.com',         '069300018'),
('Matvei',       'Catalina',          'matvei.catalina@mail.com',        '069300019'),
('Mereuta',      'Natalia',           'mereuta.natalia@mail.com',        '069300020'),
('Mironiuc',     'Daniel',            'mironiuc.daniel@mail.com',        '069300021'),
('Pomirleanu',   'Daniela',           'pomirleanu.daniela@mail.com',     '069300022'),
('Popa',         'Daniela',           'popa.daniela@mail.com',           '069300023'),
('Rotari',       'Marina',            'rotari.marina@mail.com',          '069300024'),
('Secrieru',     'Valentina',         'secrieru.valentina@mail.com',     '069300025'),
('Spita',        'Ana-Maria',         'spita.ana@mail.com',              '069300026'),
('Spinu',        'Marian',            'spinu.marian@mail.com',           '069300027'),
('Stepanuc',     'Nicoleta',          'stepanuc.nicoleta@mail.com',      '069300028'),
('Taureanu',     'Sveatoslav',        'taureanu.sveatoslav@mail.com',    '069300029'),
('Tibirna',      'Alina',             'tibirna.alina@mail.com',          '069300030'),
('Ustica',       'Carolina',          'ustica.carolina@mail.com',        '069300031'),
('Ustica',       'Cristina',          'ustica.cristina@mail.com',        '069300032'),
('Veverita',     'Emilia',            'veverita.emilia@mail.com',        '069300033'),
('Zemcic',       'Catalin',           'zemcic.catalin@mail.com',         '069300034'),

-- ---- Lista 4 (29 pasageri) ----
('Banari',       'Vitalie',           'banari.vitalie@mail.com',         '069400001'),
('Basencov',     'Danila',            'basencov.danila@mail.com',        '069400002'),
('Braga',        'Dumitru',           'braga.dumitru@mail.com',          '069400003'),
('Burdujan',     'Vladislav',         'burdujan.vladislav@mail.com',     '069400004'),
('Casian',       'Vlad',              'casian.vlad@mail.com',            '069400005'),
('Ceban',        'Maxim',             'ceban.maxim@mail.com',            '069400006'),
('Cebotari',     'Chiril',            'cebotari.chiril@mail.com',        '069400007'),
('Cebotari',     'Mihail',            'cebotari.mihail@mail.com',        '069400008'),
('Clichici',     'Sergiu',            'clichici.sergiu@mail.com',        '069400009'),
('Cocotchin',    'Denis',             'cocotchin.denis@mail.com',        '069400010'),
('Cristea',      'Catalin',           'cristea.catalin@mail.com',        '069400011'),
('Eftode',       'Bogdan',            'eftode.bogdan@mail.com',          '069400012'),
('Girleanu',     'Vitalie',           'girleanu.vitalie@mail.com',       '069400013'),
('Gobenco',      'Dan',               'gobenco.dan@mail.com',            '069400014'),
('Harea',        'Ion',               'harea.ion@mail.com',              '069400015'),
('Iachim',       'Ion',               'iachim.ion@mail.com',             '069400016'),
('Ibraghim',     'Bader',             'ibraghim.bader@mail.com',         '069400017'),
('Josan',        'Daniela',           'josan.daniela@mail.com',          '069400018'),
('Lutcan',       'Loredana',          'lutcan.loredana@mail.com',        '069400019'),
('Munteanu',     'Daria',             'munteanu.daria@mail.com',         '069400020'),
('Nastas',       'Vadim',             'nastas.vadim@mail.com',           '069400021'),
('Nogai',        'Ionut',             'nogai.ionut@mail.com',            '069400022'),
('Odajiu',       'Octavian',          'odajiu.octavian@mail.com',        '069400023'),
('Oprea',        'Adelina',           'oprea.adelina@mail.com',          '069400024'),
('Pascali',      'Vladislav',         'pascali.vladislav@mail.com',      '069400025'),
('Podstavek',    'Marek',             'podstavek.marek@mail.com',        '069400026'),
('Popa',         'Ion',               'popa.ion@mail.com',               '069400027'),
('Popa',         'Marian',            'popa.marian@mail.com',            '069400028'),
('Simionel',     'Emil',              'simionel.emil@mail.com',          '069400029'),

-- ---- Lista 5 (33 pasageri) ----
('Bargan',       'Iulian',            'bargan.iulian@mail.com',          '069500001'),
('Bitca',        'Ion',               'bitca.ion@mail.com',              '069500002'),
('Bujeag',       'Grigore',           'bujeag.grigore@mail.com',         '069500003'),
('Carapunarli',  'Ion',               'carapunarli.ion@mail.com',        '069500004'),
('Chiricenco',   'Vladimir',          'chiricenco.vladimir@mail.com',    '069500005'),
('Ciobanu',      'Anatolie',          'ciobanu.anatolie@mail.com',       '069500006'),
('Ciubotaru',    'Dimitrie',          'ciubotaru.dimitrie@mail.com',     '069500007'),
('Cojocari',     'Octavian',          'cojocari.octavian@mail.com',      '069500008'),
('Cojocaru',     'Stanislav',         'cojocaru.stanislav@mail.com',     '069500009'),
('Cospormac',    'Igor',              'cospormac.igor@mail.com',         '069500010'),
('Dubenco',      'Vladislav',         'dubenco.vladislav@mail.com',      '069500011'),
('Fisticanu',    'Gheorghe',          'fisticanu.gheorghe@mail.com',     '069500012'),
('Ghermanciuc',  'Alexandru',         'ghermanciuc.alexandru@mail.com',  '069500013'),
('Harabari',     'Anton',             'harabari.anton@mail.com',         '069500014'),
('Ialovoi',      'Eugeniu',           'ialovoi.eugeniu@mail.com',        '069500015'),
('Iastremschi',  'Vladimir',          'iastremschi.vladimir@mail.com',   '069500016'),
('Janga',        'Mihail',            'janga.mihail@mail.com',           '069500017'),
('Muntean',      'Mihail',            'muntean.mihail@mail.com',         '069500018'),
('Ogor',         'Cristian',          'ogor.cristian@mail.com',          '069500019'),
('Paduret',      'Adrian',            'paduret.adrian@mail.com',         '069500020'),
('Podaru',       'Ion',               'podaru.ion@mail.com',             '069500021'),
('Popovici',     'Radu',              'popovici.radu@mail.com',          '069500022'),
('Russu',        'Alexei',            'russu.alexei@mail.com',           '069500023'),
('Serbaniuc',    'Teodor-Ionut',      'serbaniuc.teodor@mail.com',       '069500024'),
('Sili',         'Nicolae',           'sili.nicolae@mail.com',           '069500025'),
('Sirghe',       'Cristian',          'sirghe.cristian@mail.com',        '069500026'),
('Starciuc',     'Cristian',          'starciuc.cristian@mail.com',      '069500027'),
('Scheau',       'Dennis',            'scheau.dennis@mail.com',          '069500028'),
('Sorodoc',      'Daniil',            'sorodoc.daniil@mail.com',         '069500029'),
('Tataru',       'Daniel',            'tataru.daniel@mail.com',          '069500030'),
('Ulianovschi',  'Nicolae',           'ulianovschi.nicolae@mail.com',    '069500031'),
('Vasciaev',     'Vladimir',          'vasciaev.vladimir@mail.com',      '069500032'),
('Zagordani',    'Daniel-Ion',        'zagordani.daniel@mail.com',       '069500033');
GO

-- =====================================================================
-- 6. REZERVARI (153 noi, ID-uri: 51-203)
-- CodRezervare VARCHAR(20) UNIQUE  -- ex: RES1051
-- =====================================================================
DECLARE @r INT = 51;
WHILE @r <= 203
BEGIN
    INSERT INTO Rezervari (CodRezervare)
    VALUES (CONCAT('RES', 1000 + @r));
    SET @r += 1;
END
GO

-- =====================================================================
-- 7. BILETE (153 noi, ID-uri: 51-203)
-- Status VARCHAR(20) DEFAULT 'Confirmat' -- Confirmat / Anulat
--
-- Pattern status:
--   @b % 3 = 0  -> 'Anulat'   (~51 bilete, 33%)
--   altfel       -> 'Confirmat' (~102 bilete, 67%)
--
-- IdZbor  ciclu 51-150  (noile 100 zboruri)
-- IdLoc   ciclu 451-603 (din cele 2000 locuri noi)
-- =====================================================================
DECLARE @b INT = 51;
WHILE @b <= 203
BEGIN
    INSERT INTO Bilete (IdRezervare, IdPasager, IdZbor, IdLoc, Pret, Status)
    VALUES (
        @b,
        @b,
        ((@b - 51) % 100) + 51,
        451 + ((@b - 51) % 2000),
        (ABS(CHECKSUM(NEWID())) % 400) + 50,
        CASE
            WHEN @b % 3 = 0 THEN 'Anulat'
            ELSE 'Confirmat'
        END
    );
    SET @b += 1;
END
GO

-- =====================================================================
-- 8. PLATI (153 noi, ID-uri: 51-203)
-- Metoda VARCHAR(20) -- Card / Cash
-- Status VARCHAR(20) -- Platit / Refuzat
--
-- Pattern metoda:  par -> 'Card', impar -> 'Cash'
-- Pattern status:  @p % 5 = 0 -> 'Refuzat' (~20%), altfel -> 'Platit'
-- =====================================================================
DECLARE @p INT = 51;
WHILE @p <= 203
BEGIN
    INSERT INTO Plati (IdRezervare, Suma, Metoda, Status)
    VALUES (
        @p,
        (ABS(CHECKSUM(NEWID())) % 500) + 50,
        CASE WHEN @p % 2 = 0 THEN 'Card' ELSE 'Cash' END,
        CASE WHEN @p % 5 = 0 THEN 'Refuzat' ELSE 'Platit' END
    );
    SET @p += 1;
END
GO

-- =====================================================================
-- SUMAR INSERARI SUPLIMENTARE
-- =====================================================================
-- Aeroporturi :  30  (ID 50-79,  CodIATA: BRU, CRL, LIS ... EDI)
-- Avioane     :  20  (ID 50-69)
-- Locuri      : 2000 (ID 451-2450, Economy / Business)
-- Zboruri     : 100  (ID 51-150,  rute KIV/OTP/IST/FRA/CDG/AMS/LHR/VIE/ATH/BUD)
-- Pasageri    : 153  (ID 51-203)
-- Rezervari   : 153  (ID 51-203, RES1051-RES1203)
-- Bilete      : 153  (Confirmat ~67% / Anulat ~33%)
-- Plati       : 153  (Card 50% / Cash 50%, Platit ~80% / Refuzat ~20%)
-- =====================================================================
