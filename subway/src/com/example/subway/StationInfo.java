package com.example.subway;
/*
 *  ���� ������ ��Ÿ������ Ŭ����
 */
public class StationInfo {
private String Line;
private String StationName;

public StationInfo(String line,String stationName){
	Line =line;
	StationName=stationName;
}

public String getLine() {
	return Line;
}



public String getStationName() {
	return StationName;
}


}
