package InAnimate;

public record Well(String name, boolean hasWater) {
    public void interact() {
        System.out.println(name + (hasWater ? " полон воды." : " пуст."));
    }

    public void describe() {
        System.out.println("Недалеко от дома находится " + name + ".");
    }
}
