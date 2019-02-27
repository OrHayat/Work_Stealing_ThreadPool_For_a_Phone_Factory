package bgu.spl.a2.sim.tools;

import bgu.spl.a2.sim.Product;

public class RandomSumPliers implements Tool {
	final String type;
	public RandomSumPliers()
	{
	type="rs-pliers";	
	}
	@Override
	public String getType() {
		return this.type;
	}
	
	@Override
	public long useOn(Product p) {
		long val=0;
		for(Product part : p.getParts())
		{
			val+=Math.abs((use(part.getFinalId())));
			
		}
		return val;
	}

	public long use(long id) {
		java.util.Random rnd=new java.util.Random(id);
		long amount_to_collect=id%10000;
		long res=0;
		for(long i=0;i<amount_to_collect;i++)
		{
			res=res+rnd.nextInt();
		}
		return res;
	}
	public String toString(){	
		return "type is "+type;	
	}
}
