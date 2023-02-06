import java.util.ArrayList;

public class StepCount implements StepCounter {

    private static int getSteps(ArrayList<Double> xAcc, ArrayList<Double> yAcc, ArrayList<Double> zAcc, double multiplier) {
        ArrayList<Double> accMags = getMagnitudes(xAcc, yAcc, zAcc);
        ArrayList<Double> peakYs = getPeakYs(accMags, multiplier);
        return peakYs.size();
    }

    private static int processData(String[] lines, double multiplier) {
        ArrayList<Double> xAcc = dataToLists(lines, 0);
        ArrayList<Double> yAcc = dataToLists(lines, 1);
        ArrayList<Double> zAcc = dataToLists(lines, 2);
        return getSteps(xAcc, yAcc, zAcc, multiplier);
    }

    private static ArrayList<Double> getPeakYs(ArrayList<Double> ttlAccMag, double multiplier) {
        ArrayList<Double> peaks = new ArrayList<>();
        for (int i = 1; i < ttlAccMag.size() - 1; i++) {
            if (ttlAccMag.get(i) > ttlAccMag.get(i + 1) && ttlAccMag.get(i) > ttlAccMag.get(i - 1)) {
                if (ttlAccMag.get(i) > getThreshold(ttlAccMag, multiplier)) {
                    peaks.add(ttlAccMag.get(i));
                }
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
        return maxVal * multiplier;
    }

    public static ArrayList<Double> getMagnitudes(ArrayList<Double> xAcc, ArrayList<Double> yAcc, ArrayList<Double> zAcc) {
        ArrayList<Double> magnitudes = new ArrayList<>();
        for (int i = 0; i < xAcc.size(); i++) {
            double currXAcc = xAcc.get(i);
            double currYAcc = yAcc.get(i);
            double currZAcc = zAcc.get(i);
            magnitudes.add(Math.sqrt(Math.pow(currXAcc, 2) + Math.pow(currYAcc, 2) + Math.pow(currZAcc, 2)));
        }
        return magnitudes;
    }

    public static ArrayList<Double> dataToLists(String[] lines, int col) {
        ArrayList<Double> list = new ArrayList<>();
        for (int line = 1; line < lines.length; line++) {
            list.add(getVal(lines[line], col));
        }
        return list;
    }

    private static Double getVal(String line, int col) {
        String[] splitLine = line.split(",");
        return Double.parseDouble(splitLine[col]);
    }

    @Override
    public int countSteps(ArrayList<Double> xAcc, ArrayList<Double> yAcc, ArrayList<Double> zAcc, ArrayList<Double> xGyro, ArrayList<Double> yGyro, ArrayList<Double> zGyro, double multiplier) {
        return getSteps(xAcc, yAcc, zAcc, multiplier);
    }

    @Override
    public int countSteps(String csvFileText, double multiplier) {
        return processData(csvFileText.split("\n"), multiplier);
    }

}
