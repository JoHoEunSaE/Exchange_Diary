package org.johoeunsae.exchangediary.image.service;

public interface ImageService {
	/**
	 * 클라이언트에서 파일을 업로드 하기 위한 URL을 반환한다.
	 *
	 * @param imageUrl 파일이 저장될 s3 경로 (Key)
	 * @return 클라이언트에서 파일을 업로드 하기 위한 presigned-url
	 */
	String getPreSignedUrl(String imageUrl);

	/**
	 * s3에 저장된 객체의 URL을 반환한다.
	 *
	 * @param imageUrl 파일이 저장된 s3 경로 (Key)
	 * @return s3에 저장된 객체의 URL
	 */
	String getImageUrl(String imageUrl);

	/**
	 * s3에 저장된 객체를 삭제한다.
	 *
	 * @param imageUrl 파일이 저장된 s3 경로 (Key)
	 */
	void deleteImage(String imageUrl);

	/**
	 * 파일 경로가 지정한 디렉토리에 있는지 확인한다. 기본적인 유효성 검사도 포함한다.
	 *
	 * @param imageUrl 파일 경로
	 * @param dirName 디렉토리 이름
	 */
	void validImageUrl(String imageUrl, String dirName);

	/**
	 * 이미지 URL에서 이미지 경로를 추출한다.
	 * @param imageUrl 이미지 URL
	 * @return 이미지 경로
	 */
	String parseImageUrl(String imageUrl, String dirName);
}
