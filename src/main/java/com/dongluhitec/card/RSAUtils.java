package com.dongluhitec.card;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class RSAUtils {
	/**
	 * 算法名称
	 */
	private final static String RSA = "RSA";

	/**
	 * 加密后的字节分隔长度
	 */
	private final static int encryptSepLength = 256;

	/**
	 * 明文字节分隔长度
	 */
	private final static int plainSepLneght = 100;
	
	private static PrivateKey prkey;
	private static PublicKey pukey;
	
	static{
		try{
			KeyPairGenerator kpg=KeyPairGenerator.getInstance("RSA");
	        kpg.initialize(1024);
	        KeyPair kp=kpg.genKeyPair();
	        pukey=kp.getPublic();
	        prkey=kp.getPrivate();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private static byte[] encrypt(byte[] text, PublicKey pubRSA)
			throws Exception {
		Cipher cipher = Cipher.getInstance(RSA);
		cipher.init(Cipher.ENCRYPT_MODE, pubRSA);
		return cipher.doFinal(text);
	}

	public final static String encrypt(String text, PublicKey uk) {
		StringBuffer sbf = new StringBuffer(200);
		try {
			text = new BASE64Encoder().encode(text.getBytes("UTF-8"));
//			text = URLEncoder.encode(text, "UTF-8");// 用这个的原因是为了支持汉字、汉字和英文混排,解密方法中同理
			byte[] plainByte = text.getBytes();
			ByteArrayInputStream bays = new ByteArrayInputStream(plainByte);
			byte[] readByte = new byte[plainSepLneght];
			int n = 0;
			// 这个位置很恶心人的写了一堆，是为了支持超过117字节，我每次加密100字节。
			while ((n = bays.read(readByte)) > 0) {
				if (n >= plainSepLneght) {
					sbf.append(byte2hex(encrypt(readByte, uk)));
				} else {
					byte[] tt = new byte[n];
					for (int i = 0; i < n; i++) {
						tt[i] = readByte[i];
					}
					sbf.append(byte2hex(encrypt(tt, uk)));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return sbf.toString();
	}

	public final static String decrypt(String data, PrivateKey rk) {
		String rrr = "";
		StringBuffer sb = new StringBuffer(100);
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(
					data.getBytes());
			// 此处之所以是 256，而不是128的原因是因为有一个16进行的转换，所以由128变为了256
			byte[] readByte = new byte[256];
			int n = 0;
			while ((n = bais.read(readByte)) > 0) {
				if (n >= encryptSepLength) {
					sb.append(new String(decrypt(hex2byte(readByte), rk)));
				} else {

				}
			}
			byte[] decodeBuffer = new BASE64Decoder().decodeBuffer(sb.toString());
			rrr = new String(decodeBuffer,"UTF-8");
//			rrr = URLDecoder.decode(sb.toString(), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rrr;
	}

	private static byte[] decrypt(byte[] src, PrivateKey rk) throws Exception {
		Cipher cipher = Cipher.getInstance(RSA);
		cipher.init(Cipher.DECRYPT_MODE, rk);
		return cipher.doFinal(src);
	}

	public static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = Integer.toHexString(b[n] & 0xFF);
			if (stmp.length() == 1)
				hs += ("0" + stmp);
			else
				hs += stmp;
		}
		return hs.toUpperCase();
	}

	public static byte[] hex2byte(byte[] b) {
		if ((b.length % 2) != 0)
			throw new IllegalArgumentException("长度不是偶数");
		byte[] b2 = new byte[b.length / 2];
		for (int n = 0; n < b.length; n += 2) {
			String item = new String(b, n, 2);
			b2[n / 2] = (byte) Integer.parseInt(item, 16);
		}
		return b2;
	}

	public static PrivateKey getPrivateKey() {
		return prkey;
	}

	public static PublicKey getPublicKey(String pubKey) {
		try {
			String publicKeyStr = pubKey;
			byte[] keyBytes;
			keyBytes = new BASE64Decoder().decodeBuffer(publicKeyStr);
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PublicKey publicKey = keyFactory.generatePublic(keySpec);
			return publicKey;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static PublicKey getPublicKey() {
		return pukey;
	}
	
	public static String getPublicKeyString(){
		return (new BASE64Encoder()).encode(pukey.getEncoded());
	}

}
