package edu.gatech.seclass.texttool;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {
    // Empty Main class for compiling Individual Project.
    // During Deliverable 1 and Deliverable 2, DO NOT ALTER THIS CLASS or implement it

    public static void main(String[] args) {
		if(args.length > 0){
			try {
				Path inFile = checkFile(args[args.length-1]);
				HashMap<String, ArrayList<String>> argMap = collectProgramArgs(args);
				errorCheck(argMap, inFile);
				execute(argMap, inFile);
			} catch (Exception e) {
				usage();
			}
		}
		else {
			usage();
		}
    }

	private static void usage(){
		System.err.println("Usage: texttool [ -f | -o output_file_name | -i | -r old new | -p prefix | -c n | -d n ] FILE");
	}

	private static Path checkFile(String filePath) throws Exception{
		Path file = Paths.get(filePath);

		//Input file doesn't exist
		if(Files.exists(file) == false){
			throw new Exception();
		}

		return file;
	}
	private static HashMap<String, ArrayList<String>> collectProgramArgs(String[] args) throws Exception {
		HashMap<String, ArrayList<String>> argMap = new HashMap<String, ArrayList<String>>();
		ArrayList<String> lastParamList = new ArrayList<String>();

		for(int i = 0; i < args.length-1; i++){
			String arg = args[i];
			if(	arg.equals("-f") || arg.equals("-o") ||
					arg.equals("-r") || arg.equals("-i") ||
					arg.equals("-p") || arg.equals("-d") ||
					arg.equals("-c")){
				lastParamList = new ArrayList<>();
				argMap.put(arg, lastParamList);
			}
			else {
				if(argMap.isEmpty()){
					throw new Exception();
				}
				lastParamList.add(arg);
			}
		}
		return argMap;
	}

	private static void errorCheck(HashMap<String, ArrayList<String>> args, Path inputFile) throws Exception {
		if(args.containsKey("-f")){
			if	(args.containsKey("-o") ||//Must be mutually exclusive with -o
				!args.get("-f").isEmpty()){//Only no parameters

				throw new Exception();
			}
		}
		if(args.containsKey("-o")){
			ArrayList<String> oParams = args.get("-o");
			if(oParams.size() != 1 ||	//Must only have 1 parameter
				Files.exists(Paths.get(oParams.get(0)))){ //Output file must not exist

				throw new Exception();
			}
		}
		if(args.containsKey("-r")){
			ArrayList<String> rParams = args.get("-r");
			if(args.containsKey("-c") ||	//Must be mutually exclusive with -c
				rParams.size() != 2 ||	//Must have 2 parameters
				rParams.get(0).isEmpty()){ //First parameter must not be empty

				throw new Exception();
			}
		}
		if(args.containsKey("-i")){
			if(!args.containsKey("-r") ||//Must be used only with -r
				!args.get("-i").isEmpty()){//Only no parameters

				throw new Exception();
			}
		}
		if(args.containsKey("-p")){
			ArrayList<String> pParams = args.get("-p");
			if(pParams.size() != 1 || //Must have only one parameter
				pParams.get(0).isEmpty()){ //Must not be blank
				throw new Exception();
			}
		}
		if(args.containsKey("-d")){
			ArrayList<String> dParams = args.get("-d");
			if(dParams.size() != 1) { //Must have 1 parameter
				throw new Exception();
			}
			else {
				int dint = Integer.parseInt(dParams.get(0)); //Throws exception if non-number
				if(dint < 1 || dint > 10) { //Must be between 1 and 10 inclusive
					throw new Exception();
				}
			}
		}
		if(args.containsKey("-c")){
			ArrayList<String> cParams = args.get("-c");
			if(cParams.size() != 1) { //Must have 1 parameter
				throw new Exception();
			}
			else {
				int cint = Integer.parseInt(cParams.get(0));  //Throws exception if non-number
				if(cint < -25 || cint > 25) { //Must be between 1 and 10 inclusive
					throw new Exception();
				}
			}
		}
		String fileContent = Files.readString(inputFile, StandardCharsets.UTF_8);
		//Non empty file must end with new line
		if(!fileContent.isEmpty() && !fileContent.endsWith(System.lineSeparator())){
			throw new Exception();
		}
	}

	private static void execute(HashMap<String, ArrayList<String>> args, Path inputFile) throws Exception{
		List<String> inputContents = Files.readAllLines(inputFile, StandardCharsets.UTF_8);

		if(args.containsKey("-r")){
			inputContents = replace(inputContents, args.get("-r").get(0), args.get("-r").get(1), args.containsKey("-i"));
		}

		if(args.containsKey("-p")){
			inputContents = prefix(inputContents, args.get("-p").get(0));
		}

		if(args.containsKey("-c")){
			inputContents = encode(inputContents, args.get("-c").get(0));
		}

		if(args.containsKey("-d")){
			inputContents = duplicateLines(inputContents, args.get("-d").get(0));
		}

		String output = inputContents.isEmpty() ? "" :
				inputContents.stream().collect(Collectors.joining(System.lineSeparator(), "", System.lineSeparator()));;

		//Set output stream
		if(args.containsKey("-f")){
			FileWriter writer = new FileWriter(inputFile.toFile());
			writer.write(output);
			writer.close();
		}
		else if(args.containsKey("-o"))
		{
			File outFile = new File(args.get("-o").get(0));
			outFile.createNewFile();
			FileWriter writer = new FileWriter(outFile);
			writer.write(output);
			writer.close();
		}
		else{
			System.out.print(output);
		}

	}

	private static List<String> replace(List<String> input, String searchText, String replaceText, boolean caseInsensitive) {
		List<String> replacedContents = new ArrayList<String>();
		String pattern = (caseInsensitive ? "(?i)" : "") + Pattern.quote(searchText);
		for(String s : input){
			replacedContents.add(s.replaceFirst(pattern, Matcher.quoteReplacement(replaceText)));
		}
		return replacedContents;
	}

	private static List<String> prefix(List<String> input, String prefix){
		List<String> prefixContents = new ArrayList<String>();
		for(String s : input){
			prefixContents.add(prefix+s);
		}
		return prefixContents;
	}

	private static List<String> encode(List<String> input, String cipherStr){
		List<String> encodedContents = new ArrayList<String>();
		int cipherInt = Integer.parseInt(cipherStr);
		if(cipherInt < 0){
			cipherInt = 26 + (cipherInt % 26);
		}

		for(String s : input){
			String out = "";
			for(int i = 0; i < s.length(); i++) {
				char c = s.charAt(i);

				if (Character.isAlphabetic(c)) {
					//Captial A-Z
					if (Character.isUpperCase(c)) {
						out += (char) (((c - 'A') + cipherInt) % 26 + 'A');
					}
					//Lowercase a-z
					else {
						out += (char) (((c - 'a') + cipherInt) % 26 + 'a');
					}
				}
				//Non-alphabetic character
				else {
					out += c;
				}
			}
			encodedContents.add(out);
		}
		return encodedContents;
	}

	private static List<String> duplicateLines(List<String> input, String dupNumStr){
		List<String> duplicateInput = new ArrayList<String>();
		int dupNumInt = Integer.parseInt(dupNumStr);
		for(String s : input){
			for(int i = 0; i <= dupNumInt; i++){
				duplicateInput.add(s);
			}
		}

		return duplicateInput;
	}
}
