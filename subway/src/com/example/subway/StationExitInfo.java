package com.example.subway;
/*
 * 역 출구정보에관한 클래스
 */
public class StationExitInfo {
	private String strExitNumber;
	private String strExitInfo;

	/**
	 * @param strExitNumber
	 * @param strExitInfo
	 */
	public StationExitInfo(String strExitNumber, String strExitInfo) {
		super();
		this.strExitNumber = strExitNumber;
		this.strExitInfo = strExitInfo;
	}

	public String getStrExitNumber() {
		return strExitNumber;
	}

	public String getStrExitInfo() {
		return strExitInfo;
	}
}
