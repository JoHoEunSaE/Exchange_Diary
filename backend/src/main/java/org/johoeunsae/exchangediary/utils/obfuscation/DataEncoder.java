package org.johoeunsae.exchangediary.utils.obfuscation;

/**
 * 데이터를 인코딩(난독화)하는 인터페이스 추후에 암호화를 적용한다면 이 인터페이스를 이용할 수 있음
 */
public interface DataEncoder {

	String encode(String data);
}
