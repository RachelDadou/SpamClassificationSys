package divide_word;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.python.antlr.PythonParser.return_stmt_return;

import ICTCLAS.I3S.AC.*;
import feature_extraction.extraction;

public class DivideWord extends WordResult{

	private String m_FilePath1="";
	private int m_FileSum1=0;
	private String m_FilePath2="";
	private int m_FileSum2=0;
	
	private int m_ClassSum=0;
	private int m_FilesSum=0;
	
	private ArrayList<String> m_MailsContent=new ArrayList<String>();
	
	private ArrayList<WordResult> m_WordResults=new ArrayList<WordResult>();
	
	private Map<String, Integer> m_WordAllMap = new HashMap<String,Integer>();
	
	private static int m_WordSum=0;
	
	public DivideWord(File[] files){
		m_ClassSum=files.length;
		for(File file:files){
			WordResult oWordResult=new WordResult();
			oWordResult.SetFileDict(file);
			m_FilesSum+=oWordResult.GetFilesSum();
			m_WordResults.add(oWordResult);
		}
	}
	
	private File GetFileInfo(String filepath,int index){
		File fDictFile=new File(filepath);
		return fDictFile;
	}
	
	/***
	 * ��ȡ�ʼ��������ݣ��ִʴ���ͳ��
	 */
	public void DealWords(){
		
	//	for(String key:m_WordAllMap.keySet()){
	//		System.out.println(key+":"+(m_WordAllMap.get(key)*1./m_WordSum));
	//	}
	}
	
	/***
	 * ��ȡ������
	 * @return
	 */
	public ArrayList<String> GetFeatureWord(){
		return null;
	}
	
	/**���Ժ���
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String filePath = "F:\\MailProject\\���鳬-��ҵ���\\emailtest3-decode";
		System.out.println("���ķִ��ļ��У�"+filePath);
	}

}
