package com.api.MoviePedia.service;

import com.api.MoviePedia.model.contentcurator.ContentCuratorCreationDto;
import com.api.MoviePedia.model.contentcurator.ContentCuratorEditDto;
import com.api.MoviePedia.model.contentcurator.ContentCuratorRetrievalDto;

import java.util.List;

public interface ContentCuratorService {
    List<ContentCuratorRetrievalDto> getAllContentCurators();

    ContentCuratorRetrievalDto getContentCuratorById(Long contentCuratorId);

    ContentCuratorRetrievalDto createContentCurator(ContentCuratorCreationDto creationDto);

    ContentCuratorRetrievalDto editContentCuratorById(Long contentCuratorId, ContentCuratorEditDto editDto);

    void deleteContentCuratorById(Long contentCuratorId);
}
