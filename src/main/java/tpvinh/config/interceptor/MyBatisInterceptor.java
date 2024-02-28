package tpvinh.config.interceptor;

import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import tpvinh.config.util.MybatisUtil;


/**
 * MyBatis SQL Debug
 * @param <T>
 */
@Service
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
public class MyBatisInterceptor implements Interceptor {
    private Logger logger = LoggerFactory.getLogger(getClass());

    final private static String LOGGING_FORMAT = "\n==================== mapperId ====================\n" + "{}\n" + "==================== query ====================\n" + "\t\t{}";

	@Override
    public Object intercept(Invocation invocation) throws Throwable {
        System.out.println("----------------MyBatisQueryIntercept--------------");
        try {
            if (logger.isDebugEnabled()) {
                Object[] args = invocation.getArgs();
                MappedStatement mappedStatement = (MappedStatement) args[0];
                Object parameterObject = args[1];
                String query = MybatisUtil.makeQuery(mappedStatement, parameterObject);
                logger.debug(LOGGING_FORMAT, mappedStatement.getId(), query);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties arg0) {
    }
}