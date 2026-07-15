package com.library.utilities;

import java.util.ArrayList;
import java.util.List;

public class CsvUtils {

    public static String toCsvLine(String... values) {

        List<String> escapedValues = new ArrayList<>();

        for (String value : values) {

            escapedValues.add(escape(value));

        }

        return String.join(",", escapedValues);
    }

    public static List<String> parseLine(String line) {

        List<String> values = new ArrayList<>();

        StringBuilder current = new StringBuilder();

        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {

            char ch = line.charAt(i);

            if (ch == '"') {

                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {

                    current.append('"');

                    i++;

                } else {

                    inQuotes = !inQuotes;

                }

            } else if (ch == ',' && !inQuotes) {

                values.add(current.toString());

                current.setLength(0);

            } else {

                current.append(ch);

            }

        }

        values.add(current.toString());

        return values;
    }

    // this method is for the like if the string value already has the comma then like do enclose it in the double inverted commas likewise
    private static String escape(String value) {

        if (value == null) {

            return "";

        }

        boolean needsQuotes = value.contains(",") || value.contains("\"") || value.contains("\n");

        if (!needsQuotes) {

            return value;

        }

        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
