package email_decode;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;

import org.python.core.PyFunction;
import org.python.util.PythonInterpreter;

import org.python.util.PythonInterpreter;
import org.python.util.install.*;  
import org.python.core.*;
import org.python.core.util.*;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		final Decode_UI frame = new Decode_UI("�ʼ�����:", 700, 550);
        frame.setVisible(true);
        
        //���밴ť�¼�
        frame.OKbtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		
        		/*�����ļ���*/
        		String filePath = frame.dictionaryText.getText();
        		try {
					dealFiles(filePath);
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
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
	
	public static void dealFiles(String filePath) throws FileNotFoundException{
		File root = new File(filePath);    //��ָ��·�����ļ�
		if(root.exists() && root.isDirectory()){    //�����ļ�����������ļ�����
			PythonInterpreter interpreter = new PythonInterpreter();  
			interpreter.execfile("F:\\MailProject\\lxc\\decodemail.py");
			PyFunction func = (PyFunction)interpreter.get("decodebody_str",PyFunction.class);
			
			System.out.println(filePath);
			File dict = new File(filePath+"-decode");
			dict.mkdirs();
			System.out.println(dict.getAbsolutePath());
			File[] files = root.listFiles();
			for(File file:files){
				Decode_Mail.decodeMail(file.getAbsolutePath(),dict.getAbsolutePath(),func);
			}
		}else{
			System.out.println("�ļ��в����ڣ�");
		}
	}
}

