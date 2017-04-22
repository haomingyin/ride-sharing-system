package controllers;

import javafx.scene.web.WebEngine;
import models.position.Position;
import models.position.StopPoint;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

class MapHandler {


	private WebEngine webEngine;
	private java.util.Map<Integer, String> uniqueAddresses;
	private StopPoint stopPoint;

	MapHandler(WebEngine webEngine) {
		this.webEngine = webEngine;
		this.webEngine.load(getClass().getResource("/views/map.html").toExternalForm());
	}

	void drawMarkers(List<StopPoint> stopPoints) {
		if (stopPoints == null) {
			executeScript("clearMarkers()");
		} else {
			uniqueAddresses = new HashMap<>();
			stopPoints.forEach(sp -> {
				if (!uniqueAddresses.containsKey(sp.getSpId())) {
					uniqueAddresses.put(sp.getSpId(), sp.getFull());
				}
			});

			JSONArray jsonArray = new JSONArray(uniqueAddresses.values());
			executeScript("drawMarkers(" + jsonArray.toString() + ")");
		}

	}

	void clearMarkers() {
		executeScript("clearMarkers()");
	}

	private void executeScript(String script) {
		try {
			webEngine.executeScript(script);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			try {
				Thread.sleep(2000);
				webEngine.executeScript(script);
			} catch (Exception e1) {
				System.out.println(e.getMessage());
			}
		}
	}

	Double getDistance(StopPoint stopPoint) {
		this.stopPoint = stopPoint;
		String request = buildRequest();

		try {

			// run for three times in case of bad connection
			for (int i = 0; i < 3; i++) {
				URL url = new URL(request);
				HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

				BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String input;
				while ((input = br.readLine()) != null) {
					sb.append(input);
				}
				br.close();

				JSONObject result = new JSONObject(sb.toString());

				if (result.getString("status").equals("OK")) {
					JSONObject row1 = (JSONObject) result.getJSONArray("rows").get(0);
					JSONObject element1 = (JSONObject) row1.getJSONArray("elements").get(0);
					Double distance = element1.getJSONObject("distance").getDouble("value");
					DecimalFormat df = new DecimalFormat(".#");
					df.setRoundingMode(RoundingMode.HALF_UP);
					return Double.valueOf(df.format(distance / 1000.0));
				}

				connection.disconnect();
			}


		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String buildRequest() {
		Position uni = Position.getUniPosition();
		String dest = String.format("destinations=%f,%f", uni.getLat(), uni.getLng());

		String origin = String.format("origins=%s", stopPoint.getFull().replaceAll("[^0-9a-zA-Z]+", "+"));

		String key = "key=AIzaSyAxayE0azS7XIrmDFel9dxnFycNlXehIBg";

		return String.format("https://maps.googleapis.com/maps/api/distancematrix/json?" +
				"language=en&%s&%s&%s", origin, dest, key);
	}
}
