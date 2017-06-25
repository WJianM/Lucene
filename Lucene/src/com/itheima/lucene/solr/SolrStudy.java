package com.itheima.lucene.solr;

public class SolrStudy {
	/**
	 * Solr是Apache的顶级来源项目,基于Lucene的全文搜索服务
	 * Solr可以独立运行在tomcat容器中,交互方式是报文json格式字符串或者xml格式字符串
	 * 
	 * 使用Solr创建索引:
	 * 		客户端可以是浏览器,也可以是java程序,用post方法向solr服务器发送一个描述field
	 * 		及其内容的xml文档,Solr服务器根据xml文档添加,删除,,更新索引
	 * 
	 * 		搜索索引:客户端用get方法向Solr服务器发送请求,然后对服务器返回的json或者xml格式的查询结果进行解析.
	 * 		Solr是一个可以独立运行的搜索服务器, 想使用Solr进行全文检索服务的话,只需要通过http请求访问该服务器即可.
	 * 
	 * 	Solr和Lucene的区别:
	 * 			Lucene是全文检索的工具包,并不是一个完整的全文检索应用.
	 * 			他仅仅提供搜索引擎和索引引擎,他是一个工具.
	 * 			Solr他是基于Lucene的搜索引擎服务,,可以独立运行,可以快速的构建企业的搜索引擎
	 * 			
	 */
	/**
	 * SolrJ 是访问Solr服务的java客户端,提供索引和搜索的方法(API)
	 */
}
