package com.api.MoviePedia.external;

import com.api.MoviePedia.model.ImgurUploadResponse;
import com.api.MoviePedia.repository.model.ImgurImageEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ImgurApiConnection {
    @Value("${imgur.client.id}")
    private String imgurClientId;

    @Value("${imgur.client.secret}")
    private String imgurClientSecret;

    @Value("${imgur.access.token}")
    private String imgurAccessToken;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String IMGUR_UPLOAD_URL = "https://api.imgur.com/3/image";

    public ImgurImageEntity saveImageToImgur(byte[] imageContents){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", imgurClientId);
        headers.setBearerAuth(imgurAccessToken);
        HttpEntity<byte[]> requestEntity = new HttpEntity<>(imageContents, headers);
        ResponseEntity<ImgurUploadResponse> responseEntity = restTemplate.exchange(IMGUR_UPLOAD_URL, HttpMethod.POST, requestEntity, ImgurUploadResponse.class);
        return new ImgurImageEntity(responseEntity.getBody().getData().getId(),
                responseEntity.getBody().getData().getLink(),
                responseEntity.getBody().getData().getDeleteHash());
    }

    public void deleteImageByHash(String imageHash) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(imgurAccessToken);
        HttpEntity<?> request = new HttpEntity<Object>(headers);
        restTemplate.exchange(IMGUR_UPLOAD_URL + "/" + imageHash, HttpMethod.DELETE, request, String.class);
    }
}
