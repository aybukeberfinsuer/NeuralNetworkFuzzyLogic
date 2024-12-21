import org.neuroph.core.data.DataSet;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        scanner.useLocale(Locale.US);

        DataSet trainingSet = null;
        DataSet testSet = null;

        try {
            System.out.println("Veri seti yükleniyor...");
            DataSet[] data = DataLoader.loadAndSplitData("salary_data.csv", 0.75);
            trainingSet = data[0];
            testSet = data[1];
            System.out.println("Veri seti başarıyla yüklendi!");
        } catch (IOException e) {
            System.err.println("Veri yükleme hatası: " + e.getMessage());
            return;
        }

        NeuralNetworkProcess nnProcess = new NeuralNetworkProcess(trainingSet, testSet);
        nnProcess.findBestTopologies();
        nnProcess.plotResults();
        int[] bestMomentumTopology = nnProcess.getBestMomentumTopology();
        int[] bestNonMomentumTopology = nnProcess.getBestNonMomentumTopology();

        while (true) {
            try {
                displayMenu();
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        trainAndTestWithMomentum(nnProcess, bestMomentumTopology);
                        break;

                    case 2:
                        trainAndTestWithoutMomentum(nnProcess, bestNonMomentumTopology);
                        break;

                    case 3:
                        trainWithEpochDisplay(nnProcess, bestMomentumTopology);
                        break;

                    case 4:
                        trainAndSingleTest(nnProcess, scanner);
                        break;

                    case 5:
                        performKFoldCrossValidation(nnProcess, scanner);
                        break;

                    case 0:
                        System.out.println("Programdan çıkılıyor...");
                        scanner.close();
                        return;

                    default:
                        System.out.println("Geçersiz seçim, tekrar deneyin.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Hatalı giriş. Lütfen bir sayı girin.");
                scanner.nextLine(); // Giriş akışını temizle
            }
        }
    }

    private static void displayMenu() {
        System.out.println("\n==== Ana Menü ====");
        System.out.println("1- Agi Egit ve Test Et (Momentumlu)");
        System.out.println("2- Agi Egit ve Test Et (Momentumsuz)");
        System.out.println("3- Agi Egit Epoch Goster");
        System.out.println("4- Agi Egit ve Tekli Test (Momentumlu)");
        System.out.println("5- K-Fold Test ve Ortalama Hata Hesapla");
        System.out.println("0- Exit");
        System.out.print("Seciminizi yapin: ");
    }

    private static void trainAndTestWithMomentum(NeuralNetworkProcess nnProcess, int[] topology) {
        System.out.println("\nMomentumlu egitim ve test basliyor...");
        nnProcess.createNetwork(topology);
        nnProcess.trainNetwork(true, 0.3, 0.9, 500);
    }

    private static void trainAndTestWithoutMomentum(NeuralNetworkProcess nnProcess, int[] topology) {
        System.out.println("\nMomentumsuz egitim ve test basliyor...");
        nnProcess.createNetwork(topology);
        nnProcess.trainNetwork(false, 0.3, 0.0, 500);
    }

    private static void trainWithEpochDisplay(NeuralNetworkProcess nnProcess, int[] topology) {
        System.out.println("\nEpoch gösterimli egitim basliyor...");
        nnProcess.createNetwork(topology); // En iyi momentumlu topoloji ile başla
        nnProcess.trainWithEpochDisplay(true);
    }

    private static void trainAndSingleTest(NeuralNetworkProcess nnProcess, Scanner scanner) {
        System.out.println("\nAgi Egit ve Tekli Test (Momentumlu) secildi...");
        nnProcess.trainAndSingleTest(true, scanner);
    }

    private static void performKFoldCrossValidation(NeuralNetworkProcess nnProcess, Scanner scanner) {
        System.out.print("K degeri girin: ");
        int k = scanner.nextInt();

        if (k <= 1) {
            System.out.println("Hatalı K değeri. Lutfen 1'den buyuk bir deger girin.");
            return;
        }

        nnProcess.performKFoldCrossValidation(k);
    }
}
