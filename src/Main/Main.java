package Main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;

import org.python.antlr.PythonParser.return_stmt_return;
import org.python.core.PyFunction;
import org.python.util.PythonInterpreter;

import email_decode.Decode_Mail;
import email_decode.Decode_UI;
import feature_extraction.extraction;

public class Main {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		final UI frame = new UI("���������ʼ�����ϵͳ", 400, 550);
        frame.setVisible(true);
        
        //���밴ť�¼�
        frame.btnEmailDecode.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		
        		/*�����ļ���*/
        		String filePath = frame.dictionaryText.getText();
        		if(filePath==""){return ;}
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
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method
				String filePath = frame.dictionaryLabel.getText()+"-decode";
				if(filePath==""){return ;}
				try{
					dealFiles_feature_extraction(filePath);
				}catch (FileNotFoundException e1){
					System.out.println(e1);
				}
				
			}
		});
        
        //�˳���ť�¼�
        frame.Exitbtn.addActionListener(new ActionListener() {    
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
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
			interpreter.execfile("F:\\MailProject\\lxc\\decodemail.py");
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
	
	/**
	 * ������ȡ
	 * @param filePath
	 * @throws FileNotFoundException
	 */
	private static void dealFiles_feature_extraction(String filePath) throws FileNotFoundException{
		File root = new File(filePath);
		if(root.exists() && root.isDirectory()){    //�����ļ�����������ļ�����			
			System.out.println(root.getAbsolutePath());  //test(�ļ���·��)
			File fileRes=new File(filePath+"result");
			//todo
			File[] files = root.listFiles();
			String featureString="";
			for(File file:files){
				featureString+=extraction.feature_extraction(file.getAbsolutePath())+"\n";
			}
			//д���ݵ�result�ļ�
		}else{
			System.out.println("�ļ��в����ڣ�");
		}
	}
}
