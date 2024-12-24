package Story;

import Enum.Mood;
import Person.*;
import InAnimate.*;

import java.util.ArrayList;
import java.util.Random;

public class Story {
    public static void go() {
        Random random = new Random();


        Father father = new Father("Anton");
        Mother mother = new Mother("Larisa");
        ArrayList<Boys> sons = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            sons.add(new Boys("Вася " + i));
        }
        Daughter daughter = new Daughter("Nastya");
        Well well = new Well("Старый колодец", random.nextBoolean()); // Наличие воды задаётся один раз
        Pitcher pitcher = new Pitcher("Глиняный кувшин");

        // Описание начального состояния
        father.describe();
        mother.describe();
        mother.act();
        well.describe();
        pitcher.describe();
        father.act();

        // Сыновья идут за водой
        for (Boys son : sons) {
            son.moveTo("колодец");

            // Проверка: если кувшин уже наполнен, сыновья возвращаются домой
            if (pitcher.isFilled()) {
                System.out.println("Кувшин уже наполнен, сыновья возвращаются домой.");
                break;
            }

            // Проверка: если колодец пуст
            if (!well.getHasWater()) {
                System.out.println("Колодец пуст! Сыновья не могут наполнить кувшин.");
                break;
            }

            // Попытка наполнить кувшин
            if (random.nextBoolean()) {
                System.out.println("Кувшин падает в колодец!");
                pitcher.breakPitcher();
                break;
            } else {
                System.out.println(son.getName() + " успешно наполняет " + pitcher.getName() + " водой.");
                pitcher.fill();
                son.moveTo("дом");
            }
        }

        // Описание состояния кувшина
        pitcher.describe();

        // Реакция отца
        if (pitcher.isBroken() || !well.getHasWater()) {
            father.setMood(Mood.ANNOYED);
            father.curse(sons);
        } else {
            System.out.println("Отец доволен: сыновья принесли воду.");
            if (random.nextBoolean()) {
                daughter.recover();
            }
        }

        // Итоговое описание
        daughter.describe();
        for (Boys son : sons) {
            son.describe();
        }
    }
}


