package tpvinh.service;

import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import tpvinh.config.security.AuthUtil;
import tpvinh.mybatis.mapper.LandMapper;
import tpvinh.mybatis.model.ForestEstimateVO;
import tpvinh.mybatis.model.LandVO;
import tpvinh.payload.FindLandReq;
import tpvinh.payload.WoodEstimateReq;

@Service
public class LandService {

    @Resource
    private LandMapper landMapper;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void createLand() {
        for(long id = 1; id <= 5000; id++) {
            LandVO landPrm = new LandVO();
            landPrm.setId(id);
            landPrm.setName(id+"");
            landMapper.createLand(landPrm);
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ForestEstimateVO updateWoodEstimate(@Valid WoodEstimateReq request) {
        ForestEstimateVO prm = new ForestEstimateVO();
        Long accountId = AuthUtil.crrAcc().getId();
        prm.setAccountId(accountId);
        prm.setLandId(request.getLandId());
        prm.setForestEstimatedPercent(request.getWoodPercent());
        landMapper.updateForestEstimate(prm);
        return landMapper.selectLandForestEstimated(accountId, request.getLandId());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ForestEstimateVO togleLockForest(@Valid Long landId) {
        Long accountId = AuthUtil.crrAcc().getId();
        landMapper.togleLockForest(landId, accountId);
        return landMapper.selectLandForestEstimated(accountId, landId);
    }

    public List<ForestEstimateVO> selectLandForestEstimatedList(@Valid FindLandReq findLandReq) {
        Long accountId = AuthUtil.crrAcc().getId();
        return landMapper.selectLandForestEstimatedList(accountId, findLandReq.getLandFrom(), findLandReq.getLandTo());
    }

    public ForestEstimateVO selectLandForestEstimated(@Valid Long landId) {
        Long accountId = AuthUtil.crrAcc().getId();
        return landMapper.selectLandForestEstimated(accountId, landId);
    }

    public void deleteForestEstimate(Long landId) {
        Long accountId = AuthUtil.crrAcc().getId();
        landMapper.deleteForestEstimate(accountId, landId);
    }
}
