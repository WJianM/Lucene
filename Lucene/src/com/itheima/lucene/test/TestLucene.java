package com.itheima.lucene.test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.itheima.lucene.dao.BookDao;
import com.itheima.lucene.dao.BookDaoImpl;
import com.itheima.lucene.pojo.Book;

public class TestLucene {// 15649
	@Test // 534636
	public void test01() {
		BookDao bd = new BookDaoImpl();
		List<Book> allList = bd.findAllList();
		for (Book book : allList) {
			System.out.println(book);
		}
	}

	@Test
	public void test02() throws Exception {
		// 1. 采集数据
		BookDao bd = new BookDaoImpl();
		List<Book> allList = bd.findAllList();

		List<Document> documentList = new ArrayList<>();
		for (Book book : allList) {
			// 2. 创建Document文档对象
			Document d = new Document();
			// 在document中添加域(把document分成几个域)
			d.add(new TextField("id", book.getId().toString(), Store.YES));
			d.add(new TextField("desc", book.getDesc(), Store.YES));
			d.add(new TextField("name", book.getName(), Store.YES));
			d.add(new TextField("price", book.getPrice().toString(), Store.YES));
			d.add(new TextField("pic", book.getPic(), Store.YES));
			// 把document存到list集合中
			documentList.add(d);
		}
		// 3. 创建分析器（分词器）--用来解析document中的域
		Analyzer analyzer = new StandardAnalyzer();
		// 4. 创建IndexWriterConfig配置信息类
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_4_10_3, analyzer);
		// 5. 创建Directory对象，声明索引库存储位置
		// ()目录对象,指向索引库的位置
		Directory dir = FSDirectory.open(new File("F:/itcast/lucene/index"));
		// 6. 创建IndexWriter写入对象
		IndexWriter indexWriter = new IndexWriter(dir, indexWriterConfig);
		// 7. 把Document写入到索引库中
		for (Document document : documentList) {
			indexWriter.addDocument(document);
		}
		// 8. 释放资源
		indexWriter.close();

	}

	@Test
	public void testQuery() throws Exception {
		// 1. 创建Query搜索对象
		// 创建分词器
		Analyzer analyzer = new StandardAnalyzer();
		// 创建搜索解析器
		QueryParser queryParser = new QueryParser("desc", analyzer);
		Query query = queryParser.parse("desc:java AND lucene");

		// 创建目录流对象,声明索引库位置
		Directory directory = FSDirectory.open(new File("F:/itcast/lucene/index"));
		// 创建搜索读取对象
		IndexReader reader = DirectoryReader.open(directory);

		// 创建索引搜索对象
		IndexSearcher sercher = new IndexSearcher(reader);

		TopDocs topDocs = sercher.search(query, 10);
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for (ScoreDoc scoreDoc : scoreDocs) {
			int id = scoreDoc.doc;
			Document document = sercher.doc(id);
			System.out.println(document);
		}
		reader.close();
	}

	@Test
	public void testCreateIndex() throws Exception {
		// 采集数据
		BookDao bd = new BookDaoImpl();
		List<Book> allList = bd.findAllList();
		// 创建documents文档集合对象
		List<Document> documents = new ArrayList<>();
		for (Book book : allList) {
			// 遍例采集到的数据,格式化,封装到创建的document对象中
			Document doc = new Document();
			// 添加id域 , 不分词 , 要索引 , 添加到文档中StringField
			doc.add(new StringField("id", book.getId().toString(), Store.YES));
			// 添加价格price域, 要分词, 要索引, 要存储
			doc.add(new FloatField("price", book.getPrice(), Store.YES));
			// 添加name域
			doc.add(new TextField("name", book.getName(), Store.YES));
			// 添加pic域
			doc.add(new StringField("pic", book.getPic(), Store.YES));
			// 添加desc域
			doc.add(new TextField("desc", book.getDesc(), Store.YES));

			documents.add(doc);
		}

		// 创建索引写入目录
		Directory directory = FSDirectory.open(new File("F:/itcast/lucene/index"));
		// 创建 创建索引的配置文件
		// 分词器analyzer
		Analyzer analyzer = new IKAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_3, analyzer);
		// 创建 创建索引indexWriter
		IndexWriter indexWriter = new IndexWriter(directory, config);// 需要索引写入目录
																		// 和
																		// 索引写入配置对象

		// 遍例文档集创建索引成功
		for (Document document : documents) {
			indexWriter.addDocument(document);
		}

		indexWriter.close();
	}

	@Test
	public void testIndexSearch() throws Exception {
		// 创建解析器对象
		Analyzer analyzer = new IKAnalyzer();

		// 创建搜索解析器对象 // 第一个参数是默认域,第二个参数是解析器
		QueryParser parser = new QueryParser("desc", analyzer);

		// 创建搜索对象
		Query query = parser.parse("desc:java AND lucene");// 通过搜索解析器获得

		// 创建索引读取对象
		Directory directory = FSDirectory.open(new File("F:/itcast/lucene/index"));
		DirectoryReader reader = DirectoryReader.open(directory);
		IndexSearcher indexSearcher = new IndexSearcher(reader);
		TopDocs topDocs = indexSearcher.search(query, 10);
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for (ScoreDoc scoreDoc : scoreDocs) {
			int id = scoreDoc.doc;
			Document doc = indexSearcher.doc(id);
			// System.out.println(doc);
			System.out.println(doc.get("name"));
			System.out.println(doc.get("price"));
			System.out.println(doc.get("id"));
		}
		reader.close();
	}

	@Test
	public void testDelete() throws Exception {
		// 创建配置目录对象
		Directory directory = FSDirectory.open(new File("F:/itcast/lucene/index"));
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_3, new IKAnalyzer());
		// 连接索引库,创建索引写入对象
		IndexWriter indexWriter = new IndexWriter(directory, config);// 缺目录文件对象和配置对象
		indexWriter.deleteDocuments(new Term("name", "apache"));
		indexWriter.close();
	}

	@Test
	public void testUpdata() throws Exception {
		// 创建配置目录对象
		Directory directory = FSDirectory.open(new File("F:/itcast/lucene/index"));
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_3, new IKAnalyzer());
		// 连接索引库,创建索引写入对象
		IndexWriter indexWriter = new IndexWriter(directory, config);// 缺目录文件对象和配置对象
		Document doc = new Document();
		doc.add(new TextField("name", "哈哈", Store.YES));
		indexWriter.addDocument(doc);
		indexWriter.close();
	}

	@Test
	public void testQuery1() throws Exception {
		// 创建配置目录对象
		Directory directory = FSDirectory.open(new File("F:/itcast/lucene/index"));
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_3, new IKAnalyzer());
		IndexReader reader = DirectoryReader.open(directory);
		
		IndexSearcher indexSearcher = new IndexSearcher(reader);
		// 创建查询有两种方法
		// 1. 使用Query子类精确查询
		Query query = new TermQuery(new Term("name", "哈哈"));

		TopDocs topDocs = indexSearcher.search(query, 10);
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for (ScoreDoc scoreDoc : scoreDocs) {
			int id = scoreDoc.doc;
			System.out.println(indexSearcher.doc(id));
		}
		
		
		// 2. 通过解析表达式查询
		QueryParser parser = new QueryParser("name", new IKAnalyzer());
		Query query2 = parser.parse("name:哈哈");
		TopDocs topDocs2 = indexSearcher.search(query2, 10);
		ScoreDoc[] scoreDocs2 = topDocs2.scoreDocs;
		for (ScoreDoc scoreDoc : scoreDocs2) {
			int id = scoreDoc.doc;
			Document doc = indexSearcher.doc(id);
			System.out.println(doc);
		}
		reader.close();
	}
	
	@Test
	public void testQuery2() throws Exception {
		Directory directory = FSDirectory.open(new File("F:/itcast/lucene/index"));
		IndexReader reader = DirectoryReader.open(directory);
		IndexSearcher indexSearcher = new IndexSearcher(reader);
		// 指定数字范围查询 NumericRangeQuery 
		Query query = NumericRangeQuery.newFloatRange("price", 70f, 100f, true, true);
		TopDocs docs = indexSearcher.search(query, 10);
		ScoreDoc[] docs2 = docs.scoreDocs;
		for (ScoreDoc scoreDoc : docs2) {
			int id = scoreDoc.doc;
			Document doc = indexSearcher.doc(id);
			System.out.println(doc.get("price"));
		}
		
	}


	@Test
	public void testBooleanQuery() throws Exception {
		Query query1 = new TermQuery(new Term("name", "lucene"));
		
		Query query2 = NumericRangeQuery.newFloatRange("price", 50f, 100f, false, true);
		
		BooleanQuery booleanQuery = new BooleanQuery();
		booleanQuery.add(query1, Occur.MUST_NOT);
		booleanQuery.add(query2, Occur.MUST);
		
		Directory directory = FSDirectory.open(new File("F:/itcast/lucene/index"));
		IndexReader reader = DirectoryReader.open(directory);
		IndexSearcher indexSearcher = new IndexSearcher(reader);
		
		TopDocs docs = indexSearcher.search(booleanQuery, 10);
		
		ScoreDoc[] docs2 = docs.scoreDocs;
		for (ScoreDoc scoreDoc : docs2) {
			int id = scoreDoc.doc;
			Document doc = indexSearcher.doc(id);
			System.out.println(doc.get("price"));
		}
		
		
		
		
		
		
		
		
		
		
	
	
	
	
	
	}

}
