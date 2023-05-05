package point;

import java.util.Objects;

public class Point {
    private int x;
    private int y;
    private int value;

    public Point(int x, int y, int value) throws InvalidPointException {
        if (x < 0 || y < 0) {
            throw new InvalidPointException("dimension cannot be negative");
        }
        this.x = x;
        this.y = y;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point point)) return false;
        return x == point.x && y == point.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                ", value=" + value +
                '}';
    }

}
