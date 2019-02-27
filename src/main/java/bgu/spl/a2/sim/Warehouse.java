package bgu.spl.a2.sim;

import bgu.spl.a2.sim.tools.GcdScrewDriver;
import bgu.spl.a2.sim.tools.NextPrimeHammer;
import bgu.spl.a2.sim.tools.RandomSumPliers;
import bgu.spl.a2.sim.tools.Tool;
import bgu.spl.a2.sim.conf.ManufactoringPlan;

import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedDeque;

import bgu.spl.a2.Deferred;

/**
 * A class representing the warehouse in your simulation
 * 
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add to this class can
 * only be private!!!
 *
 */
public class Warehouse {
	//store tools
	ConcurrentLinkedDeque<GcdScrewDriver> gs_drivers;
	ConcurrentLinkedDeque<NextPrimeHammer> np_hammer;
	ConcurrentLinkedDeque<RandomSumPliers> rs_pilers;
	//tools that needed by products
	ConcurrentLinkedDeque<Deferred<Tool>> gs_drivers_queue;
	ConcurrentLinkedDeque<Deferred<Tool>> np_hammer_queue;
	ConcurrentLinkedDeque<Deferred<Tool>> rs_pilers_queue;
//plans to create products
	Vector<ManufactoringPlan> manufacturing_plans;
	
	/**
	* Constructor
	*/
    public Warehouse()
    {
    	gs_drivers = new ConcurrentLinkedDeque<GcdScrewDriver>();
    	np_hammer = new ConcurrentLinkedDeque<NextPrimeHammer>();
    	rs_pilers = new ConcurrentLinkedDeque<RandomSumPliers>();
    	manufacturing_plans=new Vector<ManufactoringPlan>();
    //	gs_drivers_count=new Integer(0);
    	//np_hammer_count=new Integer(0);
    	//rs_pilers_count=new Integer(0);
    	gs_drivers_queue=new ConcurrentLinkedDeque<Deferred<Tool>>();
    	np_hammer_queue=new ConcurrentLinkedDeque<Deferred<Tool>>();
    	rs_pilers_queue=new ConcurrentLinkedDeque<Deferred<Tool>>();
    }

	/**
	* Tool acquisition procedure
	* Note that this procedure is non-blocking and should return immediatly
	* @param type - string describing the required tool
	* @return a deferred promise for the  requested tool
	*/
    public synchronized Deferred<Tool> acquireTool(String type){

    	Deferred<Tool> ans=new Deferred<Tool>();
    	//cases for each tool
    	if (type.equals("gs-driver")){
    		//if(gs_drivers_count>0){//if there are tools available, get one immidietly
    		GcdScrewDriver resolving=gs_drivers.pollFirst();
    			if(resolving!=null){//if managed to get the tool, resolve ans with it
    				ans.resolve(resolving);
    			//	gs_drivers_count--;
    			}
    			else{
        			gs_drivers_queue.offerFirst(ans);
    			}
    				
    		}
    	else if(type.equals("np-hammer"))
    			{
    		NextPrimeHammer resolving=np_hammer.pollFirst();
    			if(resolving!=null){//if managed to get the tool, resolve ans with it
    				ans.resolve(resolving);
    			//	gs_drivers_count--;
    			}
    			else{//need to resolve it later
    				np_hammer_queue.offerFirst(ans);
    			}
    		
   		}
    	else if(type.equals("rs-pliers"))
		{
	RandomSumPliers resolving=rs_pilers.pollFirst();
		if(resolving!=null){//if managed to get the tool, resolve ans with it
			ans.resolve(resolving);
		}
		else{//need to resolve it later
			rs_pilers_queue.offerFirst(ans);
		}
	}
    	else{
    		throw new UnsupportedOperationException("not a supported type tool "+type);
    	}
    	return ans;
    }

	/**
	* Tool return procedure - releases a tool which becomes available in the warehouse upon completion.
	* @param tool - The tool to be returned
	*/
    public synchronized void releaseTool(Tool tool)
    {
    	if (tool.getType().equals("gs-driver")){//type of tool to realase
    		if(gs_drivers_queue.isEmpty())//if no one waiting for the tool
    		{//store the tool
    			gs_drivers.add((GcdScrewDriver) tool);
    		}
    		else
    		{
    	    	Deferred<Tool> tmp  =gs_drivers_queue.pollLast();//give  waiting someone the tool
    	    	tmp.resolve(tool);
    		}
    	}
    	else if (tool.getType().equals("np-hammer")){//type of tool to realase
    		if(np_hammer_queue.isEmpty())//if no one waiting for the tool
    		{//store the tool
    			np_hammer.add((NextPrimeHammer) tool);
    		}
    		else
    		{
    	    	Deferred<Tool> tmp  =np_hammer_queue.pollLast();//give someone waiting the tool
    	    	tmp.resolve(tool);

    		}
    	}
    	else if (tool.getType().equals("rs-pliers"))//type of tool to realase
    	{//store the tool
    		if(rs_pilers_queue.isEmpty()){//if no one waiting for the tool
    			rs_pilers.add((RandomSumPliers) tool);
    		}
    		else
    		{
        	    Deferred<Tool> tmp=rs_pilers_queue.pollLast();//give someone waiting the tool
        	    tmp.resolve(tool);
    		}
    	}
    	else{
    		throw new UnsupportedOperationException("not a supported type tool");//no such tool exsists
    	}
    }

    
	/**
	* Getter for ManufactoringPlans
	* @param product - a string with the product name for which a ManufactoringPlan is desired
	* @return A ManufactoringPlan for product
	*/
    public ManufactoringPlan getPlan(String product)
    {
   		for(ManufactoringPlan plan : manufacturing_plans)
   		{
   			if(product.equals(plan.getProductName()))//find product's manufacture plan
	   		{
	   			return plan;
	   		}
   		}
   		return null;
    }
	
	/**
	* Store a ManufactoringPlan in the warehouse for later retrieval
	* @param plan - a ManufactoringPlan to be stored
	*/
    public void addPlan(ManufactoringPlan plan)//todo add not contain plan;
    {    	
    	manufacturing_plans.add(plan);
    }
    
	/**
	* Store a qty Amount of tools of type tool in the warehouse for later retrieval
	* @param tool - type of tool to be stored
	* @param qty - amount of tools of type tool to be stored
	*/
    public void addTool(Tool tool, int qty){
    	//split for different cases of tool
    	if (tool.getType()=="gs-driver"){
    		
    		for(int i = 0; i<qty; i++){//as lobg as there are tools to add
    			Deferred<Tool> tmp=null;
    			if(!gs_drivers_queue.isEmpty()){
    				//check if there are Deferred waiting for this type tool, if so give the new tool to the first waiting
    				tmp=gs_drivers_queue.pollFirst();
    				if(tmp!=null)//assure you got a Deferred to resolve
    					tmp.resolve(new GcdScrewDriver());
    			}
    			if(tmp==null){
    				//if there aren't Deferred waiting for this toolType, add the new one to the tool pool
    				gs_drivers.offerFirst(new GcdScrewDriver());
    			}
    		}
    	}
    	else if (tool.getType()=="np-hammer"){
    		
    		for(int i = 0; i<qty; i++){//as lobg as there are tools to add
    			Deferred<Tool> tmp=null;
    			if(!np_hammer_queue.isEmpty()){
    				//check if there are Deferred waiting for this type tool, if so give the new tool to the first waiting
    				tmp=np_hammer_queue.pollFirst();
    				if(tmp!=null)//assure you got a Deferred to resolve
    					tmp.resolve(new NextPrimeHammer());
    			}
    			if(tmp==null){
    				//if there aren't Deferred waiting for this toolType, add the new one to the tool pool
        			np_hammer.offerFirst(new NextPrimeHammer());
    			}
    		}
    	}
    	else if (tool.getType()=="rs-pliers"){
    		
    		for(int i = 0; i<qty; i++){//as long as there are tools to add
    			Deferred<Tool> tmp=null;
    			if(!rs_pilers_queue.isEmpty()){
    				//check if there are Deferred waiting for this type tool, if so give the new tool to the first waiting
    				tmp=gs_drivers_queue.pollFirst();
    				if(tmp!=null)//assure you got a Deferred to resolve
    					tmp.resolve(new RandomSumPliers());
    			}
    			if(tmp==null){
    				//if there aren't Deferred waiting for this toolType, add the new one to the tool pool
    				rs_pilers.offerFirst(new RandomSumPliers());
    			}	
    		}
    	}
    	else{
    		throw new UnsupportedOperationException("not a supported type tool");
    	}
    }
    public synchronized void tryResolve(Deferred<Tool> tool_deffered){
    	
    	while((!gs_drivers.isEmpty())&&(gs_drivers_queue.size()>0))
    		gs_drivers_queue.getFirst().resolve(gs_drivers.getFirst());
    	while((!np_hammer.isEmpty())&&(np_hammer_queue.size()>0))
    		gs_drivers_queue.getFirst().resolve(np_hammer.getFirst());
    	while((!rs_pilers.isEmpty())&&(rs_pilers_queue.size()>0))
    		rs_pilers_queue.getFirst().resolve(rs_pilers.getFirst());
    }

}
