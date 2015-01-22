package cl.niclabs.netstatapp.ui;

import java.util.ArrayList;

import cl.niclabs.netstatapp.core.Connection;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class StatusClickListener implements OnItemClickListener{
	Context context;
	ArrayList<Connection> connections;
	
	public StatusClickListener(Context context, ArrayList<Connection> connections) {
		this.context = context;
		this.connections = connections;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(context, StatusActivity.class);
		Bundle b = new Bundle();
		b.putString("APP", connections.get(position).getAppname());
		b.putString("PRT", connections.get(position).getProto());
		b.putString("STT", connections.get(position).getState());
		b.putString("SRC", connections.get(position).getSource());
		b.putString("DST", connections.get(position).getDestination());
		intent.putExtras(b);
		context.startActivity(intent);
		return;
	}

}
