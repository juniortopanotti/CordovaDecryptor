package com.topanotti.cordovadecryptor;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.Security;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.io.FilenameUtils;

/**
 * @author Gilberto Topanotti Júnior
 */

public class CordovaDecryptor {

	public static File folder = new File("D:\\projetos\\Truasfalca\\www");
	private static final String CRYPT_IV = "SdleC2P8j62ZvKDi";
	private static final String CRYPT_KEY = "IQmbDb1kdZd4P2b9KMX7Kjv/KVOdxjxr";
	private static final String[] INCLUDE_FILES = new String[] { "html", "htm", "js", "css" };

	public static void main(String[] args) {
		decryptIt();
	}

	public static void decryptIt() {
		System.out.println("Iniciando o processo de descriptografia na pasta " + folder.getName());
		try {
			fileCatcher(folder);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param folder
	 * @throws IOException
	 */
	public static void fileCatcher(final File folder) throws IOException {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				System.out.println("\n\n********* " + folder.getName() + " *********\n\n");
				fileCatcher(fileEntry);
			} else {
				Boolean canDecripyt = hasMatch(FilenameUtils.getExtension(fileEntry.getName()), INCLUDE_FILES);
				if (canDecripyt) {
					decryptFile(fileEntry);
				}
			}
		}
	}

	/**
	 * @param File
	 * @throws IOException
	 */
	public static void decryptFile(final File file) throws IOException {
		byte[] bytes = getFileContent(file);
		if (bytes != null) {
			String decryptedContent = decryptFileContent(bytes);
			writeInFile(file, decryptedContent);
		}
	}

	/**
	 * @param Array of bytes[] from file
	 * @return Decrypted content of file as String
	 * @throws IOException
	 */
	public static String decryptFileContent(byte[] bytes) {
		Security.setProperty("crypto.policy", "unlimited");
		ByteArrayOutputStream bos = null;
		try {
			SecretKey secretKeySpec = new SecretKeySpec(CRYPT_KEY.getBytes("UTF-8"), "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(2, secretKeySpec, new IvParameterSpec(CRYPT_IV.getBytes("UTF-8")));
			bos = new ByteArrayOutputStream();
			bos.write(cipher.doFinal(bytes));
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return bos.toString();
	}

	/**
	 * @param File
	 * @return Array of bytes with file content
	 * @throws IOException
	 */
	public static byte[] getFileContent(final File file) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		StringBuilder strb = new StringBuilder();
		while (true) {
			String line = br.readLine();
			if (line == null) {
				break;
			}
			strb.append(line);
		}
		br.close();
		byte[] bytez = null;
		try {
			bytez = Base64.getDecoder().decode(strb.toString());
		} catch (Exception ex) {
			System.out.println(file.getName() + " já está descriptografado!");
		}
		return bytez;
	}

	/**
	 * @param File
	 * @param File content as String to write in
	 * @throws IOException
	 */
	public static void writeInFile(final File file, String text) throws IOException {
		FileWriter fileWriter = new FileWriter(file, false);
		fileWriter.write(text);
		fileWriter.close();
		System.out.println(file.getName() + " - Desencriptado com sucesso!");
	}

	/**
	 * @param Extension of file
	 * @param Array     of allowed extensions
	 * @return Boolean
	 * @throws IOException
	 */
	private static boolean hasMatch(String extension, String[] regexArr) {
		for (String regex : regexArr) {
			if (extension.equals(regex)) {
				return true;
			}
		}
		return false;
	}
}
