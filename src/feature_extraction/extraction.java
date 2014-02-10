package feature_extraction;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import org.omg.CORBA.PUBLIC_MEMBER;
import org.python.antlr.PythonParser.else_clause_return;
import org.python.antlr.PythonParser.return_stmt_return;
import org.python.constantine.Constant;
import org.python.google.common.primitives.UnsignedBytes;

import com.kenai.jaffl.struct.Struct.Unsigned8;

public class extraction {
	
	final int OVECCOUNT=30;
	final int OTHERWORD=0;
	final int CHINESE=1;
	final int ENGLISH=2;
	final int NUMBER=3;
	
	private ArrayList<String> m_Content=new ArrayList<String>();
	private int m_ContentCodeLen=64;
	private String m_Code;
	
	private ArrayList<String> m_Subject=new ArrayList<String>();
	private int m_SubjectCodeLen=32;
	
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
							m_Subject.add(result[1]);
						}
						System.out.println(m_Subject);
						continue;
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
		for(int i=0;i<han.length;i++)
			System.out.println(String.format("%1$#x", han[i]));
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
	
	//******************************������ȡ��ʼ*****************************************
	
	/***
	 * ��ȡ������
	 * @param buf
	 * @return
	 */
	private String GetShortLine(ArrayList<String> buf){
		String res="";
		int base=1;
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
	private String GetCharCnt(ArrayList<String> buf){
		String res="";
		int base=10;
		if(buf.size()<=0){
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
			for(String str:buf){
				char[] ch=str.toCharArray();
				for(int i=0;i<ch.length;i++){
					char c=ch[i];
					if(c<0x7f){		//ascii����չ�ַ�
						if(Character.isDigit(c))
							sumN+=1;
						else if(Character.isLetter(c))
							sumE+=1;
						else if(engsymbol.indexOf(c)!=-1)
							sumB+=1;
						else if(samplechar.indexOf(c)==-1)//���ǻس����л��߿ո��Ʊ��
							sumO+=1;
					}else{
						int num=hanzi_feature_class(c);
						if(num==1) 
							sumS+=1;
						else if(num==2 || num==3) 
							sumT+=1;
						else if(num==4){
							if(chnsymbol.indexOf(c)!=-1)
								sumB+=1;
							else
								sumO+=1;
						}else
							sumO+=1;
					}
				}
			}
			res += GetCodeChar(sumS / base);//����
			res += GetCodeChar(sumT / base);//����
			res += GetCodeChar(sumE / base);//Ӣ��
			res += GetCodeChar(sumO / base);//������ĸ
			res += GetCodeChar(sumN / base);//����
			res += GetCodeChar(sumB / base);//������
			return res;
		}
	}
	
	/***
	 * ���ֺͷǺ��ֵ�����(���������ĺ�����ĵ�����)
	 * @param buf
	 * @return
	 */
	private String GetSampTradArrange(ArrayList<String> buf){
		String res="";
		int base=1;
		if(buf.size()<=0){
			res+="0";
			while(res.length()<3)
				res+="*";
			return res;
		}else{
			int sumE=0,sumC=0,sum=0;
			int PRECHAR=NUMBER;
			for(String str:buf){
				char[] ch=str.toCharArray();
				for(int i=0;i<ch.length;i++){
					char c=ch[i];
					if(c<0x7f){//�ո� || �Ǻ��� || Ӣ��
						if(PRECHAR==CHINESE)
							sum++;
						PRECHAR=OTHERWORD;
					}else{//����
						int label=hanzi_feature_class(c);
						if(label==1)
							PRECHAR=CHINESE;
						else{
							if(PRECHAR==CHINESE)
								sum++;
							PRECHAR=OTHERWORD;
						}
					}
				}
			}
			res+= GetCodeChar(sum / base);
			while (res.length() < 3) //��2λ����û����
				res += '*';
			return res;
		}
	}
	
	private String GetKeyWords(ArrayList<String> buf){
		String res="";
		if(buf.size()<=0){
			while(res.length()<4) res+="0";
			while(res.length()<6) res+="*";
			return res;
		}else{
			return res;
		}
	}
	
	private String GetSpaceCnt(ArrayList<String> buf,int base){
		String res="";
		if(buf.size()<=0){
			return "0";
		}else{
			int cnt=0;
			for(String str:buf){
				char[] ch=str.toCharArray();
				for(int i=0;i<ch.length;i++){
					char c=ch[i];
					if(c<0x7f){
						if(c==' ') cnt+=1;
					}else{
						String cc=String.valueOf(c);
						byte[] han=cc.getBytes();
						if(han[0]==(byte)0xa1 && han[1]==(byte)0xa1)
							cnt+=1;
					}
				}
			}
			res = GetCodeChar(cnt / base);
			return res;
		}
	}
	
	//******************************������ȡ����*****************************************
	
	/***
	 * �ʼ�ͷ����
	 * @return
	 */
	private String GetSubjectCode(){
		String res="";
		res+=GetShortLine(m_Subject);//1
		res+=GetCharCnt(m_Subject);//9
		res+=GetSampTradArrange(m_Subject);//3
		res+=GetKeyWords(m_Subject);//6
		while(res.length()<m_SubjectCodeLen)
			res+="*";
		return res;
	}
	
	/***
	 * �ʼ�������
	 * @return
	 */
	private String GetContentCode(){
		String res="";
		res+=GetShortLine(m_Content);//1
		//res+=GetCharCnt(m_Content);//9
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
		m_Content.clear();
		m_Subject.clear();
	}
	
	/***
	 * ������ȡ��ں���
	 * @param filePath
	 * @return
	 */
	public String feature_extraction(String filePath){
		Release();
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
		System.out.println(testString);
	}
}
