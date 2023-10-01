CREATE TABLE IF NOT EXISTS users
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE NOT NULL,
    username VARCHAR(255) UNIQUE NOT NULL,
    isEmailConfirmation BOOLEAN DEFAULT false,
    passwordHash VARCHAR(255) NOT NULL,
    isActive BOOLEAN DEFAULT true,
    createdAt DATETIME(6),
    updatedAt DATETIME(6)
);


CREATE TABLE IF NOT EXISTS roles
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) UNIQUE NOT NULL,
    createdAt DATETIME(6),
    updatedAt DATETIME(6)
);

INSERT IGNORE INTO roles (name,createdAt,updatedAt) values
("USER",NOW(),NOW()),
("ADMIN",NOW(),NOW());

CREATE TABLE IF NOT EXISTS user_roles (
    userId BIGINT NOT NULL,
    roleId BIGINT NOT NULL,
    FOREIGN KEY (userId) REFERENCES users(id),
    FOREIGN KEY (roleId) REFERENCES roles(id),
    PRIMARY KEY (userId, roleId)
);






CREATE TABLE IF NOT EXISTS sessions
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    userId BIGINT NOT NULL,
    refreshToken VARCHAR(255) NOT NULL,
    isBlackList BOOLEAN DEFAULT false,
    createdAt DATETIME(6),
    expiredAt DATETIME(6),
    FOREIGN KEY (userId) REFERENCES users(id)
);







