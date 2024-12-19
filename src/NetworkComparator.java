import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.nnet.*;
import org.neuroph.util.TransferFunctionType;

import java.util.ArrayList;
import java.util.List;

public class NetworkComparator {
    private final List<NeuralNetwork<?>> networks = new ArrayList<>();
    private final List<String> networkNames = new ArrayList<>();
    private final DataSet trainingSet;
    private final DataSet testSet;

    private NeuralNetwork<?> bestNetwork;
    private String bestNetworkName;
    private double bestNetworkMSE = Double.MAX_VALUE;

    public NetworkComparator(DataSet trainingSet, DataSet testSet) {
        this.trainingSet = trainingSet;
        this.testSet = testSet;
        initializeNetworks();
    }

    private void initializeNetworks() {

    	// Add different networks to the list
    	networks.add(new Adaline(3));
    	networkNames.add("Adaline");

    	networks.add(new BAM(3, 1));
    	networkNames.add("BAM");

    	int hiddenLayer = 5;
    	int contextLayer = hiddenLayer;
    	networks.add(new ElmanNetwork(trainingSet.getInputSize(), hiddenLayer - 1, contextLayer, trainingSet.getOutputSize()));
    	networkNames.add("ElmanNetwork");

    	networks.add(new JordanNetwork(3, 5, 5, 1));
    	networkNames.add("JordanNetwork");

    	networks.add(new Kohonen(3, 1));
    	networkNames.add("Kohonen");

    	networks.add(new MultiLayerPerceptron(TransferFunctionType.SIGMOID, 3, 5, 1));
    	networkNames.add("MultiLayerPerceptron");

    	networks.add(new Perceptron(3, 1));
    	networkNames.add("Perceptron");

    	networks.add(new RBFNetwork(3, 5, 1));
    	networkNames.add("RBFNetwork");

    	networks.add(new UnsupervisedHebbianNetwork(3, 1));
    	networkNames.add("UnsupervisedHebbianNetwork");

    	networks.add(new SupervisedHebbianNetwork(3, 1));
    	networkNames.add("SupervisedHebbianNetwork");

    }

    public void findBestNetwork() {
        for (int i = 0; i < networks.size(); i++) {
            NeuralNetwork<?> network = networks.get(i);
            String networkName = networkNames.get(i);

            System.out.println("Evaluating network: " + networkName);
            network.learn(trainingSet);
            double mse = calculateMSE(network);

            System.out.println("MSE for " + networkName + ": " + mse);

            if (mse < bestNetworkMSE) {
                bestNetworkMSE = mse;
                bestNetwork = network;
                bestNetworkName = networkName;
            }
        }

        System.out.println("Best network: " + bestNetworkName + " with MSE: " + bestNetworkMSE);
    }

    public NeuralNetwork<?> getBestNetwork() {
        return bestNetwork;
    }

    public String getBestNetworkName() {
        return bestNetworkName;
    }

    private double calculateMSE(NeuralNetwork<?> network) {
        double sum = 0.0;
        for (var row : testSet.getRows()) {
            network.setInput(row.getInput());
            network.calculate();
            double[] output = network.getOutput();
            double[] target = row.getDesiredOutput();
            for (int i = 0; i < output.length; i++) {
                sum += Math.pow(output[i] - target[i], 2);
            }
        }
        return sum / testSet.size();
    }
}
