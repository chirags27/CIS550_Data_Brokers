package pdf_parsing;

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
	   
	  Scanner in = new Scanner(System.in);
	  String inputPath = in.nextLine();
	  File input, output;
	  String outputFileName;
	  
      BodyContentHandler handler = new BodyContentHandler();
      Metadata metadata = new Metadata();
      input = new File(inputPath);
      FileInputStream inputstream =
    		  new FileInputStream(input);
    				  /* "C:/Users/Prahalad/Desktop/databases/project/Project_doc.pdf" */
      outputFileName = FilenameUtils.removeExtension("parsed_".concat(input.getName()));
      outputFileName = outputFileName.concat(".txt");
      output = new File(outputFileName);
      
      ParseContext pcontext = new ParseContext();
      
      //parsing the document using PDF parser
      PDFParser pdfparser = new PDFParser(); 
      try {
		pdfparser.parse(inputstream, handler, metadata,pcontext);
	} catch (SAXException e) {
		// Auto-generated catch block
		e.printStackTrace();
	}
      
      PrintWriter writer = new PrintWriter(output, "UTF-8");

      
      
      
      //getting metadata of the document
      //System.out.println("Metadata of the PDF:");
      String[] metadataNames = metadata.names();
      
      for(String name : metadataNames) {
         writer.println(nodeId++ + ", " + name + ", " + metadata.get(name) + ",");
      }
      
      //getting the content of the document
      writer.println(nodeId + ", content, " + handler.toString());
      
      writer.close();

   }
}
