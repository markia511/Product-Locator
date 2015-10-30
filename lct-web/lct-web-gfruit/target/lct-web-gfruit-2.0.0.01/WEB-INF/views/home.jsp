<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en-US">
<head>
	<jsp:include page="include/html_head.jsp" /> 
</head>	
<body>
<!-- Google Tag Manager -->
<noscript><iframe src="//www.googletagmanager.com/ns.html?id=GTM-TL3657"
height="0" width="0" style="display:none;visibility:hidden"></iframe></noscript>
<script>(function(w,d,s,l,i){w[l]=w[l]||[];w[l].push({'gtm.start':
new Date().getTime(),event:'gtm.js'});var f=d.getElementsByTagName(s)[0],
j=d.createElement(s),dl=l!='dataLayer'?'&l='+l:'';j.async=true;j.src=
'//www.googletagmanager.com/gtm.js?id='+i+dl;f.parentNode.insertBefore(j,f);
})(window,document,'script','dataLayer','GTM-TL3657');</script>
<!-- End Google Tag Manager -->
<!-- Acxiom AbiliTag v1.7 for site: find.fruitwater.com -->
<script type="text/javascript">window.acxm={api:Function(),x:function(){var k=document,e=encodeURIComponent,r="script",m=k.getElementsByTagName(r)[0],i=k.createElement(r),t="&c=";try{t=t+k.cookie.match('_acxm=([^;]*)').slice(1);}catch(z){};i.async=true;i.src="//t.acxiom-online.com/tag/423aab5a25c70299dba528a5d9d021b1/st.js?l="+e(k.location)+"&r="+e(k.referrer)+t;m.parentNode.insertBefore(i,m)}()}</script>

	<noscript>
		<h1>Fruit Water</h1>
		<section>
			<a href="http://www.fruitwater.com/">fruitwater</a>
		</section>
		<h2>fruitwater</h2>
		<section>
			sparkling ,sparkling water, beverage, drink, bubbly, steal a moment, get sparkling, zero calorie, christina applegate			
		</section>
	</noscript>
	<img class="img_background" src="resources/images/background.png" />
	<div class="page-wrap">
		<div class="inner_wrap">
		<div class="top-wrap">
			<div class="search_block">
					<div class="roof">
						<div class="top_block top_block_left"><a href='http://www.fruitwater.com'><img class="logo" src="resources/images/logo_berries.png" alt="Glaceau - fruitwater"></a></div>			 
					</div>
						<div class="search_section padding_bottom_30">
							<p class="title_text"><spring:message code="search" /></p>
							<div>
								<p class="text pText"><spring:message code="search.product" /></p>
								<select multiple name="product" id="product">
									<c:forEach items="${products}" var="product">
	    	   							<option value="${product.prod.code}">${product.prod.name}</option>
									</c:forEach>
								</select>
							</div>
						</div>
						<div class="search_section">
							<p class="title_text"><spring:message code="search.customerLocation" /></p>
							<div>
								<p class="text pText"><spring:message code="search.address" /></p>
								<input type="text" id="searchTextField" placeholder="enter a location" class="geoAddress" />
							</div>
							<div class="base_font padding_top_10">							
								<p class="text pText"><spring:message code="search.distance" /></p>
								<input type="text" name="distance" id="distance" maxlength="3" style="width: 60px;" value="5">
							</div>	
						</div>
						<div class="search_section search_buttons">
							<input id="clear" class="btn btn-default searchBtn clear" type="button" value="<spring:message code='search.button.clear' />" data-action="clear">
							<input id="search" class="btn btn-default searchBtn" type="button" value="<spring:message code='search.button.search' />" data-action="search">
						</div>
						<div>
							<div class="text_info">
								<spring:message code="search.additionalInfo" />
								<div id="moreThan" class="hidden"><spring:message code="search.more.than.25" /></div>
							</div>
						</div>
						<div id="noResultsBLock" class="noResults hidden"><spring:message code="noResults" /></div>
						<div id="wheel" class="hidden"></div>
				</div>
			<div class="map_block">
				<div class="roof">
					<div class="top_block top_block_right"><input type="button" class="btn btn-default top_button" value="nutritional information" data-action="downloadNutritionalPdf" data-link="<c:url value="/resources/files/fruitwater_2013_NutritionFacts_20oz_080414_small.pdf" />" /><img class="img_bottles" src="resources/images/bottles.png"></div> 
				</div>
					<p class="title_text"><spring:message code="map.view" /></p>
					<div id="map_canvas"></div>
					<img class="img_main_bottle" src="resources/images/main_bottle.png" />
					<img class="main_bottle_label" src="resources/images/main_bottle_label.png" />
					<div id="bubbles" class="bubbles-animation hidden">
	        			<div class="bubble bubble1"></div>
	        			<div class="bubble bubble2"></div>
	        			<div class="bubble bubble3"></div>
	        			<div class="bubble bubble4"></div>
	        			<div class="bubble bubble5"></div>
	        			<div class="bubble bubble6"></div>
	        			<div class="bubble bubble7"></div>
	        			<div class="bubble bubble8"></div>
	        			<div class="bubble bubble9"></div>
	        			<div class="bubble bubble10"></div>
	        			<div class="bubble bubble11"></div>
	        			<div class="bubble bubble12"></div>
	        			<div class="bubble bubble13"></div>
	        			<div class="bubble bubble14"></div>
	        			<div class="bubble bubble15"></div>
	        			<div class="bubble bubble16"></div>
	        			<div class="bubble bubble17"></div>
	        			<div class="bubble bubble18"></div>
	        			<div class="bubble bubble19"></div>
	        			<div class="bubble bubble20"></div>
        			</div>
			</div>
		</div>
		<div id="resultBlock" class="result hidden">
			<p id="appendWrapper" class="title_text"><spring:message code="result.search" /></p>
			<div id="wrapperTableHead" class="top_table_wrapper">
			<table class="resultTable top_table">
				<thead>
					<tr class="text_info">
						<th width="18%" style="padding-right: 5px;"><label><spring:message code="result.table.head.outletName" /></label></th>
						<th width="32%" style="padding-right: 19px;"><label><spring:message code="result.table.head.outletAddress" /></label></th>
						<th width="8%" style="padding-right: 5px;"><label><spring:message code="result.table.head.distance" /></label></th>
						<th width="42%"><label><spring:message code="result.table.head.productInfo" /></label></th>
					</tr>
				</thead>
			</table>
			</div>
			<div id="scrollableWrapper" class="scrollTable">
			<table id="scrollableTable" class="resultTable">
				<tbody id="searchResult"></tbody>
			</table>
			</div>
		</div>
		<div>
			<input type="hidden" name="longitude" id="longitude" value="" />
			<input type="hidden" name="latitude" id="latitude" value=""/>
			<input type="hidden" name="frmt_addr" id="frmt_addr" />
		</div>
		</div>
	</div>
</body>
</html>