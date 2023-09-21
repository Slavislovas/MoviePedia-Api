package com.api.MoviePedia.specification;

import com.api.MoviePedia.enumeration.Genre;
import com.api.MoviePedia.model.SearchCriteriaDto;
import com.api.MoviePedia.repository.model.ActorEntity;
import com.api.MoviePedia.repository.model.DirectorEntity;
import com.api.MoviePedia.repository.model.MovieEntity;
import com.api.MoviePedia.util.MovieSearchConstants;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public class MovieSpecification implements Specification<MovieEntity> {
    private final SearchCriteriaDto searchCriteria;
    private final Map<String, Function<String, ?>> parsingFunctions;

    public MovieSpecification(SearchCriteriaDto searchCriteria){
        super();
        this.searchCriteria = searchCriteria;
        parsingFunctions = new HashMap<>();
        parsingFunctions.put("year", Integer::parseInt);
        parsingFunctions.put("genre", Genre::getGenre);
        parsingFunctions.put("rating", Double::parseDouble);
    }

    @Override
    public Predicate toPredicate(Root<MovieEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        String stringToSearch = searchCriteria.getValue().toString().toLowerCase();
        String filterKey = searchCriteria.getFilterKey();
        String target = searchCriteria.getTarget();
        switch (Objects.requireNonNull(searchCriteria.getOperation())){
            case CONTAINS -> {
                if (target.equals("director") && MovieSearchConstants.validDirectorSearchFields.contains(filterKey)){
                    return criteriaBuilder.like(criteriaBuilder.lower(directorJoin(root).get(filterKey)), "%" + stringToSearch + "%");
                } else if (target.equals("actor") && MovieSearchConstants.validActorSearchFields.contains(filterKey)){
                    return criteriaBuilder.like(criteriaBuilder.lower(actorsJoin(root).get(filterKey)), "%" + stringToSearch + "%");
                } else if (target.equals("movie") && filterKey.equals("title")){
                    return criteriaBuilder.like(criteriaBuilder.lower(root.get(filterKey)), "%" + stringToSearch + "%");
                }
            }
            case DOES_NOT_CONTAIN -> {
                if (target.equals("director") && MovieSearchConstants.validDirectorSearchFields.contains(searchCriteria.getFilterKey())){
                    return criteriaBuilder.notLike(criteriaBuilder.lower(directorJoin(root).get(filterKey)), "%" + stringToSearch + "%");
                } else if (target.equals("actor") && MovieSearchConstants.validActorSearchFields.contains(searchCriteria.getFilterKey())){
                    return criteriaBuilder.notLike(criteriaBuilder.lower(actorsJoin(root).get(filterKey)), "%" + stringToSearch + "%");
                } else if (target.equals("movie") && filterKey.equals("title")){
                    return criteriaBuilder.notLike(criteriaBuilder.lower(root.get(filterKey)), "%" + stringToSearch + "%");
                }
            }
            case EQUAL -> {
                if (target.equals("director") && MovieSearchConstants.validDirectorSearchFields.contains(filterKey)){
                    return criteriaBuilder.equal(criteriaBuilder.lower(directorJoin(root).get(filterKey)), stringToSearch);
                } else if (target.equals("actor") && MovieSearchConstants.validActorSearchFields.contains(searchCriteria.getFilterKey())){
                    return criteriaBuilder.equal(criteriaBuilder.lower(actorsJoin(root).get(filterKey)), stringToSearch);
                } else if (target.equals("movie") && filterKey.equals("title")){
                    return criteriaBuilder.equal(criteriaBuilder.lower(actorsJoin(root).get(filterKey)), stringToSearch);
                } else if (target.equals("movie") && MovieSearchConstants.validMovieSearchFields.contains(filterKey)){
                    return criteriaBuilder.equal(root.get(filterKey), parsingFunctions.get(filterKey).apply(stringToSearch));
                }
            }
            case NOT_EQUAL -> {
                if (target.equals("director") && MovieSearchConstants.validDirectorSearchFields.contains(searchCriteria.getFilterKey())){
                    return criteriaBuilder.notEqual(criteriaBuilder.lower(directorJoin(root).get(filterKey)), stringToSearch);
                } else if (target.equals("actor") && MovieSearchConstants.validActorSearchFields.contains(searchCriteria.getFilterKey())){
                    return criteriaBuilder.notEqual(criteriaBuilder.lower(actorsJoin(root).get(filterKey)), stringToSearch);
                } else if (target.equals("movie") && filterKey.equals("title")){
                    return criteriaBuilder.notEqual(criteriaBuilder.lower(actorsJoin(root).get(filterKey)), stringToSearch);
                } else if (target.equals("movie") && MovieSearchConstants.validMovieSearchFields.contains(filterKey)){
                    return criteriaBuilder.notEqual(root.get(filterKey), parsingFunctions.get(filterKey).apply(stringToSearch));
                }
            }
            case GREATER_THAN_EQUAL -> {
                if (target.equals("movie") && filterKey.equals("year")){
                    return criteriaBuilder.greaterThanOrEqualTo(root.get(filterKey), Integer.parseInt(stringToSearch));
                } else if (target.equals("movie") && filterKey.equals("rating")){
                    return criteriaBuilder.greaterThanOrEqualTo(root.get(filterKey), Double.parseDouble(stringToSearch));
                }
            }
            case LESS_THAN_EQUAL -> {
                if (target.equals("movie") && filterKey.equals("year")){
                    return criteriaBuilder.lessThanOrEqualTo(root.get(filterKey), Integer.parseInt(stringToSearch));
                } else if (target.equals("movie") && filterKey.equals("rating")){
                    return criteriaBuilder.lessThanOrEqualTo(root.get(filterKey), Double.parseDouble(stringToSearch));
                }
            }
            case GREATER_THAN -> {
                if (target.equals("movie") && filterKey.equals("year")){
                    return criteriaBuilder.greaterThan(root.get(filterKey), Integer.parseInt(stringToSearch));

                } else if (target.equals("movie") && filterKey.equals("rating")){
                    return criteriaBuilder.greaterThan(root.get(filterKey), Double.parseDouble(stringToSearch));
                }
            }
            case LESS_THAN -> {
                if (target.equals("movie") && filterKey.equals("year")){
                    return criteriaBuilder.lessThan(root.get(filterKey), Integer.parseInt(stringToSearch));
                } else if (target.equals("movie") && filterKey.equals("rating")){
                    return criteriaBuilder.lessThan(root.get(filterKey), Double.parseDouble(stringToSearch));
                }
            }
        }
        return null;
    }

    private Join<MovieEntity, Set<ActorEntity>> actorsJoin(Root<MovieEntity> root) {
        return root.join("actors");
    }

    private Join<MovieEntity, DirectorEntity> directorJoin(Root<MovieEntity> root) {
        return root.join("director");
    }
}
