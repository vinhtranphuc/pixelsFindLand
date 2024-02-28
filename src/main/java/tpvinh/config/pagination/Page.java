package tpvinh.config.pagination;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Page<T> extends PageRequest {

    private List<? extends T> content;

	public Page<T> with(PageRequest pageRequest, List<? extends T> content) {
		this.setPage(pageRequest.getPage());
		this.setPageSize(pageRequest.getPageSize());
		this.setTotal(pageRequest.total);
		this.content = content;
		return this;
	}
}
