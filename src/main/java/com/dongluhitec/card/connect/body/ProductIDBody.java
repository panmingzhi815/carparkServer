package com.dongluhitec.card.connect.body;

import com.dongluhitec.card.connect.MessageBody;

public class ProductIDBody implements MessageBody {
    public static final int LENGTH = 20;

    private String productinId = "";

    public String getProductinId() {
        return this.productinId;
    }

    public void setProductinId(String productinId) {
        this.productinId = productinId;
    }

    @Override
    public void initContent(byte[] array) {
        assert array.length == ProductIDBody.LENGTH;
        int index = -1;
        for (int i = 0; i < array.length; i++) {
            if ((array[i] == '$') || (array[i] == 0)) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            this.productinId = new String(array, 0, index);
        } else {
            this.productinId = new String(array);
        }
    }

    @Override
    public byte[] toBytes() {
        byte[] array = new byte[ProductIDBody.LENGTH];
        byte[] bytes = this.productinId.getBytes();
        assert bytes.length < (ProductIDBody.LENGTH - 1);
        System.arraycopy(bytes, 0, array, 0, bytes.length);
        array[bytes.length] = (byte) '$';
        return array;
    }

    @Override
    public String toString() {
        return "产品号: " + this.productinId;
    }
}