package Main;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.NumericShaper.Range;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilePermission;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.python.antlr.PythonParser.else_clause_return;
import org.python.antlr.PythonParser.file_input_return;
import org.python.antlr.PythonParser.return_stmt_return;
import org.python.core.PyFunction;
import org.python.util.PythonInterpreter;

import Test.Bayes;

import divide_word.DivideWord;


import email_decode.Decode_Mail;
import feature_extraction.extraction;

public class Main {
	/**
	 * @param args
	 */
	
	private static boolean IsRush = true;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		final UI frame = new UI("���������ʼ�����ϵͳ", 400, 550);
        
        //���밴ť�¼�
        frame.btnEmailDecode.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		
        		/*�����ļ���*/
        		String filePath = "";
        		filePath=frame.dictionaryText.getText();
        		if(filePath.length()<=0){return ;}
        		try {
					dealFiles_decode(filePath);
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					System.out.println(e1);
				}
        	}
        });
        
        //������ȡ��ť�¼�
        frame.btnFeatureExtractionButton.addActionListener(new ActionListener() {		
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method
				String filePath = frame.txtExtraction.getText();
				if(filePath.length()<=0){return ;}
				try{
					System.out.println(filePath);
					dealFiles_feature_extraction(filePath);
				}catch (FileNotFoundException e1){
					System.out.println(e1);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					//e1.printStackTrace();
					return ;
				}				
			}
		});
        
        //�ִʴ���ť�¼�
        frame.btnDivideWord.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String filePath1 = frame.txtDividewordText.getText();
				if(filePath1.length()<=0){return ;}
				try {
					dealFiles_divideword(filePath1);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
        
        //ʵ�鴦��ť�¼�
        frame.btnTest.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String sFilePath = frame.txtTest1.getText();
				String sFilePath2 = frame.txtTest2.getText();
				if(sFilePath.length()<=0 || sFilePath2.length()<=0) return ;
				dealFiles_Test(sFilePath,sFilePath2);
			}
		});
        
        //�˳���ť�¼�
        frame.Exitbtn.addActionListener(new ActionListener() {    
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        //�ϳ�arff
        frame.btnArff.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String sPathString = frame.txtArff.getText();
				if(sPathString.length()<=0) return ;
				try {
					RefreshArff(sPathString);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
        
        frame.jrb1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				IsRush = true;
				System.out.println("ѡ������");
			}
		});
        
        frame.jrb2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				IsRush = false;
				System.out.println("ѡ�з�����");
			}
		});
	}
	
	/**
	 * �ʼ�����
	 * @param filePath
	 * @throws FileNotFoundException
	 */
	private static void dealFiles_decode(String filePath) throws FileNotFoundException{
		File root = new File(filePath);    //��ָ��·�����ļ�
		if(root.exists() && root.isDirectory()){    //�����ļ�����������ļ�����
			PythonInterpreter interpreter = new PythonInterpreter();  
			interpreter.execfile("src\\email_decode\\decodemail.py");
			PyFunction func = (PyFunction)interpreter.get("decodebody_str",PyFunction.class);
			
			//System.out.println(filePath);
			File dict = new File(filePath+"-decode");
			dict.mkdirs();
			//System.out.println(dict.getAbsolutePath());
			File[] files = root.listFiles();
			for(File file:files){
				Decode_Mail.decodeMail(file.getAbsolutePath(),dict.getAbsolutePath(),func);
			}
		}else{
			System.out.println("�ļ��в����ڣ�");
		}
	}
	
	/***
	 * �ϳ�arff
	 * @param sPath
	 * @throws IOException 
	 */
	private static void RefreshArff(String sPath) throws IOException{
		File root = new File(sPath);
		if(root.exists() && root.isDirectory()){
			File fileRes=new File(sPath+"resultArff.arff");
			fileRes.delete();
			String res = "";
			if (fileRes.createNewFile()==true){
				File[] files = root.listFiles();
				for(int i=0;i<files.length;i++){
					File file = files[i];
					String tmp = readByFileRead(file);
					if(i!=0){
						int pos = tmp.indexOf("@data");
						tmp = tmp.substring(pos + 5);
					}
					res += tmp;
				}
				writeByFileWrite(fileRes.getAbsolutePath(), res);
			}else{
				System.out.println("�����ļ�ʧ��");
				return ;
			}
		}
		else{
			System.out.println("�ļ��в�����");
			return ;
		}
	}
	
	/**
	 * ������ȡ
	 * @param filePath
	 * @throws IOException 
	 */
	private static void dealFiles_feature_extraction(String filePath) throws IOException{
		File root = new File(filePath);
		if(root.exists() && root.isDirectory()){    //�����ļ�����������ļ�����			
			System.out.println(root.getAbsolutePath());
			File fileRes=new File(filePath+"result.txt");
			File fileArff=new File(filePath+"result.arff");
			fileRes.delete();
			fileArff.delete();
			String featureString="";
			String[] tmp;
			if(fileRes.createNewFile()==true){
				File[] files = root.listFiles();
				tmp = new String[files.length];
				extraction myExtraction = new extraction();
				int i=0;
				for(File file:files){
					String result = myExtraction.feature_extraction(file.getAbsolutePath());
					featureString+=result+"\r\n";
					tmp[i++] = result;
				}
				//System.out.println(featureString);
				//д���ݵ�result�ļ�
				writeByFileWrite(fileRes.getAbsolutePath(), featureString);
			}
			else{
				System.out.println("�����������ļ�ʧ�ܣ�");
				return ;
			}
			featureString = "\r\n@data\r\n";
			if(fileArff.createNewFile()==true){	
				int attLen = 0;
				for(String s:tmp){
					String dataString = "";
					attLen = s.length();
					for(int i=0;i<s.length();i++){
						dataString += (s.charAt(i) + ",");
					}
					if(IsRush)  // ����
						dataString += "1";
					else 
						dataString += "0";
					featureString+=dataString + "\r\n";
				}
				System.out.println(featureString);
				String relationString = "@RELATION email\r\n\r\n";
				String RANGESTRING = "*0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
				String arrVal = "{";
				for(int i=0;i<RANGESTRING.length();i++){
					if (i == RANGESTRING.length()-1)
						arrVal += RANGESTRING.charAt(i);
					else {
						arrVal += RANGESTRING.charAt(i) + ",";
					}
				}
				arrVal+="}";
				String attrTmp = "";
				for(int i=0;i<attLen;i++){
					attrTmp += String.format("@ATTRIBUTE attr%s %s\r\n", i, arrVal);
				}
				attrTmp += String.format("@ATTRIBUTE class {0,1}");
				writeByFileWrite(fileArff.getAbsolutePath(),relationString + attrTmp + featureString);
			}
			else{
				System.out.println("����arff�ļ�ʧ��!");
				return ;
			}
		}else{
			System.out.println("�ļ��в����ڣ�");
			return ;
		}
	}
	
	/***
	 * ���ķִʣ�������ѡ��
	 * @param filePath
	 * @throws IOException 
	 */
	private static void dealFiles_divideword(String filePath1) throws IOException{
		File root1 = new File(filePath1);
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
	}
	
	/***
	 * ѵ���������Լ�ʵ��
	 * @param sFilePath
	 */
	private static void dealFiles_Test(String sFilePath,String sFilePath2){
		File root = new File(sFilePath);
		File root2 = new File(sFilePath2);
		Boolean flag = root.exists() && root2.exists();
		if(flag){
			System.out.println(getFileExtention(sFilePath).equals(".arff"));
			if(getFileExtention(sFilePath).equals(".arff") && getFileExtention(sFilePath2).equals(".arff")){
				Bayes bayes = new Bayes();
				bayes.Run(sFilePath,sFilePath2);
			}else{
				System.out.println("����arff�ļ�");
				return ;
			}
		}else{
			System.out.println("�ļ�������!");
			return ;
		}
	}
	
	/**
	 * ���ļ�д����
	 * @param _sDestFile
	 * @param _sContent
	 * @throws IOException
	 */
	public static void writeByFileWrite(String _sDestFile, String _sContent) throws IOException {
			FileWriter fw = null;
			try {
				fw = new FileWriter(_sDestFile);
				fw.write(_sContent);
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				if (fw != null) {
					fw.close();
					fw = null;
				}
			}
	}
	
	/***
	 * ��ȡ�ļ�
	 * @param f
	 * @return
	 */
	public static String readByFileRead(File f){
		String resString = "";
		FileReader fr = null;
		try {
			fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			String s = "";
			try {
				s=br.readLine();
				while(s!=null){
					resString += "\r\n" + s;
					s = br.readLine();
				}
				return resString;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "";
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
	
	/***
	 * ��ȡ�ļ�����׺
	 * @param sPath
	 * @return
	 */
	private static String getFileExtention(String sPath){
		int pos = sPath.indexOf('.');
		if(pos!=-1){
			String res = sPath.substring(pos);
			return res;
		}
		return "";
	}
}
