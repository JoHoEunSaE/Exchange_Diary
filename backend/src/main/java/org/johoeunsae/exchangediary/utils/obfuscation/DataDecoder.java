package org.johoeunsae.exchangediary.utils.obfuscation;

/**
 * 난독화된 데이터를 다시 가독화하는 인터페이스 추후에 암호화를 적용한다면 복호화에 이 인터페이스를 이용할 수 있음
 */
@Deprecated
public interface DataDecoder {

	String decode(String data);
}
