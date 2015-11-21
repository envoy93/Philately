package com.philately;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

/**
 * Created by kirill on 08.11.2015.
 */
public class Utility {
    private static Utility instance;
    private String path;

    public static synchronized Utility getInstance() {
        if (instance == null) {
            instance = new Utility();
        }
        return instance;
    }

    private Utility() {
        URL url = MainApp.class.getProtectionDomain().getCodeSource().getLocation(); //Gets the path
        String jarPath = null;
        try {
            jarPath = URLDecoder.decode(url.getFile(), "UTF-8"); //Should fix it to be read correctly by the system
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        path = new File(jarPath).getParentFile().getPath(); //Path of the jar
    }

    public String getAppPath() {
        return path;
    }

    public String getFullPathToImage(String image) {
        if (image == null) {
            image = "default.png";
        }
        return "file:" + getAppPath() + "\\additionalAppResources\\" + image;
    }
}
