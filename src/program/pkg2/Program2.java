/******************************************************************************
 *      file: Program2.java
 *      author: Andy Liang
 *      class: CS 445 - Computer Graphic
 *      
 *      assignment: Program 2
 *      date last modified: 4/28/17
 *      
 *      purpose: This program reads in a text file that contains the information
 *      about multiple different polygons. According to its vertex, transformation,
 *      and color, fill the polygon and display it to a 640 x 320 screen.
 *      
 *      note: coordinates.txt need to be save in src folder.
 ******************************************************************************/
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
    //purpose: render drawings. 
    public void render(){
        //Use HashMap to stores multiple Polygons. A list of vertex matches with a list of transformations
        HashMap<ArrayList<Vertex>,ArrayList<String>> polygon = null;
        try{
            //read coordinates text file and stores the polygon in the Hash Map
            File file = new File("src/coordinates.txt");
            polygon = readPolygon(file);
        }
        catch(Exception e){
            
        }
        //run program until press esc key or click close
        while(!Display.isCloseRequested()&&!Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
            Iterator<ArrayList<Vertex>> polygonIterator = polygon.keySet().iterator();
            try{
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                glLoadIdentity();
                //Iterates through the polygon Hash Map
                while(polygonIterator.hasNext()){
                    //get the vertex list of the polygon
                    ArrayList<Vertex> vertexList = polygonIterator.next();
                    //get the transformation list of the polygon
                    ArrayList<String> atr = polygon.get(vertexList);
                    //get the color from the first string of the transformation list
                    String[] color = atr.remove(0).split(" ");
                    //set the color
                    glColor3f(Float.parseFloat(color[1]), Float.parseFloat(color[2]), Float.parseFloat(color[3]));
                    //transform the all vertex
                    vertexList = transformation(vertexList, atr);
                    //fill the the polygon by getting the all edge table and pass to fill polygon function
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
    
    //method: transformation
    //purpose: transform a list of vertexs and return the new vertexs
    public ArrayList<Vertex> transformation(ArrayList<Vertex> vertexList, ArrayList<String> transforms){
        for(int i = 0; i < transforms.size(); i++){
            //split the string to string array
            String[] curTransform = transforms.get(i).split(" ");
            //get the first character of the string array
            char type = curTransform[0].charAt(0);
            //according to the first character, perform the corresponding transformation with the information in the string
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
    
    //method: readPolygon
    //purpose: read coordinate file and form a polygon hash map 
    public HashMap<ArrayList<Vertex>,ArrayList<String>> readPolygon(File file){
        //initiallize polygon hash map, vertex arraylist
        HashMap<ArrayList<Vertex>,ArrayList<String>> polygon = new HashMap<>();
        ArrayList<Vertex> vertexList = new ArrayList<>();
        //atr standfor  attributes, use to store transformation list
        ArrayList<String> atr = null;
        try{
            //read the file
            Scanner readFile = new Scanner(file);
            //state p stand for reading veretx of polygon
            char state = 'p';           
            while(readFile.hasNextLine()){
                String line = readFile.nextLine();
                String readLine[] = line.split(" ");
                //check if in reading vertex state, store vertex to vertex list, when read 't', change state
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
                //in state t, store transformation string to atr array list
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
    
    //method: fillPoly
    //purpose: use the all edge table to fill the polygon 
    public void fillPolygon(LinkedList<ArrayList<Float>> allEdges){
        //get global edge table from all edge table
        LinkedList<ArrayList<Float>>global_edges = getGlobalEdges(allEdges);
        //create active edge table
        LinkedList<ArrayList<Float>> active_edges = new LinkedList<>();
        //initiallize parity to be even
        int parity = 0;
        //initialize scan line to be the y minimum of the first edge in global edge
        float scanLine = global_edges.getFirst().get(Y_MIN);
        //remove edge from global edge table that has y-minimum equals to the scan line and store that to active edge table
        while(!global_edges.isEmpty() && global_edges.getFirst().get(Y_MIN) == scanLine){
            active_edges.add(global_edges.removeFirst());
        }
        
        //start filling
        while(!active_edges.isEmpty()){
            
            ListIterator<ArrayList<Float>> activeEdgesList;
            activeEdgesList = active_edges.listIterator();
            //initiallize the current x at x min.
            float curX = -320;
            //float to store the next x value in the active edge table
            float nextX;
            //iterates through active edge table
            while(activeEdgesList.hasNext()){
                //get the next x value in the active edge table
                nextX = activeEdgesList.next().get(X_VAL);
                //iterate x until the next x value in the active edge table
                while(curX < nextX){
                    //if parity is 1, draw on the pixel
                    if(parity == 1){
                        draw(curX, scanLine);
                    }
                    //increment x by one
                    curX += 1;
                }
                //if parity == 1, set it to 0
                if(parity == 1){
                    parity = 0;
                }
                //if parity == 0, set it to 1
                else{
                    parity = 1;
                }
                //draw the last pixel
                draw(curX, scanLine);
            }
            //increment scan line
            scanLine += 1;
            activeEdgesList = active_edges.listIterator();
            //updates x value in active edge table
            while(activeEdgesList.hasNext()){
                ArrayList<Float> edge = activeEdgesList.next();
                edge.set(X_VAL, edge.get(X_VAL)+edge.get(SR));
            }
            //if y-max in active edge table == to scanline, remove that edge to active edge table
            while(!global_edges.isEmpty() && global_edges.getFirst().get(Y_MIN) == scanLine){
                active_edges.add(global_edges.removeFirst());
            }
            activeEdgesList= active_edges.listIterator();
            //remove edge in active edge table that has y max == to scan line
            while(activeEdgesList.hasNext()){
                if(activeEdgesList.next().get(Y_MAX) == scanLine){
                    activeEdgesList.remove();
                }
            }
            //sort active edge table by x value
            active_edges = (sortEdgesByX(active_edges));
        }       
    }
    
    //method: getVlobalEdges
    //purpose: take all edge table and return a global edge table
    public LinkedList<ArrayList<Float>> getGlobalEdges(LinkedList<ArrayList<Float>> allEdges){
        LinkedList<ArrayList<Float>> globalEdges = new LinkedList<>();
        ListIterator<ArrayList<Float>> allEdgeList = allEdges.listIterator();
        //store all edges to global edge table except the edge with slope == =
        while(allEdgeList.hasNext()){
            ArrayList<Float> curEdge = allEdgeList.next();
            if(curEdge.get(SR)!= Float.MAX_VALUE){
                globalEdges.add(curEdge);
            }
        }
        //sort the global edge list according to y-minimum and return it
        return sortGlobalEdge(globalEdges);
    }
    
    //take a list of vertex and store them to all edge table
    public LinkedList<ArrayList<Float>> getAllEdgeTable(ArrayList<Vertex> vertexList){
        LinkedList<ArrayList<Float>> all_edge = new LinkedList<>();
        Vertex first = vertexList.get(0);
        for(int i = 0; i < vertexList.size(); i++){
            ArrayList<Float> values = new ArrayList<>();
            Edge curEdge;
            //if its the last vertex, form an edge with the first vertex
            if(i == vertexList.size()-1){
                curEdge = new Edge(vertexList.get(i), first);
            }
            else {
                curEdge = new Edge(vertexList.get(i), vertexList.get(i+1));
            }
            //store the y-min, x-value, y-max, recipical of slope to vertex array list
            values.add(curEdge.getMinYVertex().getY());
            values.add(curEdge.getMaxYVertex().getY());
            values.add(curEdge.getMinYVertex().getX());
            values.add(curEdge.getSlopeRecipical()); 
            //store the arraylist to all edge linkedlist
            all_edge.add(values);
        }
        return all_edge;
    }
    
    //method: draw
    //purpose: function to draw point
    public void draw(float x, float y){
        glBegin(GL_POINTS);
            glVertex2f(x,y);
        glEnd();
                
                
    }
    
    //method: translate
    //purpose: perfom a tranlation on the vertex and return the new vertex
    public Vertex translate(Vertex v, float x, float y){
        //get translation 3x3 matrix
        Matrix translateMatrix = new Matrix();
        translateMatrix.getTranslateMatrix(x, y);
        //get vertex matrix (3x1)
        Matrix vertexMatrix = new Matrix();
        vertexMatrix.getVertexMatrix(v.getX(), v.getY());
        //multiply translation matrix with vertex matrix
        Matrix product = translateMatrix.multiplication(vertexMatrix);
        //get the vertex from product matrix and return it
        return new Vertex(product.getMatrix()[0][0], product.getMatrix()[1][0]);
    }
    
    //method: rotate
    //purpose: perfom a rotation on the vertex and return the new vertex
    public Vertex rotate(Vertex v, float degree, float pivotX, float pivotY){
        //get rotation 3x3 matrix
        Matrix rotationMatrix = new Matrix();
        //get vertex matrix (3x1)
        Matrix vertexMatrix = new Matrix();
        rotationMatrix.getRotationMatrix(degree);
        vertexMatrix.getVertexMatrix(v.getX(), v.getY());
        //multiply rotation matrix with vertex matrix
        Matrix after = rotationMatrix.multiplication(vertexMatrix);
        //get the vertex from product matrix and return it
        return new Vertex(after.getMatrix()[0][0], after.getMatrix()[1][0]);
    }
    //method: scale
    //purpose: perfom a scale on the vertex and return the new vertex
    public Vertex scale(Vertex v, float x, float y, float pivotX, float pivotY){
        //get rotation 3x3 matrix
        Matrix scaleMatrix = new Matrix();
        //get vertex matrix (3x1)
        Matrix vertexMatrix = new Matrix();
        scaleMatrix.getScaleMatrix(x, y);
        vertexMatrix.getVertexMatrix(v.getX(), v.getY());
        //multiply scale matrix with vertex matrix
        Matrix after = scaleMatrix.multiplication(vertexMatrix);
        //get the vertex from product matrix and return it
        return new Vertex(after.getMatrix()[0][0], after.getMatrix()[1][0]);
    }
    
    //method: sortEdgeByX
    //purpose: insertion sort comparing the x value of edges
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
    
    //method: sortGlobalEdge
    //purpose: insertion sort comparing the y minimum value of edges
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
    
    //method: main
    //purpose: start the fill poly program
    public static void main(String[] args) {
        // TODO code application logic here
        Program2 p2 = new Program2();
        p2.start();
    }

}
