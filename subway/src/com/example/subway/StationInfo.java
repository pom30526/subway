package com.example.subway;
/*
 *  역의 정보를 나타내내는 클래스
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
