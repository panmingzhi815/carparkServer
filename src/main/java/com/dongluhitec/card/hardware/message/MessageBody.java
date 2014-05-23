package com.dongluhitec.card.hardware.message;

import com.dongluhitec.card.hardware.message.util.Bytenize;

public interface MessageBody extends Bytenize {

	public void initContent(byte[] bytes) throws DongluInvalidMessageException;

}
