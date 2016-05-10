import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;

import org.xml.sax.SAXException;

public class TextParser {
	
public static void main(final String[] args) throws IOException,SAXException, TikaException {

	
	MongoDB_Play md = new MongoDB_Play();
	int nodeId = 30000;
   //Assume sample.txt is in your current directory
   File file = new File("./src/pdf_data/" + "data-professor.csv");
   
   //parse method parameters
   Parser parser = new AutoDetectParser();
   BodyContentHandler handler = new BodyContentHandler();
   Metadata metadata = new Metadata();
   FileInputStream inputstream = new FileInputStream(file);
   ParseContext context = new ParseContext();
   
   //parsing the file
   parser.parse(inputstream, handler, metadata, context);
   
   String contents[] = handler.toString().split("\n");
  
   int count = 0;
   
   String base[] = contents[0].split(",");
   
   for (String i: contents)
   {
 	  
 	  if(count > 0)
 	  {
 		String temp[] = contents[count].split(",");
 		int inner_count = 0;
 		for(String store: temp)
 		{
 			md.mongo_store(nodeId++, base[inner_count], store, 0, "data-professor", "csv"); 
 			inner_count++;
 		}
 	  }
 	  count++;
 	  
 	  
   }
   
//   System.out.println("File content : " + handler.toString());
   }
}