package com.api.MoviePedia.repository.model;

import com.api.MoviePedia.enumeration.Genre;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "movies")
public class MovieEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "year")
    private Integer year;

    @Column(name = "genre")
    private Genre genre;

    @Column(name = "trailerYoutubeUrl")
    private String trailerYoutubeUrl;

    @Column(name = "totalRating")
    private Integer totalRating;

    @Column(name = "totalVotes")
    private Integer totalVotes;

    @Column(name = "rating")
    private Double rating;

    @ManyToOne
    @JoinColumn(name = "director_id", nullable = false)
    private DirectorEntity director;

    @ManyToMany
    @JoinTable(
            name = "movie_actor",
            joinColumns = @JoinColumn(name = "movie_id", referencedColumnName = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "actor_id", referencedColumnName = "actor_id")
    )
    private Set<ActorEntity> actors;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.REMOVE)
    private Set<RatingEntity> ratings;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.REMOVE)
    private Set<ReviewEntity> reviews;

    @ManyToMany(mappedBy = "watchlist")
    private Set<UserEntity> usersWithMovieInWatchlist;

    @ManyToMany(mappedBy = "watchedMovies")
    private Set<UserEntity> usersWhoHaveWatchedMovie;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_imgur_image")
    private ImgurImageEntity imgurImageEntity;

    public void rateMovie(Integer rating){
        this.totalRating += rating;
        this.totalVotes++;
        this.rating = roundRating((double) (totalRating / totalVotes));
    }

    private Double roundRating(Double rating){
        BigDecimal bigDecimal = BigDecimal.valueOf(rating);
        bigDecimal = bigDecimal.setScale(2, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }
}
