package Person;

import Enum.*;

public class Mother extends Animate {

    public Mother(String name){
        super(name, Mood.ENJOY, Christen.CHRISTEN);

    }
    @Override
    public void act() {
        System.out.println("Мамa " + name + " рожает дочку, " + mood.getDescription());
    }

    @Override
    public void describe() {
        System.out.println("Мама " + name + " дает надежду отцу ");

    }
}
