package it.giara.pwmo;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;

import it.giara.utils.ImageUtils;

public class LayerInfo
{
	public int LayerNumber;
	public float ZLiftDist;
	public float ZLiftSpeed;
	public float ExposureTime;
	public float Height;
	
	public PwmoFile origin;
	public int start_offset;
	public int end_offset;
	
	File imageFile;
	byte[] encodedImageData;
	public BufferedImage Image;
	
	public LayerInfo(PwmoFile or, int layerN)
	{
		origin = or;
		LayerNumber = layerN;
		
		imageFile = new File("temp","Layer"+layerN+".png");
	}
	
	public void decodeLayer(int ImageOffset)
	{
		Image = new BufferedImage(origin.ScreenX, origin.ScreenY, BufferedImage.TYPE_INT_RGB);
		int imgX = 0;
		int imgY = 0;
		boolean done = false;
		start_offset = ImageOffset;
		
		while (!done)
		{
			byte generateCmd = origin.fileContent[ImageOffset];
			int lineSize = 0;
			int lineColor = (generateCmd & 0xF0) >> 4;
			
			int RGBColor = lineColor * 17;
			RGBColor = (RGBColor << 8) + lineColor * 17;
			RGBColor = (RGBColor << 8) + lineColor * 17;
			
			// ExtendedSizeCmd
			if (lineColor == 0x00 || lineColor == 0x0F)
			{
				lineSize = (generateCmd & 0x0F);
				lineSize = (lineSize << 8) + (origin.fileContent[ImageOffset + 1] & 0xFF);
				
				ImageOffset += 2;
			}
			else
			{
				lineSize = (generateCmd & 0x0F);
				ImageOffset += 1;
			}
			
			while (lineSize > 0)
			{
				if (imgX < origin.ScreenX)
				{
					Image.setRGB(imgX, imgY, RGBColor);
					
					imgX++;
					
					if ((imgX == origin.ScreenX - 1) && (imgY == origin.ScreenY - 1))
					{
						done = true;
						break;
					}
				}
				else if (imgX == origin.ScreenX && imgY < origin.ScreenY)
				{
					imgX = 0;
					imgY++;
					Image.setRGB(imgX, imgY, RGBColor);
					imgX++;
				}
				
				lineSize--;
			}
		}
		
		end_offset = ImageOffset;
		
		encodedImageData = Arrays.copyOfRange(origin.fileContent, start_offset, end_offset);
	}
	
	public void encodeLayer()
	{
		ArrayList<Byte> encodedData = new ArrayList<Byte>();
		
		int lastColor = -1;
		int occurrency = 0;
		
		for(int x = 0; x < origin.ScreenX; x++)
		{
			for(int y = 0; y < origin.ScreenY; y++)
			{
				int Color = (Image.getRGB(x, y) & 0xFF)/17;
				
				if(lastColor == -1)
					lastColor = Color;
				
				if(lastColor == Color)
				{
					occurrency++;
				}
				else
				{					
					if(lastColor == 0x00 || lastColor == 0x0F) // 4095
					{
						if((occurrency/ 4095) > 1)
						{
							byte colorByte = (byte) ((lastColor & 0x0F) << 4);
							for(int i =0; i< (occurrency/ 4095);i++)
							{
								colorByte &= 0x0F;
								encodedData.add(colorByte);
								encodedData.add((byte) 0xFF);
							}
						}
						
						byte colorByte = (byte) ((lastColor & 0x0F) << 4);
						occurrency = occurrency - ((int)(occurrency/ 4095)*4095);
						
						colorByte |= ((occurrency >> 8) & 0x0F);
						
						encodedData.add(colorByte);
						encodedData.add((byte) (occurrency & 0xFF));
						
					}
					else //15
					{
						
					}
					
					lastColor = Color;
					occurrency = 1;
				}
				
				
			}
		}
		
		
		
	}
	
	
	public void loadImageLayer()
	{
		Image = ImageUtils.readImage(imageFile);
	}
	
	public void unloadImageLayer()
	{
		Image = null;
	}
	
	public void saveImageLayer()
	{
		imageFile.getParentFile().mkdirs();
		try
		{
			ImageIO.write(Image, "PNG", imageFile);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
}
