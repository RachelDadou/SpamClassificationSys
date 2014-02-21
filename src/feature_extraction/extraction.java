package feature_extraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.omg.CORBA.PUBLIC_MEMBER;
import org.python.antlr.PythonParser.else_clause_return;
import org.python.antlr.PythonParser.return_stmt_return;
import org.python.antlr.ast.boolopType;
import org.python.constantine.Constant;
import org.python.google.common.primitives.UnsignedBytes;

import ICTCLAS.I3S.AC.ICTCLAS50;

import com.kenai.jaffl.struct.Struct.Unsigned8;

public class extraction {
	
	final int OVECCOUNT=30;
	final int OTHERWORD=0;
	final int CHINESE=1;
	final int ENGLISH=2;
	final int NUMBER=3;
	
	private ArrayList<String> m_ContentList=new ArrayList<String>();
	private ArrayList<String> m_HtmlContentList=new ArrayList<String>();
	private String m_Content="";
	private String m_HtmlContent="";
	private int m_ContentCodeLen=64;
	private String m_Code;
	
	private ArrayList<String> m_SubjectList=new ArrayList<String>();
	private String m_Subject="";
	private int m_SubjectCodeLen=32;
	private ArrayList<String> m_From=new ArrayList<String>();
	
	private int m_HeaderLen=0;
	
	private ArrayList<String> m_AttachNames=new ArrayList<String>();
	
	/***
	 * ���ݸ�ʽ��ȡ��Ҫ����Ϣ
	 * @param filePath
	 */
	private void GetFileContent(String filePath){
		File resfile = new File(filePath);
		String res="";
		if(resfile.exists()){
			try {
				BufferedReader bw=new BufferedReader(new FileReader(resfile));
				String sLine="";
				int iLineCnt=0;
				while((sLine=bw.readLine())!=null){
					iLineCnt+=1;
					if(sLine.contains("����")){//����
						int iStartIndex=sLine.indexOf(":");
						String result=sLine.substring(iStartIndex+1);
						if(result!="��"){
							m_Subject+=result;
							m_SubjectList.add(result);
							m_HeaderLen+=result.length();
						}
						System.out.println("���⣺"+result);
						continue;					
					}else if(sLine.contains("������")){//������
						int iStartIndex=sLine.indexOf(":");
						String result=sLine.substring(iStartIndex+1);
						if(result!="��"){
							m_From.add(result);
							m_HeaderLen+=result.length();
						}
						System.out.println("�����ˣ�"+result);
						continue;
					}
					else if(sLine.contains("Attachment")){//����
						int iStartIndex=sLine.indexOf(":");
						String sTmp=sLine.substring(iStartIndex+1);
						String[] result=sTmp.split("&&");
						for(String sFileName:result){
							m_AttachNames.add(sFileName);
							System.out.println(sFileName);
						}
						continue;
					}else if(sLine.contains("NoAttachment")) continue;
					if(iLineCnt>8){
						m_HtmlContent+=sLine;//������
						m_HtmlContentList.add(sLine);
					}
					else if(iLineCnt>2 && iLineCnt<9){//ͳ���ʼ�ͷ���ݳ���
						m_HeaderLen+=sLine.length();
					}
				}
				m_HeaderLen-=9;//��ȥ��ʾ����ַ�
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else
			return ;
	}
	
	/***
	 * ȥ����ǩ
	 */
	private String DeleteHtmlLabel(String buf) {
		String res = "";
		int iStart = -1;
		int iEnd = -1;
		int index = 0;
		while (index < buf.length()) {
			if ((iStart = buf.indexOf("<", index)) == -1
					|| (iEnd = buf.indexOf(">", index)) == -1) {
				res += buf.substring(index);
				break;
			} else {
				res += buf.substring(index, iStart);
				index = iEnd + 1;
			}
		}
		return res;

	}
	
	private void GetHideContent(){
		m_Content=DeleteHtmlLabel(m_HtmlContent);
		for(String str:m_HtmlContentList){
			m_ContentList.add(DeleteHtmlLabel(str));
		}
	}
	
	/***
	 * ��ȡ�����ȼ���[0-9][A-Z][a-z], ���ȼ�Ϊz
	 * @param num
	 * @return
	 */
	private String GetCodeChar(int num){
		char ascii;
		if (num >= 0 && num < 10) 
			return String.valueOf(num);
		num -= 10;
		if (num >= 0 && num < 26){
			ascii=(char)(num+'A');
			return String.valueOf(ascii);
		}
		num -= 26;
		if (num < 26){
			ascii=(char)(num + 'a');
			return String.valueOf(ascii);
		} 
		return String.valueOf('z');
	}
	
	/***
	 * ������ֱ���
	 * @return
	 */
	int hanzi_feature_class(char c)
	{
		String cc=String.valueOf(c);
		byte[] han=cc.getBytes();
		//for(int i=0;i<han.length;i++)
		//	System.out.println(String.format("%1$#x", han[i]));
		int label = 0;
		if ( han[0] >= (byte)0xB0 && han[0] <= (byte)0xF7 && han[1] >= (byte)0xA1 && han[1] <= (byte)0xFE) {
			label = 1; //��ͨ����
		} else if( han[0] >= (byte)0x81 && han[0] <= (byte)0xA0 && han[1] >= (byte)0x40 && han[1] <= (byte)0xFE) {
			label = 2; //������
		} else if( han[0] >= (byte)0xAA && han[0] <= (byte)0xFE && han[1] >= (byte)0x40 && han[1] <= (byte)0xA0) {
			label = 3; //������2
		} else if( han[0] >= (byte)0xA1 && han[0] <= (byte)0xA9 && han[1] >= (byte)0xA1 && han[1] <= (byte)0xFE) {
			label = 4; //����
		} else if( han[0] >= (byte)0xA8 && han[0] <= (byte)0xA9 && han[1] >= (byte)0x40 && han[1] <= (byte)0xA0) {
			label = 5; //�Ǻ���
		}
		return label;
	}
	
	/***
	 * ����������ʽƥ�䴮
	 * @param reg
	 * @return
	 */
	private int CountMatchedWords(String text,String reg){
		if(text=="" || text.length()<=0) 
			return 0;
		int cnt=0,num=-1;
		boolean isUrl=false;
		if(reg.indexOf("[0-9]")!=-1)
			num=0;
		if(reg.indexOf("[a-zA-Z]+://.+?[^\\s\"'<>/]+")!=-1)
			isUrl=true;
		String[] goodurl={".com", ".com.cn", ".com.tw", ".net.cn", ".hk", ".edu", ".edu.cn", ".org", ".net", ".gov", ".gov.cn", ".org.cn"};
		String[] ignoreStrings={"http://www.3c.org", "http://schemas.microsoft.com", "http://www.w3.org"};
		Pattern p=Pattern.compile(reg);
		Matcher m=p.matcher(text);
		int i=0;
		while(m.find()){
			boolean flag=true;
			String str_st=m.group();
			if(num!=-1 && str_st.length()>=8 && str_st.length()<=11)
				num++;
			if(num==-1){
				for(String str:ignoreStrings){
					if(str_st.indexOf(str)!=-1){
						flag=false;
						break;
					}
				}
			}
			if(flag){
				if(isUrl){
					boolean isbadurl=true;
					for(String str:goodurl){
						if(str_st.indexOf(str)!=-1){
							isbadurl=false;
							break;
						}
					}
					if(isbadurl)
						cnt++;
				}else{
					cnt++;
				}
			}
		}
		if(num!=-1)
			cnt=num;
		return cnt;
	}
	
	//******************************������ȡ��ʼ*****************************************
	
	/***
	 * ��ȡ������
	 * @param buf
	 * @return
	 */
	private String GetShortLine(ArrayList<String> buf,int base){
		String res="";
		if(buf.size()<=0)
			return "0";
		int cnt=0;
		for(String str:buf){
			if(str.length()<3)
				cnt+=1;
		}
		res+=GetCodeChar(cnt/base);
		return res;
	}
	
	/***
	 * ͳ��ÿ�������ַ��ĸ����� �м��壬���壬 Ӣ��
	 * @param buf
	 * @return
	 */
	private String GetCharCnt(String buf,int base){
		String res="";
		if(buf=="" || buf.length()<=0){
			while(res.length()<6)
				res+="0";
			while(res.length()<9)
				res+="*";
			return res;
		}else{
			String engsymbol=",./?\'\"[]!@()+-:";
			String chnsymbol="����������������������";
			String samplechar="\r\t\n ";
			int sumS = 0, sumT = 0, sumE = 0, sumO = 0, sumN = 0, sumB = 0;//���壬����, Ӣ����Ŀ, �����ַ�, ����, ������
			char[] ch = buf.toCharArray();
			for (int i = 0; i < ch.length; i++) {
				char c = ch[i];
				if (c < 0x7f) { // ascii����չ�ַ�
					if (Character.isDigit(c))
						sumN += 1;
					else if (Character.isLetter(c))
						sumE += 1;
					else if (engsymbol.indexOf(c) != -1)
						sumB += 1;
					else if (samplechar.indexOf(c) == -1)// ���ǻس����л��߿ո��Ʊ��
						sumO += 1;
				} else {
					int num = hanzi_feature_class(c);
					if (num == 1)
						sumS += 1;
					else if (num == 2 || num == 3)
						sumT += 1;
					else if (num == 4) {
						if (chnsymbol.indexOf(c) != -1)
							sumB += 1;
						else
							sumO += 1;
					} else
						sumO += 1;
				}
			}
			res += GetCodeChar(sumS / base);//����
			res += GetCodeChar(sumT / base);//����
			res += GetCodeChar(sumE / base);//Ӣ��
			res += GetCodeChar(sumO / base);//������ĸ
			res += GetCodeChar(sumN / base);//����
			res += GetCodeChar(sumB / base);//������
			while (res.length() < 9) res += '*';//ռ9��λ��
			return res;
		}
	}
	
	/***
	 * ���ֺͷǺ��ֵ�����(���������ĺ�����ĵ�����)
	 * @param buf
	 * @return
	 */
	private String GetSampTradArrange(String buf,int base){
		String res="";
		if(buf=="" || buf.length()<=0){
			res+="0";
			while(res.length()<3)
				res+="*";
			return res;
		}else{
			int sumE=0,sumC=0,sum=0;
			int PRECHAR=NUMBER;
			char[] ch = buf.toCharArray();
			for (int i = 0; i < ch.length; i++) {
				char c = ch[i];
				if (c < 0x7f) {// �ո� || �Ǻ��� || Ӣ��
					if (PRECHAR == CHINESE)
						sum++;
					PRECHAR = OTHERWORD;
				} else {// ����
					int label = hanzi_feature_class(c);
					if (label == 1)
						PRECHAR = CHINESE;
					else {
						if (PRECHAR == CHINESE)
							sum++;
						PRECHAR = OTHERWORD;
					}
				}
			}
			res += GetCodeChar(sum / base);
			while (res.length() < 3) //��2λ����û����
				res += '*';
			return res;
		}
	}
	
	/***
	 * ͳ�ƹؼ���
	 * @param buf
	 * @param base
	 * @return
	 */
	private String GetKeyWords(String buf,int base){
		String res="";
		if(buf=="" || buf.length()<=0){
			while(res.length()<4) res+="0";
			while(res.length()<6) res+="*";
			return res;
		}else{
			ArrayList<String> lKeyWord=new ArrayList<String>();
			lKeyWord.add("[qQ��]{2}");
			lKeyWord.add("[a-zA-Z]+://[^\\s\"'>]+");
			lKeyWord.add("<[iI][mM][gG].+?\\s*?[sS][rR][cC]=(['\"]?)([^>\\s]+).*?>|background.{1,60}?(.jpg|.gif|.png|.jpeg|.bmp)");
			lKeyWord.add("[a-zA-Z]+://.+?[^\\s\"'<>/]+");
			int WordCnt=0;
			for(String reg:lKeyWord){
				int c = 0;
				if (WordCnt == 1) {// �ڶ�������
					int html_st = buf.indexOf("<html");
					if (html_st != -1) {
						int html_end = buf.indexOf(">", html_st);
						String url = buf.substring(html_st, html_end);
						c = CountMatchedWords(url, reg);
						continue;
					}
				}
				c = CountMatchedWords(buf, reg);
				res += GetCodeChar(c / base);//�����ַ������ֵĴ���
				WordCnt+=1;
			}
			while (res.length() < 6) res += '*';//ǰ10���ǹؼ��֣���11��������
			return res;
		}
	}
	
	private String GetContentKeyWords(String buf,int base){
		String res="";
		if(buf=="" || buf.length()<=0){
			while (res.length() < 1) res += '0';
			while (res.length() < 5) res += '*';
			return res;
		}else{
			String reg="[0-9]+";
			int c=0;
			c=CountMatchedWords(buf, reg);	
			res += GetCodeChar(c / base);
			while(res.length()<5) res+="*";
			return res;
		}
	}
	
	/***
	 * �����ļ��û���������ͳ��
	 * @param buf
	 * @param base
	 * @param isgood
	 * @return
	 */
	private String GetChineseKeyWords(String buf,int base,boolean isgood){
		String res="";
		if(buf=="" || buf.length()<=0){
			return "0";
		}else{
			String data="";
			char[] ch = buf.toCharArray();
			for (int i = 0; i < ch.length; i++) {
				char c = ch[i];
				if (c > 0x7f) {
					int label = hanzi_feature_class(c);
					if (label == 1) {
						data += c;
					}
				}
			}
			String filename="";
			String goodfilename="";
			String badfilename="";
			String[] goodkeyword={};
			String[] badkeywordString={"����", "����", "����", "�˵�"};
			ArrayList<String> keyword=new ArrayList<String>();
			if(isgood){
				filename=goodfilename;
				for(String str:goodkeyword){
					keyword.add(str);
				}
			}else{
				filename=badfilename;
				for(String str:badkeywordString){
					keyword.add(str);
				}
			}
			File file=new File(filename);
			if(file.exists()){		
				try {
					BufferedReader bw = new BufferedReader(new FileReader(file));
					String sLine = "";
					while ((sLine = bw.readLine()) != null)
						keyword.add(sLine);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			int cnt=0;
			for(String str:keyword){
				int c=CountMatchedWords(data, str);
				if(c>0)
					cnt++;
			}
			res += GetCodeChar(cnt / base);//����
			return res;
		}
	}
	
	/***
	 * ͳ�ƿո�
	 * @param buf
	 * @param base
	 * @return
	 */
	private String GetSpaceCnt(String buf,int base){
		String res="";
		if(buf=="" || buf.length()<=0){
			return "0";
		}else{
			int cnt = 0;
			char[] ch = buf.toCharArray();
			for (int i = 0; i < ch.length; i++) {
				char c = ch[i];
				if (c < 0x7f) {
					if (c == ' ')
						cnt += 1;
				} else {
					String cc = String.valueOf(c);
					byte[] han = cc.getBytes();
					if (han[0] == (byte) 0xa1 && han[1] == (byte) 0xa1)
						cnt += 1;
				}
			}
			res = GetCodeChar(cnt / base);
			return res;
		}
	}
	
	/***
	 * ͳ�Ƹ�����Ϣ
	 * @param buf
	 * @param base
	 * @return
	 */
	private String GetAttachInfoCode(ArrayList<String> buf,int base){
		String res="";
		int imagenum = 0, comprenum = 0, othernum = 0;
		for(String str:buf){
			if(str.contains(".jpg")
			  || str.contains(".bmp")
			  || str.contains(".gif")
			  || str.contains(".png")
			  || str.contains(".jpeg")
			){
				imagenum+=1;
			}else if(str.contains(".rar")
					|| str.contains(".zip")
					|| str.contains(".iso")
					|| str.contains(".7z")
					){
				comprenum+=1;
			}else
				othernum+=1;
		}
		res += GetCodeChar(buf.size() / base);//��������
		res += GetCodeChar(imagenum / base);//ͼƬ����
		res += GetCodeChar(comprenum / base);//ѹ���ļ�����
		res += GetCodeChar(othernum / base);//�����ļ�����
		while(res.length()<6)
			res+="*";
		return res;
	}
	
	private String GetOtherInfo(){
		String res="";  
		int iContentlen=m_HtmlContent.length();
		iContentlen/=50;
		res+=GetCodeChar(iContentlen);
		res+="000";
		iContentlen=0;
		iContentlen+=m_Content.length();
		res+=GetCodeChar(iContentlen);
		res+=GetCodeChar((m_HeaderLen*16)/(100*1024));
		while(res.length()<7)
			res+="*";
		return res;
	}
	
	/***
	 * ��ȡ�����ʼ��ļ���������Ŀ
	 * @param buf
	 * @param base
	 * @return
	 */
	private String GetHtmlChineseWords(String buf,int base){
		String res="";
		if(buf=="" || buf.length()<=0){
			return "0";
		} else {
			int iWordCnt = 0;
			char[] ch = buf.toCharArray();
			for (int i = 0; i < ch.length; i++) {
				char c = ch[i];
				int label = hanzi_feature_class(c);
				if (label == 1) {
					iWordCnt += 1;
				}
			}
			res+=GetCodeChar(iWordCnt/base);
			return res;
		}
	}
	
	/***
	 * ����html�ĵ�
	 * @param base
	 * @return
	 */
	private String HtmlTreeParse(int base){
		String res="";
		return res;
	}
	
	/***
	 * ��ȡ����������Ϣ
	 * @param base
	 * @return
	 */
	private String GetFromAddrInfo(int base){
		String res="";
		String[] topdomain= {".net.cn", ".org.cn", ".edu.cn", ".com.cn", ".gov.cn", ".com.hk", ".com.tw"};
		String[] normaldomain = {".com", ".cn", ".net", ".org", ".net", ".tw", ".hk", ".edu", ".gov"};
		for(String str:m_From){
			System.out.println(str);
		}
		if(m_From.size()<=0){
			return "0000";
		}else{
			String sMailFrom=m_From.get(0).split(" ")[1];			
			int label=sMailFrom.indexOf("@");
			String sUser=sMailFrom.substring(0,label-1);
			String sMailAddress=sMailFrom.substring(label+1);
			System.out.println(sUser+"&&"+sMailAddress);
			
			
			int num=0;
			if(!sMailFrom.contains("@")) 
				return "0000";
			else{			
				char[] ch=sUser.toCharArray();
				/*------------------@ǰ����ַ�������ͳ��-----------------------*/
				int PRECHAR=OTHERWORD;
				for(int i=0;i<ch.length;i++){
					char c=ch[i];
					if(Character.isLetter(c)){ 
						if(PRECHAR==NUMBER)
							num+=1;
						PRECHAR=ENGLISH;
					}else if(Character.isDigit(c)){
						PRECHAR=NUMBER;
					}
				}
				res+=GetCodeChar(num/base);
				
				/*-----------------�쳣ID���---------------------*/
				num=0;
				int[] eng={0,0};
				int[] dig={0,0};
				int index=0;
				for(int i=0;i<ch.length;i++){
					char c=ch[i];
					if(c=='_' && index==0) {index+=1; continue; }
					if(Character.isLetter(c)) eng[index]+=1;
					if(Character.isDigit(c)) dig[index]+=1;
				}
				if((dig[0]>0 || dig[1]>0) && eng[0]>0 && eng[1]>0) num=1;
				if((eng[0]>0 || eng[1]>0) && dig[0]>0 && dig[1]>0) num=1;
				res+=GetCodeChar(num/base);
				
				/*-----------------��ļ���ͳ��------------------*/
				num=0;
				String sTmp="";
				for(String str:topdomain){
					if((index=sMailFrom.indexOf(str))!=-1){
						num=2;
						sTmp=sMailFrom.substring(0,index);
						break;
					}else{
						num=1;						
					}			
				}
				if(num==1) sTmp=sMailFrom;
				ch=sTmp.toCharArray();
				for(int i=0;i<ch.length;i++){
					char c=ch[i];
					if(c=='.')
						num+=1;
				}
				res+=GetCodeChar(num/base);
				
				/*-------------------������ͳ��------------------*/
				num=0;
				for(String str:normaldomain){
					if(sMailAddress.lastIndexOf(str)!=-1){
						num=1;
						break;
					}
				}
				res+=GetCodeChar(num/base);
			}		
		}	
		return res;
	}
	
	//******************************������ȡ����*****************************************
	
	/***
	 * �ʼ�ͷ����
	 * @return
	 */
	private String GetSubjectCode(){
		String res="";
		res+=GetShortLine(m_SubjectList,1);//1
		res+=GetCharCnt(m_Subject,1);//9
		res+=GetSampTradArrange(m_Subject,1);//3
		res+=GetKeyWords(m_Subject,1);//6
		res+=GetContentKeyWords(m_Subject, 1);//5
		res+=GetChineseKeyWords(m_Subject,1,true);//1
		res+=GetChineseKeyWords(m_Subject,1,false);//1
		res+=GetSpaceCnt(m_Subject, 1);//1
		System.out.println("Code����:"+res.length());
		while(res.length()<m_SubjectCodeLen)//5
			res+="*";
		return res;
	}
	
	/***
	 * �ʼ�������
	 * @return
	 */
	private String GetContentCode(){
		String res="";
		res+=GetShortLine(m_ContentList,1);//1
		res+=GetCharCnt(m_Content,10);//9
		res+=GetSampTradArrange(m_Content,1);//3
		res+=GetKeyWords(m_HtmlContent,1);//6
		res+=GetContentKeyWords(m_Content, 1);//5
		res+=GetChineseKeyWords(m_Content,1,true);//1
		res+=GetChineseKeyWords(m_Content,1,false);//1
		res+=HtmlTreeParse(1);//20
		res+=GetAttachInfoCode(m_AttachNames,1);//6
		res+=GetOtherInfo();//7
		res+=GetHtmlChineseWords(m_HtmlContent, 10);//1
		res+=GetFromAddrInfo(1);//4
		System.out.println("Code���ĳ���:"+res.length());
		while(res.length()<m_ContentCodeLen)
			res+="*";
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
	
	private void Release(){
		m_HeaderLen=0;
		m_Content="";
		m_Subject="";
		m_AttachNames.clear();
		m_HtmlContent="";
	}
	
	/***
	 * ������ȡ��ں���
	 * @param filePath
	 * @return
	 */
	public String feature_extraction(String filePath){
		Release();
		GetFileContent(filePath);
		GetHideContent();
		System.out.println("�������ݣ�");
		System.out.println(m_Content);
		System.out.println("html���ݣ�");
		System.out.println(m_HtmlContent);
		m_Code=GetAllCode();
		return  m_Code;
	}
	
	/***
	 * ���ܲ��Ժ���
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {	
		String filePath = "F:\\MailProject\\���鳬-��ҵ���\\emailtest1-decode\\attach.eml.txt";
		System.out.println("������ȡ�ļ���"+filePath);
		extraction myExtraction=new extraction();
		//String testString=myExtraction.feature_extraction(filePath);
		//System.out.println(testString);
	}
}
