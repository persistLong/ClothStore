// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   TagRecord.java

package com.ioter.clothesstrore.data;


import java.io.Serializable;

public class TagRecord implements Serializable
{

    public long getLastTime()
    {
        return lastTime;
    }

    public void setLastTime(long lastTime)
    {
        this.lastTime = lastTime;
    }

    private long lastTime;

    private String epcString;

    public boolean isPutBack()
    {
        return isPutBack;
    }

    public void setPutBack(boolean putBack)
    {
        isPutBack = putBack;
    }

    private boolean isPutBack;


    public String getEpcString()
    {
        return epcString;
    }

    public void setEpcString(String epcString)
    {
        this.epcString = epcString;
    }

}
