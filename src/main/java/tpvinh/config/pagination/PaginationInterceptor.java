package tpvinh.config.pagination;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

@Component
@Intercepts({@Signature(type= StatementHandler.class, method="prepare", args={Connection.class,Integer.class})})
public class PaginationInterceptor implements Interceptor {

	@Autowired
	private ModelMapper modelMapper;

	@Override
    public Object intercept(Invocation invocation) throws PagingException, Throwable {

		StatementHandler statementHandler = (StatementHandler)invocation.getTarget();
        MetaObject metaObject = MetaObject.forObject(statementHandler, SystemMetaObject.DEFAULT_OBJECT_FACTORY, SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY,new DefaultReflectorFactory());
        BoundSql boundSql = statementHandler.getBoundSql();
        Object parameterObject = boundSql.getParameterObject();

        if(Objects.isNull(parameterObject)) {
        	return invocation.proceed();
        }
        // skip
        if(!(parameterObject instanceof ParamMap)) {
        	return invocation.proceed();
        }
        // check pagination prm
        Map<?,?> parameterMap = modelMapper.map(parameterObject, Map.class);
        if(Objects.isNull(parameterMap)){
            return invocation.proceed();
        }
        Object pageRequestObject = parameterMap.get(PageRequest.PARAM_NAME);
        if(Objects.isNull(pageRequestObject)){
            return invocation.proceed();
        }

        // check varargs parameter
        if(pageRequestObject.getClass().isArray()) {
        	if(Array.getLength(pageRequestObject) < 1) {
        		return invocation.proceed(); // skip when not exits
        	}
    		pageRequestObject = Array.get(pageRequestObject, 0);
        }

        // get pagination prm
        PageRequest pageRequest = modelMapper.map(pageRequestObject, PageRequest.class);
        if(pageRequest.getPage() < 1 || pageRequest.getPageSize() < 1) {
        	throw new PagingException("Page or pageSize illegal");
        }

        // get current sql
        String sql = boundSql.getSql()+" LIMIT 9999999999";

        // get total records
        Connection connection = (Connection)invocation.getArgs()[0];
        String totalCntSql = "select count(*) from (" + sql + ") t";
        PreparedStatement countStatement = connection.prepareStatement(totalCntSql);
        ParameterHandler parameterHandler = (ParameterHandler)metaObject.getValue("delegate.parameterHandler");
        parameterHandler.setParameters(countStatement);
        ResultSet rs = countStatement.executeQuery();
        if(rs.next()) {
        	pageRequest.setTotal(rs.getInt(1));
        }

        // calc start list
        int start = (pageRequest.getPage()-1)*pageRequest.getPageSize();

        String orderBy = "ORDER BY ";
        String sortSql = "";
        if(StringUtils.isNotEmpty(pageRequest.getSortSql())) {
        	sortSql = orderBy.concat(pageRequest.getSortSql());
        }
        // create page sql
        String pageSql = "select * from (" + sql + ") t "+sortSql+" limit " + start + ", " + pageRequest.getPageSize();
        metaObject.setValue("delegate.boundSql.sql", pageSql);
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) { }
}
