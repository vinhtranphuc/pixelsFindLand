package tpvinh.config.pagination;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class PageRequest extends AbstractPage {

	public static final String PARAM_NAME = "pageRequest";

	@NotNull
    private int page = 1; // default

	@NotNull
    private int pageSize = 10; // default

    private String sortSql;

	@Override
	public void setTotal(int total) {
		this.total = total;
	}
}
