/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.sim;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import bgu.spl.a2.WorkStealingThreadPool;
import bgu.spl.a2.sim.conf.ManufactoringPlan;
import bgu.spl.a2.sim.tasks.ProductTask;
import bgu.spl.a2.sim.tools.GcdScrewDriver;
import bgu.spl.a2.sim.tools.NextPrimeHammer;
import bgu.spl.a2.sim.tools.RandomSumPliers;
import bgu.spl.a2.sim.tools.Tool;
//We most defiantly need each and every one of those imports!!!!!!

/**
 * A class describing the simulator for part 2 of the assignment
 */
public class Simulator {
	
	static WorkStealingThreadPool myPool=null;
	static Wave[][] waves;
	static Warehouse myWarehouse;
	static int threads;
	
	
	/**
	* Begin the simulation
	* Should not be called before attachWorkStealingThreadPool()
	 * @throws InterruptedException 
	*/
    public static ConcurrentLinkedQueue<Product> start() throws InterruptedException
    {
    	myPool.start();
    	ConcurrentLinkedQueue<Product> ans=new ConcurrentLinkedQueue<Product>();
    	for(Wave[] wave : waves){//Iterate the waves 2d array

    		int overallqty=count_tasks(wave);//count how many products wave got
        	CountDownLatch cdlatch = new CountDownLatch(overallqty);//starts with the number of products in this wave, and decreased each time product is made   		
        	for(Wave productWave : wave){//Iterate all products in this wave( 1d array)
    			long id = productWave.getstartId();//get product id
    			String name = productWave.getproduct();//get product name
    			ManufactoringPlan plan=myWarehouse.getPlan(name);//get product plan from warehouse.
    			
    			//params for all products of this type int this wave
    			for(int i=0; i<productWave.getqty(); i++){//create qty products with those params

    				Product currentProduct=new Product(id, name);//new product, before it's been "created" by the tools
    				ans.add(currentProduct);//save the product adress
    				ProductTask newPTask = new ProductTask(plan, myWarehouse, currentProduct);//add new product task into pool
    				newPTask.getResult().whenResolved(()->{
    					countdown(cdlatch);//we want to know when the creation of all products in this wave is finished 
    				});
    				id++;//increase the id for the next product with the same type
    				myPool.submit(newPTask);
    			}
    		}
    		cdlatch.await();//wait until all products in current wave are done beeing created
    		//TODO maybe try catch.  
    		
    		
    	}
		myPool.shutdown();    	//shutdown the threadingpool
   	
    	return ans;
    }
	
    /**
     * 
     * @param arr array of waves object-containing product data. 
     * @return how many products the wave contains
     */
    private static int count_tasks(Wave[] arr)
    {
    	int res=0;
    	for(int i=0;i<arr.length;i++)
    	{
    		res=res+arr[i].getqty();
    	}
    	return res;
    }
	/**
	* attach a WorkStealingThreadPool to the Simulator, this WorkStealingThreadPool will be used to run the simulation
	* @param myWorkStealingThreadPool - the WorkStealingThreadPool which will be used by the simulator
	*/
	public static void attachWorkStealingThreadPool(WorkStealingThreadPool myWorkStealingThreadPool)
	{
		myPool=myWorkStealingThreadPool;
	}
	/**
	 * 
	 * @param args -arguments to the program at arg[0] json file of the simulator
	 * @return 0
	 */
	public static int main(String [] args)
	{
		BufferedReader reader = null;
		myWarehouse=new Warehouse();//create new warehouse
		try {//read from file
	    	reader = new BufferedReader(new FileReader(args[0]));//take the json file from args
	    	Gson gson = new GsonBuilder().create();
	    	JsonReader json = gson.fromJson(reader,JsonReader.class);//read how many threads pool will have
	    	threads=json.threads;
	    	if(myPool==null){
	    		WorkStealingThreadPool pool=new WorkStealingThreadPool(threads);
	    		attachWorkStealingThreadPool(pool);
	    	}
	    	Tools[] toolArr=json.tools;//create array of tools  from json file
	    	for(Tools currentTool : toolArr)//pharse the Tool array into warehouse stock 
	    	{
	    		myWarehouse.addTool(createTool(currentTool.gettype()), currentTool.getqty());
	    	}	    	
	    	ManufactoringPlan[] plans=json.plans;//read manufactoring plans from file	    	
	    	for(ManufactoringPlan plan: plans)//add manufcatoring plans into warehouse stock
	    	{
	    		myWarehouse.addPlan(plan);
	    	}
	    	waves=json.waves;//read wave 2d array from json file


	    }
	    catch (FileNotFoundException ex) {
	    	//TODO add stuff and maybe finally
	    }
	    
    	ConcurrentLinkedQueue<Product> SimulationResult;

    	try{
    	//SimulationResult = SimulatorImpl.start();
       	SimulationResult = start();//the results of the simulator
    	FileOutputStream fout = new FileOutputStream("result.ser");//write the output to file
    	ObjectOutputStream oos = new ObjectOutputStream(fout);
    	oos.writeObject(SimulationResult);//write the output
    	oos.close();//close the file output stream.
    	}
    	catch(InterruptedException e){System.out.println("interrupted exception in  simulator.start");}
    	catch(IOException e){e.printStackTrace();}
		return 0;
	}
	
	/**
	 * a synchronised function for latch.countdown, needed to assure there aren't interruptions while finalising products and counting how many of the current wave 
	 * have been finalised
	 * @param cdlatch
	 */
	private static synchronized void countdown(CountDownLatch cdlatch)
	{
		cdlatch.countDown();
	}
	
	//returns an instance of a tool according to the name given
	private static Tool createTool(String name){
		Tool ans;
		//=null;
		switch(name)
		{
		case "gs-driver":
		{
			ans=new GcdScrewDriver();
			break;
		}
		case "np-hammer": 
		{
			ans = new NextPrimeHammer();
			break;
		}
		case "rs-pliers":
		{
			ans = new RandomSumPliers();
			break;

		}
        default:{
        	throw new UnsupportedOperationException("not a supported tool! in createTool");
        }
		}
        return ans;
	}
	
}
	
