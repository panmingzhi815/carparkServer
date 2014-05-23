package com.dongluhitec.card.hardware.message.body;

import com.dongluhitec.card.hardware.message.DongluInvalidMessageException;
import com.dongluhitec.card.hardware.message.MessageBody;


/**
 * Created by panmingzhi815 on 14-3-21.
 */
public class CarparkNowRecordBody implements MessageBody {

    public static final int LENGTH = 18;

    final private int BIT_OF_NORECORD = 0;
    final private static byte BIT_NORECORD = 0x6E;

    final private int BIT_OF_CARDID = 1;
    final private int BIT_CARDID_LENGTH = 8;

    private CardIDBody cardID = new CardIDBody();
    private boolean hasRecord = false;

    public boolean isHasRecord() {
        return hasRecord;
    }

    public void setHasRecord(boolean hasRecord) {
        this.hasRecord = hasRecord;
    }

    @Override
    public void initContent(byte[] bytes) throws DongluInvalidMessageException {
        assert bytes.length == CarparkNowRecordBody.LENGTH;

        this.hasRecord = true;
        if (bytes[BIT_OF_NORECORD] == CarparkNowRecordBody.BIT_NORECORD) {
            this.hasRecord = false;
            return;
        }

        byte[] array = new byte[BIT_CARDID_LENGTH];

        System.arraycopy(bytes, BIT_OF_CARDID, array, 0, BIT_CARDID_LENGTH);
        this.cardID.initContent(array);
    }

    @Override
    public byte[] toBytes() {
        byte[] result = new byte[CarparkNowRecordBody.LENGTH];

        if (isHasRecord() == false) {
            result[BIT_OF_NORECORD] = BIT_NORECORD;
            return result;
        }

        byte[] array = cardID.toBytes();
        System.arraycopy(array, 0, result, BIT_OF_CARDID, BIT_CARDID_LENGTH);

        return result;
    }

    public String getCardID() {
        return cardID.getCardID();
    }
}
