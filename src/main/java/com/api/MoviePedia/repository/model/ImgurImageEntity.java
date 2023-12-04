package com.api.MoviePedia.repository.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "imgur_images")
public class ImgurImageEntity {
    @Id
    @Column(name = "id_imgur_image")
    private String id;

    @Column(name = "link")
    private String link;

    @Column(name = "delete_hash")
    private String deleteHash;
}
