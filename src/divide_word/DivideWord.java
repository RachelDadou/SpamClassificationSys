package divide_word;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Queue;

import org.python.antlr.PythonParser.classdef_return;
import org.python.antlr.PythonParser.return_stmt_return;

import com.hp.hpl.sparta.xpath.ThisNodeTest;

import weka.core.pmml.FieldMetaInfo.Value;

import ICTCLAS.I3S.AC.*;
import Main.Main;
import feature_extraction.extraction;



public class DivideWord{
		
	public class MyWord implements Comparable<MyWord> {

		private String m_Word = "";
		private double m_Val = 0;
		
		public MyWord(){
			
		}
		
		public void Set(String s,double val){
			m_Word = s;
			m_Val = val;
		}
		
		public String GetWord(){ return m_Word; } 
		
		@Override
		public int compareTo(MyWord o) {
			// TODO Auto-generated method stub
			double detal = this.m_Val - o.m_Val;
			if(detal > 0)
				return -1;
			else {
				return 1;
			}
		}	
	}
	
	private int m_ClassSum=0;
	private int m_FilesSum=0;
	private Map<String, String> m_IGStaticMap=new Hashtable<String,String>();
	private ArrayList<Integer> m_FileSumList = new ArrayList<Integer>();
	
	private int MAXNUM = 10;
	
	public DivideWord(File[] dfiles,int cnt){
		m_ClassSum = cnt;
		for(int i=0;i<cnt;i++){
			File file = dfiles[i];
			File[] files = file.listFiles();
			m_FilesSum+=files.length;
			m_FileSumList.add(files.length);
			for(int j=0;j<files.length;j++){
				String wordid = String.format("%s%s", i,j);
				ArrayList<String> wordlist = new ArrayList<String>();
				wordlist = GetDivideWord(files[j]);
				for(String word:wordlist){
					RefreshMap(word,wordid);
				}
			}
		}
		System.out.println(String.format("�����:{%s} ���ĵ���:{%s}", m_ClassSum,m_FilesSum));
	}
		
	/***
	 * ������ѡ����
	 */
	public void DealWord(){
		MyWord[] objs = new MyWord[m_IGStaticMap.size()];
		int cnt = 0;
		for(String str:m_IGStaticMap.keySet()){
			double iIGVal = GetInformationGain(str);
			MyWord obj = new MyWord();
			obj.Set(str, iIGVal);
			objs[cnt++] = obj;
		}
		Arrays.sort(objs);
		String sText = "";
		for(int i=0;i<MAXNUM;i++){
			sText += objs[i].GetWord();
			if(i<9)
				sText += "\r\n";
		}
		try {
			Main.writeByFileWrite("./src/txt/word.txt", sText);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("�����ִ��ļ�ʧ�ܣ�");
		}
	}
	
	/***
	 * ��ȡ�ִ�
	 * @return
	 */
	private ArrayList<String> GetDivideWord(File file){
		extraction e = new extraction();
		e.GetEmailContent(file.getAbsolutePath());
		String sContent = e.GetmChinese();
		ArrayList<String> WordList = new ArrayList<String>();
		String[] strs = ICTCLASStringProcess(sContent).split(" ");
		for(String str:strs){
			if(str=="") continue;
			if(WordList.contains(str)) continue;
			WordList.add(str);
		}
		return WordList;
	}
	
	private void RefreshMap(String key,String val){
		if(m_IGStaticMap.containsKey(key)){
			String value = m_IGStaticMap.get(key);
			val = value + "," + val;
		}
		m_IGStaticMap.put(key, val);
	}
	
	public void Release(){
		m_IGStaticMap.clear();
		m_FileSumList.clear();
		m_FilesSum = 0;
		m_ClassSum = 0;
	}
	
	/***
	 * ��Ϣ������㹫ʽ
	 * @return
	 */
	private double GetInformationGain(String word){
		return (double)(GetOne() + GetTwo(word, true) + GetTwo(word, false));
	}
	
	/***
	 * ��ʽ��һ�� 
	 * @return
	 */
	private double GetOne(){
		double ans = 0.0;
		for(int i=0;i<m_ClassSum;i++){
			double pi = 1./m_ClassSum;
			ans += pi * (Math.log(pi) / Math.log(2));
		}
		return -ans;
	}
		
	/***
	 * ��ʽ�ڶ�������
	 * @param flag
	 * @return
	 */
	private double GetTwo(String word,Boolean flag){
		double ans = 0.0;
		double pt = GetPT(word,flag);
		for(int i=0;i<m_ClassSum;i++){
			double pct = GetPCT(word, i, flag);
			if(pct==0) pct = 1;
			ans += pct * (Math.log(pct) / Math.log(2));
		}
		ans *= pt;
		return ans;
	}
	
	private double GetPT(String word, Boolean flag){
		String[] sValues = m_IGStaticMap.get(word).split(",");
		int sum = sValues.length;
		if(!flag)
			sum = m_FilesSum - sum;
		return sum*1./m_FilesSum;
	}
	
	private double GetPCT(String word,int ClassType,Boolean flag){
		String[] sValues = m_IGStaticMap.get(word).split(",");
		int filesum = sValues.length;
		if(!flag)
			filesum = m_FilesSum - sValues.length;
		int sum = 0;
		if(flag){
			for(String str:sValues){
				String s0 = String.format("%s", str.charAt(0));
				int iType = Integer.parseInt(s0);
				if(iType == ClassType)
					sum+=1;
			}
		}else{
			int iClassFileSum = m_FileSumList.get(ClassType);
			for(String str:sValues){
				String s0 = String.format("%s", str.charAt(0));
				int iType = Integer.parseInt(s0);
				if(iType==ClassType)
					iClassFileSum-=1;
			}
			sum = iClassFileSum;
		}
		return 1.*sum/filesum;
	}
	
	/***
	 * �п�Ժ�ִ�
	 * @param sInput
	 * @return
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
			System.out.println("�ִ�ǰ:" + sInput);
			System.out.println("�ִʽ���� " + nativeStr);//��ӡ���
			return nativeStr;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	/**���Ժ���
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String filePath = "F:\\MailProject\\���鳬-��ҵ���\\���ķִʲ���";
		File root1 = new File(filePath);
		if(root1.exists() && root1.isDirectory()){
			File[] allfiles = root1.listFiles();
			File[] files = new File[allfiles.length];
			int cnt=0;
			for(File file:allfiles){
				if(file.exists() && file.isDirectory()){
					files[cnt++] = file;
				}
			}
			DivideWord oDivideWord=new DivideWord(files,cnt);
			oDivideWord.DealWord();
		}else{
			System.out.println("�ļ��в����ڣ�");
			return ;
		}
		System.out.println("���ķִ��ļ��У�"+filePath);
	}

}
