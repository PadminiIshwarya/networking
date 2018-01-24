
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeMap;

public class AndroidMibHash {
	static Scanner sc= new Scanner(System.in);
	static HashMap<String, String> hmap = new HashMap<>();
	public static void main(String[] args) throws FileNotFoundException {
		int choice;
		
		 hmap.put("1.3.6.1.2.1.999.1.0", "50");
		 hmap.put("1.3.6.1.2.1.999.2.1.1", "sda1");
		 hmap.put("1.3.6.1.2.1.999.2.1.2", "sda2");
		 hmap.put("1.3.6.1.2.1.999.2.2.1", "80");
		 hmap.put("1.3.6.1.2.1.999.2.2.2", "90");
		 hmap.put("1.3.6.1.2.1.999.3.1.1", "cpu1");
		 hmap.put("1.3.6.1.2.1.999.3.1.2", "cpu2");
		 hmap.put("1.3.6.1.2.1.999.3.2.1", "50");
		 hmap.put("1.3.6.1.2.1.999.3.2.2", "70");
		 hmap.put("1.3.6.1.2.1.999.4.0", "85");
		while(true) {
			System.out.println("1.SNMP GET REQUEST");
			System.out.println("2.SNMP GET NEXT REQUEST");
			System.out.println("3.SNMP GET BULK REQUEST");
			System.out.println("ENTER CHOICE");
			choice=sc.nextInt();
			switch(choice)
			{
			case 1 : get_request();
					 System.out.println("SNMP GET REQUEST");
					 break;
			case 2 : System.out.println("SNMP GET NEXT REQUEST");
					 get_next_request();
					 break;
			case 3 : System.out.println("SNMP GET BULK REQUEST");
					 get_bulk_request();
					 break;
			default : System.exit(0);
			}
		}

	}
	private static void get_bulk_request() {
		int nonRepeaters;
		int maxRepeaters;
		System.out.println("NON REPEATERS :");
		nonRepeaters=sc.nextInt();
		System.out.println("MAX REPEATERS :");
		maxRepeaters=sc.nextInt();
		int size=nonRepeaters+maxRepeaters;
		System.out.println("Nuber of oid's :");
		int oidsize=sc.nextInt();
		String oid[]=new String[oidsize];
		for(int i=0;i<oidsize;i++)
		{
			oid[i]=sc.next();
		}
		int k;
		for(k=0;k<nonRepeaters;k++)
		{
			if(hmap.containsKey(oid[k]))
					{
						System.out.println(hmap.get(oid[k]));
					}
		}
		//System.out.println(k);

		int j = 0;
		TreeMap<String,String> tmap1=new TreeMap<String,String>();
		tmap1.putAll(hmap);
		
		for(int i=k;i<oidsize;i++)
		{
			String s=oid[i];
			String substrofs=s.substring(s.length() - 5,s.length());
			String firstparts=s.substring(0,s.length() - 5);
			//System.out.println(substrofs);
			//System.out.println(firstparts);
			String substr2=substrofs.substring(0,substrofs.length()-2);
			//System.out.println(substr2);
			for ( String key : hmap.keySet())
			{
				if(j>maxRepeaters)
				{
					break;
				}
				else
				{
					if(key.contains(firstparts.concat(substr2)))
						System.out.println(tmap1.get(firstparts.concat(substr2)));
				}
			}


		}
	}
	private static void get_next_request() {
			
		TreeMap<String,String> tmap=new TreeMap<String,String>();
		tmap.putAll(hmap);
		String reskey="";
		//String[] mapKeys = new String[tmap.size()];
		int pos = 0;
		String s4;
		System.out.println("OID :");
		s4=sc.next();
		for (String key : tmap.keySet()) {
			pos++;
			if(pos<=tmap.size())
	    	{
				if(key.equals(s4)) {
		    	//System.out.println(pos);
		    	
		    	reskey=(String) tmap.keySet().toArray()[pos];
				System.out.println(tmap.get(reskey));
		    	break;
		    	}
	    	}
		    else
		    	System.out.println("NO OID");
		}
		
	}
	private static void get_request() {
		 System.out.println("OID:");
		 String s2=sc.next();
		 boolean flag=false;
		 if(hmap.containsKey(s2))
			 System.out.println(hmap.get(s2));
		 else
		 {
			 String substr= s2.substring(s2.length() - 5,s2.length());
			 String firstsub = s2.substring(0,s2.length() - 5);
			 String substr1=substr.substring(0,substr.length()-2);

		 	System.out.println(substr);
			System.out.println(substr1);
			System.out.println(firstsub);
			 String strappend=firstsub.concat(substr1);
			 System.out.println(strappend);
			 for ( String key : hmap.keySet())
			 {
				 String substr2=substr1.substring(0,substr1.length()-2);
				 System.out.println(substr2);
				 System.out.println(strappend);
				 if(key.contains(strappend))// key.contains(firstsub.concat(substr2)))
				 {
					 flag=true;
					 break;
				 }
				else
					if(!(key.contains(firstsub.concat(substr2))))
						flag=false;
			 }
			 if(flag)
				 System.out.println("Instance Not Found");
			 else
				 System.out.println("Object Not Found");

		 }
	}

}
