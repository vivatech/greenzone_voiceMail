package com.vivatelecoms.greenzone.wapchatezee.model;

public class VoiceMailDashboardDetails {
	String todayTraffic;
	String yesterdayTraffic;
	String lastSevenDaysTraffic;
	String lastThirtyDaysTraffic;
	
	/***This is dashboard*/
		
	public VoiceMailDashboardDetails() {
		
		this.todayTraffic = "0";
		this.yesterdayTraffic = "0";
		this.lastSevenDaysTraffic = "0";
		this.lastThirtyDaysTraffic = "0";
	}
	public String getTodayTraffic() {
		return todayTraffic;
	}
	public void setTodayTraffic(String todayTraffic) {
		this.todayTraffic = todayTraffic;
	}
	public String getYesterdayTraffic() {
		return yesterdayTraffic;
	}
	public void setYesterdayTraffic(String yesterdayTraffic) {
		this.yesterdayTraffic = yesterdayTraffic;
	}
	public String getlastSevenDaysTraffic() {
		return lastSevenDaysTraffic;
	}
	public void setlastSevenDaysTraffic(String lastSevenDaysTraffic) {
		this.lastSevenDaysTraffic = lastSevenDaysTraffic;
	}
	public String getlastThirtyDaysTraffic() {
		return lastThirtyDaysTraffic;
	}
	public void setlastThirtyDaysTraffic(String lastThirtyDaysTraffic) {
		this.lastThirtyDaysTraffic = lastThirtyDaysTraffic;
	}
	
	

}
