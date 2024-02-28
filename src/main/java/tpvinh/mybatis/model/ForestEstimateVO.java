package tpvinh.mybatis.model;

import lombok.Data;

@Data
public class ForestEstimateVO {

    private Long landId;
    private Long accountId;
    private Integer forestEstimatedPercent;
    private String forestEstimatedDt;
    private Boolean locked;
}
