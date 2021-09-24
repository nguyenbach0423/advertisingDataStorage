package dev.mapper;

import dev.model.Campaign;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CampaignMapper {
    void insertMultiCampaign (List<Campaign> campaigns);
}
