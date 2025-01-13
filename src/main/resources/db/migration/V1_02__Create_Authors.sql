-- 著者テーブル
CREATE TABLE authors (
    id SERIAL PRIMARY KEY,
    last_name VARCHAR(100) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name_kana VARCHAR(100) NOT NULL,
    first_name_kana VARCHAR(100) NOT NULL,
    birth DATE NOT NULL,
    created_by VARCHAR(45) NOT NULL,
    updated_by VARCHAR(45) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
)