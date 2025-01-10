package Person;

import Enum.*;

import java.util.ArrayList;

public class Father extends Animate {
    public Father(String name) {
        super(name, Mood.HOPES, Christen.CHRISTEN);
    }

    public void curse(ArrayList<Boys> sons) {
        System.out.println((name + " " + mood.getDescription() + " и проклинает сыновей!"));
        for (Boys son : sons) {
            son.transform();
        }
    }

    @Override
    public void describe() {
        System.out.println("Отец семи сыновей " + name + " " + mood.getDescription() + " на рождение дочери");

    }

    @Override
    public void act() {
        System.out.println(name + " посылает сыновей за водой для крещения");

    }
}

