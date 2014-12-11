package com.dongluhitec.card.connect.util;

import java.util.Date;

import org.joda.time.DateTime;

import com.dongluhitec.card.connect.exception.DongluInvalidMessageException;

public class BCDDateTimeAdaptor {

	public static final int BIT_OFF_YEAR = 0;
	public static final int BIT_OFF_MONTH = 1;
	public static final int BIT_OFF_DAY = 2;
	public static final int BIT_OFF_WEEK = 3;
	public static final int BIT_OFF_HOUR = 4;
	public static final int BIT_OFF_MIN = 5;
	private static final int LENGTH = 5;

	// in format YYMMDDHHMM
	// Calendar instance = Calendar.getInstance();
	private DateTime dateTime;

	public BCDDateTimeAdaptor(int year, int month, int day, int hour, int min) {
		dateTime = new DateTime(year, month, day, hour, min);
	}

	public BCDDateTimeAdaptor(Date date) {
		DateTime dateTime2 = new DateTime(date);
		dateTime = dateTime2.withMillisOfSecond(0);
	}

	public BCDDateTimeAdaptor(byte[] array, int start, boolean hasWeekByte)
			throws DongluInvalidMessageException {
		int year = this
				.initYear(array[start + BCDDateTimeAdaptor.BIT_OFF_YEAR]);
		int month = this.initMonth(array[start
				+ BCDDateTimeAdaptor.BIT_OFF_MONTH]);
		int day = this.initDay(array[start + BCDDateTimeAdaptor.BIT_OFF_DAY]);
		int hour = this
				.initHour(array[(start + BCDDateTimeAdaptor.BIT_OFF_HOUR)
						- (hasWeekByte ? 0 : 1)]);
		int min = this.initMin(array[(start + BCDDateTimeAdaptor.BIT_OFF_MIN)
				- (hasWeekByte ? 0 : 1)]);

		dateTime = new DateTime(year, month, day, hour, min);
	}

	public byte[] getBytes() {
		return this.getBytes(false);
	}

	public Date getDate() {
		return this.dateTime.toDate();
	}

	public byte[] getBytes(boolean hasWeekByte) {
		int additonalLength = hasWeekByte ? 1 : 0;
		byte[] rr = new byte[BCDDateTimeAdaptor.LENGTH + additonalLength];

		rr[BCDDateTimeAdaptor.BIT_OFF_YEAR] = this.getYearBCD();
		rr[BCDDateTimeAdaptor.BIT_OFF_MONTH] = this.getMonthBCD();
		rr[BCDDateTimeAdaptor.BIT_OFF_DAY] = this.getDayBCD();

		rr[BCDDateTimeAdaptor.BIT_OFF_HOUR - (hasWeekByte ? 0 : 1)] = this
				.getHourBCD();
		rr[BCDDateTimeAdaptor.BIT_OFF_MIN - (hasWeekByte ? 0 : 1)] = this
				.getMinuteBCD();

		if (hasWeekByte) {
			rr[BCDDateTimeAdaptor.BIT_OFF_WEEK] = this.getWeekdayBCD();
		}

		return rr;
	}

	public DateTime getDateTime() {
		return dateTime;
	}

	public byte getYearBCD() {
		int i = this.dateTime.getYearOfCentury();
		// int year = i % 100;
		byte bcdConvertFromIntToByte = Utils.BCDConvertFromIntToByte(i);
		return bcdConvertFromIntToByte;
	}

	public byte getMonthBCD() {
		int i = this.dateTime.getMonthOfYear();

		byte bcdConvertFromIntToByte = Utils.BCDConvertFromIntToByte(i);
		return bcdConvertFromIntToByte;
	}

	public byte getDayBCD() {
		int i = this.dateTime.getDayOfMonth();
		byte bcdConvertFromIntToByte = Utils.BCDConvertFromIntToByte(i);
		return bcdConvertFromIntToByte;
	}

	public byte getWeekdayBCD() {
		int i = this.dateTime.getDayOfWeek();
		byte r = Utils.BCDConvertFromIntToByte(i);
		return r;
	}

	public byte getHourBCD() {
		int i = this.dateTime.getHourOfDay();
		return Utils.BCDConvertFromIntToByte(i);
	}

	public byte getMinuteBCD() {
		int i = this.dateTime.getMinuteOfHour();
		return Utils.BCDConvertFromIntToByte(i);
	}

	private int initYear(byte b) {
		int bcdConvertFromByteToInt = Utils.BCDConvertFromByteToInt(b);
		int year = 2000 + bcdConvertFromByteToInt;
		return year;
	}

	private int initMonth(byte b) {
		int bcdConvertFromByteToInt = Utils.BCDConvertFromByteToInt(b);
		return bcdConvertFromByteToInt;
	}

	private int initDay(byte b) {
		return Utils.BCDConvertFromByteToInt(b);
	}

	private int initHour(byte b) {
		int hour = Utils.BCDConvertFromByteToInt(b);
		return hour;
	}

	private int initMin(byte b) {
		int min = Utils.BCDConvertFromByteToInt(b);
		return min;
	}

	@Override
	public String toString() {
		return this.dateTime.toString("yyyy-MM-dd:HH-mm");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dateTime == null) ? 0 : dateTime.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BCDDateTimeAdaptor other = (BCDDateTimeAdaptor) obj;
		if (dateTime == null) {
			if (other.dateTime != null)
				return false;
		} else if (!dateTime.equals(other.dateTime))
			return false;
		return true;
	}

}
