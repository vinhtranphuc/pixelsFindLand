package tpvinh.mybatis.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import tpvinh.mybatis.model.AccountVO;

@Mapper
public interface AuthMapper {

    AccountVO selectAccount(@Param("id") String id, @Param("loginId") String loginId);
}
