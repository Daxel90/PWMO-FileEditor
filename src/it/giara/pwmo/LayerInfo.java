package it.giara.pwmo;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class LayerInfo
{
	public float ZLiftDist;
	public float ZLiftSpeed;
	public float ExposureTime;
	public float Height;
	
	public BufferedImage Image;
	
	public LayerInfo()
	{
		
	}
	
	public void saveImageLayer(File outputfile)
	{
		outputfile.getParentFile().mkdirs();
		try
		{
			ImageIO.write(Image, "PNG", outputfile);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
}
