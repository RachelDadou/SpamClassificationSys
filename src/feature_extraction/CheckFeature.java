package feature_extraction;
import net.sourceforge.pinyin4j.*;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.* ;

public class CheckFeature {
	
	public String  getHanyutoPinyin(String  s )   //��ú��ֻ������ƴ��
	{
		HanyutoPinyin pinyin = new HanyutoPinyin();
		String strPinyin = pinyin.getStringPinYin(s);
		return strPinyin;
	}
	
	public String getFtToJt(String s) throws IOException  //��÷���ת����ļ���
	{
		FtToJt fttojt = new FtToJt();
		return fttojt.simplized(s);
	}

}




class HanyutoPinyin //����תƴ��
{
		private HanyuPinyinOutputFormat format = null;
        private String[] pinyin;
        public HanyutoPinyin()
         {
                   format = new HanyuPinyinOutputFormat();                  
                   //WITH_TONE_NUMBER�����ƴ�������� 
                  	//WITHOUT_TONE ��û���������

                  // format.setToneType(HanyuPinyinToneType.WITH_TONE_NUMBER);
                   format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);                
                   pinyin = null;
         }
         //ת�������ַ�
         public String getCharacterPinYin(char c)
         {
               try
                {
                            pinyin = PinyinHelper.toHanyuPinyinStringArray(c, format);
             }
                  catch(BadHanyuPinyinOutputFormatCombination e)

                   {

                            e.printStackTrace();

                   }
                   // ���c���Ǻ��֣�toHanyuPinyinStringArray�᷵��null

                   if(pinyin == null) return null;
                   // ֻȡһ������������Ƕ����֣���ȡ��һ������
                   return pinyin[0];   
         }
      //ת��һ���ַ���
         public String getStringPinYin(String str)

         {
                   StringBuilder sb = new StringBuilder();
                   String tempPinyin = null;
                   for(int i = 0; i < str.length(); ++i)

                   {
                            tempPinyin =getCharacterPinYin(str.charAt(i));
                            if(tempPinyin == null)

                            {
                                     // ���str.charAt(i)�Ǻ��֣��򱣳�ԭ��
                                     sb.append(str.charAt(i));
                            }
                            else
                            {
                                     sb.append(tempPinyin);
                            }
                   }

                   return sb.toString();
         }

}


 class FtToJt   		//�������ת��
 {
	String jtPy = "";
	String ftPy ="";
       
	FtToJt() throws IOException
	{
		FileInputStream fis = new FileInputStream("./src/txt/jtchar.txt");   //�򿪼���Ⲣ�����ݸ�ֵ�ַ���jtPy
		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader brjt = new BufferedReader(isr);
		String str = brjt.readLine();
		while(str!= null)
		{	
			jtPy=jtPy+str;
			str = brjt.readLine();
		}
		brjt.close();
	//	System.out.println(jtPy);
		
		FileInputStream fisft = new FileInputStream("./src/txt/ftchar.txt");   //�򿪷���Ⲣ�����ݸ�ֵ�ַ���jtPy
		InputStreamReader isrft = new InputStreamReader(fisft);
		BufferedReader brft = new BufferedReader(isrft);
		String strft = brft.readLine();
	
		while(strft!= null)
		{
			ftPy=ftPy+strft;
			strft = brft.readLine();
		}
		brft.close();
	//	System.out.println(ftPy);
		
	}
	 
    String simplized(String st) {   //����ת����
        String stReturn = "";   
        for (int i = 0; i < st.length(); i++) {   
            char temp = st.charAt(i);   
            if (ftPy.indexOf(temp) != -1)   
                stReturn += jtPy.charAt(ftPy.indexOf(temp));   
            else   
                stReturn += temp;   
        }   
        return stReturn;   
    }   
}

