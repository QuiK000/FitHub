CREATE TABLE users
(
    id                 VARCHAR(36)  NOT NULL,
    user_email         VARCHAR(255) NOT NULL,
    user_password      VARCHAR(255) NOT NULL,
    user_enabled       BOOLEAN      NOT NULL DEFAULT FALSE,
    created_date       TIMESTAMP    NOT NULL,
    last_modified_date TIMESTAMP,
    created_by         VARCHAR(255) NOT NULL,
    last_modified_by   VARCHAR(255),
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uq_users_email UNIQUE (user_email)
);

CREATE INDEX idx_user_email ON users (user_email);
CREATE INDEX idx_user_enabled ON users (user_enabled);
CREATE INDEX idx_user_created ON users (created_date DESC);

CREATE TABLE roles
(
    id                 VARCHAR(36)  NOT NULL,
    role_name          VARCHAR(100) NOT NULL,
    created_date       TIMESTAMP    NOT NULL,
    last_modified_date TIMESTAMP,
    created_by         VARCHAR(255) NOT NULL,
    last_modified_by   VARCHAR(255),
    CONSTRAINT pk_roles PRIMARY KEY (id),
    CONSTRAINT uq_roles_name UNIQUE (role_name)
);

CREATE TABLE user_roles
(
    user_id VARCHAR(36) NOT NULL,
    role_id VARCHAR(36) NOT NULL,
    CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);

CREATE INDEX idx_user_roles_user ON user_roles (user_id);
CREATE INDEX idx_user_roles_role ON user_roles (role_id);


CREATE TABLE verification_tokens
(
    id         VARCHAR(36)  NOT NULL,
    token      VARCHAR(255) NOT NULL,
    token_type VARCHAR(50)  NOT NULL,
    user_id    VARCHAR(36)  NOT NULL,
    expires_at TIMESTAMP    NOT NULL,
    used       BOOLEAN      NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_verification_tokens PRIMARY KEY (id),
    CONSTRAINT uq_vt_token UNIQUE (token),
    CONSTRAINT uq_vt_user_type_used UNIQUE (user_id, token_type, used),
    CONSTRAINT fk_vt_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_token_value ON verification_tokens (token);
CREATE INDEX idx_token_user_type ON verification_tokens (user_id, token_type);
CREATE INDEX idx_token_expires_used ON verification_tokens (expires_at, used);
CREATE INDEX idx_token_used ON verification_tokens (used);

