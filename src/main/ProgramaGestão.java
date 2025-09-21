package main;

import java.awt.Component;

import guis.InterfaceLogin;
import usuário.Usuario;

public class ProgramaGestão {
	public static Usuario usuario;
	DatabaseDAO db = new DatabaseDAO();
	public static Component currentWindow;
	
	public static void main(String args[]) {
		currentWindow = new InterfaceLogin();
		
	}
}



