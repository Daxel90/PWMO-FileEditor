package it.giara.pwmo;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class PwmoFile
{
	File file;
	
	float Thickness;
	float ExposureTime; // 0x48
	float OffTime;
	float BottomExposureTime; // 0x50
	int LayersNumber;
	int BottomLayers;
	
	ArrayList<BufferedImage> LayersImages = new ArrayList<BufferedImage>();
	
	public PwmoFile(File pFile)
	{
		file = pFile;
	}
	
	public void decode() throws IOException
	{
		int temp;
		byte[] fileContent = Files.readAllBytes(file.toPath());
		
		//ExposureTime
		temp = fileContent[0x48];
		temp |= (fileContent[0x49] << 8);
		temp |= (fileContent[0x4A] << 16);
		temp |= (fileContent[0x4B] << 24);
		ExposureTime = Float.intBitsToFloat(temp);
		
		//BottomExposureTime
		temp = fileContent[0x50];
		temp |= (fileContent[0x51] << 8);
		temp |= (fileContent[0x52] << 16);
		temp |= (fileContent[0x53] << 24);
		BottomExposureTime = Float.intBitsToFloat(temp);
	}
	
}
