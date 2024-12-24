package Person;

import Enum.Mood;

public class Daughter extends Animate {
    protected boolean isWeak;
    public Daughter(String name){
        super(name, Mood.NONE);
        this.isWeak = true;
    }
    public void recover(){
        isWeak = false;
        setMood(Mood.ENJOY);
        System.out.println(name + " выздоравливает");
    }

    @Override
    public void act() {
        System.out.println(name + " лежит, наблюдая за семьей");
    }

    @Override
    public void describe() {
        System.out.println("Дочка " + name + (isWeak ? " маленькая и хилая" : " Sздоровая"));
    }
}
