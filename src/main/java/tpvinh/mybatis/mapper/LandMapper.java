package tpvinh.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import tpvinh.mybatis.model.ForestEstimateVO;
import tpvinh.mybatis.model.LandVO;

public interface LandMapper {

    void createLand(LandVO land);

    void updateForestEstimate(ForestEstimateVO prm);

    void togleLockForest(
            @Param("landId") Long landId,
            @Param("accountId") Long accountId);

    List<ForestEstimateVO> selectLandForestEstimatedList(
            @Param("accountId") Long accountId,
            @Param("landFrom") Long landFrom,
            @Param("landTo") Long landTo);

    ForestEstimateVO selectLandForestEstimated(
            @Param("accountId") Long accountId,
            @Param("landId") Long landId);

    void deleteForestEstimate(
            @Param("accountId") Long accountId,
            @Param("landId") Long landId);

}
