package bgu.spl.a2.sim.tasks;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import bgu.spl.a2.Task;
import bgu.spl.a2.sim.Product;
import bgu.spl.a2.sim.Warehouse;
import bgu.spl.a2.sim.conf.ManufactoringPlan;
public class ProductTask extends Task<Product> {
	
	ManufactoringPlan plan;
	Warehouse ware;
	Product product;
	AtomicInteger runningTasks;
	AtomicLong ans;
	/**
	 * ctor for tooltask that will create the product
	 * @param plan the plans to manufacture the product
	 * @param ware warehouse to acquire tools from
	 * @param product product to manufacture
	 */
	public ProductTask(ManufactoringPlan plan, Warehouse ware, Product product)
	{
		this.plan=plan;
		this.ware=ware;
		this.product=product;
		 runningTasks=new AtomicInteger(0);
		 ans=new AtomicLong();
	}
	
	/**
	 * increase the tasks done on tools
	 * it needs to be sinchronised as it could cause wrong increasing otherwise 
	 */
	private synchronized void inc(){
		boolean dgl=false;
		while(dgl==false){
			int x=runningTasks.get();
			dgl=runningTasks.compareAndSet(x,x+1);
		}
	}

		
	/**
	 * start the processing of the creation of the product according to the plans given in the ctor, using tools from the warehouse
	 * given in the ctor 
	 */
	@Override
	protected void start() {
		long id=this.product.getStartId();//keep the id of the corrent product
		if(this.plan.getParts().length>0){//in case there are parts need to be manufactured do this proccesing in order to manufacture them before the product itself
			List<Task<Product>> tasks=new Vector<Task<Product>>();//get the list of parts
			for(String partName : this.plan.getParts()){
				Product partProduct=new Product(id+1, partName);//create new product for each part with id +1 to the corrent's product id
				product.addPart(partProduct);//"save" the current product for use after done proccessing the parts
				ProductTask partTask=new ProductTask(ware.getPlan(partName), ware, partProduct);//create a new product task for the part
				tasks.add(partTask);//add the new task to the corrents product productTask list to wait for it
				spawn(partTask);//spawn new task
			}
		whenResolved(tasks,()->{//await the construction of all parts
			finishProduct();
		});
		}
		else{//if there are no parts need manufacturing finish manufacturing current product
			finishProduct();
		}
		
	}
	
		
	
		//finish manufacturing the product after it can be done(because it has no parts to wait for or they are finished) using the needed tools
		private void finishProduct(){
			if(0==ware.getPlan(product.getName()).getTools().length){
				summrise();//if there are no tools neded to be used done
				return;
			}
				
			for(int i=0; i<ware.getPlan(product.getName()).getTools().length; i++)//count all the tools needed for this product
			{
				this.inc();
			}
			
			Vector<Task<Long>> queue=new Vector<Task<Long>>();//a vector that will hold the toolTasks
			for(String toolName : ware.getPlan(product.getName()).getTools())//ittirate the needed tools
			{
				Task<Long> tool_task=new ToolTask(toolName,product,ware);//create the new tooltask
				queue.add(tool_task);//"save" that tool task
				spawn(tool_task);//spawn the new tooltask
			}
			whenResolved(queue,()->{//await the finishing of the tool tasks
				for(int i=0;i<queue.size();i++)
				{
					this.ans.set(ans.get()+queue.get(i).getResult().get());//sum up the results
				}
				summrise();//finish creating the product

			});
	
		}
		
		//finish creating the product
		private void summrise(){
			product.set_final_id(ans.get()+product.getStartId());
			complete(product);
		}

		}

					