package sklse.yongfeng.experiments;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import sklse.yongfeng.data.FilesSearcher;
import weka.classifiers.CostMatrix;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.meta.CostSensitiveClassifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.supervised.instance.Resample;
import weka.filters.supervised.instance.SMOTE;

/***
 * <p>The difference of <b>ImbalanceProcessingAve</b> and <b>ImbalanceProcessing</b> is that 
 * the former class get the average result from each 10 dataset for one project.</p>
 * @version To be uploaded
 */
public class ImbalanceProcessingAve {
	
	/** To save all 10 results in one project**/
	private static double[][] results = new double[10][7];
	
	/**short-cut name for projects*/
//	private static String[] projectNames = {"COD", "ORM", "JSQ", "COL", "IO", "JSO", "MAN"};

	public static void main(String[] args) throws Exception{
		String[] pathsCode = FilesSearcher.search("D:/Users/LEE/Desktop/New_Data/", "codec");
		String[] pathsORM = FilesSearcher.search("D:/Users/LEE/Desktop/New_Data/", "ormlite");
		String[] pathsJSQ = FilesSearcher.search("D:/Users/LEE/Desktop/New_Data/", "jsqlparser");
		String[] pathsCOL = FilesSearcher.search("D:/Users/LEE/Desktop/New_Data/", "collections");
		String[] pathsIO = FilesSearcher.search("D:/Users/LEE/Desktop/New_Data/", "io");
		String[] pathsJSO = FilesSearcher.search("D:/Users/LEE/Desktop/New_Data/", "jsoup");
		String[] pathsMAN = FilesSearcher.search("D:/Users/LEE/Desktop/New_Data/", "mango");
		
		List<String[]> dataCollection = new ArrayList<>(); // add all 7 path arrays into the list
		dataCollection.add(pathsCode);
		dataCollection.add(pathsORM);
		dataCollection.add(pathsJSQ);
		dataCollection.add(pathsCOL);
		dataCollection.add(pathsIO);
		dataCollection.add(pathsJSO);
		dataCollection.add(pathsMAN);	
		
		for(String[] dataset: dataCollection){ // for each project
			int index = 0;
			for(String arffs: dataset){ // each project has 10 arff files
//				getEvalResultbyNo(arffs, index); 
//				getEvalResultbySMOTE(arffs, index);
//				getEvalResultbyCost(arffs, index);
				getEvalResultbyResampling(arffs, index);
				index += 1;
			}
			double p0 = 0.0d, 
					   p1 = 0.0d, 
					   r0 = 0.0d, 
					   r1 = 0.0d,
					   f0 = 0.0d,
					   f1 = 0.0d,
					   acc = 0.0d;
			for(int m=0; m<10; m++){
				p0 += results[m][0];
				r0 += results[m][1];
				f0 += results[m][2];
				p1 += results[m][3];
				r1 += results[m][4];
				f1 += results[m][5];
				acc += results[m][6];
			}
			System.out.printf("%6.3f %6.3f %6.3f %6.3f %6.3f %6.3f %6.3f\n", 
					p0*1.0/10.0, r0*1.0/10.0, f0*1.0/10.0, p1*1.0/10.0, r1*1.0/10.0, f1*1.0/10.0, acc*1.0/10.0);

		}			

	}
	
	/***
	 * <p>To get 10-fold cross validation in one single arff in <b>path</b></p>
	 * <p>Use C4.5 and <b>SMOTE</b> to classify the dataset.</p>
	 * @param path dataset path
	 * @throws Exception
	 */
	public static void getEvalResultbySMOTE(String path, int index) throws Exception{
		
		Instances ins = DataSource.read(path);
		int numAttr = ins.numAttributes();
		ins.setClassIndex(numAttr - 1);
		
		SMOTE smote = new SMOTE();
		smote.setInputFormat(ins);
		
		/** classifiers setting*/
		J48 j48 = new J48();
//		j48.setConfidenceFactor(0.4f);
		j48.buildClassifier(ins);

		FilteredClassifier fc = new FilteredClassifier();
		fc.setClassifier(j48);
		fc.setFilter(smote);
			
		Evaluation eval = new Evaluation(ins);	
		eval.crossValidateModel(fc, ins, 10, new Random(1));
		
//		System.out.printf(" %4.3f %4.3f %4.3f", eval.precision(0), eval.recall(0), eval.fMeasure(0));
//		System.out.printf(" %4.3f %4.3f %4.3f", eval.precision(1), eval.recall(1), eval.fMeasure(1));
//		System.out.printf(" %4.3f \n\n", (1-eval.errorRate()));
		results[index][0] = eval.precision(0);
		results[index][1] = eval.recall(0);
		results[index][2] = eval.fMeasure(0);
		results[index][3] = eval.precision(1);
		results[index][4] = eval.recall(1);
		results[index][5] = eval.fMeasure(1);
		results[index][6] = 1-eval.errorRate();
				
	}
	
	/***
	 * <p>To get 10-fold cross validation in one single arff in <b>path</b></p>
	 * <p>Only use C4.5 to classify the dataset.</p>
	 * @param path dataset path
	 * @throws Exception
	 */
	public static void getEvalResultbyNo(String path, int index) throws Exception{
		
		Instances ins = DataSource.read(path);
		int numAttr = ins.numAttributes();
		ins.setClassIndex(numAttr - 1);
		
		/** classifiers setting*/
		J48 j48 = new J48();
//		j48.setConfidenceFactor(0.4f);
		j48.buildClassifier(ins);
		
		Evaluation eval = new Evaluation(ins);	
		eval.crossValidateModel(j48, ins, 10, new Random(1));
		
//		System.out.printf(" %4.3f %4.3f %4.3f", eval.precision(0), eval.recall(0), eval.fMeasure(0));
//		System.out.printf(" %4.3f %4.3f %4.3f", eval.precision(1), eval.recall(1), eval.fMeasure(1));
//		System.out.printf(" %4.3f \n\n", (1-eval.errorRate()));
		results[index][0] = eval.precision(0);
		results[index][1] = eval.recall(0);
		results[index][2] = eval.fMeasure(0);
		results[index][3] = eval.precision(1);
		results[index][4] = eval.recall(1);
		results[index][5] = eval.fMeasure(1);
		results[index][6] = 1-eval.errorRate();
			
	}
	
	/***
	 * <p>To get 10-fold cross validation in one single arff in <b>path</b></p>
	 * <p>Use C4.5 and <b>Resampling</b> to classify the dataset.</p>
	 * @param path dataset path
	 * @throws Exception
	 */
	public static void getEvalResultbyResampling(String path, int index) throws Exception{
		
		Instances ins = DataSource.read(path);
		int numAttr = ins.numAttributes();
		ins.setClassIndex(numAttr - 1);
		
		Resample resample = new Resample();
		resample.setInputFormat(ins);
		
		/** classifiers setting*/
		J48 j48 = new J48();
//		j48.setConfidenceFactor(0.4f);
		j48.buildClassifier(ins);

		FilteredClassifier fc = new FilteredClassifier();
		fc.setClassifier(j48);
		fc.setFilter(resample);
			
		Evaluation eval = new Evaluation(ins);	
		eval.crossValidateModel(fc, ins, 10, new Random(1));
		
//		System.out.printf(" %4.3f %4.3f %4.3f", eval.precision(0), eval.recall(0), eval.fMeasure(0));
//		System.out.printf(" %4.3f %4.3f %4.3f", eval.precision(1), eval.recall(1), eval.fMeasure(1));
//		System.out.printf(" %4.3f \n\n", (1-eval.errorRate()));
		results[index][0] = eval.precision(0);
		results[index][1] = eval.recall(0);
		results[index][2] = eval.fMeasure(0);
		results[index][3] = eval.precision(1);
		results[index][4] = eval.recall(1);
		results[index][5] = eval.fMeasure(1);
		results[index][6] = 1-eval.errorRate();
			
	}
	
	/***
	 * <p>To get 10-fold cross validation in one single arff in <b>path</b></p>
	 * <p>Use C4.5 and <b>Cost-sensitive learning</b> to classify the dataset.</p>
	 * @param path dataset path
	 * @throws Exception
	 */
	public static void getEvalResultbyCost(String path, int index) throws Exception{
		
		Instances ins = DataSource.read(path);
		int numAttr = ins.numAttributes();
		ins.setClassIndex(numAttr - 1);
		
		/**Classifier setting*/
		J48 j48 = new J48();
//		j48.setConfidenceFactor(0.4f);
		j48.buildClassifier(ins);
		
		CostSensitiveClassifier csc = new CostSensitiveClassifier();
		csc.setClassifier(j48);
		csc.setCostMatrix(new CostMatrix(new BufferedReader(new FileReader("files/costm"))));
		
		Evaluation eval = new Evaluation(ins);
		
		eval.crossValidateModel(csc, ins, 10, new Random(1));
		
//		System.out.printf(" %4.3f %4.3f %4.3f", eval.precision(0), eval.recall(0), eval.fMeasure(0));
//		System.out.printf(" %4.3f %4.3f %4.3f", eval.precision(1), eval.recall(1), eval.fMeasure(1));
//		System.out.printf(" %4.3f \n\n", (1-eval.errorRate()));
		results[index][0] = eval.precision(0);
		results[index][1] = eval.recall(0);
		results[index][2] = eval.fMeasure(0);
		results[index][3] = eval.precision(1);
		results[index][4] = eval.recall(1);
		results[index][5] = eval.fMeasure(1);
		results[index][6] = 1-eval.errorRate();
			
	}
	
}
