package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.HttpRequestUtils;
import util.IOUtils;

public class HttpRequest {

	private Map<String, String> headers = new HashMap<>();
	private Map<String, String> params;

	private RequestLine requestLine;

	private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

	public HttpRequest(InputStream is) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String line = br.readLine();
			log.debug("request line : {}", line);

			if (line == null) {
				return;
			}

			requestLine = new RequestLine(line);

			while (!line.equals("")) {
				line = br.readLine();
				if (!line.equals("") ) {
					log.debug("header : {}", line);
					String[] tokens = line.split(":");
					headers.put(tokens[0], tokens[1].trim());
				}
			}

			
			
			
			if (requestLine.isPost()) {
				String body = IOUtils.readData(br, Integer.parseInt(headers.get("Content-Length")));
				params = HttpRequestUtils.parseQueryString(body);
			} else {
				params = requestLine.getParams();
			}
		} catch (IOException io) {
			log.error(io.getMessage());
		}
	}
	
	
	public boolean isLogin() {
        Map<String, String> cookies = HttpRequestUtils.parseCookies(headers.get("Cookie"));
        String value = cookies.get("logined");
        if (value == null) {
            return false;
        }
        return Boolean.parseBoolean(value);
	}
	
	public HttpMethod getMethod() {
		return requestLine.getMethod();
	}

	public String getPath() {
		return requestLine.getPath();
	}

	public String getHeader(String name) {
		return headers.get(name);
	}

	public String getParameter(String name) {
		return params.get(name);
	}
}
