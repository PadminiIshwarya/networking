package snmpWalk;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
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

public class SnmpWalk {

	public static void main(String[] args) throws Exception {
		PDU pdu = new PDU();
		Scanner sc = new Scanner(System.in);
		System.out.println("Community : ");
		String community=sc.next();
		System.out.println("IP ADDRESS : ");
		String ip = sc.next();
		CommunityTarget target = new CommunityTarget();		
		target.setCommunity(new OctetString(community));
		target.setAddress(GenericAddress.parse("udp:"+ ip +"/161")); // supply your own IP and port
		target.setVersion(SnmpConstants.version2c);
		DefaultPDUFactory dpf = new DefaultPDUFactory();
		pdu = dpf.createPDU(target);
		System.out.println(pdu);
		char option;
		do
		{
			System.out.println("Enter OID :");
			String oid = "."+sc.next();
			Map<String, String> result = doWalk(oid, target); // ifTable, mib-2 interfaces
		for (Map.Entry<String, String> entry : result.entrySet()) {
			if (entry.getKey().startsWith(".1.3.6.1.2.1.1.5")) {
				System.out.println("Device Name" + entry.getKey().replace(oid, "") + ": " + entry.getValue());
			}
			if (entry.getKey().startsWith(".1.3.6.1.2.1.2.2.1.6")) {
				System.out.println("ifPhysAddress" + entry.getKey().replace(oid, "") + ": " + entry.getValue());
			}
			if (entry.getKey().startsWith(".1.3.6.1.2.1.4.20.1.1")) {
				System.out.println("ipAdEntAddr" + entry.getKey().replace(oid, "") + ": " + entry.getValue());
			}
			if (entry.getKey().startsWith(".1.3.6.1.2.1.4.20.1.3")) {
				System.out.println("ipAdEntNetMask" + entry.getKey().replace(oid, "") + ": " + entry.getValue());
			}
			if (entry.getKey().startsWith(".1.3.6.1.2.1.4.22.1.2")) {
				System.out.println("ipNetToMediaPhysAddress" + entry.getKey().replace(oid, "") + ": " + entry.getValue());
			}
		}
		System.out.println("Another OID ? (Y/N) :");
		option=sc.next().charAt(0);
		}while(option=='y'||option=='Y');
	}
	 
	public static Map<String, String> doWalk(String tableOid, Target target) throws IOException {
		Map<String, String> result = new TreeMap<>();
		TransportMapping<? extends Address> transport = new DefaultUdpTransportMapping();
		Snmp snmp = new Snmp(transport);
		transport.listen();

		TreeUtils treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
		List<TreeEvent> events = treeUtils.getSubtree(target, new OID(tableOid));
		if (events == null || events.size() == 0) {
			System.out.println("Error: Unable to read table...");
			return result;
		}

		for (TreeEvent event : events) {
			if (event == null) {
				continue;
			}
			if (event.isError()) {
				System.out.println("Error: table OID [" + tableOid + "] " + event.getErrorMessage());
				continue;
			}

			VariableBinding[] varBindings = event.getVariableBindings();
			if (varBindings == null || varBindings.length == 0) {
				continue;
			}
			for (VariableBinding varBinding : varBindings) {
				if (varBinding == null) {
					continue;
				}
				
				result.put("." + varBinding.getOid().toString(), varBinding.getVariable().toString());
			}

		}
		snmp.close();
		return result;
	}

}
