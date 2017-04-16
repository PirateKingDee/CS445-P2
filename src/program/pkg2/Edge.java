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
public class Edge {
    private Vertex v1;
    private Vertex v2;
    private Vertex maxYVertex;
    private Vertex minYVertex;
    private float dy;
    private float dx;
    
    public Edge(Vertex v1, Vertex v2){
        this.v1 = v1;
        this.v2 = v2;
        if(v1.getY()>v2.getY()){
            maxYVertex = v1;
            minYVertex = v2;
        }
        else{
            maxYVertex = v2;
            minYVertex = v1;
        }
        dy = v2.getY() - v1.getY();
        dx = v2.getX() - v1.getX();
    }
    
    
    public Vertex getMaxYVertex(){
        return maxYVertex;
    }
    
    public Vertex getMinYVertex(){
        return minYVertex;
    }
    
    public float getSlope(){
        return dy/dx;
    }
    
    public float getSlopeRecipical(){
        //except dy = 0
        return dx/dy;
    }
    
    public void setXValue(float x){
        getMinYVertex().setX(x);
    }
    
    @Override
    public String toString(){
        return "\nY-min: "+getMinYVertex().getY()+"\nY-max: "+getMaxYVertex().getY()+"\nX-Val: "+getMinYVertex().getX()+"\n1/m: "+getSlopeRecipical()+" ";
    }
    
}
