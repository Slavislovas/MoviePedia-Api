package com.api.MoviePedia.model.movie;

import com.api.MoviePedia.enumeration.DataOption;
import com.api.MoviePedia.enumeration.SearchOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SearchCriteriaDto {
    private String target;
    private String filterKey;
    private Object value;
    private SearchOperation operation;
    private DataOption dataOption;

    public SearchCriteriaDto(String target, String filterKey, Object value, SearchOperation operation){
        this.target = target;
        this.filterKey = filterKey;
        this.value = value;
        this.operation = operation;
    }
}
