-- 書籍テーブル
CREATE TABLE mapping_book_author (
    book_id INT,
    author_id INT,
    created_by VARCHAR(45) NOT NULL,
    updated_by VARCHAR(45) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    PRIMARY KEY (book_id, author_id),
    FOREIGN KEY (book_id) references books(id),
    FOREIGN KEY (author_id) references authors(id)
)