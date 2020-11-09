package it.giara;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import it.giara.gui.MainFrame;
import it.giara.pwmo.PwmoFile;

public class PwmoFileEditor
{
	public static void main(String[] args) throws IOException
	{
		//TEST
		PwmoFile file = new PwmoFile(new File("C:\\Users\\Giara\\Desktop\\TEST.pwmo"));
		file.decode();
		
		Scanner scanner = new Scanner(System.in);
		scanner.nextLine();
		
		file.encodeLAYERDEF_DATA();
		
		
//		try
//		{
//			MainFrame frame = new MainFrame();
//			frame.setVisible(true);
//		} catch (Exception e)
//		{
//			e.printStackTrace();
//		}
	}
}
