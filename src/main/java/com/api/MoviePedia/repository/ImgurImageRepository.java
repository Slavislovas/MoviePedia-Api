package com.api.MoviePedia.repository;

import com.api.MoviePedia.repository.model.ImgurImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImgurImageRepository extends JpaRepository<ImgurImageEntity, String> {
}
