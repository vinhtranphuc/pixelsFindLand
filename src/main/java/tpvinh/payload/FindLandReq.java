package tpvinh.payload;

import lombok.Data;

@Data
public class FindLandReq {

    private Long landFrom;
    private Long landTo;
}
