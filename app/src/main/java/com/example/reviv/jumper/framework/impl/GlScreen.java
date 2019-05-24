package com.example.reviv.jumper.framework.impl;

import com.example.reviv.jumper.framework.Game;
import com.example.reviv.jumper.framework.Screen;

public abstract class GlScreen extends Screen {
    protected final GLGraphics glGraphics;
    protected  final GLGame glGame;

    public GlScreen(Game game)
    {
        super(game);
        glGame=(GLGame)game;
        glGraphics=glGame.getGlGraphics();
    }
}
