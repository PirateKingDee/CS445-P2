/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package program.pkg2;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import org.lwjgl.input.Keyboard;

public class Program2 {
    
    private final int Y_MIN = 0;
    private final int Y_MAX = 1;
    private final int X_VAL = 2;
    //SR = Slope Recipical
    private final int SR = 3;
    //method: start
    //purpose: start the program.
    public void start(){
        try {
            //create Window
            createWindow();
             //Initialize GL
            initGL();
            //render
            render();
        } catch (LWJGLException ex) {
            ex.printStackTrace();
        }
    }
    
    //method: createWindow
    //purpose: create window for the program.
    public void createWindow() throws LWJGLException{
        Display.setFullscreen(false);
        Display.setDisplayMode(new DisplayMode(640,480));
        Display.setTitle("Program 2");       
        Display.create();
        
    }
    
    //method: initGL
    //purpose: intitalize properties for GL
    public void initGL(){
        glClearColor(0f, 0f, 0f, 0.0f);
        
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        //origin in center
        glOrtho(-320, 320, -240, 240, 1, -1);
        
        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
    }
    
    //method: render
    //purpose: render drawings
    public void render(){
        HashMap<ArrayList<Vertex>,ArrayList<String>> polygon = null;
        try{
            File file = new File("src/coordinates.txt");
            polygon = readPolygon(file);
        }
        catch(Exception e){
            
        }
        while(!Display.isCloseRequested()&&!Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
            Iterator<ArrayList<Vertex>> polygonIterator = polygon.keySet().iterator();
            try{
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                glLoadIdentity();
                while(polygonIterator.hasNext()){
                    ArrayList<Vertex> vertexList = polygonIterator.next();
                    ArrayList<String> atr = polygon.get(vertexList);
                    String[] color = atr.remove(0).split(" ");
                    glColor3f(Float.parseFloat(color[1]), Float.parseFloat(color[2]), Float.parseFloat(color[3]));
                    vertexList = transformation(vertexList, atr);
                    fillPolygon(getAllEdgeTable(vertexList));
                }
                Display.update();
                Display.sync(60);
            }
            catch(Exception ex){
                
            }     
        }    
        Display.destroy();
    }
    
    public ArrayList<Vertex> transformation(ArrayList<Vertex> vertexList, ArrayList<String> transforms){
        for(int i = 0; i < transforms.size(); i++){
            String[] curTransform = transforms.get(i).split(" ");
            char type = curTransform[0].charAt(0);
            switch(type){
                case 'r': 
                    for(int j = 0; j < vertexList.size(); j++){
                        vertexList.set(j, rotate(vertexList.get(j),Float.parseFloat(curTransform[1]),Float.parseFloat(curTransform[2]),Float.parseFloat(curTransform[3])));        
                    }
                    break;
                case 't':
                    for(int j = 0; j < vertexList.size(); j++){
                        vertexList.set(j, translate(vertexList.get(j),Float.parseFloat(curTransform[1]),Float.parseFloat(curTransform[2])));        
                    }
                    break;
                case 's':
                    for(int j = 0; j < vertexList.size(); j++){
                        vertexList.set(j, scale(vertexList.get(j),Float.parseFloat(curTransform[1]),Float.parseFloat(curTransform[2]),Float.parseFloat(curTransform[3]),Float.parseFloat(curTransform[4])));        
                    }
                    break;
            }
        }
        return vertexList;
    }
    public HashMap<ArrayList<Vertex>,ArrayList<String>> readPolygon(File file){
        HashMap<ArrayList<Vertex>,ArrayList<String>> polygon = new HashMap<>();
        ArrayList<Vertex> vertexList = new ArrayList<>();
        ArrayList<String> atr = null;
        try{
            Scanner readFile = new Scanner(file);
            char state = 'p';           
            while(readFile.hasNextLine()){
                String line = readFile.nextLine();
                String readLine[] = line.split(" ");
                
                if(state == 'p'){
                    if(readLine[0].equals( "P")){
                        atr = new ArrayList<>();
                        atr.add(line);
                        continue;
                    }
                    else if(readLine[0].equals( "T")){
                        state = 't';
                    }
                    else{
                        vertexList.add(new Vertex(Float.parseFloat(readLine[0]), Float.parseFloat(readLine[1])));
                        continue;
                    }
                }
                if(state == 't'){
                    if(readLine[0].equals("T")){
                        continue;
                    }
                    else if(readLine[0].equals("P")){
                        state = 'p';
                        polygon.put(vertexList, atr);
                        atr = new ArrayList<String>();
                        vertexList = new ArrayList<Vertex>();
                        atr.add(line);
                    }
                    else{
                        atr.add(line);
                    }
                }
            }
            polygon.put(vertexList,atr);               
        }
        catch(Exception e){
            e.printStackTrace();
        }


        return polygon;
    }
    
    public void fillPolygon(LinkedList<ArrayList<Float>> allEdges){
        LinkedList<ArrayList<Float>> global_edges;
        LinkedList<ArrayList<Float>> active_edges = new LinkedList<>();
        int parity = 0;
        global_edges = getGlobalEdges(allEdges);
        float scanLine = global_edges.getFirst().get(Y_MIN);
        while(!global_edges.isEmpty() && global_edges.getFirst().get(Y_MIN) == scanLine){
            active_edges.add(global_edges.removeFirst());
        }
        
        //start filling
        while(!active_edges.isEmpty()){
            while(!global_edges.isEmpty() && global_edges.getFirst().get(Y_MIN) == scanLine){
                active_edges.add(global_edges.removeFirst());
            }
            active_edges = (sortEdgesByX(active_edges));
            ListIterator<ArrayList<Float>> activeEdgesList;
            activeEdgesList= active_edges.listIterator();
            while(activeEdgesList.hasNext()){
                if(activeEdgesList.next().get(Y_MAX) == scanLine){
                    activeEdgesList.remove();
                }
            }
            activeEdgesList = active_edges.listIterator();
            float nextX;
            float curX = -320;
            while(activeEdgesList.hasNext()){
                
                nextX = activeEdgesList.next().get(X_VAL);
                while(curX < nextX){
                    
                    if(parity == 1){
                        draw(curX, scanLine);
                        System.out.println(curX+" "+scanLine);
                    }
                    curX += 1;
                }
                if(parity == 1){
                    parity = 0;
                }
                else{
                    parity = 1;
                }
                draw(curX, scanLine);
                System.out.println(curX+" "+scanLine);
            }
            scanLine += 1;
            activeEdgesList = active_edges.listIterator();
            while(activeEdgesList.hasNext()){
                ArrayList<Float> edge = activeEdgesList.next();
                edge.set(X_VAL, edge.get(X_VAL)+edge.get(SR));
            }
            
        }       
    }
    
    public LinkedList<ArrayList<Float>> getGlobalEdges(LinkedList<ArrayList<Float>> allEdges){
        LinkedList<ArrayList<Float>> globalEdges = new LinkedList<>();
        ListIterator<ArrayList<Float>> allEdgeList = allEdges.listIterator();
        while(allEdgeList.hasNext()){
            ArrayList<Float> curEdge = allEdgeList.next();
            if(curEdge.get(SR)!= Float.MAX_VALUE){
                globalEdges.add(curEdge);
            }
        }
        return sortGlobalEdge(globalEdges);
    }
    
    public LinkedList<ArrayList<Float>> getAllEdgeTable(ArrayList<Vertex> vertexList){
        LinkedList<ArrayList<Float>> all_edge = new LinkedList<>();
        Vertex first = vertexList.get(0);
        for(int i = 0; i < vertexList.size(); i++){
            ArrayList<Float> values = new ArrayList<>();
            Edge curEdge;
            if(i == vertexList.size()-1){
                curEdge = new Edge(vertexList.get(i), first);
            }
            else {
                curEdge = new Edge(vertexList.get(i), vertexList.get(i+1));
            }
            values.add(curEdge.getMinYVertex().getY());
            values.add(curEdge.getMaxYVertex().getY());
            values.add(curEdge.getMinYVertex().getX());
            values.add(curEdge.getSlopeRecipical()); 
            all_edge.add(values);
        }
        return all_edge;
    }
    
    public void draw(float x, float y){
        glBegin(GL_POINTS);
            glVertex2f(x,y);
        glEnd();
                
                
    }
    public Vertex translate(Vertex v, float x, float y){
        Matrix translateMatrix = new Matrix();
        translateMatrix.getTranslateMatrix(x, y);
        Matrix vertexMatrix = new Matrix();
        vertexMatrix.getVertexMatrix(v.getX(), v.getY());
        Matrix after = translateMatrix.multiplication(vertexMatrix);
        System.out.println(after.toString());
        return new Vertex(after.getMatrix()[0][0], after.getMatrix()[1][0]);
    }
    public Vertex rotate(Vertex v, float degree, float pivotX, float pivotY){
        Matrix rotationMatrix = new Matrix();
        Matrix vertexMatrix = new Matrix();
        rotationMatrix.getRotationMatrix(degree);
        vertexMatrix.getVertexMatrix(v.getX(), v.getY());
        Matrix after = rotationMatrix.multiplication(vertexMatrix);
        return new Vertex(after.getMatrix()[0][0], after.getMatrix()[1][0]);
    }
    public Vertex scale(Vertex v, float x, float y, float pivotX, float pivotY){
        Matrix scaleMatrix = new Matrix();
        Matrix vertexMatrix = new Matrix();
        scaleMatrix.getScaleMatrix(x, y);
        vertexMatrix.getVertexMatrix(v.getX(), v.getY());
        Matrix after = scaleMatrix.multiplication(vertexMatrix);
        return new Vertex(after.getMatrix()[0][0], after.getMatrix()[1][0]);
    }
    
    public LinkedList<ArrayList<Float>> sortEdgesByX(LinkedList<ArrayList<Float>> edges){
        LinkedList<ArrayList<Float>> sortedEdge = new LinkedList<>();
        ListIterator<ArrayList<Float>> iterator = edges.listIterator();
        while(iterator.hasNext()){
            ArrayList<Float> edge = iterator.next();
            if(sortedEdge.isEmpty()){
                    sortedEdge.add(edge);
                    continue;
            }
            else{
                ListIterator<ArrayList<Float>> second_iterator = sortedEdge.listIterator();
                boolean inserted = false;
                while(second_iterator.hasNext()){
                        ArrayList<Float> curEdge = second_iterator.next();
                        if(edge.get(X_VAL)< curEdge.get(X_VAL)){	
                                second_iterator.previous();
                                second_iterator.add(edge);	
                                inserted = true;			
                                break;
                        }
                }
                if(inserted == false){				
                        second_iterator.add(edge);		
                }
            }
        }
        return sortedEdge;
    }
    
    public LinkedList<ArrayList<Float>> sortGlobalEdge(LinkedList<ArrayList<Float>> globalEdge){
        LinkedList<ArrayList<Float>> sortedEdge = new LinkedList<>();
        ListIterator<ArrayList<Float>> iterator = globalEdge.listIterator();
        while(iterator.hasNext()){
            ArrayList<Float> edge = iterator.next();
            if(sortedEdge.isEmpty()){
                    sortedEdge.add(edge);
                    continue;
            }
            else{
                ListIterator<ArrayList<Float>> sortedEdgeList = sortedEdge.listIterator();
                boolean inserted = false;
                while(sortedEdgeList.hasNext()){	
                        ArrayList<Float> curEdge = sortedEdgeList.next();
                        if(edge.get(Y_MIN)< curEdge.get(Y_MIN)){	
                            sortedEdgeList.previous();
                            sortedEdgeList.add(edge);	
                            inserted = true;			
                            break;
                        }
                        else if(edge.get(Y_MIN) == curEdge.get(Y_MIN)){
                            if(edge.get(X_VAL)< curEdge.get(X_VAL)){	
                                sortedEdgeList.previous();
                                sortedEdgeList.add(edge);	
                                inserted = true;			
                                break;
                            }
                            else if(edge.get(X_VAL) == curEdge.get(X_VAL)){
                                if(edge.get(Y_MAX)< curEdge.get(Y_MAX)){	
                                    sortedEdgeList.previous();
                                    sortedEdgeList.add(edge);	
                                    inserted = true;			
                                    break;
                                }
                            }
                        }
                }
                if(inserted == false){				
                        sortedEdgeList.add(edge);		
                }
            }
        }
        return sortedEdge;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Program2 p2 = new Program2();
        p2.start();

    }

}
