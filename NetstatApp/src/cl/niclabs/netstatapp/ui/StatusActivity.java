package cl.niclabs.netstatapp.ui;

import cl.niclabs.netstatapp.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class StatusActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statusfragment);
		
		Bundle bundle = getIntent().getExtras();
		TextView apptxt = (TextView)findViewById(R.id.appname_data);
		TextView srctxt = (TextView)findViewById(R.id.src_data);
		TextView dsttxt = (TextView)findViewById(R.id.dst_data);
		TextView prttxt = (TextView)findViewById(R.id.proto_data);
		TextView stttxt = (TextView)findViewById(R.id.state_data);
		
		apptxt.setText(bundle.getString("APP"));
		srctxt.setText(bundle.getString("SRC"));
		dsttxt.setText(bundle.getString("DST"));
		prttxt.setText(bundle.getString("PRT"));
		stttxt.setText(bundle.getString("STT"));
	}
}
