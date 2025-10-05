package collection;

import model.Person;

/**
 * Класс для валидации данных объекта {@link Person}.
 */
public class Validator {
    private IdGenerator idGenerator;

    public IdGenerator getIdGenerator() {
        return this.idGenerator;
    }

    /**
     * Метод для валидации объекта {@code Person}.
     * Проверяет, что все обязательные поля корректны, и генерирует новый ID, если он равен 0.
     * Если объект валиден, возвращает его, иначе — {@code null}.
     *
     * @param person Объект {@code Person}, который необходимо проверить.
     * @return Валидированный объект {@code Person} или {@code null}, если данные некорректны.
     */
    public Person getValidatedElement(Person person) {
        // Проверка на валидность полей объекта Person
        if (person.getId() <= 0 ||
                person.getName() == null || person.getName().isBlank() ||
                person.getCoordinates() == null || person.getCoordinates().getX() > 59 ||
                person.getHeight() <= 0 ||
                person.getWeight() <= 0 ||
                (person.getPassportID() != null && (person.getPassportID().length() < 6 || person.getPassportID().length() > 41))) {
            return null;  // Если данные некорректны, возвращаем null
        } else {
            // Генерация ID, если он равен 0
            if (person.getId() == 0) {
                person.setId(new IdGenerator().generateId());  // Генерация нового ID
            }
            return person;  // Возвращаем валидированный объект Person
        }
    }
}
