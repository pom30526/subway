package com.example.subway;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
/*
 * ���ã�⿡ ���õ� ���̺�� ��Ȱ�뿡 ���� Ŭ����
 */
public class LocalDBAdapter {
	public static final String STATIONNAME = "StationName"; //������ ���ɰ�
	public static final String STATIONLINE = "StationLine";
	public static final String FREQUENCY = "Frequency"; //���ã��
	
	public static SQLiteDatabase mDb; //�������
	 
	private static final String DATABASE_NAME="localSubway.db"; //����̸�
	private static final String DATABASE_TABLE="RecentStation"; //��� ���̺� �ֱٿ� ���ã��
	//�ֱٿ� 
	private static final String DATABASE_CREATE = "CREATE TABLE IF NOT exists RecentStation( StationName, StationLine, Frequency int, primary key ( StationLine, StationName ));";
		//���ã�⸸��� ���̺�
	public static void open(Context context){
		mDb=context.openOrCreateDatabase(DATABASE_NAME, SQLiteDatabase.CREATE_IF_NECESSARY, null);
	}
		public static void close(){
			try{
				mDb.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		/**
		 * �ֱ� �˻��� ����
		 */
		public static void insertItem(String strStationName, String strStationLine){
			int cnt=1;
			ContentValues initialValues=new ContentValues();
			initialValues.put(STATIONNAME,strStationName);
			initialValues.put(STATIONLINE, strStationLine);
			initialValues.put(FREQUENCY,cnt);
			try {
				Cursor c = mDb.query(DATABASE_TABLE, null, "StationName = '" + strStationName + "' AND StationLine = '" + strStationLine + "'", null,	null, null, null, null);

				if (c.getCount() == 0)
					mDb.insert(DATABASE_TABLE, null, initialValues);
				else {
					while (c.moveToNext()) {
						cnt = c.getInt(c.getColumnIndexOrThrow(FREQUENCY));
					}

					initialValues.put(FREQUENCY, ++cnt);
					mDb.update(DATABASE_TABLE, initialValues, "StationName = '"	+ strStationName + "' AND StationLine = '" + strStationLine + "'", null);
				}

				c.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		/**
		 * �ֱٰ˻��� ����Ʈ ��ȸ
		 * 
		 * @return
		 */
		public static Cursor select() {
			return mDb.query(DATABASE_TABLE, null, null, null, null, null, FREQUENCY + " DESC", null);
		}

		/**
		 * ���� �� ����
		 * 
		 * @param strStationName
		 * @param strStationLine
		 * @param strStationLocal
		 */
		public static void deleteItem(String strStationName, String strStationLine) {
			mDb.delete(DATABASE_TABLE, "StationName = '" + strStationName + "' AND StationLine = '" + strStationLine + "'", null);
		}

		/**
		 * �ֱٰ˻��� ����Ʈ ��ü ����
		 * 
		 * @param strStationLocal
		 */
		public static void deleteItemAll() {
			mDb.delete(DATABASE_TABLE, null, null);
		}
	}
	

