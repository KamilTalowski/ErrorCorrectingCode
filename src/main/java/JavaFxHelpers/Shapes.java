package JavaFxHelpers;

import javafx.scene.shape.Rectangle;

public class Shapes {
    public static Rectangle createRectangle(float x, float y, float width, float height){
        Rectangle rectangle = new Rectangle();

        rectangle.setX(x);
        rectangle.setY(y);
        rectangle.setWidth(width);
        rectangle.setHeight(height);
        return rectangle;
    }
}
