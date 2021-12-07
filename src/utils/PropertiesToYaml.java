package utils;

import java.io.*;
import java.util.*;

public class PropertiesToYaml {

    private static String outputPath;

    public static void convertSingleFile(File file) throws IOException {
        if (file.exists() && file.getName().endsWith(".properties")) {
            outputPath = file.getParent() + "/out";
            p2y(file);
        }
    }

    public static void convertDir(String inputDir) throws IOException {
        File file = new File(inputDir);
        if (file.exists() && file.isDirectory()) {
            outputPath = file.getCanonicalPath() + "/out";
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (File inputFile : files) {
                    if (inputFile.getName().endsWith(".properties")) {
                        p2y(inputFile);
                    }
                }
            }
        }
    }

    private static void p2y(File file) throws IOException {
        Properties properties = getProperties(file);
        new File(outputPath).mkdir();
        String name = file.getName();
        FileWriter fileWriter = new FileWriter(outputPath + "/" + name.substring(0, name.lastIndexOf(".")) + ".yml");
        ArrayList<String> sortedKeys = sortKeys(properties);
        for (int i = 0; i < sortedKeys.size(); i++) {
            String[] thisKey = sortedKeys.get(i).split("\\.");
            if (i > 0) {
                String[] previousKey = sortedKeys.get(i - 1).split("\\.");
                for (int j = 0; j < Math.min(thisKey.length, previousKey.length); j++) {
                    if (thisKey[j].equals(previousKey[j])) {
                        thisKey[j] = "  ";
                    }else {
                        break;
                    }
                }
            }
            StringBuilder stringBuilder = new StringBuilder();
            for (String s : thisKey) {
                if (s.equals("  ")) {
                    stringBuilder.append(s);
                }else {
                    stringBuilder.append("  ");
                    fileWriter.write("\n" + stringBuilder + s + ":");
                }
            }
            fileWriter.write(" " + properties.getProperty(sortedKeys.get(i)));
        }
        fileWriter.close();
    }

    private static Properties getProperties(File file) throws IOException {
        Properties properties = new Properties();
        FileInputStream fileInputStream = new FileInputStream(file);
        properties.load(fileInputStream);
        fileInputStream.close();
        return properties;
    }

    private static ArrayList<String> sortKeys(Properties properties) {
        Set<Object> keySet = properties.keySet();
        ArrayList<String> keys = new ArrayList<>(keySet.size());
        keySet.forEach(e -> keys.add((String) e));
        keys.sort(String.CASE_INSENSITIVE_ORDER);
        return keys;
    }

}
