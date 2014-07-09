package edu.training.droidbountyhunter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DetalleActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle oExt = this.getIntent().getExtras();
		this.setTitle(oExt.getString("title") + " - [" + oExt.getString("id") + "]");
		this.sID = oExt.getString("id");
		setContentView(R.layout.activity_detalle);
		TextView oMsg = (TextView)this.findViewById(R.id.lblMsg);
		if (oExt.getInt("mode") == 0)
			oMsg.setText("El fugitivo no ha sido atrapado");
		else {
			Button oBtn1 = (Button)this.findViewById(R.id.btnCap);
			oBtn1.setVisibility(View.GONE);
			oMsg.setText("Atrapado!!!");
		}
	}

	String sID;

	public void onCaptureClic(View v) {
		MainActivity.oDB.UpdateFugitivo("1", sID);
		NetServices oNS = new NetServices(new OnTaskCompleted() {
			@Override
			public void onTaskCompleted(Object feed) {
				MessageClose(feed.toString());
			}
			@Override
			public void onTaskError(Object feed) {
				Toast.makeText(getApplicationContext(), "Ocurrio un problema en la comunicacion con el Servicio Web", Toast.LENGTH_LONG).show();
			}
		});
		oNS.oActRef = this;
		oNS.execute("Atrapar", MainActivity.UDID);
		Button oBtn1 = (Button)this.findViewById(R.id.btnCap);
		Button oBtn2 = (Button)this.findViewById(R.id.btnDel);
		oBtn1.setVisibility(View.GONE);
		oBtn2.setVisibility(View.GONE);
		setResult(0);
	}

	public void onDeleteClick(View v) {
		MainActivity.oDB.DeleteFugitivo(sID);
		setResult(0);
		finish();
	}

	public void MessageClose(String sMsg) {
		AlertDialog.Builder oADb = new AlertDialog.Builder(this);
		AlertDialog oAD = oADb.create();
		oAD.setTitle("Alerta");
		oAD.setMessage(sMsg);
		oAD.setOnDismissListener(new DialogInterface.OnDismissListener() {
			
			@Override
			public void onDismiss(final DialogInterface dialog) {
				finish();
			}
		});
		oAD.show();
	}
}