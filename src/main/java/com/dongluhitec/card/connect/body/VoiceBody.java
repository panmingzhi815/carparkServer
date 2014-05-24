package com.dongluhitec.card.connect.body;

import com.dongluhitec.card.connect.MessageBody;
import com.dongluhitec.card.connect.exception.DongluInvalidMessageException;
import com.google.common.base.Strings;

public class VoiceBody implements MessageBody {
	//大写转播报指令
	public static enum Single{
		零((byte)0x01), 壹((byte)0x02), 贰((byte)0x03), 叁((byte)0x04),
		肆((byte)0x05), 伍((byte)0x06), 陆((byte)0x07), 柒((byte)0x08), 
		捌((byte)0x09), 玖((byte)0x10), 拾((byte)0x11), 佰((byte)0x12), 
		仟((byte)0x13),元((byte)0x15), 万((byte)0x14),	整((byte)0x00),
		角((byte)0x37),分((byte)0x38),日((byte)0x33);
		
		private byte b;
		
		Single(byte b){
			this.b = b;
		}
		public byte bit(){
			return b;
		}
		public static Single parse(String name){
			if(Strings.isNullOrEmpty(name)) return Single.整;
			
			Single[] values = Single.values();
			for (Single single : values) {
				if(single.name().equals(name)){
					return single;
				}
			}
			
			return Single.整;
		}
	}

	//如果要增加新语音,只需要修改这个enum即可
	public static enum Property{
		欢迎光临_请入场停车(new byte[]{0x25,0x29,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00}),
		祝你一路平安(new byte[]{0x30,0x00,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00}),
		此卡未入场(new byte[]{0x19,0x00,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00}),
		此卡无效(new byte[]{0x20,0x00,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00}),
		此卡已过期(new byte[]{0x21,0x00,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00}),
		此卡已入场(new byte[]{0x22,0x00,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00}),
		此卡即将过期_请到管理处交费续期(new byte[]{0x44,0x46,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00}),
        此卡剩余使用日期(new byte[]{0x43,0x00,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00}),
		请交费(new byte[]{0x28,0x00,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00}),
		请充值(new byte[]{0x45,0x00,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00}),
		此卡被禁用(new byte[]{0x18,0x00,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00}),
		此卡金额不足_请充值(new byte[]{0x23,0x45,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00});
		private byte[] command;
		
		Property(byte[] command){
			this.command = command;
		}
		
		public byte[] bit(){
			return command;
		}
		
		public void addFloat(float f){
			this.command = new byte[]{0x28,0x00,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00};
			String digitUppercase = SimpleMoneyFormat.getInstance().format(f);
			for(int i =0 ;i < digitUppercase.length() ; i++){
				if(i >= 8) return;
				Single parse = Single.parse(digitUppercase.substring(i, i+1));
				if(parse != null){
					command[i+1] = parse.bit();
				}
			}
		}

        public void addLeftDays(int day){
        	this.command = new byte[]{0x43,0x00,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00};
            String digitUppercase = SimpleMoneyFormat.getInstance().format(day);
            digitUppercase = digitUppercase.replaceAll("元", "").replaceAll("整", "");
            for(int i =0 ;i < digitUppercase.length() ; i++){
                if(i >= 8) return;
                Single parse = Single.parse(digitUppercase.substring(i, i+1));
                if(parse != null){
                    command[i+1] = parse.bit();
                }
            }
            command[digitUppercase.length()+1] = Single.日.bit();
        }

        public static void main(String[] args) {
            String digitUppercase = SimpleMoneyFormat.getInstance().format(29);
            digitUppercase = digitUppercase.replaceAll("元","").replaceAll("整","");
            System.out.println(digitUppercase);
        }
    }
	
	public static final int LENGTH = 9;

	//语音指令结束标志
	public static final byte endOfBit = 0x00;
	//待发送的语音
	private byte[] sendVoice = {0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 };
	
	public byte[] getSendVoice() {
		return sendVoice;
	}

	public void setSendVoice(Property p) {
		sendVoice = p.bit();
	}

	@Override
	public void initContent(byte[] bytes) throws DongluInvalidMessageException {
		sendVoice = bytes;
	}

	@Override
	public byte[] toBytes() {
		if(sendVoice.length != LENGTH){
			throw new DongluInvalidMessageException("语音指令长度有误,发送失败!");
		}
		return sendVoice;
	}
	
}
