package Entity;

public abstract class Entity {
    public String name;
    public Entity(String name){
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public abstract void describe();
}
