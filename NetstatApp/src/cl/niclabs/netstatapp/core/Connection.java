package cl.niclabs.netstatapp.core;

public class Connection {
	private Proto proto;
	String src;
	String spt;
	String dst;
	String dpt;
	String state;
	String app;
	
	public Connection(Proto p) {
		proto = p;
	}
	
	public String getProto() {
		switch (proto) {
		case UDP:
			return "UDP4";
		case TCP:
			return "TCP4";
		case UDP6:
			return "UDP4";
		case TCP6: default:
			return "TCP6";
		}
	}
	
	public String getSource() {
		return src+":"+spt;
	}

	public String getDestination() {
		return dst+":"+dpt;
	}
	
	public String getAppname() {
		return app;
	}
	
	public String getState() {
		return state;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append(getProto());
		sb.append("//");
		sb.append(state);
		sb.append("//");
		sb.append(app);
		sb.append("//");
		sb.append(src);
		sb.append("//");
		sb.append(spt);
		sb.append("//");
		sb.append(dst);
		sb.append("//");
		sb.append(dpt);
		sb.append("\n");
		return sb.toString();
	}
}
