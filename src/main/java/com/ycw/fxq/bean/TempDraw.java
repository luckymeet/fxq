package com.ycw.fxq.bean;


public class TempDraw {

	private String bankName;
	private String time;
	private String inOrOut;
	private String name1;
	private String card1;
	private String money;
	private String name2;
	private String card2;
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getInOrOut() {
		return inOrOut;
	}
	public void setInOrOut(String inOrOut) {
		this.inOrOut = inOrOut;
	}
	public String getName1() {
		return name1;
	}
	public void setName1(String name1) {
		this.name1 = name1;
	}
	public String getCard1() {
		return card1;
	}
	public void setCard1(String card1) {
		this.card1 = card1;
	}
	public String getMoney() {
		return money;
	}
	public void setMoney(String money) {
		this.money = money;
	}
	public String getName2() {
		return name2;
	}
	public void setName2(String name2) {
		this.name2 = name2;
	}
	public String getCard2() {
		return card2;
	}
	public void setCard2(String card2) {
		this.card2 = card2;
	}

	@Override
	public String toString() {
		return "Temp_Draw [bankName=" + bankName + ", time=" + time + ", inOrOut=" + inOrOut + ", name1=" + name1
				+ ", card1=" + card1 + ", money=" + money + ", name2=" + name2 + ", card2=" + card2 + "]";
	}

}
