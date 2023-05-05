package bg.sofia.uni.fmi.mjt.markdown;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class MarkdownConverterTest {

    @TempDir
    Path testingDir;

    Path testingFile;
    Path markdownFile;

    @BeforeEach
    void setupMarkdownFile() {

        markdownFile = testingDir.resolve("markdown" + ".md");
        if (Files.exists(markdownFile)) {
            return;
        }
        try (FileWriter writer = new FileWriter(markdownFile.toString(),false)) {
            writer.write("###### Header" + System.lineSeparator());
            writer.write("i am **Shtiliyan**" + System.lineSeparator());
            writer.write("from *FMI*" + System.lineSeparator());
            writer.write("I am `coding`");
        } catch( IOException e) {
            throw new IllegalStateException("could not create testing markdown file", e);
        }
    }

    @BeforeEach
    void setUp() {
        try {
            testingFile = testingDir.resolve("temp");
        } catch ( InvalidPathException e) {
            throw new IllegalStateException("testing directory not found" ,e );
        }
    }
    @AfterEach
    void tearDownTestingFile() {
        try (FileWriter cleaner = new FileWriter(testingFile.toString(),false)) {

        } catch (IOException e) {
            throw new IllegalStateException("could not clear testing file", e);
        }
    }

    @Test
    void testConvertLineWithHeading6Only() {
        String line = "###### greetings";
        String convertedLine = MarkdownConverter.convertLine(line);
        String expected = "<h6>greetings</h6>";
        assertEquals(convertedLine,expected,"conversion to html line failed");
    }
    @Test
    void testConvertLineWithHeading5Only() {
        String line = "##### greetings";
        String convertedLine = MarkdownConverter.convertLine(line);
        String expected = "<h5>greetings</h5>";
        assertEquals(convertedLine,expected,"conversion to html line failed");
    }
    @Test
    void testConvertLineWithHeading4Only() {
        String line = "#### greetings";
        String convertedLine = MarkdownConverter.convertLine(line);
        String expected = "<h4>greetings</h4>";
        assertEquals(convertedLine,expected,"conversion to html line failed");
    }
    @Test
    void testConvertLineWithHeading3Only() {
        String line = "###greetings";
        String convertedLine = MarkdownConverter.convertLine(line);
        String expected = "<h3>greetings</h3>";
        assertEquals(convertedLine,expected,"conversion to html line failed");
    }
    @Test
    void testConvertLineWithHeading2Only() {
        String line = "## greetings";
        String convertedLine = MarkdownConverter.convertLine(line);
        String expected = "<h2>greetings</h2>";
        assertEquals(convertedLine,expected,"conversion to html line failed");
    }
    @Test
    void testConvertLineWithHeading1Only() {
        String line = "# greetings";
        String convertedLine = MarkdownConverter.convertLine(line);
        String expected = "<h1>greetings</h1>";
        assertEquals(convertedLine,expected,"conversion to html line failed");
    }


    @Test
    void testConvertLineWithBold() {
        String line = "**hello**";
        String convertedLine = MarkdownConverter.convertLine(line);
        String expected = "<strong>hello</strong>";
        assertEquals(convertedLine,expected,"conversion to html line failed");
    }

    @Test
    void testConvertLineWithItalic() {
        String line = "*hello* i am *Bonshte*";
        String convertedLine = MarkdownConverter.convertLine(line);
        String expected = "<em>hello</em> i am <em>Bonshte</em>";
        assertEquals(convertedLine,expected,"conversion to html line failed");
    }
    @Test
    void testConvertLineWithCode() {
        String line = "`hello` i am `Bonshte`";
        String convertedLine = MarkdownConverter.convertLine(line);
        String expected = "<code>hello</code> i am <code>Bonshte</code>";
        assertEquals(convertedLine,expected,"conversion to html line failed");
    }

    @Test
    void testConvertLineAllThree() {
        String line = "`hello` i am *Bonshte* and i am **bored**";
        String convertedLine = MarkdownConverter.convertLine(line);
        String expected = "<code>hello</code> i am <em>Bonshte</em> and i am <strong>bored</strong>";
        assertEquals(convertedLine,expected,"conversion to html line failed");
    }
    @Test
    void testConvertLineDoesNotConvertWithoutCloseCode() {
        String line = "`hello i am Bonshte and i am bored";
        String convertedLine = MarkdownConverter.convertLine(line);
        String expected = "`hello i am Bonshte and i am bored";
        assertEquals(convertedLine,expected,"conversion to html line failed");
    }
    @Test
    void testConvertMarkdownNullReaderAndWriterPassed() {
        Reader from = null;
        Writer to = null;
        MarkdownConverter converter = new MarkdownConverter();
        assertThrows(IllegalArgumentException.class, () -> converter.convertMarkdown(from,to));
    }

    @Test
    void testConvertLineDoesNotConvertWithoutCloseItalic() {
        String line = "*hello i am Bonshte and i am bored";
        String convertedLine = MarkdownConverter.convertLine(line);
        String expected = "*hello i am Bonshte and i am bored";
        assertEquals(convertedLine,expected,"conversion to html line failed");
    }



    @Test
    void testConvertMarkdownReaderAndWriter() {
        try (Reader input = new StringReader("**hi there** *care to join*" +
                System.lineSeparator() + "####Surprise");
             Writer output = new FileWriter(testingFile.toString(),false)) {
            MarkdownConverter converter = new MarkdownConverter();
            converter.convertMarkdown(input,output);
            String written = Files.readString(Path.of(testingFile.toString()));
            String expectedResult = "<html>" + System.lineSeparator() + "<body>" + System.lineSeparator() +
                    "<strong>hi there</strong> <em>care to join</em>" + System.lineSeparator() +
                    "<h4>Surprise</h4>" + System.lineSeparator() + "</body>" + System.lineSeparator() +
                    "</html>";

            assertEquals(written,expectedResult,"conversion is not same");

        } catch (IOException e) {
            throw new IllegalStateException("could not create file", e);
        }

    }

    @Test
    void testConvertMarkDownWithPath() {
        Path from = markdownFile;
        Path to = testingFile;
        MarkdownConverter converter = new MarkdownConverter();
        converter.convertMarkdown(from, to);
        String expectedResult = "<html>" + System.lineSeparator() +
                "<body>" + System.lineSeparator() +
                "<h6>Header</h6>" + System.lineSeparator() +
                "i am <strong>Shtiliyan</strong>" + System.lineSeparator() +
                "from <em>FMI</em>" + System.lineSeparator() +
                "I am <code>coding</code>" + System.lineSeparator() +
                "</body>" + System.lineSeparator() +
                "</html>";
        try {
            String insideFile = Files.readString(testingFile);
            assertEquals(expectedResult,insideFile,"conversion from file to file failed");
        } catch (IOException e) {
            throw new IllegalStateException("could not read from testing file", e);
        }
    }

    @Test
    void testConvertMarkdownPathWithNull() {
        Path from  = null;
        Path to = testingFile;
        MarkdownConverter markdown = new MarkdownConverter();
        assertThrows(IllegalArgumentException.class, () -> markdown.convertMarkdown(from,to));
    }

    @Test
    void testConvertMarkdownPathWithInvalidSource() {
        Path from = Path.of("fake");
        Path to = testingFile;
        MarkdownConverter converter = new MarkdownConverter();
        assertThrows(IllegalStateException.class, () -> converter.convertMarkdown(from, to));
    }

    @Test
    void testConvertAllMarkdownFilesNullDirectory() {
        Path sourceDirectory = null;
        Path destinationDirectory = null;
        MarkdownConverter converter = new MarkdownConverter();
        assertThrows(IllegalArgumentException.class, () -> converter.convertAllMarkdownFiles(sourceDirectory, destinationDirectory));
    }

    @Test
    void testConvertAllMarkdownFilesFilePassed() {
        Path sourceDirectory = testingFile;
        Path destinationDirectory = sourceDirectory;
        MarkdownConverter converter = new MarkdownConverter();
        assertThrows(IllegalArgumentException.class, () -> converter.convertAllMarkdownFiles(sourceDirectory,destinationDirectory));
    }

    @Test
    void testConvertAllMarkdownFiles() {
        Path srcDir = testingDir;
        Path destDir = testingDir;
        MarkdownConverter converter = new MarkdownConverter();
        converter.convertAllMarkdownFiles(srcDir,destDir);
        Path created = destDir.resolve("markdown.html");
        assertTrue(Files.exists(created),"could not create tmp.html");

    }



}