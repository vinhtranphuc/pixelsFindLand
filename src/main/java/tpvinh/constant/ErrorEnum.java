package tpvinh.constant;

import lombok.Getter;

@Getter
public enum ErrorEnum {
    /**
     * system error
     */
    // error excel template
    EXCEL_TEMPLATE("sys.excel.001");
    private String code;

    ErrorEnum(String code) {
        this.code = code;
    }
}
