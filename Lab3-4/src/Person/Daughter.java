package Person;

import Enum.*;


public class Daughter extends Animate {
    protected boolean isWeak;
    public Daughter(String name){
        super(name, Mood.NONE, Christen.NOTCHRISTEN);
        this.isWeak = true;
    }
    public void recover(){
        isWeak = false;
        setMood(Mood.ENJOY);
        System.out.println(name + " выздоравливает");
    }

    public void christen() {
        if (getChristen() == Christen.CHRISTEN) {
            System.out.println("Уже крещена.");
        } else {
            setChristen(Christen.CHRISTEN);
            System.out.println("Крещена.");
        }
    }
    @Override
    public void act() {
        System.out.println(name + " лежит, наблюдая за семьей");
    }

    @Override
    public void describe() {
        System.out.println("Дочка " + name + (isWeak ? " маленькая и хилая и "+ getChristen().getDescription() : " здоровая и "+ getChristen().getDescription()));
    }
}
