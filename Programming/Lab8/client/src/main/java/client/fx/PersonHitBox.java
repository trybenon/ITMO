package client.fx;

import shared.model.Person;

public class PersonHitBox {
    private Person person;
    private double x;
    private double y;
    private double radius;

    public PersonHitBox(Person person, double x, double y, double radius) {
        this.person = person;
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public boolean contains(double mouseX, double mouseY) {
        double dx = mouseX - this.x;
        double dy = mouseY - this.y;
        return dx * dx + dy * dy <= this.radius * this.radius;
    }

    public Person getPerson(){
        return this.person;
    }
}
