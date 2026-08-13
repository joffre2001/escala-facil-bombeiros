CREATE TABLE escalas (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    data_inicio DATE NOT NULL,
    data_fim DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    criada_em TIMESTAMP NOT NULL,
    vagas_nao_preenchidas INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT ck_escalas_periodo CHECK (data_fim >= data_inicio),
    CONSTRAINT ck_escalas_status CHECK (status IN ('EM_REVISAO', 'PUBLICADA'))
);

CREATE TABLE itens_escala (
    id BIGSERIAL PRIMARY KEY,
    escala_id BIGINT NOT NULL,
    bombeiro_id BIGINT NOT NULL,
    inicio_plantao TIMESTAMP NOT NULL,
    fim_plantao TIMESTAMP NOT NULL,
    conflito BOOLEAN NOT NULL DEFAULT FALSE,
    observacao VARCHAR(255),
    CONSTRAINT fk_itens_escala_escala
        FOREIGN KEY (escala_id) REFERENCES escalas(id) ON DELETE CASCADE,
    CONSTRAINT fk_itens_escala_bombeiro
        FOREIGN KEY (bombeiro_id) REFERENCES bombeiros(id),
    CONSTRAINT ck_itens_escala_24h
        CHECK (fim_plantao = inicio_plantao + INTERVAL '24 hours'),
    CONSTRAINT ck_itens_escala_inicio_08h
        CHECK (inicio_plantao::time = TIME '08:00:00'),
    CONSTRAINT uk_item_escala_bombeiro_inicio
        UNIQUE (escala_id, bombeiro_id, inicio_plantao)
);

CREATE INDEX idx_escalas_periodo ON escalas(data_inicio, data_fim);
CREATE INDEX idx_itens_escala_inicio ON itens_escala(escala_id, inicio_plantao);
CREATE INDEX idx_itens_escala_bombeiro ON itens_escala(bombeiro_id);