package com.api.MoviePedia.builder;

import com.api.MoviePedia.enumeration.DataOption;
import com.api.MoviePedia.enumeration.SearchOperation;
import com.api.MoviePedia.model.SearchCriteriaDto;
import com.api.MoviePedia.repository.model.MovieEntity;
import com.api.MoviePedia.specification.MovieSpecification;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class MovieSpecificationBuilder {
    private List<SearchCriteriaDto> params;

    public MovieSpecificationBuilder(){
        this.params = new ArrayList<>();
    }

    public final MovieSpecificationBuilder with(String target, String key, SearchOperation searchOperation, Object value){
        params.add(new SearchCriteriaDto(target, key, value, searchOperation));
        return this;
    }

    public final MovieSpecificationBuilder with(SearchCriteriaDto searchCriteria){
        params.add(searchCriteria);
        return this;
    }

    public Specification<MovieEntity> build(){
        if (params.size() == 0){
            return null;
        }
        Specification<MovieEntity> result = new MovieSpecification(params.get(0));
        for (int i = 1; i < params.size(); i++){
            SearchCriteriaDto searchCriteria = params.get(i);
            result = searchCriteria.getDataOption() == DataOption.ALL ?
                    Specification.where(result).and(new MovieSpecification(searchCriteria)) :
                    Specification.where(result).or(new MovieSpecification(searchCriteria));
        }
        return result;
    }
}
