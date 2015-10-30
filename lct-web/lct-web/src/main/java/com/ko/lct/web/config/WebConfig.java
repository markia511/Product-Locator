package com.ko.lct.web.config;

import java.io.File;

import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.DiskStoreConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.ko.lct.web.repository.LocatorRepository;
import com.ko.lct.web.repository.WebServiceLocatorCache;
import com.ko.lct.web.util.APIUsageExcelView;
import com.ko.lct.web.util.QueriesPerBrandExcelView;
import com.ko.lct.web.util.SearchListExcelView;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "com.ko.lct.web" })
@EnableCaching
@PropertySources(value = {@PropertySource("classpath:application.properties")})
public class WebConfig extends WebMvcConfigurerAdapter implements CachingConfigurer {

    private static final int RESOURCES_CACHE_PERIOD = 8 * 60 * 60; // 8 hours
    private static final int MAX_ELEMENTS_IN_MEMORY = 500;
    private static final int MAX_ELEMENTS_ON_DISK = 10000;
    private static final int EHCACHE_TIME_TO_IDLE = 12 * 60 * 60; // 12 hours
    private static final int EHCACHE_TIME_TO_LIVE = 24 * 60 * 60; // 24 hours
    private static final long EHCACHE_DISK_EXPIRY_THREAD_INTERVAL = 150;

    private static final String PRODUCT_PACKAGES_TYPES_CACHE_NAME = "productPackageTypes";

    private static final String[] CACHE_NAMES = new String[] {
	    "beverageCategories",
	    "brands",
	    "prods",
	    "flavors",
	    "tradeChannels",
	    "subTradeChannels",
	    "primaryContainers",
	    "correctBrand",
	    "correctPrimaryContainer",
	    "shortPrimaryContainers",
	    "secondaryPackages",
	    "shortSecondaryPackages",
	    "states",
	    "keywordDictionary",
	    "products",
	    "physicalStates",
	    "tradeChannelsAll",
	    "packages",
	    PRODUCT_PACKAGES_TYPES_CACHE_NAME,
	    "businessTypes",
	    "statesAll",
	    "countries"
    };

    protected final Log logger = LogFactory.getLog(getClass());

    @Value("${web_service_url}")
    private String webServiceUrl;

    @Value("${google.api.clientId}")
    private String googleAPIClientId;

    @Value("${google.api.channel}")
    private String googleAPIChannel;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
	super.addViewControllers(registry);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
	registry.addResourceHandler("/resources/**")
		.addResourceLocations("/resources/")
		.setCachePeriod(Integer.valueOf(RESOURCES_CACHE_PERIOD));
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer() {
	return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public InternalResourceViewResolver internalResourceViewResolver() {
	InternalResourceViewResolver resolver = new InternalResourceViewResolver();
	resolver.setPrefix("/WEB-INF/views/");
	resolver.setSuffix(".jsp");
	return resolver;
    }

    @Bean(destroyMethod = "shutdown")
    public net.sf.ehcache.CacheManager ehCacheManager() {
	net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();

	CacheConfiguration cacheConfiguration = new CacheConfiguration("default", MAX_ELEMENTS_IN_MEMORY);
	cacheConfiguration.setMemoryStoreEvictionPolicyFromObject(MemoryStoreEvictionPolicy.LRU);
	cacheConfiguration.setMaxElementsOnDisk(MAX_ELEMENTS_ON_DISK);
	cacheConfiguration.setTimeToIdleSeconds(EHCACHE_TIME_TO_IDLE);
	cacheConfiguration.setTimeToLiveSeconds(EHCACHE_TIME_TO_LIVE);
	cacheConfiguration.setDiskExpiryThreadIntervalSeconds(EHCACHE_DISK_EXPIRY_THREAD_INTERVAL);
	cacheConfiguration.setEternal(false);
	cacheConfiguration.addPersistence((new PersistenceConfiguration()).strategy(PersistenceConfiguration.Strategy.LOCALTEMPSWAP));
	config.addDefaultCache(cacheConfiguration);
	for (String cacheName : CACHE_NAMES) {
	    boolean isProdOrPrimCont = (!PRODUCT_PACKAGES_TYPES_CACHE_NAME.equals(cacheName))
		    && (cacheName.toUpperCase().contains("PROD") || cacheName.toUpperCase().contains("PRIM"));
	    cacheConfiguration = new CacheConfiguration(cacheName, MAX_ELEMENTS_IN_MEMORY);
	    cacheConfiguration.setMemoryStoreEvictionPolicyFromObject(MemoryStoreEvictionPolicy.LRU);
	    cacheConfiguration.setMaxElementsOnDisk(isProdOrPrimCont ? MAX_ELEMENTS_ON_DISK : 0);
	    cacheConfiguration.setTimeToIdleSeconds(EHCACHE_TIME_TO_IDLE);
	    cacheConfiguration.setTimeToLiveSeconds(EHCACHE_TIME_TO_LIVE);
	    cacheConfiguration.setDiskExpiryThreadIntervalSeconds(EHCACHE_DISK_EXPIRY_THREAD_INTERVAL);
	    cacheConfiguration.setEternal(false);
	    cacheConfiguration.addPersistence((new PersistenceConfiguration()).strategy(
		    isProdOrPrimCont ? PersistenceConfiguration.Strategy.LOCALTEMPSWAP : PersistenceConfiguration.Strategy.NONE));
	    config.addCache(cacheConfiguration);
	}
	DiskStoreConfiguration diskStoreConfiguration = new DiskStoreConfiguration();
	String tempDir = System.getProperty("java.io.tmpdir");
	if (tempDir != null) {
	    if (!tempDir.endsWith(File.separator)) {
		tempDir += File.separator;
	    }
	    tempDir += "lct-web";
	    diskStoreConfiguration.setPath(tempDir);
	}
	config.addDiskStore(diskStoreConfiguration);
	return net.sf.ehcache.CacheManager.newInstance(config);
    }

    @Bean
    @Override
    public CacheManager cacheManager() {
	return new EhCacheCacheManager(ehCacheManager());
    }

    @Bean
    @Override
    public KeyGenerator keyGenerator() {
	return new SimpleKeyGenerator();
    }

    /*
     * @Bean public EhCacheCacheManager cacheManager() { return new EhCacheCacheManager(ehCache().getObject()); }
     * 
     * @Bean public EhCacheManagerFactoryBean ehCache() { EhCacheManagerFactoryBean retValue = new EhCacheManagerFactoryBean(); retValue.setConfigLocation(new
     * ClassPathResource("ehcache.xml")); return retValue; }
     */

    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
	ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
	source.setBasenames("classpath:messages");
	source.setDefaultEncoding("UTF-8");
	source.setFallbackToSystemLocale(false);
	source.setUseCodeAsDefaultMessage(false);
	return source;
    }

    @Bean(name = "webServiceLocatorCache")
    public WebServiceLocatorCache webServiceLocatorCache() {
	String url = this.webServiceUrl;
	this.logger.info("webServiceUrl = " + url);
	WebServiceLocatorCache retValue = new WebServiceLocatorCache(url, restTemplate());
	return retValue;
    }

    @Bean
    public LocatorRepository locatorRepository() {
	LocatorRepository retValue = new LocatorRepository();
	retValue.setGoogleAPIChannel(this.googleAPIChannel);
	retValue.setGoogleAPIClientId(this.googleAPIClientId);
	return retValue;
    }

    @Bean
    public BeanNameViewResolver beanNameViewResolver() {
	BeanNameViewResolver retValue = new BeanNameViewResolver();
	retValue.setOrder(0);
	return retValue;
    }

    /*
     * @Bean public MappingJackson2HttpMessageConverter jacksonMessageConverter() { return new MappingJackson2HttpMessageConverter(); }
     */

    @Bean
    public RestTemplate restTemplate() {
	RestTemplate retValue = new RestTemplate();
	/*
	 * retValue.getMessageConverters().add(jacksonMessageConverter());
	 */
	return retValue;
    }

    @Bean(name = "SearchListExcel")
    public SearchListExcelView searchListExcel() {
	return new SearchListExcelView();
    }

    @Bean(name = "ReportQueriesPerBrandExcel")
    public QueriesPerBrandExcelView reportQueriesPerBrandExcel() {
	return new QueriesPerBrandExcelView();
    }

    @Bean(name = "ReportAPIUsageExcel")
    public APIUsageExcelView reportAPIUsageExcel() {
	return new APIUsageExcelView();
    }

}
