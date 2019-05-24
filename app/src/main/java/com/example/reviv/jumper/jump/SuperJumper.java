package com.example.reviv.jumper.jump;

import com.example.reviv.jumper.framework.Screen;
import com.example.reviv.jumper.framework.impl.GLGame;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class SuperJumper extends GLGame {
    boolean firstTimeCreate=true;

    @Override
    public Screen getStartScreen() { return new MainMenuScreen(this); }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        super.onSurfaceCreated(gl,config);
        if(firstTimeCreate)
        {
            Settings.load(getFileIO());
            Assets.load(this);
            firstTimeCreate=false;
        }
        else
        {
            Assets.reaload();
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if(Settings.soundEnabled)
            Assets.music.pause();
    }
}
