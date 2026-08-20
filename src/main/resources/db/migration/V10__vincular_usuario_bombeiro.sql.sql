ALTER TABLE usuarios
    ADD COLUMN bombeiro_id BIGINT;

ALTER TABLE usuarios
    ADD CONSTRAINT fk_usuarios_bombeiro
    FOREIGN KEY (bombeiro_id)
    REFERENCES bombeiros (id);

ALTER TABLE usuarios
    ADD CONSTRAINT uk_usuarios_bombeiro
    UNIQUE (bombeiro_id);