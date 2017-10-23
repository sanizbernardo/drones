package utils;

import java.io.InputStream;
import java.util.Scanner;

public class Utils {

    /**
     * Read a file given by filename. It will start searching in the folder marked a resource location!
     * @param fileName
     *        The name of the file (with path of necessary)
     * @return
     *        The content of the file as a String
     * @throws Exception
     *        If something goes wrong
     */
    public static String loadResource(String fileName) throws Exception {
        String result;
        try (InputStream in = Utils.class.getClass().getResourceAsStream(fileName);
             Scanner scanner = new Scanner(in, "UTF-8")) {
            result = scanner.useDelimiter("\\A").next();
        }
        return result;
    }

}
