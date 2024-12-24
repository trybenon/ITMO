package Story;

import Person.*;
import InAnimate.*;
import java.util.ArrayList;
import java.util.Random;
import Enum.*;

public class StoryManager {
    private Father father;
    private Mother mother;
    private ArrayList<Boys> sons;
    private Daughter daughter;
    private Well well;
    private Pitcher pitcher;
    private Random random;

    public StoryManager() {
        random = new Random();
        initializeCharacters();
        initializeObjects();
    }

    private void initializeCharacters() {
        father = new Father("Anton");
        mother = new Mother("Larisa");
        sons = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            sons.add(new Boys("Вася " + i));
        }
        daughter = new Daughter("Nastya");
    }

    private void initializeObjects() {
        well = new Well("Старый колодец", random.nextBoolean());
        pitcher = new Pitcher("Глиняный кувшин");
    }

    public void describeInitialState() {
        father.describe();
        mother.describe();
        mother.act();
        well.describe();
        pitcher.describe();
        father.act();
    }

    public void sonsFetchWater() {
        for (Boys son : sons) {
            son.moveTo("колодец");

            if (pitcher.isFilled()) {
                System.out.println("Кувшин уже наполнен, сыновья возвращаются домой.");
                return;
            }

            if (!well.getHasWater()) {
                System.out.println("Колодец пуст! Сыновья не могут наполнить кувшин.");
                return;
            }

            if (random.nextBoolean()) {
                System.out.println("Кувшин падает в колодец!");
                pitcher.breakPitcher();
                return;
            } else {
                System.out.println(son.getName() + " успешно наполняет " + pitcher.getName() + " водой.");
                pitcher.fill();
                son.moveTo("дом");
            }
        }
    }

    public void fatherReaction() {
        if (pitcher.isBroken() || !well.getHasWater()) {
            father.setMood(Mood.ANNOYED);
            father.curse(sons);
        } else {
            System.out.println("Отец доволен: сыновья принесли воду.");
            if (random.nextBoolean()) {
                daughter.recover();
            }
        }
    }

    public void describeFinalState() {
        daughter.describe();
        for (Boys son : sons) {
            son.describe();
        }
    }
}
