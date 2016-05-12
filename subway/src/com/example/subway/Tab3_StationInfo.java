package com.example.subway;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;




public class Tab3_StationInfo extends Activity {
	/**
	 * @see android.app.Activity#onCreate(Bundle)
	 */
	TextView TextViewAddress = null;
	TextView TextViewTel = null;
	String strStationInfo = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stationinfo);

		TextViewAddress = (TextView) this.findViewById(R.id.TextAddress);
		TextViewTel = (TextView) this.findViewById(R.id.TextTel);

		// 전화번호를 클릭 할 때 Dial Intent 실행
//		TextViewTel.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri
//						.parse("tel:" + TextViewTel.getText()));
//				dialIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				startActivity(dialIntent);
//			}
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		MakeData();
	}

	void MakeData() {
		strStationInfo = Tab1_ArrivalInfo.strStationInfo.trim();

		// 초기화
		TextViewAddress.setText("");
		TextViewTel.setText("");

		if (strStationInfo.compareTo("") != 0) {
			// 주소
			String strAddress = strStationInfo.substring(
					strStationInfo.indexOf("주소 : "),
					strStationInfo.indexOf("\n")).replace("주소 : ", "").trim();
			// 전화번호
			String strTel = strStationInfo.substring(
					strStationInfo.indexOf("전화번호 : ") + 7,
					strStationInfo.length()).replace("전화번호 : ", "").trim();

			TextViewAddress.setText(strAddress);
			TextViewTel.setText(strTel);
		}
	}
}
