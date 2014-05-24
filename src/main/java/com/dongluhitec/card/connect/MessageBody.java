package com.dongluhitec.card.connect;

import com.dongluhitec.card.connect.exception.DongluInvalidMessageException;


public interface MessageBody extends Bytenize {

	public void initContent(byte[] bytes) throws DongluInvalidMessageException;

}
