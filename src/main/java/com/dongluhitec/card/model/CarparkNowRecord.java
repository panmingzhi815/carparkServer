package com.dongluhitec.card.model;

public class CarparkNowRecord {

	private String cardID;
	private int readerID;

	public CarparkNowRecord(int readerID, String cardID) {
		this.readerID = readerID;
		this.cardID = cardID;
	}

	public String getCardID() {
		return cardID;
	}

	public void setCardID(String cardID) {
		this.cardID = cardID;
	}

	public int getReaderID() {
		return readerID;
	}

	public void setReaderID(int readerID) {
		this.readerID = readerID;
	}

	
}
