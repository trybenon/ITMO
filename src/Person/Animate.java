package Person;
import Entity.Entity;
import Interfaces.Actionable;
import Enum.Mood;

public abstract class Animate extends Entity implements Actionable {
    protected Mood mood;
    public Animate(String name, Mood mood){
        super(name);
        this.mood = mood;
    }

    public Mood getMood() {
        return mood;
    }

    public void setMood(Mood mood) {
        this.mood = mood;
    }

}
