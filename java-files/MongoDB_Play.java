import com.mongodb.*;
import com.sun.xml.internal.txw2.Document;

import sun.misc.Queue;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Stack;
import java.util.TreeMap;

import javax.imageio.ImageIO;


public class MongoDB_Play {
	
	
	static HashMap<Integer,ArrayList<Integer>> linker = new HashMap<Integer,ArrayList<Integer>>();
	static ArrayList<ArrayList<Integer>> final_paths_ret = new ArrayList<>();
	static ArrayList<ArrayList<Integer>> final_single = new ArrayList<>();
	void mongo_store(int node_id, String key, String value, int parent_id, String doc_name, String type )
	{
		try{
	         // To connect to mongodb server
	         MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
	         // Now connect to your databases
	         DB db = mongoClient.getDB( "test" );
	         //System.out.println("Connect to database successfully")
	         BasicDBObject document = new BasicDBObject();
	         document.put("_id", node_id);
	         document.put("key", key);
	         document.put("value", value);
	         document.put("parent_id", parent_id);
	         document.put("doc_name",doc_name);
	         document.put("type", type);
	         
	         db.getCollection("ext_table").insert(document);
	         mongo_inv_store(key, node_id);
	         mongo_inv_store(value, node_id);
	         System.out.println("Done");
	         mongoClient.close();
				
	      }catch(Exception e){
	    	  System.out.println("Already added node id: " + node_id);
	    	  //System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      }
		
		
	}
	
	// store the index of nodes to node ID's
	static void mongo_inv_store(String key, int value)
	{
		try{
	         // To connect to mongodb server
	         MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
	         DB db = mongoClient.getDB( "test" );
	         DBCollection collection = db.getCollection("inv_table"); 
	         DBCursor cursor = collection.find(new BasicDBObject("_id", key));
	         if(cursor.size() == 0)
	         {
	        	 BasicDBObject document = new BasicDBObject();
		         document.put("_id", key);
		         document.put("value_list", Integer.toString(value));
		         document.put("size", 1);
		         collection.insert(document);
		         System.out.println("Stored for the 1st time");
	         }
	         else if(cursor.size()==1)
	         {
	        	 String existing = (String) cursor.one().get("value_list");
	        	 int size = (int) cursor.one().get("size");
	        	 size += 1;
	        	 existing = existing + "_";
	        	 existing = existing + Integer.toString(value);
	        	 collection.remove(new BasicDBObject("_id", key));
	        	 BasicDBObject document = new BasicDBObject();
		         document.put("_id", key);
		         document.put("value_list", existing);
		         document.put("size", size);
		         collection.insert(document);
		         System.out.println("Appended");
	         }
	         mongoClient.close();
				
	      }catch(Exception e){
	         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      }
	}
	
	void store_linker(int node1, int node2, int weight)
	{
		try{
	         // To connect to mongodb server
	         MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
	         // Now connect to your databases
	         DB db = mongoClient.getDB( "test" );
	         //System.out.println("Connect to database successfully")
	         BasicDBObject document = new BasicDBObject();
	         DBCollection collection = db.getCollection("linker_table");
	         DBCursor cursor = collection.find(new BasicDBObject("n1", node1).append("n2", node2));
//	         
	         if(cursor.size() > 0)
	        	 return;
	         
	         document.put("n1", node1);
	         document.put("n2", node2);
	         document.put("wt", weight);
	         
	         
	         db.getCollection("linker_table").insert(document);
	         //System.out.println("Done");
	         mongoClient.close();
				
	      }catch(Exception e){
	    	  System.out.println("Exception");
	      }
	}
	
	void make_linker()
	{
		try{
	         // To connect to mongodb server
	         MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
	         DB db = mongoClient.getDB( "test" );
	         DBCollection collection = db.getCollection("ext_table"); 
	         DBCursor cursor = collection.find();
	         while(cursor.hasNext())
	         {
	        	 	DBObject o = cursor.next();
	                String key_name = (String) o.get("key");
	                int main_id = (int) o.get("_id");
	                // find this key in all the keys and values
	                DBCursor cursor_key = collection.find(new BasicDBObject("key", key_name));
	                DBCursor cursor_value = collection.find(new BasicDBObject("value", key_name));
	                
	                while(cursor_key.hasNext())
	                {

	                	DBObject o_temp1 = cursor_key.next();
	                	//String temp = (String) o_temp1.get("key");
		                int node_id_key = (int) o_temp1.get("_id");
		                if(main_id!=node_id_key)
		                	store_linker(main_id, node_id_key , 1);
		  
	                }
	                while(cursor_value.hasNext())
	                {
	                	DBObject o_temp2 = cursor_value.next();
		                int node_id_key = (int) o_temp2.get("_id");
		                if(main_id!=node_id_key)	
		                	store_linker(main_id, node_id_key, 1);
	                }
	         }
	         
	         cursor = collection.find();
	         while(cursor.hasNext())
	         {
	        	 	DBObject o = cursor.next();
	                String key_name = (String) o.get("value");
	                int main_id = (int) o.get("_id");
	                // find this key in all the keys and values
	                DBCursor cursor_key = collection.find(new BasicDBObject("key", key_name));
	                DBCursor cursor_value = collection.find(new BasicDBObject("value", key_name));
	                
	                while(cursor_key.hasNext())
	                {
	                	
	                	DBObject o_temp1 = cursor_key.next();
		                int node_id_key = (int) o_temp1.get("_id");
		                if(main_id!=node_id_key)
		                	store_linker(main_id, node_id_key, 1);
		  
	                }
	                while(cursor_value.hasNext())
	                {
	                	DBObject o_temp2 = cursor_value.next();
		                int node_id_key = (int) o_temp2.get("_id");
		                if(main_id!=node_id_key)	
		                	store_linker(main_id,node_id_key, 1);
	                }
	         }
	         mongoClient.close();
	         
		}
		catch(Exception e)
		{
			System.out.println("Error");
		}
	}
	
	
	
	void recurseBFS(int source,int destination,ArrayList<Integer> alreadyVisited,ArrayList<Integer> path) throws InterruptedException
	{
		
    		alreadyVisited.add(source);
    		path.add(source);
			if(destination == source)
			{
				//path.add(destination);
				//System.out.println(path.toString());
				ArrayList<Integer> temp_list = new ArrayList<>(path);
				final_paths_ret.add(temp_list);
				return;
			}

    		ArrayList<Integer> new_list= linker.get(source);

    		if(new_list == null)
    		{
    			return;
    		}
    		else
    		{
    			for(Integer temp_i : new_list)
    		{


    			if(alreadyVisited.contains(temp_i))
    			{
    				continue;
    			}
    			else
    			{
    				//visit.enqueue(temp_i);
    				
    	        	recurseBFS(temp_i, destination,alreadyVisited,path);
    	        	path.remove(path.size()-1);
    			}
    		}
    		}
    }
	
	void resolveQuery(String query)
	{
		String q[] = query.split(" ");
		
		int no_of_paths = 0;
		
		
		if(q.length ==1)
		{
			//only 1 word query
			try
			{
		         // To connect to mongodb server
		         MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
		         DB db = mongoClient.getDB( "test" );
		         DBCollection collection = db.getCollection("inv_table"); 
		         DBCursor cursor = collection.find(new BasicDBObject("_id", q[0]));
		         // return the document names corresponding to these id's 
		         if(cursor.size() == 0)
		         {
		        	 System.out.println("Nothing stored");
//		        	 return null;
		         }
		         else if(cursor.size() > 0)
		         {
		        	String res = (String) cursor.one().get("value_list");
		        	String final_results[] = res.split("_");
		        	// return the array list of node ids
		        	ArrayList<Integer> path_single = new ArrayList<Integer>();
		        	for(int temp = 0; temp< final_results.length; temp++)
		        		path_single.add(Integer.parseInt(final_results[temp]));
		        	
		        	final_single.add(path_single);
//		        	final_paths_ret.add(path_single);
//		        	return final_paths_ret;	
		        	//System.out.println("Node ID's obtained");
		         }
			}
			catch(Exception e)
			{
				//
			}
			
		}
		else if(q.length >=2)
		{
			// find q[0] find node id's and traverse until reached node id's of q1
			
			//only 1 word query
			try
			{
		         // To connect to mongodb server
		         MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
		         DB db = mongoClient.getDB( "test" );
		         DBCollection collection = db.getCollection("inv_table"); 
		         DBCursor cursor1 = collection.find(new BasicDBObject("_id", q[0]));
		         DBCursor cursor2 = collection.find(new BasicDBObject("_id", q[1]));
		         // return the document names corresponding to these id's 
		         if(cursor1.size() == 0 || cursor2.size() ==0)
		         {
		        	 System.out.println("No relation between the entered stuff");
		        	 System.exit(0);
		        	 return;
		         }
		         else if(cursor1.size() > 0 && cursor1.size() > 0)
		         {
		        	String res1 = (String) cursor1.one().get("value_list");
		        	String res2 = (String) cursor2.one().get("value_list");
		        	
//		        	System.out.println( "res1 :" + res1);
//		        	System.out.println( "res2 :" + res2);		        	
		        	String final_nodeids1[] = res1.split("_");
		        	String final_nodeids2[] = res2.split("_");
		        	// basic algo
		        	// search for links in the linker table
		        	// 1st start with node1, if resuls obtained great
		        	//else start with node2 as the starting point
		        	
		        	loadLinkerIntoMemory();
		
		        	int n1 =Integer.parseInt(final_nodeids1[0]);
		        	int n2 =Integer.parseInt(final_nodeids2[0]);
//		        	System.out.println("n1:" + n1 );
//		        	System.out.println("n2:" + n2 );
		        	ArrayList<Integer> alreadyVisited = new ArrayList<Integer>();
		        	ArrayList<Integer> path = new ArrayList<Integer>();
		        	Queue<Integer> visit = new Queue<Integer>();
		        	List<Integer> final_list = new ArrayList<Integer>();
		        	
		        	for(int temp_to_en = 0 ; temp_to_en<final_nodeids1.length; temp_to_en++)
		        		{
		        	for(int temp_to_en2 = 0 ; temp_to_en2<final_nodeids2.length; temp_to_en2++)
		        		{
		        			recurseBFS(Integer.parseInt(final_nodeids1[temp_to_en]), Integer.parseInt(final_nodeids2[temp_to_en2]), alreadyVisited,path);
		        			//System.out.println("here");
		        			path = new ArrayList<Integer>();
		        			alreadyVisited = new ArrayList<Integer>();
		        			
		        		}
		        	}

		          	
		        	
		         }
			}
			catch(Exception e)
			{
	        	System.out.println("Some error");
				//
			}
			
			
		}

	}
	
	
	public static void loadLinkerIntoMemory() throws UnknownHostException
	{
        // To connect to mongodb server
        MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
        // Now connect to your databases
        DB db = mongoClient.getDB( "test" );
        //System.out.println("Connect to database successfully")
        BasicDBObject document = new BasicDBObject();
        DBCollection collection = db.getCollection("linker_table");
        DBCursor cursor = collection.find();
        
        while(cursor.hasNext())
        {

        	DBObject o_temp1 = cursor.next();
            int node1 = (int) o_temp1.get("n1");
            int node2 = (int) o_temp1.get("n2");
            //linker.put(key, value)
            if(linker.containsKey(node1))
            {
            	linker.get(node1).add(node2);
            }
            else
            {
            	List<Integer> temp = new ArrayList<>();
            	temp.add(node2);
            	linker.put(node1,(ArrayList<Integer>) temp);     
        	}
	}
//        for (Integer i: linker.keySet())
//        {
//        	System.out.println( " Node "+ i + " -------> " + linker.get(i).toString());
//        }
	}
	
	String mapIDtoDocName(int nodeId)
	{
		try
		{
	         MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
	         DB db = mongoClient.getDB( "test" );
	         DBCollection collection = db.getCollection("ext_table"); 
	         DBCursor cursor = collection.find(new BasicDBObject("_id", nodeId));
	        
         	DBObject o_temp1 = cursor.next();
             String doc_name = (String) o_temp1.get("doc_name");
             mongoClient.close();
             return doc_name;
		}
		catch(Exception e)
		{
			return null;
			// Do nothing
		}
	}
	
	ArrayList<String> getNeighBoursOf(int node)
	{
		MongoClient mongoClient;
		try {
			MongoDB_Play md = new MongoDB_Play();
			ArrayList<String> toSend  = new ArrayList<>();
			mongoClient = new MongoClient( "localhost" , 27017 );
			// Now connect to your databases
	        DB db = mongoClient.getDB( "test" );
	        //System.out.println("Connect to database successfully")
	        BasicDBObject document = new BasicDBObject();
	        DBCollection collection = db.getCollection("linker_table");
	        DBCursor cursor = collection.find(new BasicDBObject("n1",node));
	        int neigh_count = 0;
	        while(neigh_count<5 && cursor.hasNext())
	        {
	        	DBObject o_temp1 = cursor.next();
	            int node2 = (int) o_temp1.get("n2");
	            toSend.add(Integer.toString(node2) );
	            neigh_count++;
	        }
	        return toSend;
	        
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			return null;
			//e.printStackTrace();
		}
        
	}
	
//	
	public static void main(String args[]) throws IOException
	{
		MongoDB_Play m = new MongoDB_Play();
////		m.mongo_store(1, "chirag", "shah", 1, "name", "type");
////		m.mongo_store(2, "samarth", "shah", 3, "name", "type");
////		m.mongo_store(3, "akshay", "shah", 3, "name", "type");
////		m.mongo_store(4, "shah", "praladh", 3, "name", "type");
////		m.mongo_store(5, "chirag", "pandey", 3, "name", "type");
////		m.mongo_store(6, "india", "mumbai", 1, "name", "type");
////		m.mongo_store(7, "karnataka", "bangalore", 3, "name", "type");
////		m.mongo_store(8, "shah", "india", 3, "name", "type");
////		m.mongo_store(9, "akshay", "bangalore", 3, "name", "type");
////		m.mongo_store(10, "samarth", "pandey", 3, "name", "type");
			m.make_linker();
//		TreeMap<Integer,ArrayList<String>> tm = new TreeMap<>();
//		String permission = "base";
//		
//		
//		if(args.length == 2) // for 1 word query -- word permission
//		{
//			m.resolveQuery(args[0]);
//			permission = args[1];
//			
//		}
//		else if(args.length == 3) // for 2 word query -- word word word_permission
//		{
//			m.resolveQuery(args[0] + " " + args[1]);
//			permission = args[2];
//		}
//		
//		//m.resolveQuery("Zachary IBM");
//
//		
//	//	String user_email_id = args[2];
//		
//		
//		
//		//ArrayList<ArrayList<String>> final_doc_connections = new ArrayList<>();
//		boolean first_time = true;
//		ArrayList<String> doc_single_conn = new ArrayList<>();
//		
//		ArrayList<String> list_docs = new ArrayList<>();
//
//		if(final_paths_ret!=null && final_paths_ret.size() >0)
//		{
//			for(ArrayList<Integer> i : final_paths_ret)
//			{
//				
//				//System.out.println("Final path of nodes covered" + i.toString());
//				int path_len = 0;
//				for(Integer a: i)
//				{
//					//System.out.println("----------" + check++);
//					String retval = m.mapIDtoDocName(a);
//					//System.out.println("XXXXXXXXXX" + check++);
//					if(retval!=null)
//					{
//						retval = retval + "_" +  a.toString();
//						list_docs.add(retval);
//					}
//					path_len++;
//				}
//				//System.out.println(list_docs.toString());
//				
//				// put into the tree map according to the permissions
//				if(permission.equals("root"))
//				{	
//				tm.put(path_len, list_docs);
//				//final_doc_connections.add(list_docs);
//				list_docs = new ArrayList<>();
//
//				}
//				else
//				{
//					boolean add_flag = true;
//					// only add this if the path does not contain any pdf and csv
//					for(String filter: list_docs)
//					{
//						//System.out.println(filter + "-- > " + filter.contains("."));
//						if(filter.contains(".") == false) //implies pdf and csv
//						{
//							add_flag = false;
//							break;
//						}
//					}
//					//System.out.println("Done");
//					if(add_flag == true)   // Only add to this if the the user is permitted to view stuff
//					{
//						tm.put(path_len, list_docs);
//						//final_doc_connections.add(list_docs);
//						//list_docs = new ArrayList<>();
//					}
//					list_docs = new ArrayList<>();
//
//				}
//			}
//			
//			//System.out.println("here");
//			//print the paths in sorted order
//			LinkedHashMap<String, ArrayList<String>> toSendDraw = new LinkedHashMap<>();
//			int show_count = 0;
//			
//			if(tm.keySet().size() == 0)
//			{
//				System.out.println("No relation between the entered stuff");
//				System.exit(0);
//			}
//			
//			for(Integer i: tm.keySet() )
//			{
//				if(show_count == 6)
//					
//				{
//					break;
//				}
//				ArrayList<String> temp_list_fp = tm.get(i);
//				
//				for(String i_i : temp_list_fp )
//				{
//					//System.out.println(i_i);
//					String spl[] = i_i.split("_");
//					int node_id_forn = Integer.parseInt(spl[1]);
//					toSendDraw.put(i_i, m.getNeighBoursOf(node_id_forn)) ;
//				}
//				// Send this to Prahladh's draw function
//				
//				if(first_time == true)
//				{
//			        Graph graphdraw = new Graph("Output");
//
//			        graphdraw.setSize(2000,2000);
//			        
//			        graphdraw.setVisible(true);
//			        
//					GraphDrawTest.fetchData(graphdraw, toSendDraw);
//					 BufferedImage bImg = new BufferedImage(graphdraw.getWidth(), graphdraw.getHeight(), BufferedImage.TYPE_INT_RGB);
//					    Graphics2D cg = bImg.createGraphics();
//					    graphdraw.paintAll(cg);
//					    try {
//					            if (ImageIO.write(bImg, "png", new File("./output_image.png")))
//					            {
//					                //System.out.println("-- saved");
//					            }
//					    } catch (IOException e) {
//					            // TODO Auto-generated catch block
//					            e.printStackTrace();
//					    }
//					    
//				        graphdraw.setVisible(false);
//					    
//					    
//					    first_time = false;
//				
//				}
//
//				
//				System.out.println(tm.get(i));
//				show_count++;
//			}
//				
//			
//			System.exit(0);
//		}
//		else if(final_single!=null && final_single.size()>0)
//		{
//			for(ArrayList<Integer> i : final_single)
//			{
//				for(Integer a: i)
//				{
//					String retval = m.mapIDtoDocName(a);
//					if(retval!=null)
//					{
//						if( permission.equals("base") )
//						{
//							if(retval.contains(".") == true)
//							{
//								retval = retval + "_" + a.toString();
//								doc_single_conn.add(retval);
//							}
//
//						}
//						else
//						{
//							retval = retval + "_" + a.toString();
//							doc_single_conn.add(retval);
//						}
//
//						
//					}
//					
//				}
//				System.out.println("Final List for a single word query" + doc_single_conn.toString());
//				
//			}
//			//System.out.println("Node ID's with this query" + i.toString());
//			System.exit(0);
//		}
//		else
//		{
//			System.out.println("No relation between the entered stuff");
//			System.exit(0);
//		}

	}
}

