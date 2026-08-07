CREATE TABLE indisponibilidades (
    id BIGSERIAL PRIMARY KEY,
    bombeiro_id BIGINT NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    data_inicio DATE NOT NULL,
    data_fim DATE NOT NULL,
    negociavel BOOLEAN NOT NULL,
    motivo VARCHAR(255),
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_indisponibilidade_bombeiro
        FOREIGN KEY (bombeiro_id)
        REFERENCES bombeiros(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_periodo_indisponibilidade
        CHECK (data_fim >= data_inicio),

    CONSTRAINT chk_tipo_indisponibilidade
        CHECK (tipo IN (
            'FERIAS',
            'FOLGA',
            'LICENCA',
            'AFASTAMENTO',
            'COMPROMISSO',
            'OUTRO'
        )),

    CONSTRAINT chk_ferias_inegociaveis
        CHECK (tipo <> 'FERIAS' OR negociavel = FALSE)
);

CREATE INDEX idx_indisponibilidade_bombeiro
    ON indisponibilidades(bombeiro_id);

CREATE INDEX idx_indisponibilidade_periodo
    ON indisponibilidades(data_inicio, data_fim);