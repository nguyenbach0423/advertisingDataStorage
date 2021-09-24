package dev.mapper;

import dev.model.Ad;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdMapper {
    void insertMultiAd (List<Ad> ads);
}
