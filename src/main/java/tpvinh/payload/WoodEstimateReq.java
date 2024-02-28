package tpvinh.payload;

import lombok.Data;

@Data
public class WoodEstimateReq {

    private Long landId;
    private Integer woodPercent;
}
