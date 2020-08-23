package pw.cdmi.starlink.core.math;

/**
 * 使用Java实现矩阵计算
 */
public class MatrixMath {
    public static final int OPEROPERATION_ADD = 1;
    public static final int OPEROPERATION_SUB = 2;
    public static final int OPEROPERATION_MUL = 3;
    /**
     * 矩阵加法运算
     * @param matrix_a 矩阵a
     * @param matrix_b 矩阵b
     * @return result1 运算合法，返回结果
     */
    public static double[][] additive(double[][] matrix_a, double[][] matrix_b){
        double[][] result1 = new double[matrix_a.length][matrix_a[0].length];
        if(legalOperation(matrix_a, matrix_b, OPEROPERATION_ADD)){
            for(int i = 0; i < matrix_a.length; i++){
                for(int j = 0; j < matrix_a[0].length; j++){
                    result1[i][j] = matrix_a[i][j] + matrix_b[i][j];
                }
            }
        }
        return result1;
    }
    /**
     * 矩阵减法运算
     * @param matrix_a 矩阵a
     * @param matrix_b 矩阵b
     * @return result2 运算合法，返回结果
     */
    public static double[][] subtraction(double[][] matrix_a, double[][] matrix_b){
        double[][] result2 = new double[matrix_a.length][matrix_a[0].length];
        if(legalOperation(matrix_a, matrix_b, OPEROPERATION_SUB)){
            for(int i = 0; i < matrix_a.length; i++){
                for(int j = 0; j < matrix_a[0].length; j++){
                    result2[i][j] = matrix_a[i][j] - matrix_b[i][j];
                }
            }
        }
        return result2;
    }
    /**
     * 矩阵乘法运算a 矩阵与矩阵相乘
     * @param matrix_a 矩阵a
     * @param matrix_b 矩阵b
     * @return result3 运算合法，返回结果; null 运算不合法
     */
    public static double[][] multiplication_a(double[][] matrix_a, double[][] matrix_b){
        double[][] result3 = new double[matrix_a.length][matrix_b[0].length];
        if(legalOperation(matrix_a, matrix_b, OPEROPERATION_MUL)){
            for(int i = 0; i < matrix_a.length; i++){
                for(int j = 0;j < matrix_a[0].length; j++){
                    result3[i][j] = calculateSingleResult(matrix_a, matrix_b, i, j);
                }
            }
            return result3;
        }else{
            return null;
        }
    }
    /**
     * 矩阵乘法运算b 矩阵的数乘
     * @param matrix_a 矩阵a
     * @param n 数n
     * @return result4 运算合法，返回结果
     */
    public static double[][] multiplication_b(double[][] matrix_a, double n){
        double[][] result4 = new double[matrix_a.length][matrix_a[0].length];
        for(int i = 0; i < matrix_a.length; i++){
            for(int j = 0; j < matrix_a[0].length; j++){
                result4[i][j] = n * matrix_a[i][j];
            }
        }
        return result4;
    }
    /**
     * 矩阵乘法a中result每个元素的单一运算
     * @param matrix_a 矩阵a
     * @param matrix_b 矩阵b
     * @param row 参与单一运算的行标
     * @param col 参与单一运算的列标
     * @return result 运算结果
     */
    public static double calculateSingleResult(double[][] matrix_a, double[][] matrix_b, int row, int col){
        int result = 0;
        for(int i = 0; i < matrix_a[0].length; i++){
            result += matrix_a[row][i] * matrix_b[i][col];
        }
        return result;
    }
    /**
     * 判断矩阵是否可以进行合法运算
     * @param matrix_a 矩阵a
     * @param matrix_b 矩阵b
     * @param type 判断运算类型，是加法，减法，还是乘法运算
     * @return legal true 运算合法; false 运算不合法
     */
    private static boolean legalOperation(double[][] matrix_a, double[][] matrix_b, int type){
        boolean legal = true;
        if(type == OPEROPERATION_ADD || type == OPEROPERATION_SUB){
            if(matrix_a.length != matrix_b.length || matrix_a[0].length != matrix_b[0].length){
                legal = false;
            }
        }else if(type == OPEROPERATION_MUL){
            if(matrix_a.length != matrix_b[0].length){
                legal = false;
            }
        }
        return legal;
    }

    public static void main(String[] args) {
        double a[][] = new double[][]{{1,2},{3,4}};
        double b[][] = new double[][]{{5,6},{7,8}};
        System.out.println("addition two matrix");
        double[][] result = MatrixMath.additive(a, b);
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[0].length; j++) {
                System.out.print("\t" + result[i][j]);
            }
            System.out.println();
        }
        System.out.println("substract two matrix");
        double[][] result1 = MatrixMath.subtraction(a, b);
        for (int i = 0; i < result1.length; i++) {
            for (int j = 0; j < result1[0].length; j++) {
                System.out.print("\t" + result1[i][j]);
            }
            System.out.println();
        }
        System.out.println("multiplex one matrix");
        result = MatrixMath.multiplication_b(a, 3);
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[0].length; j++) {
                System.out.print("\t" + result[i][j]);
            }
            System.out.println();
        }
        System.out.println("multiplex two matrix");
        result = MatrixMath.multiplication_a(a, b);
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[0].length; j++) {
                System.out.print("\t" + result[i][j]);
            }
            System.out.println();
        }
    }
}
