# StepCounter
- Using phone accelerometer data, this program predicts the number of steps that were taken.
- The magnitude at each index is calculated by using the distance formula on its x, y, and z values.
- If a magnitude array is bigger than it's two neighbors, and larger than the threshold value, then it is counted as a peak.
  - The threshold value is calculated by the highest magnitude of that set times a multiplier.
  - This multiplier is chosen by findOptimalThreshold, which loops through multiplier values from 0.3 to 0.7, and returns the multiplier that yields the least mean squared error for every data set.
- The program then runs and predicts the number of steps using this optimal threshold multiplier.