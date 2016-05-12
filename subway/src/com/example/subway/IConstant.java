package com.example.subway;

import java.io.File;

import android.annotation.SuppressLint;
import android.os.Environment;

/*
 *  DB�쒙옙占쎌럡由�占쎄쑵釉�疫꿸퀡��占쎌꼵瑗랃옙�쇱젟
 */

public interface IConstant {
final boolean DEBUG_MODE =true;// 占쎈뗀苡�뉩占쏙쭗�ㅻ굡揶쏉옙占쎌꼷堉깍옙�덈뮉揶쏉옙
/*
 *  占썬끇履잞옙占쏙옙占쎈립 疫꿸퀡���λ뜃由곤옙�쇱젟 
 */
final int DIALOG_LINE_LIST = 0;
final int DIALOG_DELETE_ALL =1;
final int DIALOG_EXIT =2;
final int DELETE_ID = 3;
final int DIALOG_NOSDCARD =4;
final int JOB_DBCOPY=10;
final int JOB_EXIT_WAIT =11;

final int LIST_LINE =20;
final int LIST_FAVORITE=21;

final int NETWORK_ERP =31;
final int PAGE_ERR=32;
final int REALTIME =33;
final int NO_REALTIME=34;

final String SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
final String DB_PATH = "/Android/data/com.example.subway/databases/";

}
