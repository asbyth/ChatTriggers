package com.chattriggers.ctjs.libs;

import com.chattriggers.ctjs.utils.console.Console;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class FileLib {
    public static void write(String importName, String fileName, String toWrite) {
        try {
            FileUtils.write(new File("./mods/ChatTriggers/Imports/" + importName + "/" + fileName), toWrite);
        } catch (IOException exception) {
            Console.getConsole().printStackTrace(exception);
        }
    }

    public static String read(String importName, String fileName) {
        try {
            File file = new File("./mods/ChatTriggers/Imports/" + importName + "/" + fileName);
            BufferedReader br = new BufferedReader(new FileReader(file));

            if (!file.exists() || br.readLine() == null) {
                br.close();
                return null;
            }

            br.close();
            return FileUtils.readFileToString(file);
        } catch (IOException exception) {
            Console.getConsole().printStackTrace(exception);
        }

        return null;
    }

    public static String getUrlContent(String theUrl) {
        StringBuilder content = new StringBuilder();

        try {
            URL url = new URL(theUrl);
            URLConnection urlConnection = url.openConnection();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (Exception exception) {
            Console.getConsole().printStackTrace(exception);
        }

        return content.toString();
    }
}
