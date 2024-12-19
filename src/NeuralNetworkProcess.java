import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.JFrame;

public class NeuralNetworkProcess {
    private DataSet trainingSet;
    private DataSet testSet;
    private MultiLayerPerceptron neuralNet;
    
    private Map<int[], Double> momentumResults = new LinkedHashMap<>();
    private Map<int[], Double> nonMomentumResults = new LinkedHashMap<>();

    private int[] bestMomentumTopology;
    private int[] bestNonMomentumTopology;

    public NeuralNetworkProcess(DataSet trainingSet, DataSet testSet) {
        this.trainingSet = trainingSet;
        this.testSet = testSet;
    }

    public void createNetwork(int... topology) {
        neuralNet = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, topology);
        System.out.println("Sinir agi olusturuldu: Topoloji = " + Arrays.toString(topology));
    }

    public void trainNetwork(boolean useMomentum, double learningRate, double momentum, int maxIterations) {
        MomentumBackpropagation backPropagation = (MomentumBackpropagation) neuralNet.getLearningRule();
        backPropagation.setLearningRate(learningRate);
        backPropagation.setMomentum(useMomentum ? momentum : 0.0);
        backPropagation.setMaxIterations(maxIterations);

        try {
            System.out.println("Sinir agi egitiliyor...");
            neuralNet.learn(trainingSet);
            System.out.println("Egitim tamamlandi.");

            double trainingMSE = calculateMSE(trainingSet);
            System.out.println("Egitim MSE Hatasi: " + trainingMSE);

            double testMSE = calculateMSE(testSet);
            System.out.println("Test MSE Hatasi: " + testMSE);
        } catch (Exception e) {
            System.err.println("Egitim sirasinda hata olustu: " + e.getMessage());
        }
    }

    public void trainWithEpochDisplay(boolean useMomentum) {
        MomentumBackpropagation backPropagation = (MomentumBackpropagation) neuralNet.getLearningRule();
        backPropagation.setLearningRate(0.3);
        backPropagation.setMomentum(useMomentum ? 0.9 : 0.0);

        int maxEpochs = 200; // Toplam epoch sayisi
        backPropagation.setMaxIterations(1); // Her seferde bir epoch calistir

        System.out.println("Ag egitimi epoch bazinda baslatildi. Toplam Epoch Sayisi: " + maxEpochs);

        for (int epoch = 1; epoch <= maxEpochs; epoch++) {
            System.out.println("\nEpoch " + epoch + " calistiriliyor...");
            try {
                neuralNet.learn(trainingSet); // Tek bir epoch calistir

                // Egitim ve test hatalarini hesapla
                double trainingMSE = calculateMSE(trainingSet);
                double testMSE = calculateMSE(testSet);

                // Sonuclari ekrana yazdir
                System.out.println("Epoch " + epoch + " - Egitim MSE: " + trainingMSE + ", Test MSE: " + testMSE);
            } catch (Exception e) {
                System.err.println("Epoch sirasinda hata olustu: " + e.getMessage());
                break;
            }
        }

        System.out.println("\nEpoch bazinda egitim tamamlandi.");
    }

    public void findBestTopologies() {
        System.out.println("Farkli ag topolojileri deneniyor...");

        int[][] topologies = {
            {3, 4, 1}, {3, 6, 1}, {3, 8, 1}, {3, 4, 4, 1}, {3, 6, 6, 1},
            {3, 8, 8, 1}, {3, 5, 3, 1}, {3, 10, 1}, {3, 4, 3, 2, 1}, {3, 7, 5, 1}
        };

        double bestMomentumMSE = Double.MAX_VALUE;
        double bestNonMomentumMSE = Double.MAX_VALUE;

        for (int[] topology : topologies) {
            System.out.println("\nDeneme başlıyor: Topoloji = " + Arrays.toString(topology));

            // Momentumlu eğitim
            createNetwork(topology);
            trainNetwork(true, 0.3, 0.9, 500);
            double momentumTestMSE = calculateMSE(testSet);
            momentumResults.put(topology.clone(), momentumTestMSE);
            System.out.println("Momentumlu Test MSE: " + momentumTestMSE);

            if (momentumTestMSE < bestMomentumMSE) {
                bestMomentumMSE = momentumTestMSE;
                bestMomentumTopology = topology;
            }

            // Momentumsuz eğitim
            createNetwork(topology);
            trainNetwork(false, 0.3, 0.0, 500);
            double nonMomentumTestMSE = calculateMSE(testSet);
            nonMomentumResults.put(topology.clone(), nonMomentumTestMSE);
            System.out.println("Momentumsuz Test MSE: " + nonMomentumTestMSE);

            if (nonMomentumTestMSE < bestNonMomentumMSE) {
                bestNonMomentumMSE = nonMomentumTestMSE;
                bestNonMomentumTopology = topology;
            }
        }
        


        System.out.println("\nEn iyi momentumlu topoloji: " + Arrays.toString(bestMomentumTopology));
        System.out.println("Momentumlu en iyi Test MSE: " + bestMomentumMSE);

        System.out.println("\nEn iyi momentumsuz topoloji: " + Arrays.toString(bestNonMomentumTopology));
        System.out.println("Momentumsuz en iyi Test MSE: " + bestNonMomentumMSE);
    }
    public void plotResults() {
	    DefaultCategoryDataset dataset = new DefaultCategoryDataset();

	    for (Map.Entry<int[], Double> entry : momentumResults.entrySet()) {
	        String topology = Arrays.toString(entry.getKey());
	        dataset.addValue(entry.getValue(), "Momentumlu", topology);
	    }

	    for (Map.Entry<int[], Double> entry : nonMomentumResults.entrySet()) {
	        String topology = Arrays.toString(entry.getKey());
	        dataset.addValue(entry.getValue(), "Momentumsuz", topology);
	    }

	    JFreeChart chart = ChartFactory.createLineChart(
	        "Topoloji - Hata Grafiği",
	        "Topoloji",
	        "Hata (MSE)",
	        dataset
	    );

	    JFrame frame = new JFrame("Grafik");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.add(new ChartPanel(chart));
	    frame.pack();
	    frame.setVisible(true);
	}

    public int[] getBestMomentumTopology() {
        return bestMomentumTopology;
    }

    public int[] getBestNonMomentumTopology() {
        return bestNonMomentumTopology;
    }

    public void trainAndSingleTest(boolean useMomentum, Scanner scanner) {
        System.out.println("Momentumlu Backpropagation ile egitim basliyor...");
        createNetwork(bestMomentumTopology);
        MomentumBackpropagation backPropagation = (MomentumBackpropagation) neuralNet.getLearningRule();
        backPropagation.setLearningRate(0.3);
        backPropagation.setMomentum(useMomentum ? 0.9 : 0.0);
        backPropagation.setMaxIterations(500);

        try {
            neuralNet.learn(trainingSet); // Egitim
            System.out.println("Egitim tamamlandi.");
        } catch (Exception e) {
            System.err.println("Egitim sirasinda hata olustu: " + e.getMessage());
            return;
        }

        System.out.println("Egitim tamamlandi. Tek bir girdi icin tahmin yapilacak.");
        System.out.print(
            "Lutfen " + bestMomentumTopology[0] + " adet giris degerini virgulle ayirarak girin.\n" +
            "1. Deger: Deneyim yili (0-25 arasinda bir deger girin)\n" +
            "2. Deger: Cinsiyet (0: Kadin, 1: Erkek)\n" +
            "3. Deger: Egitim yili (0-20 arasinda bir deger girin)\n"
        );

        scanner.nextLine();
        String inputLine = scanner.nextLine();
        String[] inputsStr = inputLine.split(",");
        double[] inputs = new double[inputsStr.length];

        if (inputs.length != bestMomentumTopology[0]) {
            System.out.println("Hatali giris sayisi. Beklenen giris sayisi: " + bestMomentumTopology[0]);
            return;
        }

        try {
            for (int i = 0; i < inputs.length; i++) {
                inputs[i] = Double.parseDouble(inputsStr[i]);
            }
        } catch (NumberFormatException e) {
            System.err.println("Hatali giris formati: " + e.getMessage());
            return;
        }

        neuralNet.setInput(inputs);
        neuralNet.calculate();
        double[] output = neuralNet.getOutput();

        System.out.println("Verilen giris icin tahmin edilen cikti: " + Arrays.toString(output));
    }

    public void performKFoldCrossValidation(int k) {
        if (k <= 1 || k > trainingSet.size()) {
            System.out.println("K degeri uygun degil. Veri boyutundan kucuk ve 1'den buyuk olmali.");
            return;
        }

        System.out.println("K-Fold Cross Validation Basladi. K = " + k);

        int foldSize = trainingSet.size() / k;
        double totalTrainMSE = 0.0;
        double totalTestMSE = 0.0;

        for (int i = 0; i < k; i++) {
            System.out.println("\nFold " + (i + 1) + " basliyor...");

            DataSet trainFold = new DataSet(trainingSet.getInputSize(), trainingSet.getOutputSize());
            DataSet testFold = new DataSet(trainingSet.getInputSize(), trainingSet.getOutputSize());

            for (int j = 0; j < trainingSet.size(); j++) {
                if (j >= i * foldSize && j < (i + 1) * foldSize) {
                    testFold.addRow(trainingSet.getRowAt(j));
                } else {
                    trainFold.addRow(trainingSet.getRowAt(j));
                }
            }

            createNetwork(bestMomentumTopology);
            try {
                neuralNet.learn(trainFold);
            } catch (Exception e) {
                System.err.println("Egitim sirasinda hata olustu: " + e.getMessage());
            }

            double trainMSE = calculateMSE(trainFold);
            double testMSE = calculateMSE(testFold);
            System.out.println("Fold " + (i + 1) + " - Egitim MSE: " + trainMSE + ", Test MSE: " + testMSE);

            totalTrainMSE += trainMSE;
            totalTestMSE += testMSE;
        }

        double avgTrainMSE = totalTrainMSE / k;
        double avgTestMSE = totalTestMSE / k;

        System.out.println("\nK-Fold Cross Validation Tamamlandi.");
        System.out.println("Ortalama Egitim MSE: " + avgTrainMSE);
        System.out.println("Ortalama Test MSE: " + avgTestMSE);
    }

    private double calculateMSE(DataSet dataSet) {
        double sum = 0.0;
        for (DataSetRow row : dataSet.getRows()) {
            neuralNet.setInput(row.getInput());
            neuralNet.calculate();
            double[] output = neuralNet.getOutput();
            double[] target = row.getDesiredOutput();
            for (int i = 0; i < output.length; i++) {
                sum += Math.pow(output[i] - target[i], 2);
            }
        }
        return sum / dataSet.size();
    }
}
