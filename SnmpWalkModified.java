package snmpWalk;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import org.snmp4j.CommunityTarget;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TreeEvent;
import org.snmp4j.util.TreeUtils;


public class SnmpWalkModified {
	static Scanner sc = new Scanner(System.in);
	static String community;
	static String ip;
	static String[] index1=new String[50];
	static String[] key1=new String[50];
	static String[] index2=new String[40];
	static Map<String, String> result = new TreeMap<>();
	public static void main(String[] args) throws Exception {
		int k=0;
		CommunityTarget target = new CommunityTarget();
		System.out.println("Community :");
		community=sc.next();
		System.out.println("IP :");
		ip=sc.next();
		target=connection();
		String ipAddressOid="1.3.6.1.2.1.4.20.1.2";
		String ifPhysAddressOid="1.3.6.1.2.1.2.2.1.6";
		String ipAdEntNetMaskOid="1.3.6.1.2.1.4.20.1.3";
		String ipDefaultRouterLifetime="1.3.6.1.2.1.4.37.1.4";
		String subStr1="";
		String subStr2="";
		Map<String, String> result1= doWalk1(ipAddressOid, target);
		for(Map.Entry<String,String> m : result1.entrySet())
		{
			//System.out.println("Value :" + m.getKey());
			index1[k]=m.getValue();
			key1[k]=m.getKey().replace(ipAddressOid + ".", "");
			k++;
		}

		Map<String, String> result2= doWalk1(ifPhysAddressOid, target);
		Map<String, String> result3= doWalk1(ipAdEntNetMaskOid, target);
		Map<String, String> result4= doWalk1(ipDefaultRouterLifetime, target);

		int count=0;
		for(int p=0;p<index1.length;p++)
		{
			count++;
			if(count==index1.length || index1[p]==null)
				break;
			else
			{
				//System.out.println("index1[p] :" + index1[p]);
				for(Map.Entry<String,String> m1 : result2.entrySet())
				{
					try
					{
						if(index1[p].equals(m1.getKey().replace(ifPhysAddressOid + ".", "")))
						{	
							System.out.print("IP Index :" + index1[p] + " \t MAC :" + m1.getValue());
							break;
						}
					} 	
					catch(NullPointerException e)
					{
						System.out.println("Caught NullPointerException" + e);
					}
				}
				for(Map.Entry<String,String> m3 : result3.entrySet())
				{
					if(key1[p].equals(m3.getKey().replace(ipAdEntNetMaskOid + ".","")))
						System.out.println("\t NET MASK :" + m3.getValue());
				}
				for(Map.Entry<String,String> m4 : result4.entrySet())
				{
					int count1 = 0;
					for(int i=0;i<m4.getKey().length();i++)
					{
						if(m4.getKey().charAt(i)=='.')
						{
							count1++;
							if(count==5)
							{
								subStr1=m4.getKey().substring(i+1);
								subStr2 = m4.getKey().substring(2, i+1);
							}
						}
					}
					if(index1[p].equals(subStr1))
					{
						System.out.println("Default Router : "+subStr2);						
					}
				}
			}
		}
		System.out.println("END");
	}

	private static Map<String, String> doWalk1(String oid, CommunityTarget target) throws IOException {
		TransportMapping<? extends Address> transport = new DefaultUdpTransportMapping();
		Snmp snmp = new Snmp(transport);
		transport.listen();
		TreeUtils treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
		List<TreeEvent> events1 = treeUtils.getSubtree(target, new OID(oid));
		for (TreeEvent event1 : events1) {
			if (event1 == null) {
				continue;
			}
			if (event1.isError()) {
				System.out.println("Error: table OID [" + oid + "] " + event1.getErrorMessage());
				continue;
			}

			VariableBinding[] varBindings1 = event1.getVariableBindings();
			for (VariableBinding varBinding1 : varBindings1) {
				if (varBinding1 == null) {
					continue;
				}
				
				result.put(varBinding1.getOid().toString(), varBinding1.getVariable().toString());
			}			
		}
		return result;
	}
	private static CommunityTarget connection() {
		CommunityTarget target = new CommunityTarget();
		target.setCommunity(new OctetString(community));
		target.setAddress(GenericAddress.parse("udp:"+ ip +"/161")); // supply your own IP and port
		target.setVersion(SnmpConstants.version2c);
		return target;
		
	}

}
