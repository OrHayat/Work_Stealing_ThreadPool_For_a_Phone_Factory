package bgu.spl.a2.sim.tools;

import bgu.spl.a2.sim.Product;

public class NextPrimeHammer implements Tool {
	final String type;

	public NextPrimeHammer()
	{
		type="np-hammer";
	}
	@Override
	public String getType() {
		return type;
	}

	@Override
	public long useOn(Product p) {
		
		long val=0;
		for(Product part : p.getParts())
		{
			val+=Math.abs(GetNextPrime(part.getFinalId()));
			
		}
		return val;	}
	
	public String toString(){	
		return "type is "+type;	
	}

	long GetNextPrime(long n)
	{
		if(n<1)
		{return 2;}
	    boolean isPrime = true;
	    boolean prime_found=false;
	    long primenb = n+1;
	    while(prime_found==false)
		  {isPrime=true;
		    if(primenb%2==0)
		    	{
		    		isPrime=false;
		    	}
		    for (long i=3; i<Math.sqrt(primenb)+1&&(isPrime); i=i+2)
		    {
		    	if(primenb%i==0)
		    	{
		    	isPrime=false;	
		    	}
		    }
			    if(isPrime==true)
			    {
			    	prime_found=true;
			    }
			    else 
			    {
			    	isPrime=true;
			    	primenb++;
			    }
	    }
		return primenb;

	}
	
	
	
}
