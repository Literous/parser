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
	 * ��pathָ�����ļ��ж�ȡ�ı�����charsetNameָ���ļ�����
	 * @param path �ļ�·��
	 * @param charsetName �ļ�����
	 * @return �ı�
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
	 * ��pathָ�����ļ��ж�ȡ�ı�����ϵͳĬ�ϱ���
	 * <p>�൱��readText(path, Charset.defaultCharset().toString())
	 * @param path �ļ�·��
	 * @return �ı�
	 */
	public static String readText(String path) {
		return readText(path, Charset.defaultCharset().toString());
	}
	
	/**
	 * ��outputPathָ�����ļ���д���ı�content����charsetNameָ���������
	 * <p>�˷���רΪWindowsƽ̨��ƣ����з�����\r\nȡ��\n
	 * @param content ��д����ı�����
	 * @param outputPath �ļ�·��
	 * @param append ָ��д�뷽ʽ�Ƿ�Ϊ׷��
	 * @param charsetName �������
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
	 * ��outputPathָ�����ļ���д���ı�content����ϵͳĬ�ϱ��뱣��
	 * <p>�˷���רΪWindowsƽ̨��ƣ����з�����\r\nȡ��\n
	 * <p>�൱��writeText(content, outputPath, append, Charset.defaultCharset().toString())
	 * @param content ��д����ı�����
	 * @param outputPath �ļ�·��
	 * @param append ָ��д�뷽ʽ�Ƿ�Ϊ׷��
	 */
	public static void writeText(String content, String outputPath, boolean append) {
		writeText(content, outputPath, append, Charset.defaultCharset().toString());
	}
	
}
