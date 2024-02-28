package tpvinh.config.database;

import java.util.Set;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import tpvinh.config.interceptor.MyBatisInterceptor;
import tpvinh.config.pagination.PaginationInterceptor;

/**
 * Configure connection to MariaDB database
 *
 * @author Vinh
 */
@PropertySource("classpath:mariadb.properties")
@Configuration
@ConfigurationProperties
@EnableConfigurationProperties(MariadbConfig.class)
@EnableTransactionManagement
@MapperScan(basePackages = "tpvinh.mybatis", sqlSessionFactoryRef = "primartSqlSessionFactory")
public class MariadbConfig {

    private org.apache.ibatis.session.Configuration configuration;

    // TIMEZONE
    @Value("${server.timezone}")
    protected String severTimezone;

    // SEVER
    @Value("${mariadb.host}")
    protected String host;
    @Value("${mariadb.port}")
    protected String port;
    @Value("${mariadb.database}")
    protected String database;
    @Value("${mariadb.username}")
    protected String username;
    @Value("${mariadb.password}")
    protected String password;

    // ORM
    @Value("${mariadb.mybatis.model}")
    protected String mybatisModelPgk;
    @Value("${mariadb.mybatis.resources}")
    protected String myabatisResource;

    // regist pagination intercept
    @Autowired
    PaginationInterceptor pgInterceptor;

        @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    /**
     * Initialize DataSource
     *
     * @return
     */
    @Primary
    @Bean(name = "primaryDataSource")
    public DataSource primaryDataSource() {
        final String URL = "jdbc:mysql://%s:%s/%s?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=UTF-8&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=%s&zeroDateTimeBehavior=convertToNull&connectTimeout=60000&socketTimeout=60000";
        String formattedUrl = String.format(URL, host, port, database, severTimezone);
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(formattedUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    /**
     * Initialize SqlSessionFactory
     *
     * @param applicationContext
     * @return
     * @throws Exception
     */
    @Primary
    @Bean
    public SqlSessionFactory primartSqlSessionFactory(ApplicationContext applicationContext) throws Exception {
        final SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();

        // mybatis config
        configuration = new org.apache.ibatis.session.Configuration();
        configuration.setCacheEnabled(false);
        configuration.setCallSettersOnNulls(true);
        configuration.setUseGeneratedKeys(true);
        configuration.setDefaultExecutorType(ExecutorType.REUSE);
         configuration.setMapUnderscoreToCamelCase(true);
        registTypeAlias(mybatisModelPgk);

        sqlSessionFactory.setConfiguration(configuration);

        // mybatis mapper config
        sqlSessionFactory.setMapperLocations(applicationContext.getResources(myabatisResource));

        // set data source
        sqlSessionFactory.setDataSource(primaryDataSource());

        // ibatis transaction
        sqlSessionFactory.setTransactionFactory(new SpringManagedTransactionFactory ());

        // mybatis interceptor
        MyBatisInterceptor myBatisQueryIntercept = new MyBatisInterceptor();
        sqlSessionFactory.setPlugins(new Interceptor[] { myBatisQueryIntercept, pgInterceptor });

        return sqlSessionFactory.getObject();
    }

    /**
     * Register type alias for model
     *
     * @param packageScan
     * @param classes
     * @throws ClassNotFoundException
     */
    // regist type alias
    private void registTypeAlias(final String packageScan, Class<?>... classes) throws ClassNotFoundException {
        final ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(
                false);
        provider.addIncludeFilter(new RegexPatternTypeFilter(Pattern.compile(".*")));
        final Set<BeanDefinition> packageScanClasses = provider.findCandidateComponents(packageScan);

        // model bean
        for (BeanDefinition bean : packageScanClasses) {
            Class<?> clazz = Class.forName(bean.getBeanClassName());
            configuration.getTypeAliasRegistry().registerAlias(clazz);
        }
        for (Class<?> clazz : classes) {
            // register with lowerCase field
            configuration.getTypeAliasRegistry().registerAlias(clazz);
        }
    }

    /**
     * Initialize SqlSessionTemplate
     *
     * @param primarySqlSessionFactory
     * @return
     */
    @Primary
    @Bean
    public SqlSessionTemplate primarySqlSessionTemplate(SqlSessionFactory primartSqlSessionFactory) {
        return new SqlSessionTemplate(primartSqlSessionFactory);
    }

    @Bean
    public DataSourceTransactionManager transactionManager() {
        return new DataSourceTransactionManager(primaryDataSource());
    }
}
