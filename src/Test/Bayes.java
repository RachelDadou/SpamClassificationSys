package Test;

import java.io.File;

import jxl.Workbook;
import jxl.write.*;

import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class Bayes {

	public Bayes(){}
	 
	/***
	 * ִ�б�Ҷ˹�㷨
	 * @param sPath
	 */
	public void Run(String sPath,String sPath2){
		Instances instancesTrain = null;
        Instances instancesTest = null;
        try {
            /*
             * 1.����ѵ��
             * �ڴ����ǽ�ѵ�������Ͳ�����������weka�ṩ��segment���ݼ����ɵ�
             */

            File inputFile = new File(sPath);   //ѵ�������ļ�
            ArffLoader atf = new ArffLoader();
            atf.setFile(inputFile);

            instancesTrain = atf.getDataSet();

            //��ʹ������֮ǰһ��Ҫ��������instances��classIndex��������ʹ��instances�����ǻ��׳��쳣
            instancesTrain.setClassIndex(instancesTrain.numAttributes() - 1);

            inputFile = new File(sPath2); // ���������ļ�
            atf.setFile(inputFile);
            instancesTest = atf.getDataSet();    //��������ļ�
            //��ʹ������֮ǰһ��Ҫ��������instances��classIndex��������ʹ��instances�����ǻ��׳��쳣

            instancesTest.setClassIndex(instancesTest.numAttributes() - 1);                                                                           //���÷������������кţ���һ��Ϊ0�ţ���instancesTest.numAttributes()����ȡ����������

            double sum = instancesTest.numInstances(), right = 0.0f;           //��������ʵ����

            System.out.println("����������" + sum);

            /*
             * 3.���ݷ����㷨ѵ�����Ҳ���ÿ������
             */

            //�����ļ�

            WritableWorkbook book = Workbook.createWorkbook(new File(GetFilePath(sPath) +  "StatisResult.xls"));

            //������Ϊ����һҳ���Ĺ���������0��ʾ���ǵ�һҳ

            WritableSheet sheet = book.createSheet("��һҳ", 0);

            //��Label����Ĺ�������ָ����Ԫ��λ���ǵ�һ�е�һ��(0,0) �Լ���Ԫ������Ϊ�Ƿ������ʼ�

            Label label = new Label(0, 0, "�ʼ�");

            //������õĵ�Ԫ����ӵ���������

            sheet.addCell(label);


            //��ӷ����㷨��
            label = new Label(1, 0, "Bayes");
            sheet.addCell(label);
            label = new Label(2, 0, "�����ǲ��������ʼ�");
            sheet.addCell(label);
            right = 0;

            //Bayes�Ĳ�������

            right = 0;
            NaiveBayes bayes = new NaiveBayes();

            bayes.buildClassifier(instancesTrain);  // ѵ��������

              for (int i = 0; i < sum; i++) {

            	label = new Label(0,i+1,String.format("�ʼ�%s", i+1));
            	sheet.addCell(label);
            	  
                if (bayes.classifyInstance(instancesTest.instance(i)) == 1.0) {
                    label = new Label(1, i + 1, "YES");
                    sheet.addCell(label);
                } else {
                    label = new Label(1, i + 1, "NO");
                    sheet.addCell(label);
                }
                if (bayes.classifyInstance(instancesTest.instance(i)) == instancesTest.instance(i).classValue()) //���Ԥ��ֵ�ʹ�ֵ��ȣ����������еķ������ṩ����Ϊ��ȷ�𰸣�����������壩
                {
                    right++;
                }

            }
            jxl.write.Number number = new jxl.write.Number(1, (int)sum + 1, (right / sum));
            sheet.addCell(number);



             for (int i = 0; i < sum; i++) {

                if (instancesTest.instance(i).classValue() == 1.0) {
                    label = new Label(2, i + 1, "YES");
                    sheet.addCell(label);
                } else {
                    label = new Label(2, i + 1, "NO");
                    sheet.addCell(label);
                }

            }
            number = new jxl.write.Number(2, (int)sum + 1, 1);
            sheet.addCell(number);

            book.write();
            book.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	private String GetFilePath(String path){
		int pos = path.lastIndexOf("\\");
		if(pos!=-1){
			String res = path.substring(0,pos+1);
			return res;
		}
		return path;
	}
	
}
