package com.api.MoviePedia.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImgurUploadResponse {

    private ImgurData data;

    // Constructors, getters, and setters

    @Override
    public String toString() {
        return "ImgurUploadResponse{" +
                "data=" + data +
                '}';
    }

    @Data
    public static class ImgurData {

        private String id;
        private String link;
        private String deleteHash;

        // Constructors, getters, and setters

        @Override
        public String toString() {
            return "ImgurData{" +
                    "id='" + id + '\'' +
                    ", link='" + link + '\'' +
                    '}';
        }
    }
}
