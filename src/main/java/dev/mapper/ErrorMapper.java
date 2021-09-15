package dev.mapper;

import dev.model.Error;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ErrorMapper {
    void insertError(Error error);
}
