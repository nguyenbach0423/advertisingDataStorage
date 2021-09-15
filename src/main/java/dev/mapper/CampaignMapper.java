package dev.mapper;

import dev.model.Campaign;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CampaignMapper {
    void insertCampaign (Campaign campaign);
}
