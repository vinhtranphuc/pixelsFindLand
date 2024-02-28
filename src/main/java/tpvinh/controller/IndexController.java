package tpvinh.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import tpvinh.mybatis.model.ForestEstimateVO;
import tpvinh.payload.FindLandReq;
import tpvinh.payload.WoodEstimateReq;
import tpvinh.service.LandService;

@Controller
@RequestMapping("")
public class IndexController {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private LandService landService;

    @GetMapping("")
    public String index(Model model, HttpServletRequest request, HttpServletResponse response) {
        return "/forestFindLand";
    }

    @GetMapping("forestEstimated")
    public String forestEstimated(Model model, HttpServletRequest request, HttpServletResponse response) {
        return "/forestEstimated";
    }

    @PostMapping(path = "updateWoodEstimate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ForestEstimateVO> updateWoodEstimate(@Valid @RequestBody WoodEstimateReq request) {
        ForestEstimateVO result = landService.updateWoodEstimate(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping(path = "togleLockForest", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ForestEstimateVO> togleLockForest(@Valid @RequestBody WoodEstimateReq request) {
        ForestEstimateVO result = landService.togleLockForest(request.getLandId());
        return ResponseEntity.ok(result);
    }

    @PostMapping(path = "deleteForestEstimate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteForestEstimate(@Valid @RequestBody WoodEstimateReq request) {
        landService.deleteForestEstimate(request.getLandId());
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "forestEstimatedList", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ForestEstimateVO>> selectLandForestEstimatedList(@Valid @RequestBody FindLandReq findLandReq) {
        List<ForestEstimateVO> result = landService.selectLandForestEstimatedList(findLandReq);
        return ResponseEntity.ok(result);
    }

    @PostMapping(path = "forestEstimated", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ForestEstimateVO> forestEstimated(@Valid @RequestBody Long landId) {
        ForestEstimateVO result = landService.selectLandForestEstimated(landId);
        return ResponseEntity.ok(result);
    }
}
