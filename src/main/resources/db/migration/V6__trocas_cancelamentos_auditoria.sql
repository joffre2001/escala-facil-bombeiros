ALTER TABLE itens_escala ADD COLUMN cancelado BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE itens_escala ADD COLUMN motivo_cancelamento VARCHAR(255);

CREATE TABLE solicitacoes_troca (
    id BIGSERIAL PRIMARY KEY,
    item_escala_id BIGINT NOT NULL,
    solicitante_id BIGINT NOT NULL,
    substituto_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL,
    motivo VARCHAR(255),
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_troca_item FOREIGN KEY (item_escala_id) REFERENCES itens_escala(id) ON DELETE CASCADE,
    CONSTRAINT fk_troca_solicitante FOREIGN KEY (solicitante_id) REFERENCES bombeiros(id),
    CONSTRAINT fk_troca_substituto FOREIGN KEY (substituto_id) REFERENCES bombeiros(id),
    CONSTRAINT ck_troca_status CHECK (status IN ('AGUARDANDO_ACEITE','AGUARDANDO_APROVACAO','APROVADA','RECUSADA','CANCELADA')),
    CONSTRAINT ck_troca_pessoas CHECK (solicitante_id <> substituto_id)
);

CREATE INDEX idx_troca_item ON solicitacoes_troca(item_escala_id);
CREATE INDEX idx_troca_participantes ON solicitacoes_troca(solicitante_id, substituto_id);
CREATE INDEX idx_troca_status ON solicitacoes_troca(status);

CREATE TABLE historico_escalas (
    id BIGSERIAL PRIMARY KEY,
    escala_id BIGINT NOT NULL,
    item_escala_id BIGINT,
    acao VARCHAR(40) NOT NULL,
    descricao VARCHAR(500) NOT NULL,
    ator_email VARCHAR(150) NOT NULL,
    ator_perfil VARCHAR(20) NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_historico_escala ON historico_escalas(escala_id, criado_em);
