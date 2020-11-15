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
		
		file.Layers[0].ExposureTime = 5;
		file.Layers[1].ExposureTime = 5;
		file.Layers[2].ExposureTime = 5;
		file.Layers[3].ExposureTime = 5;
		file.Layers[4].ExposureTime = 180;
		file.Layers[5].ExposureTime = 180;
		
		file.encode();
		
		
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
