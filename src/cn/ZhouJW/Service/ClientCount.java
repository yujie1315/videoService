package cn.ZhouJW.Service;

public class ClientCount {
	private  int ClientNumber = 0;
	public static ClientCount ginstance = null;
	
	public int getClientNumber(){
		
		return ClientNumber;
		
	}
	
	private synchronized void addClientNumber(){
		
		++ClientNumber;
		
	}
	private synchronized void subClientNumber(){
		
		if( --ClientNumber < 0 )
			ClientNumber = 0;
		
	}

	private ClientCount() {
		// TODO Auto-generated constructor stub
	}
	
	public static ClientCount getInstance(){
		
		if(ginstance == null)
			ginstance = new ClientCount();
		return ginstance;
	}
	public static void add(){
		
		if(ginstance == null)
			ginstance = new ClientCount();
		
		ginstance.addClientNumber();
	}
	
	public static void sub(){
		
		if(ginstance == null)
			ginstance = new ClientCount();
		
		ginstance.subClientNumber();
	}
	
	public static int getCount(){
		
		if(ginstance == null)
			ginstance = new ClientCount();
		
		return ginstance.getClientNumber();
	}
	
}
