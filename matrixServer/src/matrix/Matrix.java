package matrix;

import point.Point;

public class Matrix {
    private int rows;
    private int cols;
    private int[][] matrix;

  public Matrix(int rows, int cols) {
      if (rows < 1 || cols < 1){
          throw new IllegalArgumentException("dimensions must be positive");
      }
      this.rows = rows;
      this.cols = cols;
      this.matrix = new int[rows][cols];
  }



  public void insertValue(Point point) {
        matrix[point.getX()][point.getY()] = point.getValue();

  }

  public String getMatrixAsString(){
      StringBuilder matrixInString = new StringBuilder();
      for (int i = 0 ; i < rows ; ++i) {
          for (int j = 0 ; j < cols ; ++j){
              matrixInString.append(String.valueOf(matrix[i][j] + " "));
          }
          matrixInString.append('\n');
      }
      return matrixInString.toString();
  }

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }
}
