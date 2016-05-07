import com.mongodb.*;
import com.sun.xml.internal.txw2.Document;

import sun.misc.Queue;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;


public class MongoDB_Play {
	
	
	static HashMap<Integer,ArrayList<Integer>> linker = new HashMap<Integer,ArrayList<Integer>>();
	static ArrayList<ArrayList<Integer>> final_paths_ret = new ArrayList<>();
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
	         System.out.println("Done");
				
	      }catch(Exception e){
	    	  System.out.println("Already added node id: " + node_id);
	    	  //System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      }
		
		
	}
	
	// store the index of nodes to node ID's
	void mongo_inv_store(String key, int value)
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
				final_paths_ret.add(path);
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
		        	System.out.println("n1:" + n1 );
		        	System.out.println("n2:" + n2 );
		        	ArrayList<Integer> alreadyVisited = new ArrayList<Integer>();
		        	ArrayList<Integer> path = new ArrayList<Integer>();
		        	Queue<Integer> visit = new Queue<Integer>();
		        	List<Integer> final_list = new ArrayList<Integer>();
		        	
		        	for(int temp_to_en = 0 ; temp_to_en<final_nodeids1.length; temp_to_en++)
		        		{
		        			visit.enqueue(Integer.parseInt(final_nodeids1[temp_to_en]));
		        			//System.out.println(Integer.parseInt(final_nodeids1[temp_to_en]));
		        		}
		        		
		        	
		          	for(int temp_to_en = 0 ; temp_to_en<final_nodeids2.length; temp_to_en++)
		        		{
		          			final_list.add(Integer.parseInt(final_nodeids2[temp_to_en]));
		          			//System.out.println(Integer.parseInt(final_nodeids2[temp_to_en]));
		        		} 	
		        	// add all the nodes to the queue
		        	
		        	// Algorithm to do BSF over the graph
		        	recurseBFS(n1, n2, alreadyVisited,path);
		        	
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
        for (Integer i: linker.keySet())
        {
        	System.out.println( " Node "+ i + " -------> " + linker.get(i).toString());
        }
	}
	
	public static void main(String args[]) throws UnknownHostException
	{
		MongoDB_Play m = new MongoDB_Play();
		m.mongo_store(1, "chirag", "shah", 1, "name", "type");
		m.mongo_store(2, "samarth", "shah", 3, "name", "type");
		m.mongo_store(3, "akshay", "shah", 3, "name", "type");
		m.mongo_store(4, "shah", "praladh", 3, "name", "type");
		m.mongo_store(5, "chirag", "pandey", 3, "name", "type");
		m.mongo_inv_store("chirag", 1);
		m.mongo_inv_store("shah", 1);
		m.mongo_inv_store("samarth", 2);
		m.mongo_inv_store("shah", 2);
		m.mongo_inv_store("akshay", 3);
		m.mongo_inv_store("shah", 3);
		m.mongo_inv_store("shah", 4);
		m.mongo_inv_store("praladh", 4);
		m.mongo_inv_store("chirag", 5);
		m.mongo_inv_store("pandey", 5);
		m.make_linker();
		m.resolveQuery("samarth pandey");
		if(final_paths_ret!=null)
		{
			for(ArrayList<Integer> i : final_paths_ret)
			System.out.println("Final path of nodes covered" + i.toString());
		}
	}
}

