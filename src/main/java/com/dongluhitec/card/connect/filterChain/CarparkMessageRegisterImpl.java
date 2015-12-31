package com.dongluhitec.card.connect.filterChain;

import java.util.Map;

import com.dongluhitec.card.connect.MessageBodyInfo;
import com.dongluhitec.card.connect.MessageConstance;
import com.dongluhitec.card.connect.body.*;

public class CarparkMessageRegisterImpl extends AbstractMessageRegister {

	@Override
	public void registerRequestBody(Map<Byte, MessageBodyInfo> requestMap) {
		requestMap.put(MessageConstance.Message_ReadNowRecord, new MessageBodyInfo(EmptyBody.LENGTH,EmptyBody.class));
		requestMap.put(MessageConstance.Message_OpenDoor, new MessageBodyInfo(SimpleBody.LENGTH,SimpleBody.class));
		requestMap.put(MessageConstance.Message_ScreenVoiceDoor, new MessageBodyInfo(ScreenVoiceDoorBody.LENGTH,ScreenVoiceDoorBody.class));
		requestMap.put(MessageConstance.Message_SetTime, new MessageBodyInfo(SetDateTimeBody.LENGTH,SetDateTimeBody.class));
	}

	@Override
	public void registerResponseBody(Map<Byte, MessageBodyInfo> responseMap) {
		responseMap.put(MessageConstance.Message_ReadNowRecord, new MessageBodyInfo(CarparkNowRecordBody.LENGTH,CarparkNowRecordBody.class));
		responseMap.put(MessageConstance.Message_OpenDoor, new MessageBodyInfo(SimpleBody.LENGTH,SimpleBody.class));
		responseMap.put(MessageConstance.Message_ScreenVoiceDoor, new MessageBodyInfo(SimpleBody.LENGTH,SimpleBody.class));
	}

}
