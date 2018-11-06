import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class Extractor {
	
	public ArrayList<String> finalList = new ArrayList<String>();	
	// store the dependencies of current .java file based on import keywords
	public void extract (String fromFilePath){
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(fromFilePath)); 
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("import org.apache")){
			        String toFilePath = line.substring(7);
			        toFilePath.replace(";", ".java");
					finalList.add(fromFilePath+","+toFilePath);
				}
		    }
		}
		catch (IOException e){
			System.out.println(e.getMessage());
		}
	}
	
	
	public ArrayList<String> storeDirectoryContentPath(File dir){
		ArrayList<String> javaFilePath = new ArrayList<String>();

		try {
			File[] files = dir.listFiles();
			for (File file : files){
				if (file.isDirectory())
					storeDirectoryContentPath(file);
				else
				{
					String currFilePath = file.getCanonicalPath();
					if (currFilePath.contains(".java"))
						javaFilePath.add(currFilePath);
				}
			}
		} 
		catch (IOException e){
			System.out.println(e.getMessage());
		}
		
		return javaFilePath;
	}
	
	
	public static void main (String[] args){
		
		
	}
	

}
