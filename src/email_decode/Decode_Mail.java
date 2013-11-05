package email_decode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeUtility;

import org.python.util.PythonInterpreter;
import org.python.util.install.*;  
import org.python.antlr.PythonParser.return_stmt_return;
import org.python.core.*;
import org.python.core.util.*;

public class Decode_Mail {
	
	protected static PythonInterpreter interpreter;
	
	/**
	 * �ʼ�ͷ����	
	 * @param filePath
	 * @param Path
	 */
	private static void decodeHeader(String filePath,String Path){
		File file = new File(filePath);				
		String strSubject = "Subject:";
		String strDate = "Date:";
		String strFrom ="From:";
		String strTo="To:";
		String charset = "";
		String encodeType="";
		String str;
		
		if(file.exists()){
			try{
				Scanner input = new Scanner(file);
				//str=input.nextLine();
				if(input.hasNext()){
				str=input.next();
				while(input.hasNext()){
				//	System.out.println(str);
					if(str.startsWith("charset=")){      //�ַ���
						str = str.replace("charset=", "");
						charset=str.replace("\"", "");
					}else if(str.equalsIgnoreCase("Content-Transfer-Encoding:")){   
						str=input.next();
						if("base64".equals(str) || "quoted-printable".equals(str)){
							encodeType=str;
						}
					}else if(strSubject.equalsIgnoreCase(str)){   //��ȡ����
						str=input.next();
						int i=1;
						while(str.startsWith("=?")){
							if(i==1){
								i++;
								strSubject = str;
							}
							else{
								strSubject+=" "+str;							
							}
							str=input.next();
						}
						continue;
					}else if(strDate.equalsIgnoreCase(str)){   //��ȡ����
						String[] date = new String[6];
						for(int i=0;i<6;i++){
							date[i]=input.next();
						}
						date[0] = date[0].replace(",", "");
						strDate = date[3] + "-"+date[2]+"-"+date[1]+" CST "+date[0]+" "+date[4];
						System.out.println(strDate);						
					}else if(strFrom.equalsIgnoreCase(str)){   //��ȡFrom
						str=input.next();
						if (str.startsWith("=?")) {							
                            strFrom = str;
                            str = input.next();
                            if (str.startsWith("<")) {
                                strFrom += " " + str;
                                str=input.next();
                            }
						}else {
							str=str.replace("\"", "");
							strFrom = str;
							str=input.next();
						/*	while (true) {
                                if (str.startsWith("<")) {
                                    strFrom += " " + str;
                                    break;
                                }
                                strFrom += " " + str;   //todo
                                str = input.next();
                            }
                         */
						}
						System.out.println(strFrom);
						continue;
					}else if(strTo.equalsIgnoreCase(str)){  //��ȡTo
						str=input.next();
						str=str.replace("\"","");
						int i=1;			
						strTo=str;
						while(str.startsWith("=?")){   //������
							if(i==1){
								strTo=str;
								i++;
							}else{
								strTo+=str;
							}
							str=input.next();
							if(str.startsWith("<")){
								strTo+=" "+str;
							}
							str=input.next();
							str=str.replace("\"", "");
						}
						while(str.endsWith(",")){  //������
							if(i==1){
								strTo=str;
								i++;
							}else{
								strTo+=" "+str;
							}
							str=input.next();
							str=str.replace("\"", "");
						}
						System.out.println(strTo);
						continue;
					}
					
					str=input.next();
				}
				}
				
			/**********************ͷ������ϣ�д�ļ�*****************************/	
				File outputfile = new File(Path+"\\"+file.getName()+".txt");
				PrintWriter output = new PrintWriter(outputfile);
				output.println("Դ�ʼ���"+file.getAbsolutePath());
				output.println("�����ʼ���"+file.getAbsolutePath()+".txt\r\n\r\n");
				
				
				if(strSubject.startsWith("=?")){
					if(strSubject.indexOf("=?x-unknown?")>=0){
						strSubject = strSubject.replaceAll("x-unknown","utf-8");
					}
					strSubject=decodeText(strSubject);				
				}else if(strSubject.equalsIgnoreCase("Subject:")){
					strSubject="��";
				}
				output.println("���⣺"+strSubject);
				
				
				System.out.println(strFrom);
				if(strFrom.startsWith("=?")){
					if(strFrom.indexOf("=?x-unknown?")>=0){
						strFrom = strFrom.replaceAll("x-unknown", "utf-8");
					}
					strFrom=decodeText(strFrom);
					InternetAddress ia = new InternetAddress(strFrom);
					output.println("�����ˣ�"+ia.getPersonal()+" "+ia.getAddress());
				}else if(strFrom.equalsIgnoreCase("From:")){
					strFrom="��";
					output.println("�����ˣ�"+strFrom);
				}else{
					output.println("�����ˣ�"+strFrom);
				}
			
					
				System.out.println(strTo);
				String[] Tos = strTo.split(",");
				if(!strTo.startsWith("To:")){				
					strTo="";
					for(String var:Tos){
						if(strTo.startsWith("=?")){
							if(var.indexOf("=?x-unknown")>=0){
								var = var.replaceAll("x-unknown", "utf-8");
							}
							var=decodeText(var);
						}
						strTo+=var+"\r\n";
					}
				}else{
					strTo="��";
				}
				output.println("�ռ��ˣ�\r\n"+strTo);
				
				if (strDate.length() > 10) {
                    output.println("�������ڣ�"+strDate + "\r\n");
                }
				
				input.close();
				output.close();
				
			}catch(Exception ex){
				System.out.println(ex);
			}
		}else{
			System.out.println("�ļ������ڣ�");
		}
	}
	
	/**
	 * ���ʼ����Ľ���
	 * @param filePath  
	 * @param func      
	 * @param Path
	 * @throws FileNotFoundException
	 */
	private static void decodeBody(String filePath,PyFunction func,String Path) throws FileNotFoundException{
		File file = new File(filePath);				
		File outputfile = new File(Path+"\\"+file.getName()+".txt");			 
		PyObject ans = func.__call__(new PyString(filePath),new PyString(outputfile.getAbsolutePath()));	//����python�ļ����н���
	}
	
	
	/**
	 * �ʼ�����
	 * @param filePath  eml�ʼ��ľ���·��
	 * @param Path      eml�ʼ�������ļ��еľ���·��
	 * @param func      decodemail.py�ļ����decode_str����
	 * @throws FileNotFoundException
	 */
	public static void decodeMail(String filePath,String Path,PyFunction func) throws FileNotFoundException{
		decodeHeader(filePath,Path);
		decodeBody(filePath,func,Path);
	}
	
	
	/**
	 * ���ݶ�Ӧ���ַ�������ַ������н���
	 * @param text
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private static String decodeText(String text)
            throws UnsupportedEncodingException {
        if (text.startsWith("=?")) {
            return MimeUtility.decodeText(text);
        } else {
            return new String(text.getBytes("ISO8859_1"));
        }
    }
	
	public static void main(String[] args) throws FileNotFoundException {
		interpreter = new PythonInterpreter();  
		interpreter.execfile("F:\\MailProject\\lxc\\decodemail.py");
		PyFunction func = (PyFunction)interpreter.get("decodebody_str",PyFunction.class);
		
		String filePath = "F:\\MailProject\\lxc\\emailtest1\\errorhead3.eml";
		File file = new File(filePath);	
		if(file.exists()){
			String path=file.getParent();
			File dict = new File(path+"-decode");
			dict.mkdirs();
			path=dict.getAbsolutePath();
			
			decodeHeader(filePath,path);
			decodeBody(filePath,func,path);
		}else{
			System.out.println("�ļ������ڣ�");
		}
	}
}
