package feature_extraction;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import org.omg.CORBA.PUBLIC_MEMBER;
import org.python.antlr.PythonParser.return_stmt_return;

public class extraction {
	
	private ArrayList<String> m_Content=new ArrayList<String>();
	private String m_Code;
	private String m_Subject;
	
	private void GetFileContent(String filePath){
		File resfile = new File(filePath);
		String res="";
		if(resfile.exists()){
			try {
				Scanner input = new Scanner(resfile);
				while(input.hasNextLine()){
					String str=input.nextLine();
					if(str.contains("����")){
						String[] result=str.split(":");
						if(result.length==2){
							m_Subject=result[1];
						}else{
							m_Subject="";
						}
						System.out.println(m_Subject);
					}
					m_Content.add(str);
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else
			return ;
	}
	
	/***
	 * �ʼ�ͷ����
	 * @return
	 */
	private String GetSubjectCode(){
		String res="";
		return res;
	}
	
	/***
	 * �ʼ�������
	 * @return
	 */
	private String GetContentCode(){
		String res="";
		return res;
	}
	
	/***
	 * ��ȡ������
	 * @return
	 */
	private String GetAllCode(){
		String str="";
		str+=GetSubjectCode();
		str+=GetContentCode();
		return str;
	}
	
	
	/***
	 * ������ȡ��ں���
	 * @param filePath
	 * @return
	 */
	public String feature_extraction(String filePath){
		GetFileContent(filePath);
		System.out.println("�������ݣ�");
		for(String str:m_Content){
			System.out.println(str);
		}
		m_Code=GetAllCode();
		return  m_Code;
	}
	
	/***
	 * ���ܲ��Ժ���
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {	
		String filePath = "F:\\MailProject\\���鳬-��ҵ���\\emailtest1-decode\\errorhead1.eml.txt";
		System.out.println("������ȡ�ļ���"+filePath);
		extraction myExtraction=new extraction();
		String testString=myExtraction.feature_extraction(filePath);
	}
}
