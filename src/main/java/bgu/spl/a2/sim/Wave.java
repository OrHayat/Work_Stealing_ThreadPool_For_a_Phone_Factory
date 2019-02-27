package bgu.spl.a2.sim;

public class Wave {
	String product;
	int qty;
	long startId;	
	
	public Wave(String product,int qty,long startId)
	{
		this.product=product;
		this.qty=qty;
		this.startId=startId;
	}
	public int getqty(){
		return qty;
	}
	public String getproduct(){
		return this.product;
	}
	public void setqty(int nqty){
		this.qty=nqty;
	}
	public void setproduct(String product)
	{
		this.product=product;
	}
	public long getstartId()
	{
		return this.startId;
	}
	//setmatybe startid
	
	
}
