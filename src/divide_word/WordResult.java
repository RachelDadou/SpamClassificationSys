package divide_word;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.python.antlr.PythonParser.return_stmt_return;

import ICTCLAS.I3S.AC.ICTCLAS50;

import feature_extraction.extraction;

public class WordResult {
	
	private File m_FileDict;
	private File[] m_Files;
	private int m_FilesSum=0;
	
	private Map<String, Integer> m_WordAllMap = new HashMap<String,Integer>();
	
	private int m_ClassIndex=-1;
	private ArrayList<String> m_Words=new ArrayList<String>();
	
	public WordResult(){
		
	}
	
	/***
	 * ��ñ�����ĵ�����
	 * @return
	 */
	public int GetFilesSum(){
		return m_FilesSum;
	}
	
	public void SetFileDict(File file){
		m_FileDict=file;
		m_Files=m_FileDict.listFiles();
		m_FilesSum=m_Files.length;
	}
	
	public void SetFilesWord(){
		extraction oExtraction=new extraction();
		String sContent="";
		String sResult="";
		String[] ssWord;
		for(File file:m_Files){
			oExtraction.Release();
			sContent=oExtraction.GetEmailContent(file.getAbsolutePath());
			sContent=sContent.substring(0, sContent.length()/2);
			if(sContent.length()>0)
				sResult=ICTCLASStringProcess(sContent);
			ssWord=sResult.split(" ");
			for(int i=0;i<ssWord.length;i++){
				Integer oValue=m_WordAllMap.get(ssWord[i]);
				if(oValue!=null)
					m_WordAllMap.put(ssWord[i], oValue+1);
				else
					m_WordAllMap.put(ssWord[i], 1);
			}
		}	
	}
	
	/***
	 * ���ַ����ִ�(�п�Ժ�ִ�)
	 */
	private String ICTCLASStringProcess(String sInput){	
		try {
			ICTCLAS50 oICTCLAS50 = new ICTCLAS50();
			String argu = ".";
			//��ʼ��
			if (oICTCLAS50.ICTCLAS_Init(argu.getBytes("GB2312")) == false)
			{
				System.out.println("Init Fail!");
				return "";
			}
			//���ô��Ա�ע��(0 ������������ע����1 ������һ����ע����2 ���������ע����3 ����һ����ע��)
			oICTCLAS50.ICTCLAS_SetPOSmap(2);
			//�����û��ʵ�ǰ�ִ�
			byte nativeBytes[] = oICTCLAS50.ICTCLAS_ParagraphProcess(sInput.getBytes("GB2312"), 0, 0);//�ִʴ���
			System.out.println(nativeBytes.length);
			String nativeStr = new String(nativeBytes, 0, nativeBytes.length, "GB2312");
			//System.out.println("�ִʽ���� " + nativeStr);//��ӡ���
			return nativeStr;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
}
