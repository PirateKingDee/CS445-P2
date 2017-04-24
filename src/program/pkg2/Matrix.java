/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package program.pkg2;

/**
 *
 * @author andyliang
 */
import java.util.Arrays;
import java.util.Random;

public class Matrix {
    float[][] matrix;
    int row;
    int col;
    
    public Matrix(){
        matrix = null;
        row = 0;
        col = 0;
    }
    public Matrix( float[][] m){
        row = m.length;
        col = m[0].length;
        matrix = new float[row][col];
        for(int i=0; i<row; i++){
            for(int j=0; j<col; j++){
                matrix[i][j] = m[i][j];
            }
        }
    }
    
    public float[][] getVertexMatrix(float x, float y){
        float[][] tm = {{x},{y},{1}};
        row = tm.length;
        col = tm[0].length;
        matrix = tm;
        return tm;
    }
    public float[][] getTranslateMatrix(float x, float y){
        float[][] tm = {{1,0,x},{0,1,y},{0,0,1}};
        row = tm.length;
        col = tm[0].length;
        matrix = tm;
        return tm;
    }
    public float[][] getRotationMatrix(float degree){
        double radian = degree*Math.PI/180;
        float[][] tm = {{(float)(Math.cos(radian)), -(float)Math.sin(radian), 0},{(float)Math.sin(radian), (float)Math.cos(radian), 0},{0,0,1}};
        row = tm.length;
        col = tm[0].length;
        matrix = tm;
        return tm;
    }
    public float[][] getScaleMatrix(float x, float y){
        
        float[][] tm = {{x,0,0},{0,y,0},{0,0,1}};
        row = tm.length;
        col = tm[0].length;
        matrix = tm;
        return tm;
    }
    public float[][] getMatrix(){
        return matrix;
    }
    public int getRowSize(){
        return row;
    }
    public int getColSize(){
        return col;
    }
    public float[] getRow(int row){
        return matrix[row];
    }
    public float[] getCol(int col){
        float[] colArray = new float[this.row];
        for(int i=0; i<colArray.length; i++){
            colArray[i] = matrix[i][col];
        }
        return colArray;
    }
    public Matrix multiplication(Matrix rhs){
        float[][] answer = new float[row][rhs.getColSize()];
        for(int i=0; i<answer.length; i++){
            for(int j=0; j<answer[0].length; j++){
                answer[i][j] = getRowTimesCol(this.getRow(i),rhs.getCol(j));
            }
        }
        return new Matrix(answer);
    }
    public int getRowTimesCol(float[] row, float[] col){
        int answer=0;
        for(int i=0; i<row.length;i++){
            answer += row[i]*col[i];
        }
        return answer;
    }
    public String toString(){
        StringBuilder str = new StringBuilder();
        for(int i=0; i<row; i++){
            str.append(Arrays.toString(matrix[i])+"\n");
        }
        return str.toString();
    }
}
