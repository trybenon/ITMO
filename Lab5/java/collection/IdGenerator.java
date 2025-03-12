package collection;

import model.Person;
import java.util.HashSet;

/**
 * Класс для генерации уникальных ID.
 * Использует текущее время в миллисекундах для генерации уникальных значений ID.
 */
public class IdGenerator {
    private static HashSet<Long> generatedIds = new HashSet<>();  // Храним ID типа long

    /**
     * Генерирует уникальный ID, используя текущее время в миллисекундах.
     * Повторно генерирует ID, если он уже существует в наборе.
     * @return сгенерированный уникальный ID
     */
    public long generateId() {
        long id = System.currentTimeMillis();  // Используем long вместо int
        // Если ID уже существует в наборе, генерируем новый
        while (generatedIds.contains(id)) {
            id = System.currentTimeMillis();
        }
        generatedIds.add(id);  // Добавляем новый ID в набор
        return id;
    }

    /**
     * Добавляет ID человека в набор сгенерированных ID.
     * @param person объект Person, чье ID добавляется в набор
     */
    public void addId(Person person) {
        generatedIds.add(person.getId());  // Добавляем ID человека в набор
    }

    /**
     * Получает набор всех сгенерированных ID.
     * @return набор сгенерированных ID
     */
    public HashSet<Long> getGeneratedIds() {
        return generatedIds;  // Возвращаем набор сгенерированных ID
    }
}
