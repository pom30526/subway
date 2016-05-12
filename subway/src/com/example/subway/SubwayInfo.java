package com.example.subway;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;

@SuppressLint({ "NewApi", "HandlerLeak" })
public class SubwayInfo extends ListActivity implements Runnable {

	static private final String tag = "Androidside_Subway";

	public static Activity ctx;
	ArrayList<StationInfo> arrStation = new ArrayList<StationInfo>();
	StationInfo stationInfo;
	Thread thread = null;

	private ProgressDialog progressDialog;
	private Dialog dialog;
	private Dialog Line_dialog;
	public static String strDBname;

	String strCID = "7000";
	StationAdapter mAdapter = null;
	int mJobId = 0;
	int mListId = 0;

	private SharedPreferences preferences;
	private static String strVersion;

	boolean bSearch = false; // 검색 플래그

	static AutoCompleteTextView txtViewStationName;
	static Button btnSearch;
	static Button btnLine;
	static Display display;
	
	static final String[] STATION = new String[] { "괴정", "교대", "구서", "남산",
		"남포", "노포", "당리", "대티", "동대신", "동래", "두실", "명륜", "범내골", "범어사",
		"범일", "부산대", "부산역", "부산진", "부전", "사하", "서대신", "서면", "시청", "신평",
		"양정", "연산", "온천장", "자갈치", "장전", "좌천", "중앙", "초량", "토성", "하단", "가야",
		"감전", "개금", "경성대.부경대", "광안", "구남", "구명", "금곡", "금련산", "남양산", "남천",
		"냉정", "대연", "덕천", "덕포", "동백", "동원", "동의대", "모덕", "모라", "못골", "문전",
		"문현", "민락", "부산대양산캠퍼스", "부암", "사상", "서면", "센텀시티", "수영", "수정",
		"시립미술관", "양산", "율리", "장산", "전포", "주례", "중동", "지게골", "해운대", "호포",
		"화명", "강서구청", "거제", "구포", "남산정", "대저", "덕천", "만덕", "망미", "물만골",
		"미남", "배산", "사직", "수영", "숙등", "연산", "종합운동장", "체육공원", "고촌", "금사",
		"낙민", "동래", "동부산대학", "명장", "미남", "반여농산물시장", "서동", "석대", "수안", "안평",
		"영산대", "충렬사", "가야대", "공항", "괘법 르네시떼", "김해대학", "김해시청", "대사", "대저",
		"덕두", "등구", "박물관", "봉황", "부원", "불암", "사상", "서부산 유통지구", "수로왕릉",
		"연지공원", "인제대", "장신대", "지내", "평강",

};

	public void Update() {
		progressDialog = ProgressDialog.show(ctx, "Loading", getString(R.string.loading),
				true, false);

		thread = new Thread(this);

		mJobId = IConstant.JOB_DBCOPY;

		try {
			thread.start();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public void doCopy() {
		File outDir = null;
		File outfile = null;


		// 외장메모리가 사용가능 상태인지 확인
		strDBname = IConstant.SD_PATH + "/data/com.example.subway/databases/subway.db";
        outDir = new File( IConstant.SD_PATH + "/data/com.example.subway/databases");
 
        outDir.mkdirs();
        outfile = new File(outDir, "subway.db");
            
        InputStream is = null;
        OutputStream os = null;
        int size;
 
        try {                        
            // AssetsManager를 이용하여 subway.db파일 읽기
            is = getAssets().open("subway.db");
            size = is.available();
                          
            outfile.createNewFile();           // 디렉토리 생성           
            os = new FileOutputStream(outfile);
            
            byte[] buffer = new byte[size];
            
            is.read(buffer);
            os.write(buffer);
            
            is.close();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                os.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


	/** Called when the activity is first created. */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.main);
		
		txtViewStationName = (AutoCompleteTextView) findViewById(R.id.txtViewStationName);
		btnSearch = (Button) this.findViewById(R.id.btnSearch);
		btnLine = (Button) this.findViewById(R.id.btnline);
	
		ctx = this;
		preferences = getPreferences(MODE_WORLD_WRITEABLE);
		strVersion = preferences.getString("VERSION", "");

		// SharedPreferences에 저장된 버전과 app 버전이 다를 때 DB파일 외장메모리로 복사
		if (getString(R.string.appversion).compareTo(strVersion) != 0) {
			// 외장메모리가 사용가능할 때
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				Update();
			} else {
				showDialog(IConstant.DIALOG_NOSDCARD);
			}
		}
	
		// 자동완성 리스트 선택 시 자동으로 해당 역 조회
		txtViewStationName
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						String strStationName = (parent
								.getItemAtPosition(position)).toString();
						SearchButton(strStationName);
					}
				});
		
		// 검색 버튼
		btnSearch.setOnClickListener(new OnClickListener() {
		
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 버튼 클릭 할 때 소프트 키보드가 없어지게..
				InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(txtViewStationName
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		
				// AutoCompleteTextView에서 역명 가져오기.
				String strStationName = txtViewStationName.getText().toString()
						.trim();
				SearchButton(strStationName);
			}
		});
		
		// 호선버튼
		btnLine.setOnClickListener(new OnClickListener() {
		
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 버튼 클릭 할 때 소프트 키보드가 없어지게..
				InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(txtViewStationName
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		
				lineButton();
			}
		});
	
		registerForContextMenu(getListView()); // 컨텍스트 메뉴 등록
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	
		// 자동완성 텍스트뷰 구현
		ArrayAdapter adapter = new ArrayAdapter(this,
				android.R.layout.simple_dropdown_item_1line, STATION);
		txtViewStationName.setAdapter(adapter);
		txtViewStationName.setText("");
	
		// 최근 검색역 리스트 조회
		makeRecentStation();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		// 뒤로가기 버튼
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (bSearch) {
				makeRecentStation();

				bSearch = false;
				return true;
			}

			finish();
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case IConstant.DIALOG_DELETE_ALL:
			return dialog = new AlertDialog.Builder(ctx).setTitle(R.string.delete)
					.setMessage(R.string.delete_message)
					.setPositiveButton(R.string.alert_dialog_ok,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int whichButton) {
									// TODO Auto-generated method stub
									LocalDBAdapter.open(ctx);
									LocalDBAdapter.deleteItemAll(/*strCID*/);
									LocalDBAdapter.close();

									// 최근 검색 리스트 생성
									makeRecentStation();
								}
							}).setNegativeButton(R.string.alert_dialog_cancle,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int whichButton) {
									// TODO Auto-generated method stub
								}
							}).create();

		case IConstant.DIALOG_EXIT:
			return dialog = new AlertDialog.Builder(ctx).setTitle(R.string.exit)
					.setMessage(R.string.exit_message).setPositiveButton(
							R.string.alert_dialog_ok,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int whichButton) {
									// TODO Auto-generated method stub
									finish();
								}
							}).setNegativeButton(R.string.alert_dialog_cancle,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
								}
							}).create();

		case IConstant.DIALOG_NOSDCARD:
			return new AlertDialog.Builder(ctx)
					.setTitle(R.string.sd_error)
					.setMessage(R.string.sd_error_detail)
					.setPositiveButton("확인",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int whichButton) {
									// TODO Auto-generated method stub
									// 종료
									finish();
								}
							}).create();
		}

		return super.onCreateDialog(id);
	}

	/**
	 * 리스트 아이템 클릭
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		
		// 버튼 클릭 할 때 소프트 키보드가 없어지게..
		InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(txtViewStationName
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	
		StationInfo stationInfo = (StationInfo) l.getItemAtPosition(position);
		String strStationName = stationInfo.getStationName();
		String strStationLine = stationInfo.getLine();
	
		Intent intent = new Intent(SubwayInfo.this, Station.class);
	
		intent.putExtra("StationName", strStationName);
		intent.putExtra("StationLine", strStationLine);
		intent.putExtra("StationLocal", strCID);
	
		startActivity(intent);
	}

	/**
	 * 리스트 롱클릭 컨텍스트 메뉴
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (mListId == IConstant.LIST_FAVORITE) {
			super.onCreateContextMenu(menu, v, menuInfo);
			menu.setHeaderTitle(getString(R.string.delete_item));
			menu.add(0, IConstant.DELETE_ID, 2, R.string.menu_delete);
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// 개별삭제
		case IConstant.DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			final StationInfo station = arrStation.get((int) info.id);

			LocalDBAdapter.open(ctx);

			LocalDBAdapter.deleteItem(station.getStationName(), station
					.getLine()/*, station.getStationLocal()*/);

			LocalDBAdapter.close();
			makeRecentStation();

			return true;
		}

		return super.onContextItemSelected(item);
	}

	/**
	 * 옵션메뉴
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		if (mListId == IConstant.LIST_LINE) {
			menu.getItem(0).setVisible(false);
		} else if (mListId == IConstant.LIST_FAVORITE) {
			menu.getItem(0).setVisible(true);
		}
	
		return super.onPrepareOptionsMenu(menu);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
	
		switch (item.getItemId()) {
		case R.id.deleteall:
			// 즐겨찾기 모두 삭제
			showDialog(IConstant.DIALOG_DELETE_ALL);
			break;
		
		case R.id.exit:
			// 애플리케이션 종료
			finish();
			break;
		}
	
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 검색 버튼
	 * 
	 * @param strStationName
	 */
	public void SearchButton(String strStationName) {
		bSearch = true;
	
		// 검색어가 없는 경우
		if ("".equals(strStationName)) {
			return;
		}
	
		// 검색어에서 "역" 제거
		if (strStationName.contains("역")) {
			strStationName.replace("역", "");
		}
	
		try {
			// DB Open
			DBAdapter.open();
	
			Log.e("com.androidside.subway", strStationName);
	
			// 역명, 지역으로 역 리스트 조회
			Cursor c = DBAdapter.selectStationToName(strStationName/*, strCID*/);
	
			// 리스트뷰에 채우기
			fillData(c);
	
			// 커서 닫기
			c.close();
	
			// DB Close
			DBAdapter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		// AutoCompleteTextView 초기화
		txtViewStationName.setText("");
	}

	/**
	 * 호선 버튼
	 */
	public void lineButton() {
		// 호선선택 다이얼로그는 한번만 떠야 한다.
		if (Line_dialog != null) {
			if (Line_dialog.isShowing()) {
				return;
			}
		}
	
		Line_dialog = new AlertDialog.Builder(ctx).setTitle(
				R.string.line_select).setItems(R.array.cid_7000,
				new DialogInterface.OnClickListener() {
	
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						// TODO Auto-generated method stub
						String[] arrSubwayLine = getResources().getStringArray(
								R.array.cid_7000);
	
						try {
							// DB Open
							DBAdapter.open();
	
							// 호선명, 지역으로 역 리스트 조회
							Cursor c = DBAdapter.selectStationToLine(
									arrSubwayLine[whichButton]/*, strCID*/);
							// 리스트뷰에 채우기
							fillData(c);
	
							// Cursor Close
							c.close();
							// DB Close
							DBAdapter.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).setNegativeButton(R.string.alert_dialog_cancle,
				new DialogInterface.OnClickListener() {
	
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						// TODO Auto-generated method stub
					}
				}).create();
	
		Line_dialog.show();
	}
	
	/**
	 * 최근 검색역 리스트 조회
	 */
	public void makeRecentStation() {
		try {
			// LocalDB Open
			LocalDBAdapter.open(ctx);
	
			// 최근검색역 조회
			Cursor c = LocalDBAdapter.select();
	
			// 리스트 생성
			fillData(c);
	
			c.close();
			LocalDBAdapter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 역 리스트 생성
	 */
	private void fillData(Cursor c) {
		arrStation.clear();
	
		if (dialog != null) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
		}
	
		if (Line_dialog != null) {
			if (Line_dialog.isShowing()) {
				Line_dialog.dismiss();
			}
		}
	
		while (c.moveToNext()) {
			String strStationLine = c.getString(1);
			String strStationName = c.getString(0);
	
			StationInfo stationInfo = new StationInfo(strStationLine,
					strStationName);
			arrStation.add(stationInfo);
		}
	
		// 어댑터를 생성합니다.
		mAdapter = new StationAdapter(this, R.layout.row, arrStation);
		setListAdapter(mAdapter);
	
		mListId = IConstant.LIST_FAVORITE;
		mAdapter.notifyDataSetChanged();
	
		c.close();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		switch (mJobId) {
		case IConstant.JOB_DBCOPY:
			doCopy();
			handler.sendEmptyMessage(IConstant.JOB_DBCOPY);
			break;
		}
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (mJobId) {
			case IConstant.JOB_DBCOPY:
				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
				}

				savePreferences();
				makeRecentStation();
				break;
			}
		}
	};

	/**
	 * 설정값 저장
	 */
	private void savePreferences() {
		preferences.edit().putString("VERSION", getString(R.string.appversion))
				.commit();
	}
}