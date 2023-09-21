package com.api.MoviePedia.model;

import com.api.MoviePedia.enumeration.DataOption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SearchDto {
    private List<SearchCriteriaDto> searchCriteriaList;
    private DataOption dataOption;
}
