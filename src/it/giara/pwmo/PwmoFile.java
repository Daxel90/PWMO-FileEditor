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
	public byte[] fileContent;
	
	//SECTION OFFSET
	int offset_ANYCUBIC= 0;
	int offset_HEADER = 0;
	int offset_PREVIEW = 0;
	int offset_LAYERDEF = 0;
	
	
	//------HEADER OFFSET------
	//0x06; 80?
	//0x0A; 51?
	int offset_LayerThickness = 0x0E;
	int offset_ExposureTime = 0x12;
	int offset_OffTime = 0x16;
	int offset_BottomExposureTime = 0x1A;
	int offset_BottomLayers = 0x1E;
	int offset_ZLift = 0x22;
	int offset_ZSpeed = 0x26;
	int offset_ZRetract = 0x2A;
	//0x2E; 6.024?
	//0x32; 1?
	int offset_ScreenX = 0x36;
	int offset_ScreenY = 0x3A;
	//0x3E; 6.024?
	//0x42; 2.401?
	//0x42; 2.401?
	
	// ------LAYERDEF OFFSET------
	int LayerQty_offset = 0x08;
	int FirstLayer_offset = 0x0C;
	int SingleLayerData_size = 0x20;
	
	// LAYERDEF Single Layer Offset
	int Padding1_offsetInLayer = 0x00;
	int Padding2_offsetInLayer = 0x04;
	int ZLiftDist_offsetInLayer = 0x08;
	int ZLiftSpeed_offsetInLayer = 0x0C;
	int ExposureTime_offsetInLayer = 0x10;
	int LayerHeight_offsetInLayer = 0x14;
	int Padding3_offsetInLayer = 0x18;
	int Padding4_offsetInLayer = 0x1C;
	
	
	//------HEADER DATA------
	float LayerThickness;
	float ExposureTime;
	float OffTime;
	float BottomExposureTime;
	float BottomLayers;
	float ZLift;
	float ZSpeed;
	float ZRetract;
	int ScreenX;
	int ScreenY;
	
	//------LAYERDEF DATA------
	int LayerQty;
	LayerInfo[] Layers;
	
	public PwmoFile(File pFile)
	{
		file = pFile;
	}
	
	public void decode() throws IOException
	{
		int temp;
		fileContent = Files.readAllBytes(file.toPath());
		
		offset_ANYCUBIC = searchData(fileContent, "ANYCUBIC".getBytes("UTF-8"));
		offset_HEADER = searchData(fileContent, "HEADER".getBytes("UTF-8"));
		offset_PREVIEW = searchData(fileContent, "PREVIEW".getBytes("UTF-8"));
		offset_LAYERDEF = searchData(fileContent, "LAYERDEF".getBytes("UTF-8"));
		
		decodeHEADER();
		decodeLAYERDEF();
		
	}
	
	public void decodeHEADER()
	{
		LayerThickness = readFloat(offset_HEADER + offset_LayerThickness);
		ExposureTime = readFloat(offset_HEADER + offset_ExposureTime);
		BottomExposureTime = readFloat(offset_HEADER+offset_BottomExposureTime);
		OffTime = readFloat(offset_HEADER+offset_OffTime);
		BottomLayers = readFloat(offset_HEADER+offset_BottomLayers);
		ZLift = readFloat(offset_HEADER+offset_ZLift);
		ZSpeed = readFloat(offset_HEADER+offset_ZSpeed);
		ZRetract = readFloat(offset_HEADER+offset_ZRetract);
		
		ScreenX = readIntLE(offset_HEADER+offset_ScreenX);
		ScreenY = readIntLE(offset_HEADER+offset_ScreenY);
	}
	
	public void decodeLAYERDEF()
	{
		LayerQty = readIntLE(offset_LAYERDEF + LayerQty_offset);
		
		Layers = new LayerInfo[LayerQty];
		
		int LayerOff = offset_LAYERDEF + FirstLayer_offset;
		
		for (int layer = 0; layer < LayerQty; layer++)
		{
			Layers[layer] = new LayerInfo(this, layer);
			Layers[layer].ZLiftDist = readFloat(LayerOff + ZLiftDist_offsetInLayer);
			Layers[layer].ZLiftSpeed = readFloat(LayerOff + ZLiftSpeed_offsetInLayer);
			Layers[layer].ExposureTime = readFloat(LayerOff + ExposureTime_offsetInLayer);
			Layers[layer].Height = readFloat(LayerOff + LayerHeight_offsetInLayer);
			LayerOff += SingleLayerData_size;
		}
		
		// Start Read LayerImage
		int ImageOffset = LayerOff;
		
		for (int layer = 0; layer < LayerQty; layer++)
		{
			
			Layers[layer].decodeLayer(ImageOffset);
			ImageOffset = Layers[layer].end_offset;
			
			//Async Save Image
			int ThLayer = layer;
			new Thread()
			{
				@Override
				public void run()
				{
					Layers[ThLayer].saveImageLayer();
					Layers[ThLayer].unloadImageLayer();
				}
			}.start();
			
			System.out.println("LayerDone: "+layer);
		}
		
	}
	
	public int readIntLE(int offset)
	{
		int result = 0;
		
		result |= (fileContent[offset + 3] & 0xFF);
		result <<= 8;
		result |= (fileContent[offset + 2] & 0xFF);
		result <<= 8;
		result |= (fileContent[offset + 1] & 0xFF);
		result <<= 8;
		result |= (fileContent[offset + 0] & 0xFF);
		
		return result;
	}
	
	public int readIntBE(int offset)
	{
		int result = 0;
		
		result |= (fileContent[offset + 0] & 0xFF);
		result <<= 8;
		result |= (fileContent[offset + 1] & 0xFF);
		result <<= 8;
		result |= (fileContent[offset + 2] & 0xFF);
		result <<= 8;
		result |= (fileContent[offset + 3] & 0xFF);
		
		return result;
	}
	
	public float readFloat(int offset)
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
	
	
	@Override
	public String toString()
	{
		String result = "";
		result += "LayerThickness: " + LayerThickness + System.lineSeparator();
		result += "Normale ExposureTime: " + ExposureTime + System.lineSeparator();
		result += "OffTime: " + OffTime + System.lineSeparator();
		result += "Bottom ExposureTime: " + BottomExposureTime + System.lineSeparator();
		result += "BottomLayers: " + BottomLayers + System.lineSeparator();
		result += "ZLift: " + ZLift + System.lineSeparator();
		result += "ZSpeed: " + ZSpeed + System.lineSeparator();
		result += "ZRetract: " + ZRetract + System.lineSeparator();
		
		result += "ScreenX: " + ScreenX + System.lineSeparator();
		result += "ScreenY: " + ScreenY + System.lineSeparator();
		
		result += "LayerQty: " + LayerQty + System.lineSeparator();
		
		return result;
	}
	
}
