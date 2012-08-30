-- -----------------------------------------------------
-- Table `lines`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `lines` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `name` TEXT NOT NULL ,
  `isLoop` INT NOT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `waypoints`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `waypoints` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `lineID` INT NOT NULL ,
  `x` INT NOT NULL ,
  `y` INT NOT NULL ,
  `z` INT NOT NULL ,
  `world` TEXT NOT NULL ,
  `speed` FLOAT NOT NULL ,
  `isWaiting` INT NOT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `activator`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `activator` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `lineID` INT NOT NULL ,
  `waypointID` INT NOT NULL ,
  `x` INT NOT NULL ,
  `y` INT NOT NULL ,
  `z` INT NOT NULL ,
  `world` TEXT NOT NULL ,
  `isBackwards` INT NOT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;