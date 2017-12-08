package com.findings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;


/*
 * 将文件名字前面加上 其包含的report number
 */
public class FileNameRename {
	
	public void renameFileNames ( String projectFolder, String nameReferFile ) {
		HashMap<String, Integer> nameReferMap = new HashMap<String, Integer>();
		
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader( new File ( nameReferFile)));
			String line = "";
			
			boolean isFirstLine = true;
			while ( ( line = br.readLine() ) != null ) {
				if ( isFirstLine == true ) {
					isFirstLine = false;
					continue;
				}
				String[] temp = line.split( ",");
				String name = temp[0];
				Integer reportNum = Integer.parseInt( temp[1] );
				
				nameReferMap.put( name, reportNum );
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		File projectsFolder = new File ( projectFolder );
		if ( projectsFolder.isDirectory() ){
			String[] projectFileList = projectsFolder.list();
			for ( int i = 0; i< projectFileList.length; i++ ){
				String fileName = projectFileList[i];
				String projectFileName = projectFolder + "/" + fileName;
				
				String key = fileName.substring( 0, fileName.length() - 4);
				int reportNum = nameReferMap.get( key );
				String newFileName = reportNum  + "-" + fileName;
				String newProjectFileName = projectFolder + "/" + newFileName;
				
				File file = new File( projectFileName );
				File newFile = new File( newProjectFileName );

				boolean success = file.renameTo( newFile);
				if (!success) {
				   System.out.println( "Rename failed!");
				}
			}	
		}
	}		
	

	public static void main ( String args[] ) {
		String projectFolder = "data/input/total crowdsourced reports";
		String nameReferFile = "data/output/findings/reportBugNumForProject.csv";
		
		FileNameRename renameTool = new FileNameRename ();
		renameTool.renameFileNames(projectFolder, nameReferFile);
	}
}
