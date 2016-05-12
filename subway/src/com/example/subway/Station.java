package com.example.subway;



import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;



@SuppressWarnings("deprecation")
public class Station extends TabActivity {
	
	public void onCrate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Intent intent=this.getIntent(); // �뺣낫瑜��살뼱��
		String strStationName=(intent.getExtras()).getString("StationName");
		String strStationLine=(intent.getExtras()).getString("StationLine");
		String strCID=(intent.getExtras()).getString("StationLocal");
		//媛앹껜 �꾨떖�섍린
		
		Intent StationInfoIntent = new Intent(Station.this,
				Tab1_ArrivalInfo.class);
		StationInfoIntent.putExtra("StationName", strStationName);
		StationInfoIntent.putExtra("StationLine", strStationLine);
		StationInfoIntent.putExtra("StationLocal", strCID);
        
		TabHost tabHost=getTabHost();
		
		tabHost.addTab(tabHost.newTabSpec("Arrival").setIndicator("열차 도착정보")
				.setContent(StationInfoIntent));

		tabHost.addTab(tabHost.newTabSpec("Exit")
				
				.setContent(new Intent(Station.this, Tab2_ExitInfo.class)));

		tabHost.addTab(tabHost.newTabSpec("StationInfo")
				
				.setContent(new Intent(Station.this, Tab3_StationInfo.class)));
	}

}
