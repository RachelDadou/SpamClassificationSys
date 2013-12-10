package Main;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class UI extends JFrame{
	JLabel dictionaryLabel = new JLabel("�������ļ���·��");
	JTextField dictionaryText = new JTextField();
	JButton btnEmailDecode = new JButton("�ʼ�����");
	JButton btnFeatureExtractionButton = new JButton("������ȡ");
	JButton Exitbtn = new JButton("�˳�");
	JPanel panelFileSource = new JPanel();//���
    JPanel panelBtn = new JPanel();//���
    JPanel panelExit = new JPanel();
    
    public UI(){}
    
    public UI(String title,int height,int width){
    	this.setSize(width,height);
    	this.setTitle(title);
    	
    	panelFileSource.setLayout(new BorderLayout());
    	panelFileSource.add(dictionaryLabel, BorderLayout.WEST);
    	panelFileSource.add(dictionaryText, BorderLayout.CENTER);
       // jp.add(btnEmailDecode, BorderLayout.EAST);
      //  jp.add(btnFeatureExtractionButton,BorderLayout.EAST);
        add(panelFileSource,BorderLayout.NORTH);
        
        panelBtn.setLayout(new FlowLayout(FlowLayout.LEFT));
        panelBtn.add(btnEmailDecode);
        panelBtn.add(btnFeatureExtractionButton);
        add(panelBtn);
        
        panelExit.setLayout(new FlowLayout(FlowLayout.RIGHT));
        panelExit.add(Exitbtn);
        add(panelExit, BorderLayout.SOUTH);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
