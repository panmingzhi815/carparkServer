package com.dongluhitec.card.connect.filterChain;

import java.util.Map;

import com.dongluhitec.card.connect.MessageBodyInfo;
import com.dongluhitec.card.connect.MessageConstance;
import com.dongluhitec.card.connect.body.CardIDBody;
import com.dongluhitec.card.connect.body.CarparkNowRecordBody;
import com.dongluhitec.card.connect.body.EmptyBody;
import com.dongluhitec.card.connect.body.ScreenVoiceDoorBody;
import com.dongluhitec.card.connect.body.SimpleBody;

public class CarparkMessageRegisterImpl extends AbstractMessageRegister {

	@Override
	public void registerRequestBody(Map<Byte, MessageBodyInfo> requestMap) {
		requestMap.put(MessageConstance.Message_ReadNowRecord, new MessageBodyInfo(EmptyBody.LENGTH,EmptyBody.class));
		requestMap.put(MessageConstance.Message_OpenDoor, new MessageBodyInfo(SimpleBody.LENGTH,SimpleBody.class));
		requestMap.put(MessageConstance.Message_ScreenVoiceDoor, new MessageBodyInfo(ScreenVoiceDoorBody.LENGTH,ScreenVoiceDoorBody.class));

	}

	@Override
	public void registerResponseBody(Map<Byte, MessageBodyInfo> responseMap) {
		responseMap.put(MessageConstance.Message_ReadNowRecord, new MessageBodyInfo(CarparkNowRecordBody.LENGTH,CarparkNowRecordBody.class));
		responseMap.put(MessageConstance.Message_OpenDoor, new MessageBodyInfo(SimpleBody.LENGTH,SimpleBody.class));
		responseMap.put(MessageConstance.Message_ScreenVoiceDoor, new MessageBodyInfo(SimpleBody.LENGTH,SimpleBody.class));
	}

}
