import java.util.Scanner;

import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;


public class NeuralNetworkProcess {
    private DataSet trainingSet;  // Eğitim verisi
    private DataSet testSet;      // Test verisi
    private MultiLayerPerceptron neuralNet;  // Sinir ağı nesnesi

    public NeuralNetworkProcess(DataSet trainingSet, DataSet testSet) {
        this.trainingSet = trainingSet;
        this.testSet = testSet;
    }

    // Sinir ağını oluştur
    public void createNetwork(boolean useMomentum) {
        // MultiLayerPerceptron ağını oluştur
        neuralNet = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, 3, 4, 1);

        // MomentumBackpropagation öğrenme kuralını al
        MomentumBackpropagation backPropagation = (MomentumBackpropagation) neuralNet.getLearningRule();

        // Momentumlu BP için parametre ayarları
        if (useMomentum) {
            backPropagation.setLearningRate(0.3); // Öğrenme oranı
            backPropagation.setMomentum(0.9); // Momentum değeri
            backPropagation.setMaxIterations(500); // İterasyon sayısı
        } else {
            // Momentumsuz BP için parametre ayarları
            backPropagation.setLearningRate(0.3); // Öğrenme oranı
            backPropagation.setMomentum(0.0); // Momentum sıfırlanır
            backPropagation.setMaxIterations(500); // İterasyon sayısı
        }

        System.out.println("Sinir agi basariyla olusturuldu.");
    }
    public void trainNetworkWithUserParameters(Scanner scanner) {
        // Kullanıcıdan parametreleri al
        System.out.print("Ogrenme Orani (learningRate): ");
        double learningRate = scanner.nextDouble();

        System.out.print("Momentum Degeri: ");
        double momentum = scanner.nextDouble();

        System.out.print("Maksimum Iterasyon Sayisi: ");
        int maxIterations = scanner.nextInt();

        // Sinir ağını oluştur
        neuralNet = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, 3, 4, 1);

        // MomentumBackpropagation öğrenme kuralı ayarları
        MomentumBackpropagation backPropagation = (MomentumBackpropagation) neuralNet.getLearningRule();
        backPropagation.setLearningRate(learningRate);
        backPropagation.setMomentum(momentum);
        backPropagation.setMaxIterations(maxIterations);

        try {
            System.out.println("\nSinir agi belirtilen parametrelerle egitiliyor...");
            neuralNet.learn(trainingSet); // Eğitim

            System.out.println("Egitim tamamlandi.");

            // Eğitim ve test hatalarını hesapla
            double trainingMSE = calculateMSE(trainingSet);
            System.out.println("Egitim MSE Hatasi: " + trainingMSE);

            double testMSE = calculateMSE(testSet);
            System.out.println("Test MSE Hatasi: " + testMSE);

        } catch (Exception e) {
            System.err.println("Egitim sirasinda hata olustu: " + e.getMessage());
        }
    }
    
      // K-Fold Cross Validation
    public void kFoldCrossValidation(Scanner scanner) {
        System.out.print("K degerini girin: ");
        int k = scanner.nextInt();

        if (k <= 1 || k > trainingSet.size()) {
            System.out.println("K degeri uygun degil. Veri boyutundan kucuk ve 1'den buyuk olmali.");
            return;
        }

        System.out.println("K-Fold Cross Validation Basladi. K = " + k);

        // Fold boyutunu hesapla
        int foldSize = trainingSet.size() / k;
        double totalTrainMSE = 0.0;
        double totalTestMSE = 0.0;

        // K iterasyonluk eğitim ve test işlemi
        for (int i = 0; i < k; i++) {
            System.out.println("\nFold " + (i + 1) + " basliyor...");

            // Veri setlerini oluştur
            DataSet trainFold = new DataSet(trainingSet.getInputSize(), trainingSet.getOutputSize());
            DataSet testFold = new DataSet(trainingSet.getInputSize(), trainingSet.getOutputSize());

            for (int j = 0; j < trainingSet.size(); j++) {
                if (j >= i * foldSize && j < (i + 1) * foldSize) {
                    testFold.addRow(trainingSet.getRowAt(j));
                } else {
                    trainFold.addRow(trainingSet.getRowAt(j));
                }
            }

            // Ağ oluştur ve eğit
            createNetwork(true); // Momentumlu eğitim
            try {
                neuralNet.learn(trainFold);
            } catch (Exception e) {
                System.err.println("Egitim hatasi: " + e.getMessage());
            }

            // Fold'a ait MSE hesaplama
            double trainMSE = calculateMSE(trainFold);
            double testMSE = calculateMSE(testFold);
            System.out.println("Fold " + (i + 1) + " - Egitim MSE: " + trainMSE + ", Test MSE: " + testMSE);

            totalTrainMSE += trainMSE;
            totalTestMSE += testMSE;
        }

        // Ortalama MSE'leri hesapla
        double avgTrainMSE = totalTrainMSE / k;
        double avgTestMSE = totalTestMSE / k;
        System.out.println("\nK-Fold Cross Validation Tamamlandi.");
        System.out.println("Ortalama Egitim MSE: " + avgTrainMSE);
        System.out.println("Ortalama Test MSE: " + avgTestMSE);
    }
 
    // Sinir ağını eğitim verisiyle eğit
    public void trainNetwork(boolean useMomentum) {
        createNetwork(useMomentum);

        try {
            // Eğitim işlemi
            neuralNet.learn(trainingSet);
            System.out.println("Sinir agi egitimi tamamlandi.");

            // Eğitim hatası (MSE)
            double trainingMSE = calculateMSE(trainingSet);
            System.out.println("Egitim MSE Hatasi: " + trainingMSE);

            // Test işlemi
            double testMSE = testNetwork();
            System.out.println("Test MSE Hatasi: " + testMSE);

        } catch (Exception e) {
            System.err.println("Egitim sirasinda hata olustu: " + e.getMessage());
        }
    }

    // Sinir ağı testi (Test verisi ile)
    private double testNetwork() {
        return calculateMSE(testSet);
    }

    // MSE hesaplama
 // MSE hesaplama
    private double calculateMSE(DataSet dataSet) {
        double mse = 0.0;
        int numRows = dataSet.size();

        for (DataSetRow row : dataSet.getRows()) {
            neuralNet.setInput(row.getInput());
            neuralNet.calculate();
            
            double[] output = neuralNet.getOutput();
            double[] desiredOutput = row.getDesiredOutput();

            // Her output için kare farkını hesapla ve MSE'yi biriktir
            for (int i = 0; i < output.length; i++) {
                mse += Math.pow(desiredOutput[i] - output[i], 2);
            }
        }

        // Ortalama MSE'yi döndür
        return mse / numRows;
    }

}
