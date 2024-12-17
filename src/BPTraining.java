//import org.neuroph.core.Neuron;
//import org.neuroph.core.data.DataSet;
//import org.neuroph.core.data.DataSetRow;
//import org.neuroph.core.Layer;
//import org.neuroph.core.transfer.Linear;
//import org.neuroph.nnet.MultiLayerPerceptron;
//import org.neuroph.core.input.WeightedSum;
//import org.neuroph.util.NeuronProperties;
//
//
//
//
//public class BPTraining {
//    private static MultiLayerPerceptron network;
//
//    public static void trainWithMomentum(DataSet trainingSet, DataSet testSet) {
//        // Momentumlu ağ oluşturma
//        network = new MultiLayerPerceptron(3, 5, 1);  // Giriş 3, çıkış 1, ara katman 5 nöron
//
//        // BackPropagation ile Momentum ayarı
//        BackPropagation learningRule = new BackPropagation();
//        learningRule.setMomentum(0.9); // Momentum oranı
//        network.setLearningRule(learningRule); // BackPropagation algoritmasını set et
//
//        // Eğitim işlemi
//        network.learn(trainingSet);
//
//        // Test işlemi
//        double mseTraining = calculateMSE(network, trainingSet);
//        double mseTesting = calculateMSE(network, testSet);
//
//        System.out.println("Eğitim MSE: " + mseTraining);
//        System.out.println("Test MSE: " + mseTesting);
//    }
//
//    public static void trainWithoutMomentum(DataSet trainingSet, DataSet testSet) {
//        // Momentumsuz ağ oluşturma
//        network = new MultiLayerPerceptron(3, 5, 1);  // Giriş 3, çıkış 1, ara katman 5 nöron
//
//        // Momentumsuz BackPropagation ile eğitim
//        BackPropagation learningRule = new BackPropagation();
//        learningRule.setMomentum(0); // Momentum sıfır yapılır
//        network.setLearningRule(learningRule); // BackPropagation algoritmasını set et
//
//        // Eğitim işlemi
//        network.learn(trainingSet);
//
//        // Test işlemi
//        double mseTraining = calculateMSE(network, trainingSet);
//        double mseTesting = calculateMSE(network, testSet);
//
//        System.out.println("Eğitim MSE: " + mseTraining);
//        System.out.println("Test MSE: " + mseTesting);
//    }
//
//    private static double calculateMSE(MultiLayerPerceptron network, DataSet dataSet) {
//        double error = 0;
//        for (DataSetRow row : dataSet) {
//            network.setInput(row.getInput());
//            network.calculate();
//            double[] output = network.getOutput();
//            double[] expected = row.getDesiredOutput();
//
//            for (int i = 0; i < output.length; i++) {
//                error += Math.pow(expected[i] - output[i], 2);
//            }
//        }
//        return error / dataSet.size();
//    }
//}
