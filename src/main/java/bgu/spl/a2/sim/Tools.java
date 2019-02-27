package bgu.spl.a2.sim;

public class Tools {

	String tool;
	int qty;
	
	public Tools(String type,int qty)
	{
		this.tool=type;
		this.qty=qty;
	}
	public int getqty(){
		return qty;
	}
	public String gettype(){
		return tool;
	}
	public void setqty(int nqty){
		this.qty=nqty;
	}
	public void settype(String ntype){
		tool=ntype;
	}
}
