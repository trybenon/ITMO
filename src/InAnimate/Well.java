package InAnimate;

public class Well extends InAnimate {
    private boolean hasWater;

    public Well(String name, boolean hasWater) {
        super(name);
        this.hasWater = hasWater;
    }

    @Override
    public void interact() {
        System.out.println(name + (hasWater ? " полон воды." : " пуст."));
    }

    @Override
    public void describe() {
        System.out.println("Недалеко от дома находится " + name + ".");
    }

    public void setHasWater(boolean hasWater) {
        this.hasWater = hasWater;
    }

    public Boolean getHasWater() {
        return hasWater;
    }
}
