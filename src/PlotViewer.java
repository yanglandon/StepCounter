import Plot.PlotWindow;
import Plot.ScatterPlot;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class PlotViewer {
    private static final String TEST_FILE_FOLDER = "testFiles/blk3";

    public static void main(String[] args) {
        StepCount counter = new StepCount();  /* instantiate your step counter here */

        ArrayList<Path> paths = MainTester.getPaths(TEST_FILE_FOLDER);


        Path pathToPlot = paths.get(18);  // <-- file to plot

        System.out.println("Plotting data for: " + pathToPlot.toString());
        FileData data = processPath(pathToPlot);

        double optimalThresh = findOptimalThresholdMultiplier(paths.get(18));
        String[] lines = data.text.split("\n");
        ArrayList<Double> accX = counter.dataToLists(lines, 0);
        ArrayList<Double> accY = counter.dataToLists(lines, 1);
        ArrayList<Double> accZ = counter.dataToLists(lines, 2);
        ArrayList<Double> mags = counter.getMagnitudes(accX, accY, accZ);

        int prediction = getPeakYs(mags, optimalThresh).size();

        System.out.println("Your prediction: " + prediction + " steps.  Actual: " + data.correctNumberOfSteps + " steps");

        /* --------------- display plot ------------------------- */



        ArrayList<Double> peaks = getPeakYs(mags, optimalThresh);
        ArrayList<Integer> peakIndexes = getPeakXs(mags, peaks);

        System.out.println(peakIndexes.size());
        System.out.println(peaks.size());

        ScatterPlot plt = new ScatterPlot(100,100,1100, 700);

        for (int i = 0; i < mags.size(); i++) {
            plt.plot(0, i, mags.get(i)).strokeColor("red").strokeWeight(2).style("-");
        }

        for (int i = 0; i < peakIndexes.size(); i++) {
            plt.plot(1, peakIndexes.get(i), peaks.get(i)).strokeColor("blue").strokeWeight(5).style(".");
        }

        PlotWindow window = PlotWindow.getWindowFor(plt, 1200,800);
        window.show();
    }
    private static ArrayList<Integer> getPeakXs(ArrayList<Double> ttlAccMag, ArrayList<Double> peakYs) {
        ArrayList<Integer> indexes = new ArrayList<>();
        int counter = 0;
        System.out.println(peakYs.size());
        for (int i = 0; i < ttlAccMag.size(); i++) {
            if (peakYs.get(counter).equals(ttlAccMag.get(i))){
                counter++;
                indexes.add(i);
                if (counter >= peakYs.size()){
                    counter--;
                }
                System.out.println(counter);
            }
        }
        return indexes;
    }

    private static ArrayList<Double> getPeakYs(ArrayList<Double> ttlAccMag, double multiplier) {
        ArrayList<Double> peaks = new ArrayList<>();
        for (int i = 1; i < ttlAccMag.size() - 1; i++) {
            if (ttlAccMag.get(i) > ttlAccMag.get(i + 1) && ttlAccMag.get(i) > ttlAccMag.get(i - 1)) {

                    peaks.add(ttlAccMag.get(i));

            }
        }
        return peaks;
    }
    private static Double getThreshold(ArrayList<Double> ttlAccMag, double multiplier) {
        double maxVal = 0;
        for (double val: ttlAccMag) {
            if (maxVal < val){
                maxVal = val;
            }
        }
//        return Math.pow(maxVal, 0.7);
        return maxVal * multiplier;
    }


    private static double findOptimalThresholdMultiplier(Path path) {
        double optimalThreshold = 0.3, optimalError = 10000000;
        for (double i = 0.1; i < 0.7; i += 0.01) {
            StepCounter counter = new StepCount();
            double errors = 0;
            FileData data = processPath( path );
            int prediction = counter.countSteps(data.text, i);
            int error = data.correctNumberOfSteps - prediction;
            errors += (error*error);
            if (errors < optimalError){
                optimalThreshold = i;
                optimalError = errors;
            }
        }
        return optimalThreshold;
    }

    public static FileData processPath(Path path) {
        String filename = path.getFileName().toString();
        int numSteps = extractNumSteps(path);
        String text;

        if (numSteps == -1) {
            System.err.println("Couldn't get correct # of steps for file: " + path);
            return null;
        }

        try {
            text = readFile(path.toString());
        } catch (Exception e) {
            System.err.println("Error reading the file: " + path);
            return null;
        }

        return new FileData(text, path.toString(), numSteps);
    }
    private static int extractNumSteps(Path path) {
        String filename = path.getFileName().toString();
        filename = filename.replaceAll("[^\\d]","");
        int steps;
        try {
            steps = Integer.parseInt(filename.trim());
        } catch (Exception e) {
            System.err.println("Error extracting # of steps from filename: " + filename);
            return -1;
        }

        return steps;
    }
    public static String readFile(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }

}

