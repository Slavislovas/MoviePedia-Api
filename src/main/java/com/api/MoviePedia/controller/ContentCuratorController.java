package com.api.MoviePedia.controller;

import com.api.MoviePedia.exception.RequestBodyFieldValidationException;
import com.api.MoviePedia.model.FieldValidationErrorModel;
import com.api.MoviePedia.model.contentcurator.ContentCuratorCreationDto;
import com.api.MoviePedia.model.contentcurator.ContentCuratorEditDto;
import com.api.MoviePedia.model.contentcurator.ContentCuratorRetrievalDto;
import com.api.MoviePedia.service.ContentCuratorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/content_curator")
@RequiredArgsConstructor
@RestController
public class ContentCuratorController {
    private final ContentCuratorService contentCuratorService;

    @GetMapping("/get/all")
    public ResponseEntity<List<ContentCuratorRetrievalDto>> getAllContentCurators(){
        return ResponseEntity.ok(contentCuratorService.getAllContentCurators());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ContentCuratorRetrievalDto> getContentCuratorById(@PathVariable("id") Long contentCuratorId){
        return new ResponseEntity<>(contentCuratorService.getContentCuratorById(contentCuratorId), HttpStatus.FOUND);
    }

    @PostMapping("/create")
    public ResponseEntity<ContentCuratorRetrievalDto> createContentCurator(@RequestBody @Valid ContentCuratorCreationDto creationDto, BindingResult bindingResult){
        validateRequestBodyFields(bindingResult);
        return new ResponseEntity<>(contentCuratorService.createContentCurator(creationDto), HttpStatus.CREATED);
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<ContentCuratorRetrievalDto> editContentCuratorById(@PathVariable("id") Long contentCuratorId,
                                                                             @RequestBody @Valid ContentCuratorEditDto editDto, BindingResult bindingResult){
        validateRequestBodyFields(bindingResult);
        return ResponseEntity.ok(contentCuratorService.editContentCuratorById(contentCuratorId, editDto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteContentCuratorById(@PathVariable("id") Long contentCuratorId){
        contentCuratorService.deleteContentCuratorById(contentCuratorId);
        return ResponseEntity.ok().build();
    }

    private void validateRequestBodyFields(BindingResult bindingResult) {
        List<FieldValidationErrorModel> fieldValidationErrors = new ArrayList<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            fieldValidationErrors.add(new FieldValidationErrorModel(fieldError.getField(), fieldError.getDefaultMessage()));
        }
        if (!fieldValidationErrors.isEmpty()){
            throw new RequestBodyFieldValidationException(fieldValidationErrors);
        }
    }
}
