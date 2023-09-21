package com.api.MoviePedia.repository.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "reviews")
public class ReviewEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @Column(name = "text")
    private String text;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "likes")
    private Integer likes;

    @Column(name = "dislikes")
    private Integer dislikes;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity reviewer;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private MovieEntity movie;

    public void incrementLikes(){
        this.likes++;
    }

    public void decrementLikes(){
        if (likes > 0){
            this.likes--;
        }
    }

    public void incrementDislikes(){
        this.dislikes++;
    }

    public void decrementDislikes(){
        if (dislikes > 0){
            this.dislikes--;
        }
    }
}
