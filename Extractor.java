import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.opencsv.CSVWriter;
import com.sun.xml.internal.ws.util.StringUtils;


public class Extractor {
	
	public ArrayList<String> finalList = new ArrayList<String>();	
	
	public ArrayList<String> javaFilePath = new ArrayList<String>();
	// store the dependencies of current .java file based on import keywords
	public void extract (String fromFilePath){
		
		String fromFileUrl = "";
		Pattern pattern = Pattern.compile("hbase-2.1.0.*$");
		Matcher matcher = pattern.matcher(fromFilePath);
		if (matcher.find()){
			//System.out.println("test");
			fromFileUrl = matcher.group();
		}
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(fromFilePath)); 
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("import org.apache")){
			        String toFilePath = line.substring(7);
			        toFilePath = toFilePath.replace(";", ".java");			        
			        toFilePath = toFilePath.replaceAll("[.]", "/");
			        toFilePath = toFilePath.replace("/java", ".java");
			        File toFileDir = new File(toFilePath);
			        //toFileDir = toFileDir.getAbsoluteFile();
			        toFilePath = findParentDirectory(fromFileUrl,toFileDir);
			        if (toFilePath.contains("hbase-2.1.0"))
			        {
				        String toFileUrl = "";
						Pattern pattern2 = Pattern.compile("hbase-2.1.0.*$");
						Matcher matcher2 = pattern2.matcher(fromFilePath);
						if (matcher2.find()){
							//System.out.println("test");
							toFileUrl = matcher2.group();
							toFilePath = toFileUrl;
						}
			        }
					finalList.add(fromFileUrl+","+toFilePath);
				}
		    }
		}
		catch (IOException e){
			System.out.println(e.getMessage());
		}
	}
	
	
	public String findParentDirectory(String fromDir,File targetPath){
		
		//System.out.println(fromDir+" : "+targetPath.toString()+" ------------------->");
		for (int i = 0;i < javaFilePath.size();i++){
			String curr = javaFilePath.get(i);
			if (curr.contains(targetPath.toString()))
				return javaFilePath.get(i);
				//System.out.println(javaFilePath.get(i));
		}
		
		return targetPath.toString();
	
	}
	
	public void storeDirectoryContentPath(File dir){

		try {
			File[] files = dir.listFiles();
			for (File file : files){
				if (file.isDirectory())
					storeDirectoryContentPath(file);
				else
				{
					String currFilePath = file.getCanonicalPath();
					//System.out.println(currFilePath);
					if (currFilePath.contains(".java")){
						//System.out.println(currFilePath);
						javaFilePath.add(currFilePath.toString());
					}
				}
			}
		} 
		catch (IOException e){
			System.out.println(e.getMessage());
		}
		
		//System.out.println(javaFilePath.size());
		//return javaFilePath;
	}
	
	
	public static void main (String[] args){
		Extractor test = new Extractor();
		File homeDir = new File("/Users/Mohammad/Documents/eecs4314/hbase-2.1.0");
		try {
			test.storeDirectoryContentPath(homeDir);
			ArrayList<String> urls = test.javaFilePath;
			//System.out.println(test.javaFilePath.size());
			for (int i = 0;i < urls.size();i++){
				//System.out.println(urls.get(i));
				test.extract(urls.get(i));
			}
			
			//test.extract(urls.get(100));
			//System.out.println(test.finalList.size());
			
			
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		
		try {
			CSVWriter writer = new CSVWriter(new FileWriter("manualResult2.csv"));
			String[] header = {"From File","To File"};
			writer.writeNext(header);
			
			for (int i = 0;i < test.finalList.size();i++){
				String[] item = test.finalList.get(i).split(",");
				//System.out.println(test.finalList.get(i));
				writer.writeNext(item);
			}
			
			writer.close();
			
		}
		 catch (IOException e){
			 System.out.println(e.getMessage());
		 }
		
		
		
		
	}
	

}
