package com.api.MoviePedia.repository.model;

import com.api.MoviePedia.enumeration.Genre;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "movies")
public class MovieEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "genre")
    private Genre genre;

    @Column(name = "picture", columnDefinition = "MEDIUMBLOB")
    @Lob
    private Byte[] picture;

    @Column(name = "trailer", columnDefinition = "LONGBLOB")
    @Lob
    private Byte[] trailer;

    @Column(name = "totalRating")
    private Integer totalRating;

    @Column(name = "totalVotes")
    private Integer totalVotes;

    @Column(name = "rating")
    private Double rating;

    @ManyToOne
    @JoinColumn(name = "director_id", nullable = false)
    private DirectorEntity directorEntity;

    @ManyToMany
    @JoinTable(
            name = "movie_actor",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "actor_id")
    )
    private Set<ActorEntity> actorEntities;

    @OneToMany(mappedBy = "movieEntity")
    private Set<ReviewEntity> reviewEntities;

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
