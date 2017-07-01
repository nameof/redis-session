package cas.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.omg.CORBA.ObjectHelper;

/**
 * 构造URL
 */
final public class UrlBuilder {

	// 协议名称
	private String scheme;

	// 服务器名称
	private String serverName;

	// 端口号
	private int serverPort;

	// 上下文名称
	private String[] paths;

	// 参数集合
	private Map<String, Object> parameters;

	// 地址缓冲
	private String urlBuffer = null;

	// 路径缓冲
	private String pathBuffer = null;

	// 参数缓冲
	private String parametersBuffer = null;

	/**
	 * 构造器
	 */
	public UrlBuilder(String scheme, String serverName, int serverPort, String[] paths) {
		if (paths != null) {
			for (int i = 0; i < paths.length; i++) {
				if (paths[i].isEmpty() == false) {
					paths[i] = trimSeparator(paths[i]);
				}
			}
		}
		this.scheme = scheme;
		this.serverName = serverName;
		this.serverPort = serverPort;
		this.paths = paths;
	}

	/**
	 * 构造器
	 */
	public UrlBuilder(String serverName, int serverPort, String[] paths) {
		this("http", serverName, serverPort, paths);
	}

	/**
	 * 构造器
	 */
	public UrlBuilder(String serverName, int serverPort) {
		this(serverName, serverPort, null);
	}

	/**
	 * 构造器
	 */
	public UrlBuilder(String serverName) {
		this(serverName, 80, null);
	}

	/**
	 * 去除字符串前后的 / 字符和空白字符
	 */
	private static String trimSeparator(String path) {
		int pos = 0, pos2 = path.length() - 1;

		// 跳过字符串前端的所有 / 字符和空格字符
		for (; pos < path.length() && (path.charAt(pos) == '/' || Character.isSpaceChar(path.charAt(pos))); pos++);
		// 跳过字符串末尾的所有 / 字符和空格字符
		for (; pos2 > 0 && (path.charAt(pos2) == '/' || Character.isSpaceChar(path.charAt(pos2))); pos2--);

		// 判断字符串是否有效
		if (pos > pos2) {
			throw new IllegalArgumentException(path);
		}
		return path.substring(pos, pos2 + 1);
	}

	/**
	 * 添加一个目录
	 */
	public void addPath(String path) {
		// 令路径缓冲字符串失效
		pathBuffer = null;
		// 令URL缓冲字符串失效
		urlBuffer = null;

		// 保存路径字符串
		if (paths == null) {
			// 将路径字符串保存为数组
			paths = new String[] { trimSeparator(path) };
		}
		else {
			// 扩展数组并在最后一项添加路径字符串
			paths = Arrays.copyOf(paths, paths.length + 1);
			paths[paths.length - 1] = trimSeparator(path);
		}
	}

	/**
	 * 添加若干目录
	 */
	public void addPath(String...paths) {
		// 遍历并去除字符串中的非法字符
		for (int i = 0; i < paths.length; i++) {
			paths[i] = trimSeparator(paths[i]);
		}
		
		// 存储保存路径的数组
		if (this.paths == null) {
			this.paths = paths;
		}
		else {
			int len = this.paths.length;
			this.paths = Arrays.copyOf(this.paths, len + paths.length);
			for (int i = 0; i < paths.length; i++) {
				this.paths[len + i] = paths[i];
			}
		}
		
		// 令缓冲失效
		pathBuffer = null;
		urlBuffer = null;
	}

	/**
	 * 删除目录
	 */
	public String removePath(int index) {
		String value = paths[index];
		
		// 新建数组比原数组长度少1
		String[] newArray = new String[paths.length - 1];
		
		// 将删除位置前的内容拷贝到新数组
		System.arraycopy(paths, 0, newArray, 0, index);
		// 将删除位置之后的内容拷贝到新数组
		System.arraycopy(paths, index + 1, newArray, index, newArray.length - index);
		
		paths = newArray;
		urlBuffer = null;
		pathBuffer = null;
		return value;
	}

	/**
	 * 转为字符串
	 */
	public String toString(String encoding) {
		// 判断缓冲是否有效
		if (urlBuffer == null) {
			StringBuilder s = new StringBuilder(scheme);	// 写入协议名
			s.append("://");	// 分隔符
			s.append(serverName);	// 写入服务器名
			s.append(serverPort == 80 ? "" : ":" + serverPort);		// 写入端口号
			if (isPathExsit()) {
				s.append("/" + getPath());	// 写入路径
			}
			
			// 写入参数字符串
			if (isParametersExsit()) {
				s.append("?");
				s.append(getParamString(encoding));
			}
			urlBuffer = s.toString();
		}
		return urlBuffer;
	}

	@Override
	public String toString() {
		return toString("UTF-8");
	}

	/**
	 * 添加一个参数
	 */
	public void addParameter(String name, Object value) {
		if (parameters == null) {
			parameters = new LinkedHashMap<>();
		}
		parameters.put(name, value);
		parametersBuffer = null;
	}

	/**
	 * 添加一系列参数
	 */
	public void addParametersMap(Map<String, Object> parameters) {
		this.parameters = parameters;
		parametersBuffer = null;
	}

	/**
	 * 删除一个参数
	 */
	public Object removeParameter(String name) {
		if (parameters == null) {
			return null;
		}
		parametersBuffer = null;
		return parameters.remove(name);
	}

	/**
	 * 获取一个参数
	 */
	public Object getParameter(String name) {
		if (parameters == null) {
			return null;
		}
		return parameters.get(name);
	}
	
	/**
	 * 删除所有参数
	 */
	public void clearParameters() {
		if (parameters != null) {
			parameters.clear();
			parametersBuffer = null;
		}
	}

	/**
	 * 设置协议名称
	 */
	public void setScheme(String scheme) {
		this.scheme = scheme;
		urlBuffer = null;
	}

	/**
	 * 获取协议名称
	 */
	public String getScheme() {
		return scheme;
	}

	/**
	 * 设置服务器名称
	 */
	public void setServerName(String serverName) {
		this.serverName = serverName;
		urlBuffer = null;
	}

	/**
	 * 获取服务器名称
	 */
	public String getServerName() {
		return serverName;
	}

	/**
	 * 设置服务器端口号
	 */
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
		urlBuffer = null;
	}

	/**
	 * 获取服务器端口号
	 */
	public int getServerPort() {
		return serverPort;
	}

	/**
	 * 获取服务器第n级路径
	 */
	public String getPath(int index) {
		return paths[index];
	}

	/**
	 * 获取服务器路径长度
	 * @return
	 */
	public int getPathCount() {
		return paths == null ? 0 : paths.length;
	}
	
	/**
	 * 网址中是否存在路径
	 */
	public boolean isPathExsit() {
		return paths != null;
	}

	/**
	 * 获取服务器路径字符串
	 */
	public String getPath() {
		if (paths == null) {
			return "";
		}
		if (pathBuffer == null) {
			StringBuilder s = new StringBuilder();
			char spliter = 0;
			for (String path : paths) {
				if (spliter == 0) {
					spliter = '/';
				} else {
					s.append(spliter);
				}
				s.append(path);
			}
			pathBuffer = s.toString();
		}
		return pathBuffer;
	}
	
	/**
	 * 是否具有参数
	 */
	public boolean isParametersExsit() {
		return parameters != null && parameters.size() > 0;
	}

	/**
	 * 获取参数字符串
	 */
	public String getParamString(String encoding) {
		if (parametersBuffer == null) {
			parametersBuffer = makeParameters(parameters, encoding);
		}
		return parametersBuffer;
	}

	/**
	 * 获取参数字符串
	 */
	public String getParamString() {
		return getParamString("UTF-8");
	}

	/**
	 * 获取网站根
	 */
	public String getBaseURL() {
		StringBuilder b = new StringBuilder(scheme);
		b.append("://")
		.append(serverName)
		.append(serverPort == 80 ? "" : ":" + serverPort)
		.append("/");
		return b.toString();
	}

	/**
	 * 解析URL
	 */
	public static UrlBuilder parse(String url, String urlEncoding) {
		
		while (url.endsWith("?")) {
			url = url.substring(0, url.lastIndexOf("?"));
		}
		
		String schema, serverName, path = null, queryString = null;
		int pos, pos2, port;

		// 查找协议分隔符位置
		pos = url.indexOf("://");
		if (pos < 0) {		// 查找失败
			// 设定默认协议
			schema = "http";
			// 重置查找起始点
			pos = 0;
		} else {
			// 获取协议名称
			schema = url.substring(0, pos).trim();
			if (schema.isEmpty()) {
				throw new IllegalArgumentException(url);
			}
			// 设置查找起始点
			pos += 3;
		}

		// 查找随后的 / 字符位置
		pos2 = url.indexOf("/", pos);
		if (pos2 < 0) {		// 查找失败
			// 查找?所在位置
			pos2 = url.indexOf("?", pos);
			if (pos2 < 0) {
				// 令字符串剩余部分作为服务器名称
				serverName = url.substring(pos).trim();
			}
			else {
				// 将?之前的部分作为服务器名称
				serverName = url.substring(pos, pos2).trim();
				queryString = url.substring(pos2 + 1).trim();
			}
		}
		else {
			// 获取服务器名称
			serverName = url.substring(pos, pos2).trim();
		}
		if (serverName.isEmpty()) {
			throw new IllegalArgumentException(url);
		}
		// 设置查找起始点
		pos = pos2 + 1;

		// 在服务器名称中查找 : 字符的位置
		pos2 = serverName.lastIndexOf(":");
		if (pos2 < 0) {		// 查找失败
			// 设定默认端口号
			port = 80;
		}
		else {
			// 获取端口号
			port = Integer.parseInt(serverName.substring(pos2 + 1));
			// 重新设定服务器名称（去除端口号部分）
			serverName = serverName.substring(0, pos2);
		}

		// 判断解析是否结束
		if (queryString == null && pos > 0) {
			// 查找?字符出现的位置
			pos2 = url.indexOf("?", pos);
			if (pos2 < 0) {		// 查找失败
				// 获取路径字符串
				path = url.substring(pos);
			}
			else {
				// 获取路径字符串
				path = url.substring(pos, pos2);
				// 获取参数字符串
				queryString = url.substring(pos2 + 1);
			}
		}

		// 生成对象实例
		UrlBuilder builder = new UrlBuilder(schema, serverName, port, path == null ? null : path.split("/"));

		// 解析参数字符串
		if (queryString != null) {
			// 获取参数数组
			String[] qs = queryString.split("&");
			if (qs != null) {
				builder.parameters = new LinkedHashMap<>();
				// 遍历参数数组
				for (String q : qs) {
					// 获取参数名称和参数值
					String[] nv = q.split("=", 2);
					if (nv != null) {
						try {
							// 设置参数
							builder.parameters.put(nv[0], nv.length > 1 ? URLDecoder.decode(nv[1], urlEncoding) : "");
						}
						catch (UnsupportedEncodingException e) {
							throw new IllegalArgumentException(url);
						}
					}
				}
			}
		}
		return builder;
	}

	/**
	 * 解析URL
	 */
	public static UrlBuilder parse(String url) {
		return parse(url, "UTF-8");
	}

	/**
	 * 产生参数字符串
	 */
	public static String makeParameters(Map<String, Object> parameters, String encoding) {
		// 判断参数集合是否有效
		if (parameters == null || parameters.size() == 0) {
			return "";
		}

		StringBuilder s = new StringBuilder();
		char spliter = 0;

		// 遍历参数集合
		for (Entry<String, Object> e : parameters.entrySet()) {
			// 写入参数分隔符
			if (spliter == 0) {
				spliter = '&';
			}
			else {
				s.append(spliter);
			}
			// 写入参数名
			s.append(e.getKey());
			s.append("=");
			try {
				// 写入参数值
				s.append(e.getValue() == null ? "" : URLEncoder.encode(String.valueOf(e.getValue()), encoding));
			}
			catch (UnsupportedEncodingException e1) {
				throw new IllegalArgumentException(encoding);
			}
		}
		// 返回参数字符串
		return s.toString();
	}

	/**
	 * 产生参数字符串
	 */
	public static String makeParameters(Map<String, Object> parameters) {
		return makeParameters(parameters, "UTF-8");
	}

	/**
	 * 连接两个地址
	 */
	public static String combinUrl(String url1, String url2) {
		boolean b1 = url1.endsWith("/"), b2 = url2.startsWith("/");

		// 判断地址1是否以 / 结尾，且地址2是否以 / 开头
		if (b1 && b2) {
			// 保留一个 / 字符
			return url1 + url2.substring(1);
		}

		// 判断地址1是否以 / 结尾，或者地址2是否以 / 开头
		if (b1 || b2) {
			// 直接连接两个地址
			return url1 + url2;
		}

		// 在两个地址间加上 / 字符后连接
		return url1 + "/" + url2;		
	}
}
