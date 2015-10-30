package com.ko.lct.web.gfruit.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
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

import com.ko.lct.common.bean.Brand;
import com.ko.lct.common.bean.Prod;
import com.ko.lct.common.bean.Product;
import com.ko.lct.web.gfruit.repository.LocatorRepository;
import com.ko.lct.web.gfruit.repository.WebServiceLocatorCache;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "com.ko.lct.web.gfruit" })
@PropertySources(value = {@PropertySource("classpath:application.properties")})
public class WebConfig extends WebMvcConfigurerAdapter {

    private static final int RESOURCES_CACHE_PERIOD = 8 * 60 * 60; // 8 hours

    protected final Log logger = LogFactory.getLog(getClass());

    @Value("${web_service_url}")
    private String webServiceUrl;

    @Value("${google.api.clientId}")
    private String googleAPIClientId;

    @Value("${google.api.channel}")
    private String googleAPIChannel;
    
    @Value("${brand.code}")
    private String brandCode;
    
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
	retValue.setBrandCode(brandCode);
	return retValue;
    }

    @Bean
    public BeanNameViewResolver beanNameViewResolver() {
	BeanNameViewResolver retValue = new BeanNameViewResolver();
	retValue.setOrder(0);
	return retValue;
    }

    @Bean
    public RestTemplate restTemplate() {
	RestTemplate retValue = new RestTemplate();
	return retValue;
    }

    @Bean(name = "products")
    public List<Product> getProducts() {
	List<Product> products = new ArrayList<Product>();
	try {
	    PropertiesConfiguration config = new PropertiesConfiguration("data.properties");
	    String[] brandValues = config.getStringArray("brand");
	    for(String value: brandValues) {
		String[] parseBrand = value.split(";");
		if(parseBrand.length == 2) {
		    Brand brand = new Brand();
		    brand.setCode(parseBrand[0]);
		    brand.setName(parseBrand[1]);
		    String[] productValues = config.getStringArray(brand.getCode() + ".product");
		    for(String valueProduct: productValues) {
			String[] parseProduct = valueProduct.split(";");
			if(parseProduct.length == 2) {
			    Prod prod = new Prod();
			    prod.setCode(parseProduct[0]);
			    prod.setName(parseProduct[1]);
			    Product product = new Product();
			    product.setBrand(brand);
			    product.setProd(prod);
			    products.add(product);
			}
		    }
		}
	    }
	} catch (ConfigurationException e) {
	    logger.error("Exception during getting data from properties \"data.properties\" " + e.getMessage());
	}
	return Collections.unmodifiableList(products);
    }
    
}