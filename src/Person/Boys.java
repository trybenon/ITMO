package Person;

import Enum.*;
import Interfaces.Movable;

public class Boys extends Animate implements Movable {

    private boolean isTransforamtion;

    public Boys(String name) {
        super(name, Mood.ENJOY, Christen.CHRISTEN);
        this.isTransforamtion = false;
    }

    public void transform(){
        isTransforamtion = true;
        setMood(Mood.ANNOYED);
        System.out.println(name + " превращается в ворона!");
    }
    @Override
    public void act() {
        if (!isTransforamtion){
            System.out.println(name + "Играет с братьями.");
        }
        else {
            System.out.println(name + "летает в небе.");
        }
    }
    @Override
    public void moveTo(String location) {
        if (!isTransforamtion) {
            System.out.println(name + " идёт в " + location + ".");
        } else {
            System.out.println(name + " летит в " + location + ".");
        }
    }
    @Override
    public void describe() {
        System.out.println("Это сын по имени: " + name + (isTransforamtion ? ", теперь он ворон." : ", Он играет с братьями."));
    }
}
