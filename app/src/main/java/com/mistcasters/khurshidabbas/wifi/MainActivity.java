package com.mistcasters.khurshidabbas.wifi;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
	File dir, fileAddress;
	File[] filelist;
	ListView listView;
	String fileName;
	String[] theNamesOfFiles;
	Random random;
	ArrayAdapter<String> adapter;
	private StorageReference storageRef;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		
		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(MainActivity.this, NewFile.class);
				startActivity(intent);
			}
		});
		
		storageRef = FirebaseStorage.getInstance().getReference();
		random = new Random();
		
		listView = (ListView) this.findViewById(R.id.myList);
		
		dir = new File(Environment.getExternalStorageDirectory(), "zMistCasters");
		
		listAndUploadFiles();
	}
	
	
	private void listAndUploadFiles() {
		filelist = dir.listFiles();
		theNamesOfFiles = new String[filelist.length];
		
		for (int i = 0; i < theNamesOfFiles.length; i++) {
			theNamesOfFiles[i] = filelist[i].getName();
		}
		
		adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, theNamesOfFiles);
		
		listView.setAdapter(adapter);
		
		
		
		
		
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				
				fileName = listView.getItemAtPosition(position).toString();
				
				fileAddress = new File(Environment.getExternalStoragePublicDirectory("zMistCasters"), "/" + fileName);
				
				if (fileAddress.delete()){
					Toast.makeText(MainActivity.this, "File Deleted Successfully!", Toast.LENGTH_SHORT).show();
					listAndUploadFiles();
				}
				else {
					Toast.makeText(MainActivity.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
				}
				
				return false;
			}
		});
		
		
		
		
		
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
				
				fileName = listView.getItemAtPosition(position).toString();
				
				fileAddress = new File(Environment.getExternalStoragePublicDirectory("zMistCasters"), "/" + fileName);

//				Snackbar.make(view, fileAddress.getAbsolutePath(), Snackbar.LENGTH_LONG).show();
				
				Uri file = Uri.fromFile(fileAddress);
				
				StorageReference riversRef = storageRef.child("files/" +
						String.valueOf(random.nextInt(1000) + 1) + file.getLastPathSegment());
				
				
				Toast.makeText(MainActivity.this, "Uploading...", Toast.LENGTH_SHORT).show();
				
				UploadTask uploadTask = riversRef.putFile(file);
				
				uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
					@Override
					public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
						
						Toast.makeText(MainActivity.this, "File Shared Successfully!", Toast.LENGTH_SHORT).show();
						
						@SuppressWarnings("VisibleForTests")
						Uri downloadUrl = taskSnapshot.getDownloadUrl();
						
						Intent smsIntent = new Intent(Intent.ACTION_VIEW);
						
						smsIntent.setData(Uri.parse("smsto:"));
						smsIntent.setType("vnd.android-dir/mms-sms");
						smsIntent.putExtra("address", "");
						smsIntent.putExtra("sms_body", "Please open following link to download shared file. \n\n" +
								downloadUrl.toString() + "\n\n" +
								"Mistcasers Inc.");
						
						Toast.makeText(MainActivity.this, "File Shared Successfully!", Toast.LENGTH_SHORT).show();
						
						startActivity(smsIntent);
					}
				});
				
				uploadTask.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception exception) {
						Snackbar.make(view, "File Sharing Failed!", Snackbar.LENGTH_SHORT).show();
					}
				});
			}
		});
	}
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.menu_main, menu);
//		return true;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// Handle action bar item clicks here. The action bar will
//		// automatically handle clicks on the Home/Up button, so long
//		// as you specify a parent activity in AndroidManifest.xml.
//		int id = item.getItemId();
//
//		//noinspection SimplifiableIfStatement
//		if (id == R.id.action_settings) {
//			return true;
//		}
//
//		return super.onOptionsItemSelected(item);
//	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		listAndUploadFiles();
		
		adapter.notifyDataSetChanged();
	}
}
