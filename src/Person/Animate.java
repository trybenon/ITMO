package Person;
import Entity.Entity;
import Interfaces.Actionable;
import Enum.*;
import Interfaces.Christening;

public abstract class Animate extends Entity implements Actionable{
    protected Mood mood;
    protected Christen christen;
    public Animate(String name, Mood mood, Christen christen){
        super(name);
        this.mood = mood;
        this.christen = christen;
    }

    public Mood getMood() {
        return mood;
    }

    public void setMood(Mood mood) {
        this.mood = mood;
    }

    public Christen getChristen() {
        return christen;
    }

    public void setChristen(Christen christen) {this.christen = christen;}







}

