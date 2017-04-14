CREATE TABLE car (
    carId        INTEGER PRIMARY KEY AUTOINCREMENT,
    plate        TEXT    UNIQUE,
    username     TEXT    NOT NULL,
    model        TEXT    NOT NULL,
    manufacturer TEXT    NOT NULL,
    color        TEXT    NOT NULL,
    year         INT     NOT NULL,
    seatNo       INT     NOT NULL,
    wof          DATE,
    performance  DOUBLE,
    CONSTRAINT car_user_username_fk FOREIGN KEY (username) REFERENCES user (username)
);

CREATE UNIQUE INDEX car_plate_uindex ON car (carId);
CREATE TABLE IF NOT EXISTS ride
(
    rideId INTEGER PRIMARY KEY AUTOINCREMENT,
    alias TEXT,
    tripId INTEGER,
    seatNo INT,
    username TEXT,
    CONSTRAINT ride_trip_tripid_fk FOREIGN KEY (tripId) REFERENCES trip (tripId),
    CONSTRAINT ride_user_username_fk FOREIGN KEY (username) REFERENCES user (username)
);
CREATE TABLE IF NOT EXISTS ride_passenger
(
    username TEXT,
    rideId INTEGER,
    spId INTEGER,
    CONSTRAINT ride_passenger_passenger_rideid_pk PRIMARY KEY (username, rideId, spId),
    CONSTRAINT ride_passenger_user_username_fk FOREIGN KEY (username) REFERENCES user (username),
    CONSTRAINT ride_passenger_ride_rideid_fk FOREIGN KEY (rideId) REFERENCES ride (rideId),
    CONSTRAINT ride_passenger_stop_point_spId_fk FOREIGN KEY (spId) REFERENCES stop_point (spId)
);
CREATE TABLE IF NOT EXISTS route
(
    routeId INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT,
    alias TEXT,
    CONSTRAINT route_user_username_fk FOREIGN KEY (username) REFERENCES user (username)
);
CREATE TABLE IF NOT EXISTS route_sp
(
    routeId INTEGER,
    spId INTEGER,
    CONSTRAINT route_sp_routeid_spid_pk PRIMARY KEY (routeId, spId),
    CONSTRAINT route_sp_route_routeid_fk FOREIGN KEY (routeId) REFERENCES route (routeId),
    CONSTRAINT route_sp_stop_point_spid_fk FOREIGN KEY (spId) REFERENCES stop_point (spId)
);
CREATE TABLE IF NOT EXISTS stop_point
(
    spId INTEGER PRIMARY KEY AUTOINCREMENT,
    streetNo TEXT NOT NULL,
    street TEXT NOT NULL,
    suburb TEXT NOT NULL,
    city TEXT NOT NULL,
    trimmed TEXT
);
CREATE TABLE trip (
    tripid     INTEGER PRIMARY KEY AUTOINCREMENT,
    alias      TEXT,
    username   TEXT,
    routeid    INTEGER,
    direction  TEXT,
    carid      INTEGER CONSTRAINT trip_car_carid REFERENCES car (carId) ON DELETE RESTRICT NOT DEFERRABLE,
    begindate  DATE,
    expiredate DATE
);
CREATE TABLE IF NOT EXISTS trip_sp
(
    tripId INTEGER,
    spId INTEGER,
    time DATETIME,
    CONSTRAINT trip_sp_tripid_sp_id_pk PRIMARY KEY (tripId, spId),
    CONSTRAINT trip_sp_trip_tripid_fk FOREIGN KEY (tripId) REFERENCES trip (tripId),
    CONSTRAINT trip_sp_stop_point_spid_fk FOREIGN KEY (spId) REFERENCES stop_point (spId)
);
CREATE TABLE IF NOT EXISTS user  (
    username    TEXT PRIMARY KEY,
    password    TEXT NOT NULL,
    email       TEXT,
    fName       TEXT,
    lName       TEXT,
    address     TEXT,
    hPhone      TEXT,
    mPhone      TEXT,
    licenceNo   TEXT,
    licenceType TEXT,
    issueDate   DATE,
    expireDate  DATE,
    photo       BLOB
);

CREATE UNIQUE INDEX IF NOT EXISTS user_username_uindex ON user (username);

CREATE VIEW IF NOT EXISTS ride_sp_view
AS
    SELECT s.spid, streetNo, street, suburb, city, trimmed
    FROM ride r LEFT JOIN trip_sp t ON r.tripId = t.tripId, stop_point s
    WHERE t.spId = s.spId
    GROUP BY s.spid
    ORDER BY trimmed ASC;

CREATE VIEW IF NOT EXISTS book_ride_view
AS
    SELECT r.rideId, r.tripId, ts.spId, t.direction, ts.time, r.seatNo - seatCounter.cnt as seatNo, r.username, t.carId
    FROM ride r
        LEFT JOIN trip t ON r.tripId = t.tripId
        LEFT JOIN trip_sp ts ON ts.tripId = r.tripId
        LEFT JOIN
        (SELECT r.rideId, count(rp.username) AS cnt
         FROM ride r
             LEFT JOIN ride_passenger rp ON r.rideId = rp.rideId
         GROUP BY r.rideId) seatCounter ON seatCounter.rideId = r.rideId
    WHERE r.seatNo - seatCounter.cnt > 0;

CREATE VIEW IF NOT EXISTS book_ride_passenger_view
AS
    SELECT r.rideId, r.tripId, ts.spId, t.direction, ts.time, r.seatNo - seatCounter.cnt as seatNo, r.username, t.carId, p.username as passenger
    FROM ride r
        LEFT JOIN trip t ON r.tripId = t.tripId
        LEFT JOIN trip_sp ts ON ts.tripId = r.tripId
        LEFT JOIN ride_passenger p ON r.rideId = p.rideId
        LEFT JOIN
        (SELECT r.rideId, count(rp.username) AS cnt
         FROM ride r
             LEFT JOIN ride_passenger rp ON r.rideId = rp.rideId
         GROUP BY r.rideId) seatCounter ON seatCounter.rideId = r.rideId