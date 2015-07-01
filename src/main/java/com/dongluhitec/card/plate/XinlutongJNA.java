package com.dongluhitec.card.plate;

import com.dongluhitec.card.plate.XinlutongCallback.XinlutongResult;

public interface XinlutongJNA {

	public void openEx(String ip,XinlutongResult xinluweiResult);

	public void closeEx();

	public void tigger(String ip);

}
