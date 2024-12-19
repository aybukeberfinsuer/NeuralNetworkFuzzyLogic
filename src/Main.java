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
        System.out.println("1- Ağı Eğit ve Test Et (Momentumlu)");
        System.out.println("2- Ağı Eğit ve Test Et (Momentumsuz)");
        System.out.println("3- Ağı Eğit Epoch Göster");
        System.out.println("4- Ağı Eğit ve Tekli Test (Momentumlu)");
        System.out.println("5- K-Fold Test ve Ortalama Hata Hesapla");
        System.out.println("0- Çıkış");
        System.out.print("Seçiminizi yapın: ");
    }

    private static void trainAndTestWithMomentum(NeuralNetworkProcess nnProcess, int[] topology) {
        System.out.println("\nMomentumlu eğitim ve test başlıyor...");
        nnProcess.createNetwork(topology);
        nnProcess.trainNetwork(true, 0.3, 0.9, 500);
    }

    private static void trainAndTestWithoutMomentum(NeuralNetworkProcess nnProcess, int[] topology) {
        System.out.println("\nMomentumsuz eğitim ve test başlıyor...");
        nnProcess.createNetwork(topology);
        nnProcess.trainNetwork(false, 0.3, 0.0, 500);
    }

    private static void trainWithEpochDisplay(NeuralNetworkProcess nnProcess, int[] topology) {
        System.out.println("\nEpoch gösterimli eğitim başlıyor...");
        nnProcess.createNetwork(topology); // En iyi momentumlu topoloji ile başla
        nnProcess.trainWithEpochDisplay(true);
    }

    private static void trainAndSingleTest(NeuralNetworkProcess nnProcess, Scanner scanner) {
        System.out.println("\nAğı Eğit ve Tekli Test (Momentumlu) seçildi...");
        nnProcess.trainAndSingleTest(true, scanner);
    }

    private static void performKFoldCrossValidation(NeuralNetworkProcess nnProcess, Scanner scanner) {
        System.out.print("K değeri girin: ");
        int k = scanner.nextInt();

        if (k <= 1) {
            System.out.println("Hatalı K değeri. Lütfen 1'den büyük bir değer girin.");
            return;
        }

        nnProcess.performKFoldCrossValidation(k);
    }
}
