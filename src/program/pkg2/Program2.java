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

public class Program2 {
    
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
        
        glOrtho(0, 640, 0, 480, 1, -1);
        
        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
    }
    
    //method: render
    //purpose: render drawings
    public void render(){
        while(!Display.isCloseRequested()){
            try{
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                glLoadIdentity();
                
                Display.update();
                Display.sync(60);
            }
            catch(Exception ex){
                
            }
               
        }    
        Display.destroy();
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
