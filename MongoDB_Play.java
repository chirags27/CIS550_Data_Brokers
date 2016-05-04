
import com.mongodb.*;
import com.sun.xml.internal.txw2.Document;

import java.net.UnknownHostException;
import java.util.Arrays;


public class MongoDB_Play {
	
	
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
		                	store_linker(Math.min(main_id, node_id_key),Math.max(main_id, node_id_key) , 1);
		  
	                }
	                while(cursor_value.hasNext())
	                {
	                	DBObject o_temp2 = cursor_value.next();
		                int node_id_key = (int) o_temp2.get("_id");
		                if(main_id!=node_id_key)	
		                	store_linker(Math.min(main_id, node_id_key),Math.max(main_id, node_id_key), 1);
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
		                	store_linker(Math.min(main_id, node_id_key),Math.max(main_id, node_id_key), 1);
		  
	                }
	                while(cursor_value.hasNext())
	                {
	                	DBObject o_temp2 = cursor_value.next();
		                int node_id_key = (int) o_temp2.get("_id");
		                if(main_id!=node_id_key)	
		                	store_linker(Math.min(main_id, node_id_key),Math.max(main_id, node_id_key), 1);
	                }
	         }
	         
		}
		catch(Exception e)
		{
			System.out.println("Error");
		}
	}
	
	
	
	public static void main(String args[])
	{
		MongoDB_Play m = new MongoDB_Play();
		m.mongo_store(1, "chirag", "shah", 1, "name", "type");
		m.mongo_store(2, "samarth", "shah", 3, "name", "type");
		m.mongo_store(3, "akshay", "shah", 3, "name", "type");
		m.mongo_store(4, "shah", "praladh", 3, "name", "type");
		m.make_linker();
//		m.mongo_inv_store("Hi", 1);
//		m.mongo_inv_store("Hi", 2);
//		m.mongo_inv_store("Hi", 3);
//		m.mongo_inv_store("Hi", 4);
	}
}

