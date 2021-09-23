package dev.mapper;

import dev.model.Error;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;

@Mapper
public interface ErrorMapper {
    public void insertError(Error error);
    public ArrayList<Error> selectAllError();
}
