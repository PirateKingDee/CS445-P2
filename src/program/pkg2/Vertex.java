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
public class Vertex {
    private float x;
    private float y;
    
    public Vertex(float x, float y){
        this.x = x;
        this.y = y;
    }
    public float getX(){
        return x;
    }
    public float getY(){
        return y;
    }
    public void setX(float x){
        this.x = x;
    }
//    public int compareX(Vertex v){
//        if(x > v.getX()) return 1;
//        else if( x < v.getX()) return -1;
//        else return 0;
//    }
//    public int compareY(Vertex v){
//        if(y > v.getY()) return 1;
//        else if( y < v.getY()) return -1;
//        else return 0;
//    }
}
