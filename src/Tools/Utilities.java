package Tools;

import java.io.BufferedReader;
import AG.Individuo;

public class Utilities {
	
	private static BufferedReader inputReader;

	public static int toInt(String sn) {

		return Integer.parseInt(sn);
	}

	public static double toDouble(String sn) {
		if (sn.equals("inf"))
			return 100;
		return Double.parseDouble(sn);
	}

	public static void printMatrix(int[][] matrix) {
		for (int i = 0; i < matrix.length; i++) {
			System.out.printf("[%2d]",i);
			for (int j = 0; j < matrix[0].length; j++)
				System.out.printf("%7d", matrix[i][j]);

			System.out.println();
		}
	}

	public static void printMatrix(double[][] matrix) {
		for (int i = 0; i < matrix.length; i++) {
			System.out.printf("[%2d]",i);
			for (int j = 0; j < matrix[0].length; j++)
				System.out.printf("%12.3f", matrix[i][j]);

			System.out.println();
		}
	}

	public static void printArray(int[] array) {
		System.out.print("[");
		for (int i = 0; i < array.length; i++)
			System.out.printf("%2d" + (i < array.length - 1 ? " |" : "]\n"),
					array[i]);
	}

	public static void printArray(double[] array) {
		System.out.print(" [");
		for (int i = 0; i < array.length; i++)
			System.out.printf("%6.6f" + (i < array.length - 1 ? ", " : "]\n"),
					array[i]);
	}
	
	public static void printObject(Object[] arr){
		System.out.println("");
		for (int i = 0; i < arr.length; i++)
			System.out.print("\t" + arr[i]);
	}

	public static String arrayToString(int[] array) {
		String s = "[";
		for (int i = 0; i < array.length; i++)
			s += array[i] + (i < array.length - 1 ? ", " : "]");
		return s;
	}
}
