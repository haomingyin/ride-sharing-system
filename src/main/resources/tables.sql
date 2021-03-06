CREATE TABLE IF NOT EXISTS car
(
    carId TEXT PRIMARY KEY,
    username TEXT NOT NULL,
    model TEXT NOT NULL,
    manufacturer TEXT NOT NULL,
    color TEXT NOT NULL,
    year INT NOT NULL,
    seatNo INT NOT NULL,
    CONSTRAINT car_user_username_fk FOREIGN KEY (username) REFERENCES user (username)
);
CREATE UNIQUE INDEX car_plate_uindex ON car (carId);
CREATE TABLE IF NOT EXISTS ride
(
    rideId INTEGER PRIMARY KEY AUTOINCREMENT,
    alias TEXT,
    tripId INTEGER,
    seatNo INT,
    CONSTRAINT ride_trip_tripid_fk FOREIGN KEY (tripId) REFERENCES trip (tripId)
);
CREATE TABLE IF NOT EXISTS ride_passenger
(
    passenger TEXT,
    rideId INTEGER,
    CONSTRAINT ride_passenger_passenger_rideid_pk PRIMARY KEY (passenger, rideId),
    CONSTRAINT ride_passenger_user_username_fk FOREIGN KEY (passenger) REFERENCES user (username),
    CONSTRAINT ride_passenger_ride_rideid_fk FOREIGN KEY (rideId) REFERENCES ride (rideId)
);
CREATE TABLE IF NOT EXISTS stop_point
(
    spId INTEGER PRIMARY KEY AUTOINCREMENT,
    streetNo TEXT NOT NULL,
    street TEXT NOT NULL,
    suburb TEXT NOT NULL,
    city TEXT NOT NULL,
    trimmed TEXT NOT NULL
);
CREATE TABLE IF NOT EXISTS trip
(
    tripId INTEGER PRIMARY KEY AUTOINCREMENT,
    alias TEXT,
    username TEXT,
    direction TEXT,
    carId TEXT,
    beginDate DATE,
    expireDate DATE
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
CREATE TABLE IF NOT EXISTS user
(
    username TEXT PRIMARY KEY,
    password TEXT NOT NULL,
    email TEXT,
    fName TEXT,
    lName TEXT,
    phone TEXT
);
CREATE UNIQUE INDEX IF NOT EXISTS user_username_uindex ON user (username);