CREATE TABLE characters (
    character_id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    role VARCHAR(50)
);

CREATE TABLE questions (
    question_id SERIAL PRIMARY KEY,
    text TEXT NOT NULL,
    time_asked TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    author_id INT NOT NULL REFERENCES characters(character_id)
);

CREATE TABLE question_detail (
    question_id INT PRIMARY KEY REFERENCES questions(question_id) ON DELETE CASCADE,
    additional_info TEXT,
    importance_level INT
);

CREATE TABLE question_watcher (
    question_id INT REFERENCES questions(question_id) ON DELETE CASCADE,
    character_id INT REFERENCES characters(character_id) ON DELETE CASCADE,
    notify_date DATE,
    PRIMARY KEY (question_id, character_id)
);

CREATE TABLE answers (
    answer_id SERIAL PRIMARY KEY,
    question_id INT REFERENCES questions(question_id) ON DELETE CASCADE,
    responder_id INT REFERENCES characters(character_id) ON DELETE CASCADE,
    text TEXT NOT NULL,
    time_answered TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO characters (name, role) VALUES
    ('Смегул', 'Шут'),
    ('Алистер', 'Путешественник'),
    ('Фродо', 'Хоббит'),
    ('Арагорн, сын Араторна', 'Король Средиземья');

INSERT INTO questions (text, author_id) VALUES
    ('Что произошло, пока я отсутствовал?', 2),
    ('Как нам попасть в Морию?', 2),
    ('Где кольцо?', 3),
    ('Как ты выжил?', 4);

INSERT INTO question_detail (question_id, additional_info, importance_level) VALUES
    (2, 'Древние шахты гномов, путь может быть опасен.', 8),
    (3, 'Речь о Кольце Всевластья, самом могущественном артефакте Средиземья.', 10);

INSERT INTO question_watcher (question_id, character_id, notify_date) VALUES
    (1, 1, '1232-06-01'),
    (1, 3, '1232-06-02'),
    (2, 1, '1232-06-03'),
    (2, 2, '1232-06-03');

INSERT INTO answers (question_id, responder_id, text) VALUES
    (1, 1, 'О, я видел много странных событий! Говорят, тьма надвигается...'),
    (2, 4, 'Спроси у Гимли.'),
    (2, 3, 'Говорят, путь лежит через секретные гномьи врата.'),
    (3, 3, 'Оно всегда при мне, это моё бремя.');
