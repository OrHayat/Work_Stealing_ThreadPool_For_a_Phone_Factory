package bgu.spl.a2.sim.tools;

import bgu.spl.a2.sim.Product;

public class GcdScrewDriver implements Tool {
final	String type;

	public GcdScrewDriver()
	{
		type="gs-driver";
	}

	@Override
	public String getType() {
		return type;
	}
/*
 * 
 * 
 *(non-Javadoc)
 * @see bgu.spl.a2.sim.tools.Tool#useOn(bgu.spl.a2.sim.Product)
 */
	
	public String toString(){	
		return "type is "+type;	
	}
	@Override
	public long useOn(Product p) {
		long val=0;
		for(Product part : p.getParts())
		{
			val+=Math.abs(use(part.getFinalId()));
			
		}
		return val;
	}
	
	private static long use(long id)
	{
		long assurePositive=Math.abs(id);
		long r=reverse(assurePositive);
		long ans=GCD(assurePositive, r);
		if(id<0)
			ans=-ans;
		return ans;
	}
	
	private static long reverse(long r){
		long ans=0;
		long original=r;
		while(original!=0){
			ans=ans*10;
			ans=ans+original%10;
			original=original/10;
		}
				
		return ans;
	}
	
	private static long GCD(long a, long b) {
		   if (b==0) return a;
		   return GCD(b,a%b);
	}
	

}
