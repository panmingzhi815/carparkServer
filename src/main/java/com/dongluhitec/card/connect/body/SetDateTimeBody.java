package com.dongluhitec.card.connect.body;

import java.util.Date;

import com.dongluhitec.card.connect.MessageBody;
import com.dongluhitec.card.connect.exception.DongluInvalidMessageException;
import com.dongluhitec.card.connect.util.BCDDateTimeAdaptor;

public class SetDateTimeBody implements MessageBody {

	public static final int LENGTH = 6;

	private Date date;

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getDate() {
		return this.date;
	}

	@Override
	public void initContent(byte[] array) throws DongluInvalidMessageException {
		this.date = new BCDDateTimeAdaptor(array, 0, true).getDate();

	}

	@Override
	public byte[] toBytes() {
		return new BCDDateTimeAdaptor(this.date).getBytes(true);
	}

	@Override
	public String toString() {
		return "时间: " + this.date.toString();
	}

}
