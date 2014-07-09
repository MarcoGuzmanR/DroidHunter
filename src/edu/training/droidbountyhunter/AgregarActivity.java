package edu.training.droidbountyhunter;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AgregarActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setTitle("Nuevo Fugitivo");
		setContentView(R.layout.activity_agregar);
	}

	public void onSaveClick(View v) {
		TextView oTxtN = (TextView)this.findViewById(R.id.txtNew);
		if (!oTxtN.getText().toString().isEmpty()) {
			MainActivity.oDB.InsertFugitivo(oTxtN.getText().toString());
			setResult(0);
			finish();
		}
		else {
			new AlertDialog.Builder(this)
			.setTitle("Alerta")
			.setMessage("Favor de capturar el Nombre del Fugitivo.")
			.show();
		}
	}
}
