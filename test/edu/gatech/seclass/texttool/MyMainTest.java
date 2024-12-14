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

public class MyMainTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();
    private final Charset charset = StandardCharsets.UTF_8;
    private ByteArrayOutputStream outStream;
    private ByteArrayOutputStream errStream;
    private PrintStream outOrig;
    private PrintStream errOrig;
    private final String USAGE_TXT = "Usage: texttool [ -f | -o output_file_name | -i | -r old new | -p prefix | -c n | -d n ] FILE";

    @Before
    public void setUp() {
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
    public void tearDown(){
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
// Frame #: 1
@Test
public void texttoolTest1() throws Exception {
    String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

    File inputFile = createInputFile(input);
    String[] args = {"the_wrong_file_name"};
    Main.main(args);

    assertEquals("stderr output does not match", USAGE_TXT, errStream.toString().strip());
    assertTrue("stdout output should be empty", outStream.toString().isEmpty());
    assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
}

    // Frame #: 2
    @Test
    public void texttoolTest2() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "PREFIX_alphanumeric_aBc789_AbC123_foobar!" + System.lineSeparator() +
                "PREFIX_alphanumeric_aBc789_AbC123_foobar!" + System.lineSeparator() +
                "PREFIX_alphanumeric_aBc789_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-f", "-r", "123", "456", "-r", "123", "789", "-i", "-p", "PREFIX_", "-d", "2", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 3
    @Test
    public void texttoolTest3() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-o", inputFile.getPath()};
        Main.main(args);

        assertEquals("stderr output does not match", USAGE_TXT, errStream.toString().strip());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 4
    @Test
    public void texttoolTest4() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        File tmpFile = createTmpFile();

        String[] args = {"-o", tmpFile.getPath(), inputFile.getPath()};
        Main.main(args);

        assertEquals("stderr output does not match", USAGE_TXT, errStream.toString().strip());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 5
    @Test
    public void texttoolTest5() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-f", "-o", inputFile.getParent() + "/outputFile.txt", inputFile.getPath()};
        Main.main(args);

        assertEquals("stderr output does not match", USAGE_TXT, errStream.toString().strip());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 6
    @Test
    public void texttoolTest6() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-o", inputFile.getParent() + "/output<?>ErrorFile.txt", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 7
    @Test
    public void texttoolTest7() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-r", inputFile.getPath()};
        Main.main(args);

        assertEquals("stderr output does not match", USAGE_TXT, errStream.toString().strip());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 8
    @Test
    public void texttoolTest8() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-r", "", "456", inputFile.getPath()};
        Main.main(args);

        assertEquals("stderr output does not match", USAGE_TXT, errStream.toString().strip());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 9
    @Test
    public void texttoolTest9() throws Exception {
        String input = "alphanumeric_aBc1\\23_AbC123_foobar!" + System.lineSeparator();

        String expected = "alphanumeric_aBc456_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-r", "1\\23", "456", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertEquals("stdout output does not match", expected, outStream.toString());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 10
    @Test
    public void texttoolTest10() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "alphanumeric_aBc_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-r", "123", "", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertEquals("stdout output does not match", expected, outStream.toString());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 11
    @Test
    public void texttoolTest11() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "alphanumeric_aBc4\\56_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-r", "123", "4\\56", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertEquals("stdout output does not match", expected, outStream.toString());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 12
    @Test
    public void texttoolTest12() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-p", inputFile.getPath()};
        Main.main(args);

        assertEquals("stderr output does not match", USAGE_TXT, errStream.toString().strip());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 13
    @Test
    public void texttoolTest13() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-p", "", inputFile.getPath()};
        Main.main(args);

        assertEquals("stderr output does not match", USAGE_TXT, errStream.toString().strip());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 14
    @Test
    public void texttoolTest14() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "Pref!x$_alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-p", "Pref!x$_", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertEquals("stdout output does not match", expected, outStream.toString());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 15
    @Test
    public void texttoolTest15() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-d", inputFile.getPath()};
        Main.main(args);

        assertEquals("stderr output does not match", USAGE_TXT, errStream.toString().strip());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 16
    @Test
    public void texttoolTest16() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-d", "One", inputFile.getPath()};
        Main.main(args);

        assertEquals("stderr output does not match", USAGE_TXT, errStream.toString().strip());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 17
    @Test
    public void texttoolTest17() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-d", "-1", inputFile.getPath()};
        Main.main(args);

        assertEquals("stderr output does not match", USAGE_TXT, errStream.toString().strip());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 18
    @Test
    public void texttoolTest18() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-d", "11", inputFile.getPath()};
        Main.main(args);

        assertEquals("stderr output does not match", USAGE_TXT, errStream.toString().strip());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 19
    @Test
    public void texttoolTest19() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-c", inputFile.getPath()};
        Main.main(args);

        assertEquals("stderr output does not match", USAGE_TXT, errStream.toString().strip());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 20
    @Test
    public void texttoolTest20() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-c", "One", inputFile.getPath()};
        Main.main(args);

        assertEquals("stderr output does not match", USAGE_TXT, errStream.toString().strip());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 21
    @Test
    public void texttoolTest21() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-c", "-26", inputFile.getPath()};
        Main.main(args);

        assertEquals("stderr output does not match", USAGE_TXT, errStream.toString().strip());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 22
    @Test
    public void texttoolTest22() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-r", "123", "456", "-c", "1", inputFile.getPath()};
        Main.main(args);

        assertEquals("stderr output does not match", USAGE_TXT, errStream.toString().strip());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 23
    @Test
    public void texttoolTest23() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-c", "26", inputFile.getPath()};
        Main.main(args);

        assertEquals("stderr output does not match", USAGE_TXT, errStream.toString().strip());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 24
    @Test
    public void texttoolTest24() throws Exception {
        String input = "";

        String expected = "";

        File inputFile = createInputFile(input);
        String[] args = {inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertEquals("stdout output does not match", expected, outStream.toString());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 25
    @Test
    public void texttoolTest25() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertEquals("stdout output does not match", expected, outStream.toString());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 26
    @Test
    public void texttoolTest26() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!";

        File inputFile = createInputFile(input);
        String[] args = {inputFile.getPath()};
        Main.main(args);

        assertEquals("stderr output does not match", USAGE_TXT, errStream.toString().strip());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 27
    @Test
    public void texttoolTest27() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "Prefixalphanumeric_Def456_AbC123_foobar!" + System.lineSeparator() +
                "Prefixalphanumeric_Def456_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-f", "-r", "AbC123", "Def456", "-i", "-p", "Prefix", "-d", "1", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 28
    @Test
    public void texttoolTest28() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "Prefixalphanumeric_Def456_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-f", "-r", "AbC123", "Def456", "-i", "-p", "Prefix", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 29
    @Test
    public void texttoolTest29() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "alphanumeric_Def456_AbC123_foobar!" + System.lineSeparator() +
                "alphanumeric_Def456_AbC123_foobar!" + System.lineSeparator();


        File inputFile = createInputFile(input);
        String[] args = {"-f", "-r", "AbC123", "Def456", "-i", "-d", "1", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 30
    @Test
    public void texttoolTest30() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "alphanumeric_Def456_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-f", "-r", "AbC123", "Def456", "-i", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 31
    @Test
    public void texttoolTest31() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "Prefixalphanumeric_aBc123_Def456_foobar!" + System.lineSeparator() +
                "Prefixalphanumeric_aBc123_Def456_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-f", "-r", "AbC123", "Def456", "-p", "Prefix", "-d", "1", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 32
    @Test
    public void texttoolTest32() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "Prefixalphanumeric_aBc123_Def456_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-f", "-r", "AbC123", "Def456", "-p", "Prefix", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 33
    @Test
    public void texttoolTest33() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "alphanumeric_aBc123_Def456_foobar!" + System.lineSeparator() +
                "alphanumeric_aBc123_Def456_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-f", "-r", "AbC123", "Def456", "-d", "1", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 34
    @Test
    public void texttoolTest34() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "alphanumeric_aBc123_Def456_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-f", "-r", "AbC123", "Def456", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 35
    @Test
    public void texttoolTest35() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "Qsfgjybmqibovnfsjd_bCd123_BcD123_gppcbs!" + System.lineSeparator() +
                "Qsfgjybmqibovnfsjd_bCd123_BcD123_gppcbs!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-f", "-p", "Prefix", "-d", "1", "-c", "1", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 36
    @Test
    public void texttoolTest36() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "Prefixalphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator()+
                "Prefixalphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-f", "-p", "Prefix", "-d", "1", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 37
    @Test
    public void texttoolTest37() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "Qsfgjybmqibovnfsjd_bCd123_BcD123_gppcbs!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-f", "-p", "Prefix", "-c", "1", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 38
    @Test
    public void texttoolTest38() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "Prefixalphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-f", "-p", "Prefix", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 39
    @Test
    public void texttoolTest39() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "bmqibovnfsjd_bCd123_BcD123_gppcbs!" + System.lineSeparator() +
                "bmqibovnfsjd_bCd123_BcD123_gppcbs!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-f", "-d", "1", "-c", "1", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 40
    @Test
    public void texttoolTest40() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator() +
                "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-f", "-d", "1", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 41
    @Test
    public void texttoolTest41() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "bmqibovnfsjd_bCd123_BcD123_gppcbs!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-f", "-c", "1", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 42
    @Test
    public void texttoolTest42() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-f", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 43
    @Test
    public void texttoolTest43() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "Prefixalphanumeric_Def456_AbC123_foobar!" + System.lineSeparator() +
                "Prefixalphanumeric_Def456_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-o", inputFile.getParent() + "/outputFile.txt", "-r", "AbC123", "Def456", "-i", "-p", "Prefix", "-d", "1", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
        assertEquals("output file content not matched", expected, getFileContent(inputFile.getParent() + "/outputFile.txt"));
    }

    // Frame #: 44
    @Test
    public void texttoolTest44() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "Prefixalphanumeric_Def456_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-o", inputFile.getParent() + "/outputFile.txt", "-r", "AbC123", "Def456", "-i", "-p", "Prefix", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
        assertEquals("output file content not matched", expected, getFileContent(inputFile.getParent() + "/outputFile.txt"));
    }

    // Frame #: 45
    @Test
    public void texttoolTest45() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "alphanumeric_Def456_AbC123_foobar!" + System.lineSeparator() +
                "alphanumeric_Def456_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-o", inputFile.getParent() + "/outputFile.txt", "-r", "AbC123", "Def456", "-i", "-d", "1", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
        assertEquals("output file content not matched", expected, getFileContent(inputFile.getParent() + "/outputFile.txt"));
    }

    // Frame #: 46
    @Test
    public void texttoolTest46() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "alphanumeric_Def456_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-o", inputFile.getParent() + "/outputFile.txt", "-r", "AbC123", "Def456", "-i", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
        assertEquals("output file content not matched", expected, getFileContent(inputFile.getParent() + "/outputFile.txt"));
    }

    // Frame #: 47
    @Test
    public void texttoolTest47() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "Prefixalphanumeric_aBc123_Def456_foobar!" + System.lineSeparator() +
                "Prefixalphanumeric_aBc123_Def456_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-o", inputFile.getParent() + "/outputFile.txt", "-r", "AbC123", "Def456", "-p", "Prefix", "-d", "1", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
        assertEquals("output file content not matched", expected, getFileContent(inputFile.getParent() + "/outputFile.txt"));
    }

    // Frame #: 48
    @Test
    public void texttoolTest48() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "Prefixalphanumeric_aBc123_Def456_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-o", inputFile.getParent() + "/outputFile.txt", "-r", "AbC123", "Def456", "-p", "Prefix", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
        assertEquals("output file content not matched", expected, getFileContent(inputFile.getParent() + "/outputFile.txt"));
    }

    // Frame #: 49
    @Test
    public void texttoolTest49() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "alphanumeric_aBc123_Def456_foobar!" + System.lineSeparator() +
                "alphanumeric_aBc123_Def456_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-o", inputFile.getParent() + "/outputFile.txt", "-r", "AbC123", "Def456", "-d", "1", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
        assertEquals("output file content not matched", expected, getFileContent(inputFile.getParent() + "/outputFile.txt"));
    }

    // Frame #: 50
    @Test
    public void texttoolTest50() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "alphanumeric_aBc123_Def456_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-o", inputFile.getParent() + "/outputFile.txt", "-r", "AbC123", "Def456", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
        assertEquals("output file content not matched", expected, getFileContent(inputFile.getParent() + "/outputFile.txt"));
    }

    // Frame #: 51
    @Test
    public void texttoolTest51() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "Qsfgjybmqibovnfsjd_bCd123_BcD123_gppcbs!" + System.lineSeparator() +
                "Qsfgjybmqibovnfsjd_bCd123_BcD123_gppcbs!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-o", inputFile.getParent() + "/outputFile.txt", "-p", "Prefix", "-d", "1", "-c", "1", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
        assertEquals("output file content not matched", expected, getFileContent(inputFile.getParent() + "/outputFile.txt"));
    }

    // Frame #: 52
    @Test
    public void texttoolTest52() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "Prefixalphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator()+
                "Prefixalphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-o", inputFile.getParent() + "/outputFile.txt", "-p", "Prefix", "-d", "1", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
        assertEquals("output file content not matched", expected, getFileContent(inputFile.getParent() + "/outputFile.txt"));
    }

    // Frame #: 53
    @Test
    public void texttoolTest53() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "Qsfgjybmqibovnfsjd_bCd123_BcD123_gppcbs!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-o", inputFile.getParent() + "/outputFile.txt", "-p", "Prefix", "-c", "1", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
        assertEquals("output file content not matched", expected, getFileContent(inputFile.getParent() + "/outputFile.txt"));
    }

    // Frame #: 54
    @Test
    public void texttoolTest54() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "Prefixalphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-o", inputFile.getParent() + "/outputFile.txt", "-p", "Prefix", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
        assertEquals("output file content not matched", expected, getFileContent(inputFile.getParent() + "/outputFile.txt"));
    }

    // Frame #: 55
    @Test
    public void texttoolTest55() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "bmqibovnfsjd_bCd123_BcD123_gppcbs!" + System.lineSeparator() +
                "bmqibovnfsjd_bCd123_BcD123_gppcbs!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-o", inputFile.getParent() + "/outputFile.txt", "-d", "1", "-c", "1", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
        assertEquals("output file content not matched", expected, getFileContent(inputFile.getParent() + "/outputFile.txt"));
    }

    // Frame #: 56
    @Test
    public void texttoolTest56() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator() +
                "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-o", inputFile.getParent() + "/outputFile.txt", "-d", "1", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
        assertEquals("output file content not matched", expected, getFileContent(inputFile.getParent() + "/outputFile.txt"));
    }

    // Frame #: 57
    @Test
    public void texttoolTest57() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "bmqibovnfsjd_bCd123_BcD123_gppcbs!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-o", inputFile.getParent() + "/outputFile.txt", "-c", "1", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
        assertEquals("output file content not matched", expected, getFileContent(inputFile.getParent() + "/outputFile.txt"));
    }

    // Frame #: 58
    @Test
    public void texttoolTest58() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-o", inputFile.getParent() + "/outputFile.txt", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
        assertEquals("output file content not matched", expected, getFileContent(inputFile.getParent() + "/outputFile.txt"));
    }

    // Frame #: 59
    @Test
    public void texttoolTest59() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "Prefixalphanumeric_Def456_AbC123_foobar!" + System.lineSeparator() +
                "Prefixalphanumeric_Def456_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-r", "AbC123", "Def456", "-i", "-p", "Prefix", "-d", "1", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertEquals("stdout output does not match", expected, outStream.toString());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 60
    @Test
    public void texttoolTest60() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "Prefixalphanumeric_Def456_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-r", "AbC123", "Def456", "-i", "-p", "Prefix", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertEquals("stdout output does not match", expected, outStream.toString());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 61
    @Test
    public void texttoolTest61() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "alphanumeric_Def456_AbC123_foobar!" + System.lineSeparator() +
                "alphanumeric_Def456_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-r", "AbC123", "Def456", "-i", "-d", "1", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertEquals("stdout output does not match", expected, outStream.toString());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 62
    @Test
    public void texttoolTest62() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "alphanumeric_Def456_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-r", "AbC123", "Def456", "-i", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertEquals("stdout output does not match", expected, outStream.toString());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 63
    @Test
    public void texttoolTest63() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "Prefixalphanumeric_aBc123_Def456_foobar!" + System.lineSeparator() +
                "Prefixalphanumeric_aBc123_Def456_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-r", "AbC123", "Def456", "-p", "Prefix", "-d", "1", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertEquals("stdout output does not match", expected, outStream.toString());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 64
    @Test
    public void texttoolTest64() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "Prefixalphanumeric_aBc123_Def456_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-r", "AbC123", "Def456", "-p", "Prefix", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertEquals("stdout output does not match", expected, outStream.toString());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 65
    @Test
    public void texttoolTest65() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "alphanumeric_aBc123_Def456_foobar!" + System.lineSeparator() +
                "alphanumeric_aBc123_Def456_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-r", "AbC123", "Def456", "-d", "1", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertEquals("stdout output does not match", expected, outStream.toString());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 66
    @Test
    public void texttoolTest66() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "alphanumeric_aBc123_Def456_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-r", "AbC123", "Def456", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertEquals("stdout output does not match", expected, outStream.toString());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 67
    @Test
    public void texttoolTest67() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "Qsfgjybmqibovnfsjd_bCd123_BcD123_gppcbs!" + System.lineSeparator() +
                "Qsfgjybmqibovnfsjd_bCd123_BcD123_gppcbs!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-p", "Prefix", "-d", "1", "-c", "1", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertEquals("stdout output does not match", expected, outStream.toString());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 68
    @Test
    public void texttoolTest68() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "Prefixalphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator()+
                "Prefixalphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-p", "Prefix", "-d", "1", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertEquals("stdout output does not match", expected, outStream.toString());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 69
    @Test
    public void texttoolTest69() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "Qsfgjybmqibovnfsjd_bCd123_BcD123_gppcbs!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-p", "Prefix", "-c", "1", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertEquals("stdout output does not match", expected, outStream.toString());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 70
    @Test
    public void texttoolTest70() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "Prefixalphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-p", "Prefix", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertEquals("stdout output does not match", expected, outStream.toString());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 71
    @Test
    public void texttoolTest71() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "bmqibovnfsjd_bCd123_BcD123_gppcbs!" + System.lineSeparator() +
                "bmqibovnfsjd_bCd123_BcD123_gppcbs!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-d", "1", "-c", "1", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertEquals("stdout output does not match", expected, outStream.toString());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 72
    @Test
    public void texttoolTest72() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator() +
                "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-d", "1", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertEquals("stdout output does not match", expected, outStream.toString());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 73
    @Test
    public void texttoolTest73() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "bmqibovnfsjd_bCd123_BcD123_gppcbs!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-c", "1", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertEquals("stdout output does not match", expected, outStream.toString());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 74
    @Test
    public void texttoolTest74() throws Exception {
        String input = "These Should Match!" + System.lineSeparator();

        String expected = "These Should Match!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertEquals("stdout output does not match", expected, outStream.toString());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 75
    @Test
    public void texttoolTest75() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "PREFIX_alphanumeric_dEf789_AbC123_foobar!" + System.lineSeparator() +
                "PREFIX_alphanumeric_dEf789_AbC123_foobar!" + System.lineSeparator() +
                "PREFIX_alphanumeric_dEf789_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-f", "-r", "aBc123", "dEf456", "-r", "aBc123", "dEf789", "-p", "PREFIX_", "-d", "2", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 76
    @Test
    public void texttoolTest76() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "PREFIX2_alphanumeric_dEf456_AbC123_foobar!" + System.lineSeparator() +
                "PREFIX2_alphanumeric_dEf456_AbC123_foobar!" + System.lineSeparator() +
                "PREFIX2_alphanumeric_dEf456_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-f", "-r", "aBc123", "dEf456", "-i", "-p", "PREFIX_", "-p", "PREFIX2_", "-d", "2", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 77
    @Test
    public void texttoolTest77() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "PREFIX_alphanumeric_dEf456_AbC123_foobar!" + System.lineSeparator() +
                "PREFIX_alphanumeric_dEf456_AbC123_foobar!" + System.lineSeparator() +
                "PREFIX_alphanumeric_dEf456_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-f", "-r", "aBc123", "dEf456", "-i", "-p", "PREFIX_", "-d", "1", "-d", "2", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", expected, getFileContent(inputFile.getPath()));
    }

    // Frame #: 78
    @Test
    public void texttoolTest78() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "Qsfgjybmqibovnfsjd_bCd123_BcD123_gppcbs!" + System.lineSeparator() +
                "Qsfgjybmqibovnfsjd_bCd123_BcD123_gppcbs!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-p", "Prefix", "-d", "1", "-c", "2", "-c", "1", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertEquals("stdout output does not match", expected, outStream.toString());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 79
    @Test
    public void texttoolTest79() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        String expected = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);
        String[] args = {"-o", inputFile.getParent() + "/outputFile.txt", "-o", inputFile.getParent() + "/outputFile2.txt", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
        assertEquals("output file content not matched", expected, getFileContent(inputFile.getParent() + "/outputFile2.txt"));
    }

    // Frame #: 80
    @Test
    public void texttoolTest80() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);

        String[] args = {"-i", inputFile.getPath()};
        Main.main(args);

        assertEquals("stderr output does not match", USAGE_TXT, errStream.toString().strip());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 81
    @Test
    public void texttoolTest81() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);

        String[] args = {};
        Main.main(args);

        assertEquals("stderr output does not match", USAGE_TXT, errStream.toString().strip());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 82
    @Test
    public void texttoolTest82() throws Exception {
        String input = "alphanumeric_aBc123_AbC123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);

        String[] args = {"-f", "invalid parameter", inputFile.getPath()};
        Main.main(args);

        assertEquals("stderr output does not match", USAGE_TXT, errStream.toString().strip());
        assertTrue("stdout output should be empty", outStream.toString().isEmpty());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: 83
    @Test
    public void texttoolTest83() throws Exception {
        String input = "alphanumeric_aBc123_aBc123_foobar!" + System.lineSeparator();

        String expected = "alphanumeric_aBc456_aBc123_foobar!" + System.lineSeparator();

        File inputFile = createInputFile(input);

        String[] args = {"-r", "123", "456", inputFile.getPath()};
        Main.main(args);

        assertTrue("stderr output should be empty", errStream.toString().isEmpty());
        assertEquals("stdout output does not match", expected, outStream.toString());
        assertEquals("input file content not matched", input, getFileContent(inputFile.getPath()));
    }
}
