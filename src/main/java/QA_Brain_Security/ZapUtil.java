package QA_Brain_Security;

import java.util.List;
import java.util.stream.Collectors;
import org.zaproxy.clientapi.core.ApiResponseList; // Import ApiResponseList

import org.openqa.selenium.Proxy;
import org.zaproxy.clientapi.core.ApiResponse;
import org.zaproxy.clientapi.core.ApiResponseElement;
import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;

public class ZapUtil {
	private static ClientApi clientApi;
	public static Proxy proxy;
	private static ApiResponse apiResponse;

	private static final String zapAddress = "127.0.0.1";
	private static final int zapPort = 8080;
	private static final String apiKey = "2hgb9u8crdjfu977mmud8229ha"; // Please add your own api key from ZAP

	static {
		clientApi = new ClientApi(zapAddress, zapPort, apiKey);
		proxy = new Proxy().setSslProxy(zapAddress + ":" + zapPort).setHttpProxy(zapAddress + ":" + zapPort);
	}

	public static void waitTillPassiveScanCompleted() {
		try {
			apiResponse = clientApi.pscan.recordsToScan();
			String tempVal = ((ApiResponseElement) apiResponse).getValue();
			while (!tempVal.equals("0")) {
				System.out.println("passive scan is in progress");
				apiResponse = clientApi.pscan.recordsToScan();
				tempVal = ((ApiResponseElement) apiResponse).getValue();
			}
			System.out.println("passive scan is completed");
		} catch (ClientApiException e) {
			e.printStackTrace();
		}
	}

	public static void addURLToScanTree(String site_to_test) throws ClientApiException {
		clientApi.core.accessUrl(site_to_test, "false");
		if (getUrlsFromScanTree().contains(site_to_test))
			System.out.println(site_to_test + " has been added to scan tree");
		else
			throw new RuntimeException(site_to_test + " not added to scan tree, active scan will not be possible");
	}

	public static List<String> getUrlsFromScanTree() throws ClientApiException {
		apiResponse = clientApi.core.urls();
		List<ApiResponse> responses = ((ApiResponseList) apiResponse).getItems();
		return responses.stream().map(r -> ((ApiResponseElement) r).getValue()).collect(Collectors.toList());
	}

	public static void performActiveScan(String site_to_test) throws ClientApiException {
		String url = site_to_test;
		String recurse = null;
		String inscopeonly = null;
		String scanpolicyname = null;
		String method = null;
		String postdata = null;
		Integer contextId = null;
		apiResponse = clientApi.ascan.scan(url, recurse, inscopeonly, scanpolicyname, method, postdata, contextId);
		String scanId = ((ApiResponseElement) apiResponse).getValue();

		waitTillActiveScanIsCompleted(scanId);
	}

	private static void waitTillActiveScanIsCompleted(String scanId) throws ClientApiException {
		apiResponse = clientApi.ascan.status(scanId);
		String status = ((ApiResponseElement) apiResponse).getValue();

		while (!status.equals("100")) {
			apiResponse = clientApi.ascan.status(scanId);
			status = ((ApiResponseElement) apiResponse).getValue();
			System.out.println("Active scan is in progress");
		}

		System.out.println("Active scan has completed");
	}

	public static void generateZapReport(String site_to_test) {
		String title = "Demo Title";
		String sites = site_to_test;
		String description = "Demo description";

		String template = "traditional-html-plus";
		String sections = "chart|alertcount|passingrules|instancecount|statistics|alertdetails";
		String theme = "light";

		String includedrisks = "High|Medium|Low";
		String includedconfidences = null;
		String reportfilename = null;
		String reportfilenamepattern = "{{yyyy-MM-dd}}-ZAP-Report-[[site]]";
		String reportdir = System.getProperty("user.dir") + "//reports";
		String display = "true";
		String contexts = null;

		try {
			clientApi.reports.generate(title, template, theme, description, contexts, sites, sections,
					includedconfidences, includedrisks, reportfilename, reportfilenamepattern, reportdir, display);
			System.out.println("Report generation requested");
		} catch (ClientApiException e) {
			e.printStackTrace();
		}
	}
}