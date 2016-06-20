import java.io.*;
import java.util.Scanner;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.commons.io.FilenameUtils;

import org.xml.sax.SAXException;

public class Pdf_parsing {

   static int nodeId = 10000;
   
   public static void main(final String[] args) throws IOException,TikaException {
	   MongoDB_Play md = new MongoDB_Play();
	   
	  Scanner in = new Scanner(System.in);
	  String folder_path = "./src/pdf_data";
	  File folder = new File(folder_path);
	  String filename = "";
	  File all_filename[] = folder.listFiles();  
	  for(int temp_len = 0; temp_len< all_filename.length; temp_len++)
	  {
		 
	  filename = all_filename[temp_len].getName();
	  //System.out.println(filename);
	  String inputPath = folder_path + "/" + filename;
	  File input;
	  String outputFileName;
	  
      BodyContentHandler handler = new BodyContentHandler();
      Metadata metadata = new Metadata();
      input = new File(inputPath);
      FileInputStream inputstream =
    		  new FileInputStream(input);      
      ParseContext pcontext = new ParseContext();
      
      //parsing the document using PDF parser
      PDFParser pdfparser = new PDFParser(); 
      try {
		pdfparser.parse(inputstream, handler, metadata,pcontext);
	} catch (SAXException e) {
		// Auto-generated catch block
		//e.printStackTrace();
	}
	
      
      String[] metadataNames = metadata.names();
      md.mongo_store(nodeId++, "filename", filename.substring(0,filename.length()-4), 0, filename.substring(0,filename.length()-4), "pdf");
      
      if(metadata.get("meta:author")!=null)
      {
      for(String name_temp: metadata.get("meta:author").split(" ") )
      {
    	  md.mongo_store(nodeId++, "author", name_temp, 0, filename.substring(0,filename.length()-4), "pdf");
      }
      }
    	  
      md.mongo_store(nodeId++, "content", handler.toString().trim().substring(0, 100).trim() , 0, filename, "pdf");   
	  }
      //writer.close();

   }
}