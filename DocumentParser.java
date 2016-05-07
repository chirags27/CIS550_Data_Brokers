import org.w3c.dom.*;
import org.w3c.tidy.Tidy;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class DocumentParser {

	static int nodeId = 0;
	static Map<String, Integer> nodeMapping = new HashMap<String, Integer>();
	static Map<String, List<Integer>> invertedIndexMap = new LinkedHashMap<String, List<Integer>>();
	static final String DOCNAME = "DATALAKE.XML";

	public static Document getDocument(String fileName) throws ParserConfigurationException, SAXException, IOException {
		
		File inputFile = new File(fileName);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(inputFile);
        doc.getDocumentElement().normalize();
        return doc;
	}
	
	public static Document parseXML(String filePath) throws IOException {
		
		byte[] xmlData = Files.readAllBytes(Paths.get(filePath));
		Tidy tidy = new Tidy(); // obtain a new Tidy instance
		tidy.setXmlTags(true);
        tidy.setInputEncoding("UTF-8");
        tidy.setOutputEncoding("UTF-8");
        tidy.setWraplen(Integer.MAX_VALUE);
        tidy.setShowErrors(0);
        tidy.setShowWarnings(false);
        tidy.setQuiet(true);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    	ByteArrayInputStream inputStream = new ByteArrayInputStream(xmlData);
    	Document doc = tidy.parseDOM(inputStream, byteArrayOutputStream);
    	return doc;
	}

	public static void traverse(NodeList nList, String parentName) { 

		String value = null;

		for (int temp = 0; temp < nList.getLength(); temp++) {
			
			Node nNode = nList.item(temp);
			String currentNodeName = nNode.getNodeName();
			
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				nodeId++;
				Element eElement = (Element) nNode;
				
//				NodeList nList = doc.getElementsByTagName(rootName);
				NodeList n = eElement.getElementsByTagName(currentNodeName);
				String firstChild = nNode.getChildNodes().item(0).getNodeName();
				if(nNode.hasChildNodes() && nNode.getChildNodes().getLength() > 1 && nNode.getChildNodes().item(0).getNodeName().equals("#text")) {
					// This loop should be iterated for nodes that dont have any value
					nodeMapping.put(currentNodeName + "_" + nodeId, 1);
//					System.out.println(currentNodeName + "_" + nodeId);
					if(value != null && value.equals(""))
						value = "NO_VAL";
					// Add entry to DB
					System.out.println(nodeId + "\t" + currentNodeName + "\t" + value + "\t" + parentName + "\t" + DOCNAME);
					addIndex(currentNodeName, nodeId);
					addIndex(value, nodeId);
				}
				
				if(nNode.hasChildNodes()) {
//					if(!nodeMapping.containsKey(nNode.getNodeName() + "_" + value))
//					{
//						id++;
//						nodeMapping.put(parentName+" "+ value,id);
//					}
					NodeList childNodesList = nNode.getChildNodes();
					int numChildNodes = childNodesList.getLength();
//					System.out.println(currentNodeName + " has " + numChildNodes + " children");
//					printChildren(currentNodeName, childNodesList);
					traverse(childNodesList, currentNodeName);
				}
			}
			else {
				
				value = nNode.getNodeValue().trim();
				String superParent = nNode.getParentNode().getParentNode().getNodeName();
				if(value != null && !value.equals("")) {

					// here parentName is the actual nodeName as we are in the iteration of the node's child which is the text value
					System.out.println(nodeId + "\t" + parentName + "\t" + value + "\t" + superParent + "\t\t" + DOCNAME);
					// Add entry to DB
					addIndex(parentName, nodeId);
					addIndex(value, nodeId);
				}
				
			}
		}
	}
	
	public static void addIndex(String index, int nodeId) {
		
		if(index == null)
			return;
		if(invertedIndexMap.containsKey(index)) {
			List<Integer> existingNodeIdList = invertedIndexMap.get(index);
			existingNodeIdList.add(nodeId);
			invertedIndexMap.put(index, existingNodeIdList);
		}
		else {
			List<Integer> nodeIdList = new ArrayList<Integer>();
			nodeIdList.add(nodeId);
			invertedIndexMap.put(index, nodeIdList);
		}
	}
	
	public static void printInvertedIndexMap() {
		
		for (Entry<String, List<Integer>> entry : invertedIndexMap.entrySet()) {
		    String key = entry.getKey();
		    List<Integer> nodeIdList = entry.getValue();
		    System.out.println(key + " : " + nodeIdList);
		}
	}
	public static void printChildren(String nodeName, NodeList nl) {
		System.out.println("*********** Printing children of " + nodeName);
		for(int i = 0; i < nl.getLength(); i++) {
			System.out.println(nodeName + " : " + nl.item(i));
		}
	}
	public static void main(String args[]) throws ParserConfigurationException, SAXException, IOException {
		
//		DocumentParser parser = new DocumentParser();
		String filePath = "datalake.xml";

		//get the root of the XML document
		Document doc = getDocument(filePath);
		Document doc2 = parseXML(filePath);
		Node root = doc.getDocumentElement();
		String rootName = root.getNodeName();
		System.out.println("ROOT: " + rootName);
		
		NodeList nList = doc.getElementsByTagName(rootName);
//		NodeList n = doc2.getChildNodes();
//		System.out.println("size: " + nList.getLength());
//		System.out.println("abc: " + n.item(1).getChildNodes().item(1).getNodeName());
//		System.out.println("abc: " + nList.item(0).getNodeName());
////		System.out.println("abc2: " + nList.item(0).getChildNodes().item(0));
//		System.out.println("abc2: " + nList.item(0).getChildNodes().item(1).getNodeName());
//		System.out.println("abc3: " + nList.item(0).getChildNodes().item(1).getChildNodes().item(1).getNodeName());
//		System.out.println("abc4: " + nList.item(0).getChildNodes().item(1).getChildNodes().item(1).getChildNodes().item(0).getNodeValue());
//
        System.out.println("----------------------------");
        System.out.println("NodeId \t KEY \t VALUE \t PARENT \t DOCNAME");
//        System.out.println(nList.item(0).getChildNodes().item(1));
        traverse(nList, rootName);
        
        System.out.println("\n\n************** Inverted Index Output ************");
        printInvertedIndexMap();
	}
}
