import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.lucene.document.Field;
import javax.xml.parsers.DocumentBuilder;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.file.Paths;

public class generateIndex {
	public static void main(String argv[]) {
	    try {
	    	String[] tags={"DOCNO","HEAD","BYLINE","DATELINE","TEXT"};
	    	/*Code to convert the merged file to a UTF-8 encoded file*/
	    	InputStream inputStream= new FileInputStream("C:\\Users\\sujit\\Desktop\\Search\\corpus\\corpus\\output.txt");
	    	Reader reader = new InputStreamReader(inputStream,"UTF-8");
	        InputSource is = new InputSource(reader);
	        is.setEncoding("UTF-8");
	        
	    	String indexPath="C:\\Users\\sujit\\Desktop\\Search\\indexed";
	    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    	Document doc = dBuilder.parse(is);
	    	doc.getDocumentElement().normalize();
	    	
	    	Analyzer analyzer=new KeywordAnalyzer();
	    	Directory dir = FSDirectory.open(Paths.get(indexPath));
	    	IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
	    	iwc.setOpenMode(OpenMode.CREATE);
	    	IndexWriter writer=new IndexWriter(dir,iwc);
	    	
	    	String DocNo="" ,Head="",ByLine="",DateLine="",Text="";
		
		/*System.out.println("Root element :" + doc.getDocumentElement().getNodeName());*/
		NodeList nList = doc.getElementsByTagName("DOC");
		/*System.out.println("----------------------------");*/
		String str="";
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			/*System.out.println("\nCurrent Element :" + nNode.getNodeName())*/;
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				org.apache.lucene.document.Document luceneDoc=new org.apache.lucene.document.Document();
				for(int tagNo=0;tagNo<tags.length;tagNo++){
					
					for(int j=0;j < eElement.getElementsByTagName(tags[tagNo]).getLength();j++){
						if(tags[tagNo]=="DOCNO")
						   DocNo+=eElement.getElementsByTagName(tags[tagNo]).item(j).getTextContent();
						else if(tags[tagNo]=="HEAD")
							Head+=eElement.getElementsByTagName(tags[tagNo]).item(j).getTextContent();
						else if(tags[tagNo]=="BYLINE")
							ByLine+=eElement.getElementsByTagName(tags[tagNo]).item(j).getTextContent();
						else if(tags[tagNo]=="DATELINE")
							DateLine+=eElement.getElementsByTagName(tags[tagNo]).item(j).getTextContent();
						else if(tags[tagNo]=="TEXT")
							Text+=eElement.getElementsByTagName(tags[tagNo]).item(j).getTextContent();
					}
					//System.out.println(DocNo+Head+ByLine+DateLine+Text+"\n");
					if(tags[tagNo]=="DOCNO")
						luceneDoc.add(new StringField("DOCNO",DocNo,Field.Store.YES));
					else if(tags[tagNo]=="HEAD")
						luceneDoc.add(new org.apache.lucene.document.TextField("HEAD",Head,Field.Store.YES));
					else if(tags[tagNo]=="BYLINE")
						luceneDoc.add(new org.apache.lucene.document.TextField("BYLINE",ByLine,Field.Store.YES));
					else if(tags[tagNo]=="DATELINE")
						luceneDoc.add(new org.apache.lucene.document.TextField("DATELINE",DateLine,Field.Store.YES));
					else if(tags[tagNo]=="TEXT")
						luceneDoc.add(new org.apache.lucene.document.TextField("TEXT",Text,Field.Store.YES));
				}
				writer.addDocument(luceneDoc);
				DocNo="";Head="";ByLine="";DateLine="";Text="";
			}
		}
		writer.close();
		
		IndexReader r = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
				//Print the total number of documents in the corpus
		System.out.println("Total number of documents in the corpus:"+r.maxDoc());
		System.out.println("Total number of occurence of the word \"the\" in the document"+r.totalTermFreq(new Term("TEXT","the")));
		} catch (Exception e) {
		e.printStackTrace();
	    }
	  }


}
