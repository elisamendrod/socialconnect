ALTER TABLE produtos ADD COLUMN nome_normalizado VARCHAR(150) GENERATED ALWAYS AS (LOWER(nome));
CREATE UNIQUE INDEX uk_produtos_nome_lower ON produtos (nome_normalizado);
