package com.ioter.clothesstrore.common.util;

import android.os.Environment;

import java.io.File;

public class VariableConstant
{

    public static final String APP_PACKAGE_MAIN = "com.ioter.clothesstrore";
    public static final String ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
            + "clothesstrore" + File.separator;
}