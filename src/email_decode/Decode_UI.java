package email_decode;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.*;

public class Decode_UI extends JFrame{
	JLabel dictionaryLabel = new JLabel("�������ļ���·��");
	JTextField dictionaryText = new JTextField();
	JButton OKbtn = new JButton("ȷ��");
	JButton Exitbtn = new JButton("�˳�");
	JPanel jp = new JPanel();//���
    JPanel jp1 = new JPanel();//���
    
    public Decode_UI(){}
    
    public Decode_UI(String title,int height,int width){
    	this.setSize(width,height);
    	this.setTitle(title);
    	
    	jp.setLayout(new BorderLayout());
        jp.add(dictionaryLabel, BorderLayout.WEST);
        jp.add(dictionaryText, BorderLayout.CENTER);
        jp.add(OKbtn, BorderLayout.EAST);
        add(jp,BorderLayout.NORTH);
        
        jp1.setLayout(new FlowLayout(FlowLayout.RIGHT));
        jp1.add(Exitbtn);
        add(jp1, BorderLayout.SOUTH);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
