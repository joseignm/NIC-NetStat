/* (C) 2012 Pragmatic Software
   This Source Code Form is subject to the terms of the Mozilla Public
   License, v. 2.0. If a copy of the MPL was not distributed with this
   file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package cl.niclabs.netstatapp.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import cl.niclabs.netstatapp.ui.MainActivity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

public class NetStat {
	private static ArrayList<Connection> connections = new ArrayList<Connection>();

	final String states[] = { "ESTABLISHED",   "SYN_SENT",   "SYN_RECV",   "FIN_WAIT1",   "FIN_WAIT2",   "TIME_WAIT",
			"CLOSED",    "CLOSE_WAIT",   "LAST_ACK",   "LISTEN",   "CLOSING",  "UNKNOWN"
	};

	private final String getAddress(final String hexa) {
		try {
			final long v = Long.parseLong(hexa, 16);
			final long adr = (v >>> 24) | (v << 24) |
					((v << 8) & 0x00FF0000) | ((v >> 8) & 0x0000FF00);
			return ((adr >> 24) & 0xff) + "." + ((adr >> 16) & 0xff) + "." + ((adr >> 8) & 0xff) + "." + (adr & 0xff);
		} catch(Exception e) {
			return "-1.-1.-1.-1";
		}
	}

	private final String getAddress6(final String hexa) {
		try {
			final String ip4[] = hexa.split("0000000000000000FFFF0000");

			if(ip4.length == 2) {
				final long v = Long.parseLong(ip4[1], 16);
				final long adr = (v >>> 24) | (v << 24) |
						((v << 8) & 0x00FF0000) | ((v >> 8) & 0x0000FF00);
				return ((adr >> 24) & 0xff) + "." + ((adr >> 16) & 0xff) + "." + ((adr >> 8) & 0xff) + "." + (adr & 0xff);
			} else {
				return "-2.-2.-2.-2";
			}
		} catch(Exception e) {
			return "-1.-1.-1.-1";
		}
	}

	private final int getInt16(final String hexa) {
		try {
			return Integer.parseInt(hexa, 16);
		} catch(Exception e) {
			return -1;
		}
	}
	
	private final String getState(String hexa) {
		int i = getInt16(hexa) - 1;
		if (i<0 || i>11)
			i = 11;
		return states[i];
	}
	
	private final boolean isValidAddress(String address) {
		if(address.equals("-1.-1-1.-1") || address.equals("-2.-2.-2.-2")
				|| address.equals("127.0.0.1") || address.equals("0.0.0.0"))
			return false;
		return true;
	}

	private void getConnectionByProto(Proto p, Context cnt) throws IOException {
		BufferedReader in;
		switch (p) {
		case TCP: 
			in = new BufferedReader(new FileReader("/proc/" + android.os.Process.myPid() + "/net/tcp"));
			break;
		case UDP:
			in = new BufferedReader(new FileReader("/proc/" + android.os.Process.myPid() + "/net/udp"));
			break;
		case UDP6:
			in = new BufferedReader(new FileReader("/proc/" + android.os.Process.myPid() + "/net/udp6"));
			break;
		case TCP6: default:
			in = new BufferedReader(new FileReader("/proc/" + android.os.Process.myPid() + "/net/tcp6"));
			break;
		}
		String line;

		while((line = in.readLine()) != null) {
			line = line.trim();
			String[] fields = line.split("\\s+", 10);

			if(fields[0].equals("sl")) {
				continue;
			}

			Connection connection = new Connection(p);

			String src[] = fields[1].split(":", 2);
			String dst[] = fields[2].split(":", 2);

			switch(p) {
			case UDP: case TCP:
				connection.src = getAddress(src[0]);   
				connection.dst = getAddress(dst[0]);
				break;
			case UDP6: case TCP6: default:
				connection.src = getAddress6(src[0]);   
				connection.dst = getAddress6(dst[0]);
				break;
			}
			if(!isValidAddress(connection.src) || !isValidAddress(connection.dst))
				continue;
			
			connection.spt = String.valueOf(getInt16(src[1]));
			connection.dpt = String.valueOf(getInt16(dst[1]));
			
			PackageManager pm = cnt.getPackageManager();
			String appname = pm.getNameForUid(Integer.parseInt(fields[7]));
			if(appname == null || appname.trim().equals(""))
				appname = "UNKNOWN";
			connection.app = appname;
			
			connection.state = getState(fields[3]);

			connections.add(connection);
		}
		in.close();
	}

	public ArrayList<Connection> getConnections(MainActivity cnt) {
		Log.d("NSAPP", "Getting connections...");
		connections.clear();
		try {
			getConnectionByProto(Proto.TCP, cnt);
			getConnectionByProto(Proto.UDP, cnt);
			getConnectionByProto(Proto.TCP6, cnt);
			getConnectionByProto(Proto.UDP6 ,cnt);
		} catch(Exception e) {
			
		}
		Log.d("NSAPP", "Getting connections: Done!");
		return connections;
	}
	
	public static String showConnections() {
		StringBuilder sb = new StringBuilder(1000);
		for(Connection c : connections) {
			sb.append(c.toString());
		}
		return sb.toString();
	}
}