package com.dongluhitec.card.connect.exception;

public class DongluInvalidMessageException extends DongluHWException {

	private static final long serialVersionUID = -2378318266726993499L;

	public DongluInvalidMessageException(String string) {
		super(string);
	}

	public DongluInvalidMessageException(String string, Exception e) {
		super(string, e);
	}

}
