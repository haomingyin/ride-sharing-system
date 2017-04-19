package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import models.position.StopPoint;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class MapController extends Controller implements Initializable {

	@FXML
	private WebView webView;

	private WebEngine webEngine;
	private Map<Integer, String> uniqueAddresses;

	@Override

	public void initialize(URL location, ResourceBundle resources) {
		webEngine = webView.getEngine();
		webEngine.load(getClass().getResource("/views/map.html").toExternalForm());
	}

	@Override
	protected void afterSetRSS() {

	}

	public void drawMarkers(List<StopPoint> stopPoints) {
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

	private void executeScript(String script) {
		try {
			webEngine.executeScript(script);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			try {
				Thread.sleep(3000);
				webEngine.executeScript(script);
			} catch (Exception e1) {
				System.out.println(e.getMessage());
			}
		}
	}
}
