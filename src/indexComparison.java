import java.io.FileInputStream;
import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class indexComparison {
	public static void main(String argv[]) {
		try {
	    	/*Code to convert the merged file to a UTF-8 encoded file*/
	    	InputStream inputStream= new FileInputStream("C:\\Users\\sujit\\Desktop\\Search\\corpus\\corpus\\output.txt");
	    	Reader reader = new InputStreamReader(inputStream,"UTF-8");
	        InputSource is = new InputSource(reader);
	        is.setEncoding("UTF-8");
	        
	    	String indexPath="C:\\Users\\sujit\\Desktop\\Search\\standard_analyzer";
	    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    	Document doc = dBuilder.parse(is);
	    	doc.getDocumentElement().normalize();
	    	
	    	//I have changed the analyzers to different types over here.
	    	Analyzer analyzer=new StandardAnalyzer();
	    	Directory dir = FSDirectory.open(Paths.get(indexPath));
	    	IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
	    	iwc.setOpenMode(OpenMode.CREATE);
	    	IndexWriter writer=new IndexWriter(dir,iwc);
	    	
	    	String Text="";
		
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
				for(int j=0;j < eElement.getElementsByTagName("TEXT").getLength();j++){
						Text+=eElement.getElementsByTagName("TEXT").item(j).getTextContent();
				}
				luceneDoc.add(new org.apache.lucene.document.TextField("TEXT",Text,Field.Store.YES));
				writer.addDocument(luceneDoc);
				Text="";
			}
		}
		writer.close();		
		IndexReader r = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
		//To print he number of tokens in the text field.
		Terms vocabulary = MultiFields.getTerms(r, "TEXT");
		System.out.println("Number of tokens for this field:"+vocabulary.getSumTotalTermFreq());	
		TermsEnum iterator = vocabulary.iterator();
		
		System.out.println("\n*******Vocabulary Count**********");
		int count=0;
		while(iterator.next() != null) {
			count++;
		}
		System.out.println("Vocabulary count"+count);
		} catch (Exception e) {	
		e.printStackTrace();
	    }
	  }
}
