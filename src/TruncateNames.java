import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.*;

/**
 * Goes recursively through a folder and all of it's files/sub-folders and renames files based on restrictions. 
 * @author sstoychev
 *
 */
public class TruncateNames {
	public static List<String> logLines = new ArrayList<String>();
	public static HashMap<String, String> map = new HashMap<String, String>();
	
	public static String originalLocationPath = "C:\\SHAREDBOT\\";
	public static String destinationPath = "ecms.seagen.com/departments/QA/MQA/";
	
	//SharePoint restrictions
	public static int fileNameRestiction = 128;
	public static int filePathRestriction = 260;
	
	public static int folderNameRestriction = 128;	
	public static int folderPathRestriction = 260;
	
	public static void main(String[] args) {
		
		File[] files = new File(originalLocationPath).listFiles();
	    showFiles(files);
	    
	    Path logFile = Paths.get("log.txt");
		try {
			Files.write(logFile, logLines, Charset.forName("UTF-8"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Goes recursively through a folder and all of it's files/sub-folders and renames files based on restrictions. 
	 * @param files
	 */
	public static void showFiles(File[] files) {
		
	    for (File file : files) {
	        if (file.isDirectory()) {
	        	if(file.getName().length() > folderNameRestriction) {
	        		System.out.println("Folder cannot exceed " + folderNameRestriction + " chars (" + (file.getName().length() )  + "): " + file.getName() + "; THE FULL PATH IS " + file.getAbsolutePath().replace(originalLocationPath, destinationPath) + "/");
	        		System.out.println();
	        		logLines.add("Folder cannot exceed " + folderNameRestriction + " chars (" + (file.getName().length() ) + "): " + file.getName() + "; THE FULL PATH IS " + file.getAbsolutePath().replace(originalLocationPath, destinationPath) + "/");
	        		logLines.add("");
	        	}
	        	if(file.getAbsolutePath().replace(originalLocationPath, destinationPath).length() >= folderPathRestriction) {
	        		System.out.println("FULL Path to Folder cannot exceed " + folderPathRestriction + " chars (" + (file.getAbsolutePath().replace(originalLocationPath, destinationPath).length() + 1) + "): " + file.getAbsolutePath().replace(originalLocationPath, destinationPath)+ "/");
	        		System.out.println();
	        		logLines.add("FULL Path to Folder cannot exceed " + folderPathRestriction + " chars (" + (file.getAbsolutePath().replace(originalLocationPath, destinationPath).length() + 1) + "): " + file.getAbsolutePath().replace(originalLocationPath, destinationPath) + "/");
	        		logLines.add("");
	        	}
	            
	            showFiles(file.listFiles()); // Calls same method again.
	        } else {
	        	
	        	//if(FilenameUtils.removeExtension(file.getName()).length() > fileNameRestiction) {
	        	//	System.out.println("File name can not exceed " + fileNameRestiction + " chars (" + FilenameUtils.removeExtension(file.getName()).length() + "): " + file.getName() + "; THE FULL PATH IS " + file.getAbsolutePath().replace(originalLocationPath, destinationPath));
	        	//	logLines.add("File name can not exceed " + fileNameRestiction + " chars (" + FilenameUtils.removeExtension(file.getName()).length() + "): " + file.getName() + "; THE FULL PATH IS " + file.getAbsolutePath().replace(originalLocationPath, destinationPath));
	        	//	renameFile(file, map, fileNameRestiction);
	        	//} else 
	        	if( file.getName().equals("05--Recent Regulatory Enforcement Trends.pdf")) {
	        		int p = 0;	        		
	        	}
	        	
	        	if(file.getAbsolutePath().replace("\'", "-").replace(originalLocationPath, destinationPath).length() > filePathRestriction) {
	        		System.out.println("FULL Path to File can not exceed " + filePathRestriction + " chars (" + file.getAbsolutePath().replace(originalLocationPath, destinationPath).length() + "): " + file.getAbsolutePath().replace(originalLocationPath, destinationPath));
	        		logLines.add("FULL Path to File can not exceed " + filePathRestriction + " chars (" + file.getAbsolutePath().replace(originalLocationPath, destinationPath).length() + "): " + file.getAbsolutePath().replace(originalLocationPath, destinationPath));
	        		//logLines.add(file.getName());
	        		renameFile(file, map, filePathRestriction);
	        		
	        	}
	        }
	    }
	    
	}
	
	/**
	 * Renames a file according to the provided restriction. 
	 * 
	 * @param file
	 * @param map
	 * @param restriction
	 * 		  	- 128 - the file name can not exceed 128 characters. The filename will be truncated to 128. 
	 * 			- 260 - the whole path including the file name can not exceed 260 characters. The filename will be truncated so the whole path (including the file name) is 260 characters
	 */
	public static void renameFile(File file, HashMap<String, String> map, int restriction) {
		
		String extension = FilenameUtils.getExtension(file.getName());
		String fileNameOnly = FilenameUtils.removeExtension(file.getName());
		int charactersToRemove = 0;
		if(restriction == fileNameRestiction) {
			charactersToRemove = file.getName().length() - restriction;
			
		} else if (restriction == filePathRestriction) {
			charactersToRemove = file.getAbsolutePath().replace(originalLocationPath,destinationPath).length() - restriction;
		}
		
		if(charactersToRemove == 0)
			charactersToRemove = 1;
		 
		String fileNameTruncated =  fileNameOnly.substring(0, fileNameOnly.length() - (charactersToRemove >=  fileNameOnly.length() ? fileNameOnly.length() : charactersToRemove)) + '.' + extension;
		
		String pathToFile = FilenameUtils.getFullPath(file.getAbsolutePath());
		
		File newFile = new File(pathToFile + fileNameTruncated);
		if(map.containsKey(pathToFile + fileNameTruncated)) { //after the truncation - a file with the same name already exists in the same folder. 
			for(int i = 33; i <= 125; i++) { //replace the last character of the name with a special symbol and then try to rename the file if it doesn't exist. 
											 //try to replace the last character 79 times... TO DO - implement a second algorithm to support more than 79 same file name changes in the same folder.
				//avoid characters that can't be a part of the file name like * \  /  "  :  <  > ...
				if(i == 34 || i == 39 || i == 42 || i == 46 || i == 47 || i == 58 || i == 37 || i == 47 || i == 60 || i == 61 || i == 62 || i == 63 || i == 124)
					continue;
				
				char ch = (char) i;
				fileNameTruncated =  fileNameOnly.substring(0, fileNameOnly.length() - (charactersToRemove >=  fileNameOnly.length() ? fileNameOnly.length() - 1 : charactersToRemove) - 1) + ch + '.' + extension;				
				newFile = new File(pathToFile + fileNameTruncated);
				if(!map.containsKey(pathToFile + fileNameTruncated)) {
					break;
				}
			}
		}
		//file.renameTo(newFile);  //uncomment to actually rename the files on the file system
		if(!map.containsKey(pathToFile + fileNameTruncated))
			map.put(pathToFile + fileNameTruncated, pathToFile + fileNameTruncated);
		
		System.out.println("RENAMED FILE TO: " + fileNameTruncated);
		System.out.println();
		logLines.add("RENAMED FILE FROM: " + file.getName() + " TO: " + fileNameTruncated);
		logLines.add("");
	}

}
