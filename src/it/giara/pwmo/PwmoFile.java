package it.giara.pwmo;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class PwmoFile
{
	File file;
	
	float Thickness;
	float ExposureTime; // 0x48
	float OffTime;
	float BottomExposureTime; // 0x50
	int LayersNumber;
	int BottomLayers;
	
	int Width = 1620;
	int Height = 2560;
	
	int LayerDefOffset = -1;
	int LayerQty_offset = 0x08;
	int FirstLayer_offset = 0x0C;
	
	// Layer Offset
	int Padding1_offsetInLayer = 0x00;
	int Padding2_offsetInLayer = 0x04;
	int ZLiftDist_offsetInLayer = 0x08;
	int ZLiftSpeed_offsetInLayer = 0x0C;
	int ExposureTime_offsetInLayer = 0x10;
	int LayerHeight_offsetInLayer = 0x14;
	int Padding3_offsetInLayer = 0x18;
	int Padding4_offsetInLayer = 0x1C;
	
	int LayerQty;
	LayerInfo[] Layers;
	
	ArrayList<BufferedImage> LayersImages = new ArrayList<BufferedImage>();
	
	public PwmoFile(File pFile)
	{
		file = pFile;
	}
	
	public void decode() throws IOException
	{
		int temp;
		byte[] fileContent = Files.readAllBytes(file.toPath());
		
		// ExposureTime
		temp = fileContent[0x48];
		temp |= (fileContent[0x49] << 8);
		temp |= (fileContent[0x4A] << 16);
		temp |= (fileContent[0x4B] << 24);
		ExposureTime = Float.intBitsToFloat(temp);
		
		// BottomExposureTime
		temp = fileContent[0x50];
		temp |= (fileContent[0x51] << 8);
		temp |= (fileContent[0x52] << 16);
		temp |= (fileContent[0x53] << 24);
		BottomExposureTime = Float.intBitsToFloat(temp);
		
		decodeLayerDef(fileContent);
		
	}
	
	public void decodeLayerDef(byte[] fileContent)
	{
		LayerDefOffset = searchData(fileContent, "LAYERDEF".getBytes());
		
		LayerQty = readIntLE(fileContent, LayerDefOffset + LayerQty_offset);
		
		Layers = new LayerInfo[LayerQty];
		
		int LayerOff = LayerDefOffset + FirstLayer_offset;
		
		for (int layer = 0; layer < LayerQty; layer++)
		{
			Layers[layer] = new LayerInfo();
			Layers[layer].ZLiftDist = readFloat(fileContent, LayerOff + ZLiftDist_offsetInLayer);
			Layers[layer].ZLiftSpeed = readFloat(fileContent, LayerOff + ZLiftSpeed_offsetInLayer);
			Layers[layer].ExposureTime = readFloat(fileContent, LayerOff + ExposureTime_offsetInLayer);
			Layers[layer].Height = readFloat(fileContent, LayerOff + LayerHeight_offsetInLayer);
			LayerOff += 0x20;
		}
		
		// Start Read LayerImage
		int startImageOffset = LayerOff;
		
		for (int layer = 0; layer < LayerQty; layer++)
		{
			BufferedImage img = new BufferedImage(Width, Height, BufferedImage.TYPE_INT_RGB);
			int imgX = 0;
			int imgY = 0;
			boolean done = false;
			
			while (!done)
			{
				byte generateCmd = fileContent[startImageOffset];
				int lineSize = 0;
				int lineColor = (generateCmd & 0xF0) >> 4;
				
				int RGBColor = lineColor * 17;
				RGBColor = (RGBColor << 8) + lineColor * 17;
				RGBColor = (RGBColor << 8) + lineColor * 17;
				
				// ExtendedSizeCmd
				if (lineColor == 0x00 || lineColor == 0x0F)
				{
					lineSize = (generateCmd & 0x0F);
					lineSize = (lineSize << 8) + (fileContent[startImageOffset + 1] & 0xFF);
					
					startImageOffset += 2;
				}
				else
				{
					lineSize = (generateCmd & 0x0F);
					startImageOffset += 1;
				}
				
				while (lineSize > 0)
				{
					if (imgX < Width)
					{
						img.setRGB(imgX, imgY, RGBColor);
						imgX++;
						
						if ((imgX == Width - 1) && (imgY == Height - 1))
						{
							done = true;
							break;
						}
					}
					else if (imgX == Width && imgY < Height)
					{
						imgX = 0;
						imgY++;
						img.setRGB(imgX, imgY, RGBColor);
						imgX++;
					}
					
					lineSize--;
				}
			}
			
			
			Layers[layer].Image = img;
			
			File outputfile = new File("Layers", "layer" + layer + ".png");
			Layers[layer].saveImageLayer(outputfile);

		}
		
	}
	
	public int readIntLE(byte[] fileContent, int offset)
	{
		int result = 0;
		
		result |= (fileContent[offset + 3] & 0xFF);
		result <<= 8;
		result |= (fileContent[offset + 2] & 0xFF);
		result <<= 8;
		result |= (fileContent[offset + 1] & 0xFF);
		result <<= 8;
		result |= (fileContent[offset] & 0xFF);
		
		return result;
	}
	
	public int readIntBE(byte[] fileContent, int offset)
	{
		int result = 0;
		
		result |= fileContent[offset];
		result <<= 8;
		result |= fileContent[offset + 1];
		result <<= 8;
		result |= fileContent[offset + 2];
		result <<= 8;
		result |= fileContent[offset + 3];
		
		return result;
	}
	
	public float readFloat(byte[] fileContent, int offset)
	{
		byte[] b = new byte[4];
		
		b[3] = fileContent[offset];
		b[2] = fileContent[offset + 1];
		b[1] = fileContent[offset + 2];
		b[0] = fileContent[offset + 3];
		
		return ByteBuffer.wrap(b).getFloat();
	}
	
	public int searchData(byte[] source, byte[] toSearch)
	{
		boolean found = false;
		for (int i = 0; i < source.length; i++)
		{
			found = true;
			
			for (int j = 0; j < toSearch.length; j++)
			{
				if (source[i + j] != toSearch[j])
				{
					found = false;
					break;
				}
			}
			
			if (found)
				return i + toSearch.length;
		}
		
		return -1;
	}
	
}
