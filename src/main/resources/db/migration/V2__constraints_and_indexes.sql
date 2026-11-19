-- Unicidade de login (conflito → 409 na API)
CREATE UNIQUE INDEX uk_usuarios_email ON usuarios (email);

-- Evita ponto de régua duplicado no mesmo bairro
CREATE UNIQUE INDEX uk_pontos_regua_nome_bairro ON pontos_regua (nome, bairro);

-- Índices de relacionamento / consulta
CREATE INDEX idx_ocorrencias_usuario ON ocorrencias (usuario_id);
CREATE INDEX idx_ocorrencias_bairro ON ocorrencias (bairro);
CREATE INDEX idx_leituras_ponto ON leituras (ponto_id);
CREATE INDEX idx_alertas_ponto ON alertas (ponto_id);
