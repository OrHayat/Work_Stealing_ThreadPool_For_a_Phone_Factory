package bgu.spl.a2.sim;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

/**
 * A class that represents a product produced during the simulation.
 */
public class Product implements Serializable {
	  List<Product> parts;
	  final long start_id;
	  final String name;
	  long final_id;
	
	  public String toString(){
		  String ans="ProductName: "+name +"  Product Id = "+final_id+"\n PartsList {\n";
		  for(Product part : parts){
			  ans=ans+" "+part.toString()+"\n";
		  }
		  ans=ans+"}";
		  return ans;
	  }
	  
	  
	/**
	* Constructor 
	* @param startId - Product start id
	* @param name - Product name
	*/
    public Product(long startId, String name)
    {
    	this.start_id=startId;
    	this.name=name;
    	final_id=startId;
    	parts=new Vector<>();
    }
	/**
	* @return The product name as a string
	*/
    public String getName()
    {
    	return this.name;
    }

	/**
	* @return The product start ID as a long. start ID should never be changed.
	*/
    public long getStartId()
    {
    	return this.start_id;
    }
    
	/**
	* @return The product final ID as a long. 
	* final ID is the ID the product received as the sum of all UseOn(); 
	*/
    public long getFinalId()
    {
    	return this.final_id;
    }

	/**
	* @return Returns all parts of this product as a List of Products
	*/
    public List<Product> getParts()
    {
    	return (this.parts);
    }

	/**
	* Add a new part to the product
	* @param p - part to be added as a Product object
	*/
    public void addPart(Product p)
    {
    	this.parts.add(p);
    }
    public synchronized void set_final_id(long final_id)
    {
    	this.final_id=final_id;
    }


}
