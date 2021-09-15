package dev.mapper;

import dev.model.Ad;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdMapper {
    void insertAd (Ad ad);
}
