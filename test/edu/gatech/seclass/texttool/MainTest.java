package edu.gatech.seclass.texttool;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

// DO NOT ALTER THIS CLASS. Use it as an example for MyMainTest.java

public class MainTest {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();
    private final Charset charset = StandardCharsets.UTF_8;
    private ByteArrayOutputStream outStream;
    private ByteArrayOutputStream errStream;
    private PrintStream outOrig;
    private PrintStream errOrig;

    @Before
    public void setUp() throws Exception {
        outStream = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outStream);
        errStream = new ByteArrayOutputStream();
        PrintStream err = new PrintStream(errStream);
        outOrig = System.out;
        errOrig = System.err;
        System.setOut(out);
        System.setErr(err);
    }

    @After
    public void tearDown() throws Exception {
        System.setOut(outOrig);
        System.setErr(errOrig);
    }

    /*
     *  TEST UTILITIES
     */

    // Create File Utility
    private File createTmpFile() throws Exception {
        File tmpfile = temporaryFolder.newFile();
        tmpfile.deleteOnExit();
        return tmpfile;
    }

    // Write File Utility
    private File createInputFile(String input) throws Exception {
        File file = createTmpFile();
        OutputStreamWriter fileWriter = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
        fileWriter.write(input);
        fileWriter.close();
        return file;
    }

    private String getFileContent(String filename) {
        String content = null;
        try {
            content = Files.readString(Paths.get(filename), charset);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    /*
     *   TEST CASES
     */

    /*
        texttool -f FILE
        input FILE:
        alphanumeric123foobar↵

        edited FILE:
        alphanumeric123foobar↵

        stdout: nothing sent to stdout
        stderr: nothing sent to stderr
     */
    @Test
    public void exampleTest1() throws Exception {
        String input = "alphanumeric123foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-f", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    /*
        texttool -r 123 456 FILE
        input FILE:
        alphanumeric123foobar123↵

        edited FILE: file not edited
        stdout:
        alphanumeric456foobar123↵

        stderr: nothing sent to stderr
     */
    @Test
    public void exampleTest2() throws Exception {
        String input = "alphanumeric123foobar123" + System.lineSeparator();

        String expected = "alphanumeric456foobar123" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-r", "123", "456", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertEquals("stdout output does not match", expected, outStream.toString());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    /*
        texttool -i -r foo candy FILE
        input FILE:
        alphanumeric123FOObar123↵

        edited FILE: file not edited
        stdout:
        alphanumeric123candybar123↵

        stderr: nothing sent to stderr
     */
    @Test
    public void exampleTest3() throws Exception {
        String input = "alphanumeric123FOObar123" + System.lineSeparator();

        String expected = "alphanumeric123candybar123" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-i", "-r", "foo", "candy", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertEquals("stdout output does not match", expected, outStream.toString());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    /*
        texttool -p ## FILE
        input FILE:
        alphanumeric123foobar↵

        edited FILE: file not edited
        stdout:
        ##alphanumeric123foobar↵

        stderr: nothing sent to stderr
     */
    @Test
    public void exampleTest4() throws Exception {
        String input = "alphanumeric123foobar" + System.lineSeparator();

        String expected = "##alphanumeric123foobar" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-p", "##", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertEquals("stdout output does not match", expected, outStream.toString());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    /*
        texttool -d 3 FILE
        input FILE:
        alphanumeric123foobar↵

        edited FILE: file not edited
        stdout:
        alphanumeric123foobar↵
        alphanumeric123foobar↵
        alphanumeric123foobar↵
        alphanumeric123foobar↵

        stderr: nothing sent to stderr
     */
    @Test
    public void exampleTest5() throws Exception {
        String input = "alphanumeric123foobar" + System.lineSeparator();

        String expected = "alphanumeric123foobar" + System.lineSeparator() +
            "alphanumeric123foobar" + System.lineSeparator() +
            "alphanumeric123foobar" + System.lineSeparator() +
            "alphanumeric123foobar" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-d", "3", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertEquals("stdout output does not match", expected, outStream.toString());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    /*
        texttool -r foo FOO -f -p !!! -d 1 FILE
        input FILE:
        foobar0Foobar1↵
        foobar2foobar3↵
        foobar4Foobar5↵
        foobar6foobar7↵
        foobar8Foobar9↵

        edited FILE:
        !!!FOObar0Foobar1↵
        !!!FOObar0Foobar1↵
        !!!FOObar2foobar3↵
        !!!FOObar2foobar3↵
        !!!FOObar4Foobar5↵
        !!!FOObar4Foobar5↵
        !!!FOObar6foobar7↵
        !!!FOObar6foobar7↵
        !!!FOObar8Foobar9↵
        !!!FOObar8Foobar9↵

        stdout: nothing sent to stdout
        stderr: nothing sent to stderr
     */
    @Test
    public void exampleTest6() throws Exception {
        String input = "foobar0Foobar1" + System.lineSeparator() +
            "foobar2foobar3" + System.lineSeparator() +
            "foobar4Foobar5" + System.lineSeparator() +
            "foobar6foobar7" + System.lineSeparator() +
            "foobar8Foobar9" + System.lineSeparator();

        String expected = "!!!FOObar0Foobar1" + System.lineSeparator() +
            "!!!FOObar0Foobar1" + System.lineSeparator() +
            "!!!FOObar2foobar3" + System.lineSeparator() +
            "!!!FOObar2foobar3" + System.lineSeparator() +
            "!!!FOObar4Foobar5" + System.lineSeparator() +
            "!!!FOObar4Foobar5" + System.lineSeparator() +
            "!!!FOObar6foobar7" + System.lineSeparator() +
            "!!!FOObar6foobar7" + System.lineSeparator() +
            "!!!FOObar8Foobar9" + System.lineSeparator() +
            "!!!FOObar8Foobar9" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-r", "foo", "FOO", "-f", "-p", "!!!", "-d", "1", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", expected, getFileContent(inputFile.getPath()));
    }

    /*
        texttool -c -20 -d 5 -d 1 -p !!! -p ## FILE
        input FILE:
        alphanumeric123foobar↵

        edited FILE: file not edited
        stdout:
        ##grvngtaskxoi123luuhgx↵
        ##grvngtaskxoi123luuhgx↵

        stderr: nothing sent to stderr
     */
    @Test
    public void exampleTest7() throws Exception {
        String input = "alphanumeric123foobar" + System.lineSeparator();

        String expected = "##grvngtaskxoi123luuhgx" + System.lineSeparator() +
            "##grvngtaskxoi123luuhgx" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-c", "-20", "-d", "5", "-d", "1", "-p", "!!!", "-p", "##", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertEquals("stdout output does not match", expected, outStream.toString());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    /*
        texttool
        input FILE:
        01234abc↵
        56789def↵
        01234ABC↵
        56789DEF↵

        edited FILE: file not edited
        stdout: nothing sent to stdout
        stderr:
        Usage: texttool [ -f | -o output_file_name | -i | -r old new | -p prefix | -c n | -d n ] FILE
     */
    @Test
    public void exampleTest8() throws Exception {
        String input = "01234abc" + System.lineSeparator() +
            "56789def" + System.lineSeparator() +
            "01234ABC" + System.lineSeparator() +
            "56789DEF" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {};
        Main.main(args);

        assertEquals("stderr output does not match", "Usage: texttool [ -f | -o output_file_name | -i | -r old new | -p prefix | -c n | -d n ] FILE", errStream.toString().strip());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    /*
        texttool -c 1 FILE
        input FILE:
        alphanumeric123foobar↵

        edited FILE: file not edited
        stdout:
        bmqibovnfsjd123gppcbs↵

        stderr: nothing sent to stderr
     */
    @Test
    public void exampleTest9() throws Exception {
        String input = "alphanumeric123foobar" + System.lineSeparator();

        String expected = "bmqibovnfsjd123gppcbs" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-c", "1", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertEquals("stdout output does not match", expected, outStream.toString());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }
}
