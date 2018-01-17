-- phpMyAdmin SQL Dump
-- version 4.7.3
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Jan 17, 2018 at 05:06 PM
-- Server version: 5.6.38
-- PHP Version: 5.6.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `granadag_sorbie`
--

-- --------------------------------------------------------

--
-- Table structure for table `comments`
--

CREATE TABLE `comments` (
  `id` int(16) NOT NULL,
  `comment` text NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `question_id` int(16) NOT NULL,
  `username` text NOT NULL,
  `user_photo` text NOT NULL,
  `isTrue` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `comments`
--

INSERT INTO `comments` (`id`, `comment`, `time`, `question_id`, `username`, `user_photo`, `isTrue`) VALUES
(1, 'it is isaac', '2018-01-17 12:53:49', 1, 'aykutuludag', 'https://lh6.googleusercontent.com/-tjI_29_lRpM/AAAAAAAAAAI/AAAAAAAAASk/Uq4-1A2bnZA/photo.jpg', 1);

-- --------------------------------------------------------

--
-- Table structure for table `questions`
--

CREATE TABLE `questions` (
  `id` int(16) NOT NULL,
  `photo` text NOT NULL,
  `question` text NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `isAnswered` int(16) NOT NULL,
  `comment_number` int(16) NOT NULL,
  `username` text NOT NULL,
  `user_photo` text NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `questions`
--

INSERT INTO `questions` (`id`, `photo`, `question`, `time`, `isAnswered`, `comment_number`, `username`, `user_photo`) VALUES
(1, 'http://granadagame.com/Sorbie/uploads/1516193610.jpeg', 'what?', '2018-01-17 12:53:30', 1, 1, 'aykutuludag', 'https://lh6.googleusercontent.com/-tjI_29_lRpM/AAAAAAAAAAI/AAAAAAAAASk/Uq4-1A2bnZA/photo.jpg'),
(2, 'http://granadagame.com/Sorbie/uploads/1516196936.jpeg', 'bu ne', '2018-01-17 13:48:56', 0, 0, 'ugurbenli', 'http://granadagame.com/Sorbie/profile.png');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` text CHARACTER SET utf8 COLLATE utf8_turkish_ci NOT NULL,
  `name` text CHARACTER SET utf8 COLLATE utf8_turkish_ci NOT NULL,
  `email` text CHARACTER SET utf8 COLLATE utf8_turkish_ci NOT NULL,
  `photo` text CHARACTER SET utf8 COLLATE utf8_turkish_ci NOT NULL,
  `gender` text CHARACTER SET utf8 COLLATE utf8_turkish_ci NOT NULL,
  `birthday` text CHARACTER SET utf8 COLLATE utf8_turkish_ci NOT NULL,
  `location` text CHARACTER SET utf8 COLLATE utf8_turkish_ci NOT NULL,
  `accType` text CHARACTER SET utf8 COLLATE utf8_turkish_ci NOT NULL,
  `job` text NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `name`, `email`, `photo`, `gender`, `birthday`, `location`, `accType`, `job`) VALUES
(1, 'aykutuludag', 'Aykut UludaÄŸ', 'aykutuludag1@gmail.com', 'https://lh6.googleusercontent.com/-tjI_29_lRpM/AAAAAAAAAAI/AAAAAAAAASk/Uq4-1A2bnZA/photo.jpg', 'Male', '01-01-2000', 'World', 'Android', 'Mechanical Engineer'),
(2, 'ugurbenli', 'Ugur Benli', 'ugurbenli12@gmail.com', 'http://granadagame.com/Sorbie/profile.png', 'DiÄŸer', '31/12/1999', 'Singapur', 'Android', 'designer');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `comments`
--
ALTER TABLE `comments`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `questions`
--
ALTER TABLE `questions`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `comments`
--
ALTER TABLE `comments`
  MODIFY `id` int(16) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;
--
-- AUTO_INCREMENT for table `questions`
--
ALTER TABLE `questions`
  MODIFY `id` int(16) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
