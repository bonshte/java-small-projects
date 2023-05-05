import bg.sofia.uni.fmi.mjt.markdown.MarkdownConverter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    private static final String H6 = "######";
    private static final String H5 = "#####";
    private static final String H4 = "####";
    private static final String H3 = "###";
    private static final String H2 = "##";
    private static final String H1 = "#";

    private static StringBuilder convertHeadingsInLine(String inputLine) {
        StringBuilder result = new StringBuilder(inputLine);
        int indexOfH6 = result.indexOf(H6);
        int indexOfH5 = result.indexOf(H5);
        int indexOfH4 = result.indexOf(H4);
        int indexOfH3 = result.indexOf(H3);
        int indexOfH2 = result.indexOf(H2);
        int indexOfH1 = result.indexOf(H1);
        if (indexOfH6 != -1) {
            int indexOfHeadingStart = indexOfH6 + H6.length();
            while (indexOfHeadingStart < result.length() &&
                    (result.charAt(indexOfHeadingStart) == ' ' || result.charAt(indexOfHeadingStart) == '\t')) {
                ++indexOfHeadingStart;
            }
            result.replace(indexOfH6, indexOfHeadingStart, "<h6>");
            result.append("</h6>");

        } else if (indexOfH5 != -1) {
            int indexOfHeadingStart = indexOfH5 + H5.length();
            while (indexOfHeadingStart < result.length() &&
                    (result.charAt(indexOfHeadingStart) == ' ' || result.charAt(indexOfHeadingStart) == '\t')) {
                ++indexOfHeadingStart;
            }
            result.replace(indexOfH5, indexOfHeadingStart, "<h5>");
            result.append("</h5>");
        } else if (indexOfH4 != -1) {
            int indexOfHeadingStart = indexOfH4 + H4.length();
            while (indexOfHeadingStart < result.length() &&
                    (result.charAt(indexOfHeadingStart) == ' ' || result.charAt(indexOfHeadingStart) == '\t')) {
                ++indexOfHeadingStart;
            }
            result.replace(indexOfH4, indexOfHeadingStart , "<h4>");
            result.append("</h4>");
        } else if (indexOfH3 != -1) {
            int indexOfHeadingStart = indexOfH3 + H3.length();
            while (indexOfHeadingStart < result.length() &&
                    (result.charAt(indexOfHeadingStart) == ' ' || result.charAt(indexOfHeadingStart) == '\t')) {
                ++indexOfHeadingStart;
            }
            result.replace(indexOfH3, indexOfHeadingStart,  "<h3>");
            result.append("</h3>");
        } else if (indexOfH2 != -1) {
            int indexOfHeadingStart = indexOfH2 + H2.length();
            while (indexOfHeadingStart < result.length() &&
                    (result.charAt(indexOfHeadingStart) == ' ' || result.charAt(indexOfHeadingStart) == '\t')) {
                ++indexOfHeadingStart;
            }
            result.replace(indexOfH2, indexOfHeadingStart, "<h2>");
            result.append("</h2>");
        } else if (indexOfH1 != -1) {
            int indexOfHeadingStart = indexOfH1 + H1.length();
            while (indexOfHeadingStart < result.length() &&
                    (result.charAt(indexOfHeadingStart) == ' ' || result.charAt(indexOfHeadingStart) == '\t')) {
                ++indexOfHeadingStart;
            }
            result.replace(indexOfH1, indexOfHeadingStart, "<h1>");
            result.append("</h1>");
        }
        return result;
    }

    private static StringBuilder convertBoldsInLine(String inputLine) {
        StringBuilder result = new StringBuilder(inputLine);
        int firstBoldPos = result.indexOf("**");
        while (firstBoldPos != -1) {
            int secondBoldPos = result.indexOf("**", firstBoldPos + 2);
            if (secondBoldPos == -1) {
                break;
            }

            result.replace(firstBoldPos, firstBoldPos + 2, "<strong>");
            secondBoldPos = result.indexOf("**");

            result.replace(secondBoldPos, secondBoldPos + 2, "</strong>");
            firstBoldPos = result.indexOf("**");
        }
        return result;
    }

    private static StringBuilder convertItalicInLine(String inputLine) {
        StringBuilder result = new StringBuilder(inputLine);
        int firstItalicPos = result.indexOf("*");
        while (firstItalicPos != -1) {
            int secondItalicPos = result.indexOf("*", firstItalicPos + 1);
            if (secondItalicPos == -1) {
                break;
            }
            result.replace(firstItalicPos, firstItalicPos + 1, "<em>");
            secondItalicPos = result.indexOf("*");
            result.replace(secondItalicPos, secondItalicPos + 1, "</em>");
            firstItalicPos = result.indexOf("*");
        }
        return result;
    }

    private static StringBuilder convertCodeInLine(String inputLine) {
        StringBuilder result = new StringBuilder(inputLine);
        int firstCodePos = result.indexOf("`");
        while (firstCodePos != -1) {
            int secondCodePos = result.indexOf("`", firstCodePos + 1);
            if (secondCodePos == -1) {
                break;
            }
            result.replace(firstCodePos, firstCodePos + 1, "<code>");
            secondCodePos = result.indexOf("`");
            result.replace(secondCodePos, secondCodePos + 1, "</code>");
            firstCodePos = result.indexOf("`");
        }
        return result;
    }

    public static String convertLine(String inputLine) {
        StringBuilder headingsConverted = convertHeadingsInLine(inputLine);
        StringBuilder boldsConverted = convertBoldsInLine(headingsConverted.toString());
        StringBuilder italicsConverted = convertItalicInLine(boldsConverted.toString());
        StringBuilder codeConverted = convertCodeInLine(italicsConverted.toString());

        return codeConverted.toString();


    }

    public static void main(String[] args) throws IOException {
        String toConvert = "<###### Proper Header>";
        toConvert = convertLine(toConvert);
        System.out.println(toConvert);

    }

}