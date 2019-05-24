package com.example.reviv.jumper.framework.impl;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.example.reviv.jumper.framework.Audio;
import com.example.reviv.jumper.framework.FileIO;
import com.example.reviv.jumper.framework.Game;
import com.example.reviv.jumper.framework.Graphics;
import com.example.reviv.jumper.framework.Input;
import com.example.reviv.jumper.framework.Screen;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class GLGame extends Activity implements Game,GLSurfaceView.Renderer {
    enum GLGameState{
        initialized,
        paused,
        Finished,
        Running,
        idle
    }
    GLSurfaceView glView;
    GLGraphics glGraphics;
    Audio audio;
    Input input;
    FileIO fileIO;
    Screen screen;
    GLGameState state= GLGameState.initialized;
    Object stateChanged=new Object();
    long startTime=System.nanoTime();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        glView=new GLSurfaceView(this);
        glView.setRenderer(this);
        setContentView(glView);
        glGraphics=new GLGraphics(glView);
        fileIO=new AndroidFileIO(this);
        audio=new AndroidAudio(this);
        input= new AndroidInput(this,glView,1,1);
    }
    @Override
    public void onResume()
    {
        super.onResume();
        glView.onResume();
    }
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        glGraphics.setGL(gl);

        synchronized (stateChanged)
        {
            if(state== GLGameState.initialized)
                screen=getStartScreen();
            state= GLGameState.Running;
            screen.resume();
            startTime=System.nanoTime();
        }
    }
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {

    }
    public void onDrawFrame(GL10 gl)
    {
        GLGameState state=null;
        synchronized (stateChanged)
        {
            state=this.state;
        }
        if(state== GLGameState.Running)
        {
            float deltatime=(System.nanoTime()-startTime)/1000000000.0f;
            startTime=System.nanoTime();
            screen.update(deltatime);
            screen.present(deltatime);
        }
        if(state== GLGameState.paused)
        {
            screen.pause();
            synchronized (stateChanged)
            {
                this.state= GLGameState.idle;
                stateChanged.notifyAll();
            }
        }
        if(state== GLGameState.Finished)
        {
            screen.pause();
            screen.dispose();
            synchronized (stateChanged)
            {
                this.state= GLGameState.idle;
                stateChanged.notifyAll();
            }
        }
    }

    @Override
    public void onPause()
    {
        synchronized (stateChanged)
        {
            if(isFinishing())
                state= GLGameState.Finished;
            else
                state= GLGameState.paused;
            while (true)
            {
                try{
                    stateChanged.wait();
                    break;
                }catch(InterruptedException e){

                }
            }
        }
        glView.onPause();
        super.onPause();
    }
    public GLGraphics getGlGraphics()
    {
        return glGraphics;
    }

    @Override
    public Input getInput() {
        return input;
    }
    public FileIO getFileIO()
    {
        return fileIO;
    }

    @Override
    public Graphics getGraphics() {
        throw new IllegalStateException("We are using OpenGL!");
    }
    public Audio getAudio()
    {
        return audio;
    }
    public void setScreen(Screen newScreen)
    {
        if(screen==null)
            throw new IllegalArgumentException("Screen must not be null");
        this.screen.pause();
        this.screen.dispose();
        newScreen.resume();
        newScreen.update(0);
        this.screen=newScreen;
    }
    public Screen getCurrentScreen()
    {
        return screen;
    }
    public Screen getStartScreen()
    {
        return getStartScreen();
    }
}
