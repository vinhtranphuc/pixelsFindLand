package tpvinh.constant;

public enum GrpRefTableEnum {

    tb_accommodation(
      new RefTypeEnum[] {
        RefTypeEnum.accommodation
      }
    );

    public final RefTypeEnum[] grpRefTypes;

    GrpRefTableEnum(RefTypeEnum[] grpRefTypes) {
        this.grpRefTypes = grpRefTypes;
    }

    public enum RefTypeEnum {
        accommodation // tb_accommodation
    }
}



