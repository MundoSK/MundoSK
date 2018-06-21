package com.pie.tlatoani.Core.Static;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Updating {
    private static int connectionTimeoutMS = 10000;
    private static int readTimeoutMS = 10000;

    private static void downloadUpdate(URL updateURL) throws IOException {
        File updateFolder = new File("update");
        updateFolder.mkdir();
        File updateFile = new File("update/mundosk.jar");
        FileUtils.copyURLToFile(updateURL, updateFile);
    }
}
