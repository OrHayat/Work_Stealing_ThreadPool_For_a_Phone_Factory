package bgu.spl.a2.sim.tasks;

import bgu.spl.a2.Deferred;
import bgu.spl.a2.Task;
import bgu.spl.a2.sim.Product;
import bgu.spl.a2.sim.Warehouse;
import bgu.spl.a2.sim.tools.Tool;

public class ToolTask extends Task<Long> {

	Warehouse ware;
	Product product;
	String tool_name;
	/**
	 * 
	 * @param tool_name the name of the tool used
	 * @param product a product that the toll will be used on
	 * @param ware the warehouse to acquire the tool from
	 * construct new tooltask
	 */
	ToolTask(String tool_name,Product product,Warehouse ware)
	{
		this.product=product;
		this.tool_name=tool_name;
		this.ware=ware;
	}
	
	
	@Override
	/**
	 * do the tooltask
	 */
	protected void start() {
		Deferred<Tool> tmp=ware.acquireTool(tool_name);//get tool from warehouse
		tmp.whenResolved(()->{
			long res=tmp.get().useOn(product);
			ware.releaseTool(tmp.get());//relase tool
			complete(res);
		});
		
	}

}
