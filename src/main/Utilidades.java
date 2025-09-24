package main;

public class Utilidades {
	public static String primeiraMaiuscula(String string) {
		String primeiraLetra = string.substring(0, 0);
		primeiraLetra = primeiraLetra.toUpperCase();
		return primeiraLetra + string.substring(1, string.length()-1);
	}
}