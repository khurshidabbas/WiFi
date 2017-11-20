package com.mistcasters.khurshidabbas.wifi;

import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Random;

public class NewFile extends AppCompatActivity {
	private Button saveButton;
	private EditText inputTextTitle, inputTextMessage;
	private File file, dir;
	private Random random;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_file);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		random = new Random();
		saveButton = (Button) this.findViewById(R.id.saveButton);
		inputTextTitle = (EditText) this.findViewById(R.id.inputTextTitle);
		inputTextMessage = (EditText) this.findViewById(R.id.inputTextMessage);
		
		
//		file = new File(Environment.getExternalStoragePublicDirectory("zMistCasters") + "/texts.txt");
		
		
		dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/zMistCasters");
		dir.mkdirs();
		
		saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if (inputTextTitle.getText().toString().isEmpty() ||
						inputTextMessage.getText().toString().isEmpty()) {
					
					Snackbar.make(v, "Please write down Title and Message", Snackbar.LENGTH_LONG).show();
				
				} else {
					
					try {
						file = new File(dir, Integer.toString(random.nextInt(1000)) +
								" - " + inputTextTitle.getText().toString() + ".txt");
						
						FileOutputStream f = new FileOutputStream(file);
						PrintWriter pw = new PrintWriter(f);
						pw.println(inputTextMessage.getText().toString());
						pw.println("");
						pw.flush();
						pw.close();
						f.close();
						
						Toast.makeText(v.getContext(), "Saved", Toast.LENGTH_LONG).show();
						
					} catch (Exception e) {
						Snackbar.make(v, e.getMessage(), Snackbar.LENGTH_LONG);
					}
				}
			}
		});
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
}