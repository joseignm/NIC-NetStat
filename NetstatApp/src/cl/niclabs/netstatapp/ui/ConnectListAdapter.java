package cl.niclabs.netstatapp.ui;

import java.util.ArrayList;

import cl.niclabs.netstatapp.R;
import cl.niclabs.netstatapp.core.Connection;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

class ConnectListAdapter extends ArrayAdapter<Connection> {

	Activity context;
	ArrayList<Connection> cns;

	ConnectListAdapter(Activity context, ArrayList<Connection> connections) {
		super(context, R.layout.nsconnect, connections);
		this.context = context;
		this.cns = connections;
	}

	@SuppressLint("InflateParams")
	public View getView(int position, View convertView, ViewGroup parent) {
		View item = convertView;
		ViewHolder holder;
		
		if(item==null) {
			LayoutInflater inflater = context.getLayoutInflater();
			item = inflater.inflate(R.layout.nsconnect, null);
			
			holder = new ViewHolder();
			holder.appname = (TextView)item.findViewById(R.id.nscn_appname);
			holder.src = (TextView)item.findViewById(R.id.nscn_src);
			holder.dst = (TextView)item.findViewById(R.id.nscn_dst);
			
			item.setTag(holder);
		} else {
			holder = (ViewHolder)item.getTag();
		}

		holder.appname.setText(cns.get(position).getAppname());
		holder.src.setText(cns.get(position).getSource());
		holder.dst.setText(cns.get(position).getDestination());

		return item;
	}
	
	static class ViewHolder {
		TextView appname;
		TextView src;
		TextView dst;
	}
}