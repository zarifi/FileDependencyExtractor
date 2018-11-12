import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.opencsv.CSVWriter;
import com.sun.xml.internal.ws.util.StringUtils;


public class Extractor {
	
	public ArrayList<String> finalList = new ArrayList<String>();	
	
	public ArrayList<String> fileDifferenceList = new ArrayList<String>();

	public ArrayList<String> javaFilePath = new ArrayList<String>();
	
	public ArrayList<String> instances = new ArrayList<String>();
	
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
			        //System.out.println(fromFileUrl+" -------> "+toFilePath);
			        toFilePath = findParentDirectory(fromFileUrl,toFileDir);
			        //System.out.println(fromFileUrl+" -------> "+toFilePath);

			        //System.out.println(toFilePath);
			        if (toFilePath.contains("hbase-2.1.0"))
			        {
				        String toFileUrl = "";
						Pattern pattern2 = Pattern.compile("hbase-2.1.0.*$");
						Matcher matcher2 = pattern2.matcher(toFilePath);
						if (matcher2.find()){
							//System.out.println("test");
							toFileUrl = matcher2.group();
							toFilePath = toFileUrl;
						}
			        }
			        if (toFilePath.startsWith("org"))
			        	fileDifferenceList.add(fromFileUrl+","+toFilePath);
					finalList.add("cLinks "+fromFileUrl+" "+toFilePath);
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
						instances.add("$INSTANCE "+currFilePath.toString()+" cFile");
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
			//writer.writeNext(header);
			
//			for (int i = 0;i < test.finalList.size();i++){
//				//String[] item = test.finalList.get(i).split(",");
//		
//				//System.out.println(item[0]+ "  --->  "+item[1]);
//			    //writer.writeNext(item);
//			}
			
			writer.close();
			
		}
		 catch (IOException e){
			 System.out.println(e.getMessage());
		 }
		
		try {
			CSVWriter writer = new CSVWriter(new FileWriter("manualResultDiff.csv"));
			String[] header = {"From File","To File"};
			//writer.writeNext(header);
			
//			for (int i = 0;i < test.fileDifferenceList.size();i++){
//				String[] item = test.fileDifferenceList.get(i).split(",");
//				System.out.println(test.finalList.get(i));
//				//writer.writeNext(item);
//			}
			
			//writer.close();
			
		}
		 catch (IOException e){
			 System.out.println(e.getMessage());
		 }
		
		
		try {
    		 PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("/Users/Mohammad/Documents/workspace/DependencyExtractor/src/manualDependency.ta", true)));
    		 out.println("FACT TUPLE :");
    		 for (int i = 0;i < test.instances.size();i++){
    			    String instanceUrl = "";
    				Pattern pattern = Pattern.compile("hbase-2.1.0.*$");
    				Matcher matcher = pattern.matcher(test.instances.get(i));
    				if (matcher.find()){
    					//System.out.println("test");
    					instanceUrl = matcher.group();
    				}
    				System.out.println(instanceUrl);
    			 out.println("$INSTANCE "+instanceUrl);
    		 }
    		 
    		 for (int i = 0;i < test.finalList.size();i++){
 				out.println(test.finalList.get(i));
 			}   
    		 //out.println(lastId+","+sender+","+message + "\n");
			 out.close();
    } catch (IOException e) {}
		
		
		
	}
	

}
