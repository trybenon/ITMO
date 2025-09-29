package server.collection;

import shared.model.Person;

import java.util.Comparator;

/**
 * Компаратор для сортировки объектов {@link Person} по росту.
 * Если рост одинаковый, сравнение идет по весу.
 */
public class HeightComparator implements Comparator<Person> {

    /**
     * Сравнивает двух людей по росту.
     * Если рост одинаковый, сравнивает по весу.
     *
     * @param p1 первый человек
     * @param p2 второй человек
     * @return результат сравнения
     */
    @Override
    public int compare(Person p1, Person p2) {
        int result = Integer.compare(p1.getHeight(), p2.getHeight());
        return (result != 0) ? result : Long.compare(p1.getWeight(), p2.getWeight());
    }
}
