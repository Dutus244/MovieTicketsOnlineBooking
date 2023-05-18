CREATE DATABASE  IF NOT EXISTS `movie_tickets_online_booking`;
begin;
USE `movie_tickets_online_booking`;
begin;

drop table if exists `user`;
create table `user`(
	`id` varchar(36) not null,
    `username` varchar(50) default null,
	`hashpassword` text default null,
    `name` varchar(50) default null,
    `email` varchar(50) default null,
    `dob` date default null,
    `sex` enum("Male", "Female", "Other") default null,
    `tel` varchar(10) default null,
    `role` enum("normal", "admin") default "normal",
	`is_deleted` bit default false,
	`is_banned` bit default false,
    primary key(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
begin;

drop table if exists `cinema`;
create table `cinema`(
	`id` varchar(36) not null,
	`img_url` text default null,
    `name` varchar(256) default null,
    `address` varchar(256) default null,
    `auditoriums_no` int default null,
	`status` text default null,
    `is_deleted` bit default false,
    primary key(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
begin;

drop table if exists `auditorium`;
create table `auditorium`(
	`id` varchar(36) not null,
    `cinema_id` varchar(36) not null,
    `name` varchar(32) not null,
    `seats_no` int not null,
    `is_deleted` bit default false,
    primary key(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
begin;

drop table if exists `screening`;
create table `screening`(
	`id` varchar(36) not null,
    `movie_id` varchar(36) not null,
    `auditorium_id` varchar(36) not null,
    `screening_start` datetime not null,
    `screening_end` datetime not null,
    `is_deleted` bit default false,
    primary key(id),
    unique index screening_ak_1 (movie_id, auditorium_id, screening_start)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
begin;

drop table if exists `seat`;
create table `seat`(
	`id` varchar(36) not null,
    `auditorium_id` varchar(36) not null,
    `row` varchar(1) default null, -- A, B, C, D
    `col` int default null, -- 1, 2, 3
    primary key(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
begin;

drop table if exists `ticket`;
create table `ticket`(
	`id` varchar(36) not null,
    `seat_id` varchar(36) not null,
    `screening_id` varchar(36) not null,
    `reservation_id` varchar(36) not null,
    `price` int not null,
    primary key(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
begin;

drop table if exists `reservation`;
create table `reservation`(
	`id` varchar(36) not null,
    `user_id` varchar(36) not null,
    `screening_id` varchar(36) not null,
    `total_price` int not null,
    primary key(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
begin;

drop table if exists `movie`;
create table `movie`(
	`id` varchar(36) not null,
    `title` varchar(256) not null,
    `poster_url` text default null,
	`vid_url` text default null,
    `director` varchar(256) default null,
    `cast` varchar(1024) default null,
    `description` text default null,
    `release_date` date default null,
    `classification` enum("P", "C13", "C16", "C18") default null,
    `rating` float default 0,
	`is_active` bit default true,
    `is_deleted` bit default false,
    primary key(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
begin;

alter table `password_salt`
	add constraint `fk_passwordsalt_user` foreign key(user_id) references `user`(id);
begin;

alter table `auditorium`
	add constraint `fk_auditorium_cinema` foreign key(cinema_id) references cinema(id);
begin;

alter table `screening`
	add constraint `fk_screening_movie` foreign key(movie_id) references movie(id),
    add constraint `fk_screening_auditorium` foreign key(auditorium_id) references auditorium(id);
begin;

alter table `seat`
	add constraint `fk_seat_auditorium` foreign key(auditorium_id) references auditorium(id);
begin;

alter table `ticket`
	add constraint `fk_ticket_seat` foreign key(seat_id) references seat(id),
    add constraint `ticket_screening` foreign key(screening_id) references screening(id),
    add constraint `fk_ticket_reservation` foreign key(reservation_id) references reservation(id);
begin;

alter table `reservation`
	add constraint `fk_reservation_user` foreign key(user_id) references `user`(id),
	add constraint `fk_reservation_screening` foreign key(screening_id) references screening(id);
begin;
