package algo.weatherdata;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
 * Retrieves temperature data from a weather station file.
 */
public class WeatherDataHandler {

	private static class WeatherRecord {
		private final String quality;
		private final double temperature;

		public WeatherRecord(LocalDate date, LocalTime time, double temperature, String quality) {
			this.quality = quality;
			this.temperature = temperature;
		}

		public double getTemperature() {
			return temperature;
		}

		public String getQuality() {
			return quality;
		}
	}
	private final Map<LocalDate, List<WeatherRecord>> dataMap;
	public WeatherDataHandler(){
		dataMap = new HashMap<>();
	}
	/**
	 * Load weather data from file.
	 *
	 * @param filePath path to file with weather data
	 * @throws IOException if there is a problem while reading the file
	 */
	public void loadData(String filePath) throws IOException {
		List<String> fileData = Files.readAllLines(Paths.get(filePath));

		//TODO: Structure data and put it in appropriate data structure
		for (String line : fileData){
			String[] parts = line.split(";");
			LocalDate date = LocalDate.parse(parts[0]);
			LocalTime time = LocalTime.parse(parts[1]);
			double temperature = Double.parseDouble(parts[2]);
			String quality = parts[3];
			WeatherRecord record = new WeatherRecord(date, time, temperature, quality);

			// If the date already exists in the map, add the record to the existing list
			// Otherwise, create a new list and add the record to it
			dataMap.computeIfAbsent(date, k -> new ArrayList<>()).add(record);
		}
	}
	/**
	 * Search for average temperature for all dates between the two dates (inclusive).
	 * Result is sorted by date (ascending). When searching from 2000-01-01 to 2000-01-03
	 * the result should be:
	 * 2000-01-01 average temperature: 0.42 degrees Celsius
	 * 2000-01-02 average temperature: 2.26 degrees Celsius
	 * 2000-01-03 average temperature: 2.78 degrees Celsius
	 *
	 * @param dateFrom start date (YYYY-MM-DD) inclusive
	 * @param dateTo end date (YYYY-MM-DD) inclusive
	 * @return average temperature for each date, sorted by date
	 */
	public List<String> averageTemperatures(LocalDate dateFrom, LocalDate dateTo) {
		//TODO: Implements method
		List<String> result = new ArrayList<>();

		for (LocalDate date = dateFrom; !date.isAfter(dateTo); date = date.plusDays(1)) {
			List<WeatherRecord> records = dataMap.get(date);
			if (records == null) {
				result.add(date + " No date available");
				continue;
			}

			double sum = 0;
			int count = 0;
			for (WeatherRecord record : records) {
				sum += record.getTemperature();
				count++;
			}

			double average = sum / count;
			result.add(date + " average temperature: " + average + " degrees Celsius");
		}
		return result;
	}
	/**
	 * Search for missing values between the two dates (inclusive) assuming there
	 * should be 24 measurement values for each day (once every hour). Result is
	 * sorted by number of missing values (descending). When searching from
	 * 2000-01-01 to 2000-01-03 the result should be:
	 * 2000-01-02 missing 1 values
	 * 2000-01-03 missing 1 values
	 * 2000-01-01 missing 0 values
	 *
	 * @param dateFrom start date (YYYY-MM-DD) inclusive
	 * @param dateTo end date (YYYY-MM-DD) inclusive
	 * @return dates with missing values together with number of missing values for each date, sorted by number of missing values (descending)
	 */
	public List<String> missingValues(LocalDate dateFrom, LocalDate dateTo) {
		//TODO: Implements method
		List<String> result = new ArrayList<>();

		for (LocalDate date = dateFrom; !date.isAfter(dateTo); date = date.plusDays(1)){
			List<WeatherRecord> records = dataMap.get(date);
			if (records == null) {
				result.add(date + " No data available");
				continue;
			}

			int missingValues = 24 - records.size();
			result.add(date + " missing " + missingValues + " values");
		}
		result.sort((a, b) -> {
			int missingValuesA = Integer.parseInt(a.split(" ")[2]);
			int missingValuesB = Integer.parseInt(b.split(" ")[2]);
			return Integer.compare(missingValuesB, missingValuesA);
		});

		return result;
	}
	/**
	 * Search for percentage of approved values between the two dates (inclusive).
	 * When searching from 2000-01-01 to 2000-01-03 the result should be:
	 * Approved values between 2000-01-01 and 2000-01-03: 32.86 %
	 *
	 * @param dateFrom start date (YYYY-MM-DD) inclusive
	 * @param dateTo end date (YYYY-MM-DD) inclusive
	 * @return period and percentage of approved values for the period
	 */
	public List<String> approvedValues(LocalDate dateFrom, LocalDate dateTo) {
		//TODO: Implements method
		List<String> result = new ArrayList<>();
		 int totalValues = 0;
		 int approvedValues = 0;

		 for (LocalDate date = dateFrom; !date.isAfter(dateTo); date = date.plusDays(1)){
			 List<WeatherRecord> records = dataMap.get(date);
			 if (records == null) {
				 continue;
			 }

			 for (WeatherRecord record : records) {
				 totalValues++;
				 if (record.getQuality().equals("G")){
					 approvedValues++;
				 }
			 }
		 }

		 double percentage = (double) approvedValues / totalValues * 100;
		 result.add("Approved values between " + dateFrom + " and " + dateTo + ": " + String.format("%.2f", percentage) + " %");

		return result;
	}

}
