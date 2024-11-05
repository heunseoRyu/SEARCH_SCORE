package com.example.gal.domain.files.dto;

import lombok.Builder;
import org.bson.types.ObjectId;

import java.util.List;

@Builder
public record StudentScore(
        ObjectId id,
        Integer key,
        String grade,
        String cls,
        String num,
        List<String> values) {
}
