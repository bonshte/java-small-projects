package matrix;

import point.Point;

public class MatrixFiller implements Runnable{
    private Matrix matrix;
    private Point point;

    public MatrixFiller(Matrix matrix, Point point) {
        this.matrix = matrix;
        this.point = point;
    }

    @Override
    public void run() {
        matrix.insertValue(point);
    }
}
