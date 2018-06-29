package kisaragi.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

public class IO {
	/**
	 * 从path指定的文件中读取文本，以charsetName指定文件编码
	 * @param path 文件路径
	 * @param charsetName 文件编码
	 * @return 文本
	 */
	public static String readText(String path, String charsetName) {
		StringBuffer sb = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(path), charsetName));
			sb = new StringBuffer();
			String line = null;
			while(null != (line = br.readLine())) {
				sb.append(line + "\n");
			}
			if(null != br) {
				br.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.length() == 0?null:sb.toString();
	}
	
	/**
	 * 从path指定的文件中读取文本，以系统默认编码
	 * <p>相当于readText(path, Charset.defaultCharset().toString())
	 * @param path 文件路径
	 * @return 文本
	 */
	public static String readText(String path) {
		return readText(path, Charset.defaultCharset().toString());
	}
	
	/**
	 * 向outputPath指定的文件中写入文本content，以charsetName指定保存编码
	 * <p>此方法专为Windows平台设计，换行符会以\r\n取代\n
	 * @param content 待写入的文本内容
	 * @param outputPath 文件路径
	 * @param append 指定写入方式是否为追加
	 * @param charsetName 保存编码
	 */
	public static void writeText(String content, String outputPath, boolean append, String charsetName) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath, append), charsetName));
			Pattern p = Pattern.compile("(?<!\r)\n");
			bw.write(p.matcher(content).replaceAll("\r\n"));
			if(null != bw) {
				bw.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 向outputPath指定的文件中写入文本content，以系统默认编码保存
	 * <p>此方法专为Windows平台设计，换行符会以\r\n取代\n
	 * <p>相当于writeText(content, outputPath, append, Charset.defaultCharset().toString())
	 * @param content 待写入的文本内容
	 * @param outputPath 文件路径
	 * @param append 指定写入方式是否为追加
	 */
	public static void writeText(String content, String outputPath, boolean append) {
		writeText(content, outputPath, append, Charset.defaultCharset().toString());
	}
	
}
