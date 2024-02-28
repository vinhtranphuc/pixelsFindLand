package tpvinh.constant;

import lombok.Getter;

@Getter
public enum RoleAdminEnum {

	ROLE_ADMIN("1"),
	ROLE_SUPER_ADMIN("2");

	private String id;

	RoleAdminEnum(String roleId) {
		this.id = roleId;
	}
}
