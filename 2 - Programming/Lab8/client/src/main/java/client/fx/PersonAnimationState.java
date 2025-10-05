package client.fx;


public class PersonAnimationState {
    private double y;
    private double velocity;
    private final double gravity = 0.2;
    private final double bounceFactor = 0.7;

    public PersonAnimationState(double initialY) {
        this.y = initialY;
        this.velocity = 0;
    }

    public void update() {
        velocity += gravity;
        y += velocity;


        if (y >= 0) {
            y = 0;
            velocity = -velocity * bounceFactor;
        }
    }

    public double getY() {
        return y;
    }
}
