CREATE TABLE bombeiros (
    id BIGSERIAL PRIMARY KEY,
    nome_completo VARCHAR(150) NOT NULL,
    matricula VARCHAR(30) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    telefone VARCHAR(30),
    cargo VARCHAR(80) NOT NULL,
    equipe VARCHAR(80),
    data_admissao DATE,
    status VARCHAR(20) NOT NULL CHECK (status IN ('ATIVO', 'INATIVO')),
    criado_em TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_bombeiros_nome ON bombeiros(nome_completo);
CREATE INDEX idx_bombeiros_status ON bombeiros(status);
