package client.fx;


import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import shared.model.Person;


public class PersonVisual {
    private static double canvasWidth;
    private static double canvasHeight;



    public static void draw(GraphicsContext gc, double size, double x, double y, Color color) {
        double scaleFactor = size / 200.0;
        double bodyWidth = canvasWidth * 0.08 * scaleFactor; // Ширина тела
        double bodyHeight = canvasHeight * 0.15 * scaleFactor; // Высота тела
        double headRadius = bodyWidth * 0.6; // Радиус головы
        double armWidth = bodyWidth * 0.3; // Ширина рук
        double armHeight = bodyHeight * 0.5; // Длина рук
        double legWidth = bodyWidth * 0.3; // Ширина ног
        double legHeight = bodyHeight * 0.5; // Длина ног
        double arcWidth = bodyWidth * 0.2;
        double arcHeight = bodyHeight * 0.2;

        // Рисуем голову
        gc.setFill(color);
        gc.fillOval(x - headRadius , y - bodyHeight - headRadius, headRadius * 2, headRadius * 2);

        // Рисуем тело
        gc.fillRoundRect(x - bodyWidth / 2, y - bodyHeight, bodyWidth, bodyHeight, arcWidth, arcHeight);

        // Рисуем левую руку
        gc.save();
        gc.translate(x - bodyWidth / 2 - armWidth / 2, y - bodyHeight + armHeight/3);
        gc.rotate(-30);
        gc.fillRoundRect(-armWidth / 2, -armHeight / 2, armWidth, armHeight, arcWidth, arcHeight);
        gc.restore();

        // Рисуем правую руку
        gc.save();
        gc.translate(x + bodyWidth / 2 + armWidth / 2, y - bodyHeight + armHeight / 3);
        gc.rotate(30);
        gc.fillRoundRect(-armWidth / 2, -armHeight / 2, armWidth, armHeight, arcWidth, arcHeight);
        gc.restore();

        // Рисуем левую ногу
        gc.fillRoundRect(x - bodyWidth / 4 - legWidth / 2, y - bodyWidth / 4, legWidth, legHeight, arcWidth, arcHeight);

        // Рисуем правую ногу
        gc.fillRoundRect(x + bodyWidth / 4 - legWidth / 2, y - bodyWidth / 4, legWidth, legHeight, arcWidth, arcHeight);

        // Рисуем глаза (маленькие черные овалы)
        gc.setFill(Color.BLACK);
        gc.fillOval(x - headRadius * 0.4, y - bodyHeight - headRadius * 0.5, headRadius * 0.2, headRadius * 0.2);
        gc.fillOval(x + headRadius * 0.2, y - bodyHeight - headRadius * 0.5, headRadius * 0.2, headRadius * 0.2);
    }

    public static void setCanvasWidth(double canvasWidth) {
        PersonVisual.canvasWidth = canvasWidth;
    }

    public static void setCanvasHeight(double canvasHeight) {
        PersonVisual.canvasHeight = canvasHeight;
    }
    }