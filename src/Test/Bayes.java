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

            instancesTrain = atf.getDataSet();             //�����ļ�haiha

            //��ʹ������֮ǰһ��Ҫ��������instances��classIndex��������ʹ��instances�����ǻ��׳��쳣
            instancesTrain.setClassIndex(instancesTrain.numAttributes() - 1);

            inputFile = new File(sPath2); // ���������ļ�
            atf.setFile(inputFile);
            instancesTest = atf.getDataSet();    //��������ļ�
            //��ʹ������֮ǰһ��Ҫ��������instances��classIndex��������ʹ��instances�����ǻ��׳��쳣

            instancesTest.setClassIndex(instancesTest.numAttributes() - 1);                                                                           //���÷������������кţ���һ��Ϊ0�ţ���instancesTest.numAttributes()����ȡ����������

            double sum = instancesTest.numInstances(), right = 0.0f;           //��������ʵ����


            /*
             * 3.���ݷ����㷨ѵ�����Ҳ���ÿ������
             */

            //�����ļ�

            WritableWorkbook book = Workbook.createWorkbook(new File("StatisResult.xls"));

            //������Ϊ����һҳ���Ĺ���������0��ʾ���ǵ�һҳ

            WritableSheet sheet = book.createSheet("��һҳ", 0);

            //��Label����Ĺ�������ָ����Ԫ��λ���ǵ�һ�е�һ��(0,0) �Լ���Ԫ������Ϊ�Ƿ������ʼ�

            Label label = new Label(0, 0, "�Ƿ������ʼ�");

            //������õĵ�Ԫ����ӵ���������

            sheet.addCell(label);


            //��ӷ����㷨��
            label = new Label(3, 0, "Bayes");
            sheet.addCell(label);
            label = new Label(4, 0, "�����ǲ��������ʼ�");
            sheet.addCell(label);
            //j48�Ĳ�������
            right = 0;
            /*
             * 2.��ʼ�������㷨��Classify method)
             */

            //Bayes�Ĳ�������

            right = 0;
            NaiveBayes bayes = new NaiveBayes();

            bayes.buildClassifier(instancesTrain);

              for (int i = 0; i < sum; i++) {

                if (bayes.classifyInstance(instancesTest.instance(i)) == 1.0) {
                    label = new Label(3, i + 1, "YES");
                    sheet.addCell(label);
                } else {
                    label = new Label(3, i + 1, "NO");
                    sheet.addCell(label);
                }
                if (bayes.classifyInstance(instancesTest.instance(i)) == instancesTest.instance(i).classValue()) //���Ԥ��ֵ�ʹ�ֵ��ȣ����������еķ������ṩ����Ϊ��ȷ�𰸣�����������壩
                {
                    right++;
                }

            }
              jxl.write.Number number = new jxl.write.Number(3, (int)sum + 1, (right / sum));
            sheet.addCell(number);



             for (int i = 0; i < sum; i++) {

                if (instancesTest.instance(i).classValue() == 1.0) {
                    label = new Label(4, i + 1, "YES");
                    sheet.addCell(label);
                } else {
                    label = new Label(4, i + 1, "NO");
                    sheet.addCell(label);
                }

            }
            number = new jxl.write.Number(4, (int)sum + 1, 1);
            sheet.addCell(number);

            book.write();
            book.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
}
