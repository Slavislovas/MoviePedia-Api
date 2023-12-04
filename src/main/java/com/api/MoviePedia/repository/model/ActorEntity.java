package com.api.MoviePedia.repository.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "actors")
public class ActorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "actor_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "dateOfBirth")
    private LocalDate dateOfBirth;

    @Column(name = "biography", columnDefinition = "TEXT")
    private String biography;

    @ManyToMany(mappedBy = "actors", fetch = FetchType.EAGER)
    private Set<MovieEntity> movies;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_imgur_image")
    private ImgurImageEntity imgurImageEntity;
}
