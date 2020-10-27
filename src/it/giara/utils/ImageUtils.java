package it.giara.utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.FileImageOutputStream;

public class ImageUtils
{
	static int i = 0;
	
	public static void saveImage(BufferedImage img, File f)
	{
		try
		{
			if (!f.getParentFile().exists())
				f.getParentFile().mkdirs();
			
			ImageIO.write(img, "png", f);
			
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void saveJPEGImage(BufferedImage img, File f)
	{
		try
		{
			if (!f.getParentFile().exists())
				f.getParentFile().mkdirs();
			
			JPEGImageWriteParam jpegParams = new JPEGImageWriteParam(null);
			jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			jpegParams.setCompressionQuality(1f);
			
			final ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
			writer.setOutput(new FileImageOutputStream(f));
			writer.write(null, new IIOImage(img, null, null), jpegParams);
			
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void saveImage(BufferedImage img, String name, boolean increment)
	{
		if (increment)
			i++;
		try
		{
			File f = new File(name + ".png");
			
			if (increment)
				f = new File(name + "_" + i + ".png");
			
			if (!f.getParentFile().exists())
				f.getParentFile().mkdirs();
			
			ImageIO.write(img, "png", f);
			
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void saveImageComposition(BufferedImage[] img, String name)
	{
		BufferedImage output = getImageComposition(img);
		File f = new File(name + ".png");
		
		if (!f.getParentFile().exists())
			f.getParentFile().mkdirs();
		
		try
		{
			ImageIO.write(output, "png", f);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}
	
	public static BufferedImage getImageComposition(BufferedImage[] img)
	{
		if (img == null)
			return null;
		
		BufferedImage output = null;
		if (img.length == 16)
			output = new BufferedImage(img[0].getWidth() * 4, img[0].getHeight() * 4, BufferedImage.TYPE_INT_RGB);
		else
			return null;
		
		int w = img[0].getWidth();
		int h = img[0].getHeight();
		
		Graphics2D g2d = output.createGraphics();
		
		g2d.drawImage(img[0], 0, 0, w, h, null);
		g2d.drawImage(img[1], w, 0, w, h, null);
		g2d.drawImage(img[2], w * 2, 0, w, h, null);
		g2d.drawImage(img[3], w * 3, 0, w, h, null);
		
		g2d.drawImage(img[4], 0, h, w, h, null);
		g2d.drawImage(img[5], w, h, w, h, null);
		g2d.drawImage(img[6], w * 2, h, w, h, null);
		g2d.drawImage(img[7], w * 3, h, w, h, null);
		
		g2d.drawImage(img[8], 0, h * 2, w, h, null);
		g2d.drawImage(img[9], w, h * 2, w, h, null);
		g2d.drawImage(img[10], w * 2, h * 2, w, h, null);
		g2d.drawImage(img[11], w * 3, h * 2, w, h, null);
		
		g2d.drawImage(img[12], 0, h * 3, w, h, null);
		g2d.drawImage(img[13], w, h * 3, w, h, null);
		g2d.drawImage(img[14], w * 2, h * 3, w, h, null);
		g2d.drawImage(img[15], w * 3, h * 3, w, h, null);
		
		g2d.dispose();
		
		return output;
	}
	
	public static BufferedImage readImage(String name)
	{
		try
		{
			return ImageIO.read(new File(name + ".png"));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static BufferedImage readImage(File file)
	{
		try
		{
			return ImageIO.read(file);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static BufferedImage resize(BufferedImage img, int newW, int newH)
	{
		Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
		BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g2d = dimg.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();
		
		return dimg;
	}
	
	public static float compareImage(BufferedImage biA, BufferedImage biB)
	{
		
		float percentage = 0;
		try
		{
			// take buffer data from both image files //
			DataBuffer dbA = biA.getData().getDataBuffer();
			int sizeA = dbA.getSize();
			DataBuffer dbB = biB.getData().getDataBuffer();
			int sizeB = dbB.getSize();
			int count = 0;
			// compare data-buffer objects //
			if (sizeA == sizeB)
			{
				
				for (int i = 0; i < sizeA; i++)
				{
					
					if (dbA.getElem(i) == dbB.getElem(i))
					{
						count = count + 1;
					}
					
				}
				percentage = (count * 100) / sizeA;
			}
			else
			{
				// System.out.println("Both the images are not of same size");
			}
			
		} catch (Exception e)
		{
			// System.out.println("Failed to compare image files ...");
		}
		return percentage;
	}
	
	public static double compareImageV2(BufferedImage imgA, BufferedImage imgB)
	{
		
		double percentage = 0;
		try
		{
			int width1 = imgA.getWidth();
			int width2 = imgB.getWidth();
			int height1 = imgA.getHeight();
			int height2 = imgB.getHeight();
			
			if ((width1 != width2) || (height1 != height2))
				System.out.println("Error: Images dimensions" + " mismatch");
			else
			{
				long difference = 0;
				for (int y = 0; y < height1; y++)
				{
					for (int x = 0; x < width1; x++)
					{
						int rgbA = imgA.getRGB(x, y);
						int rgbB = imgB.getRGB(x, y);
						int redA = (rgbA >> 16) & 0xff;
						int greenA = (rgbA >> 8) & 0xff;
						int blueA = (rgbA) & 0xff;
						int redB = (rgbB >> 16) & 0xff;
						int greenB = (rgbB >> 8) & 0xff;
						int blueB = (rgbB) & 0xff;
						difference += Math.abs(redA - redB);
						difference += Math.abs(greenA - greenB);
						difference += Math.abs(blueA - blueB);
					}
				}
				
				// Total number of red pixels = width * height
				// Total number of blue pixels = width * height
				// Total number of green pixels = width * height
				// So total number of pixels = width * height * 3
				double total_pixels = width1 * height1 * 3;
				
				// Normalizing the value of different pixels
				// for accuracy(average pixels per color
				// component)
				double avg_different_pixels = difference / total_pixels;
				
				// There are 255 values of pixels in total
				percentage = (avg_different_pixels / 255) * 100;
				
				percentage = 100 - percentage;
			}
		} catch (Exception e)
		{
			System.out.println("Failed to compare image files ...");
		}
		return percentage;
	}
	
	public static String encodeToString(BufferedImage image)
	{
		String imageString = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		try
		{
			ImageIO.write(image, "JPG", bos);
			byte[] imageBytes = bos.toByteArray();
			
			imageString = Base64.getEncoder().encodeToString(imageBytes);
			
			bos.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return imageString;
	}
	
	public static BufferedImage deepCopy(BufferedImage source)
	{
		BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
		Graphics g = b.getGraphics();
		g.drawImage(source, 0, 0, null);
		g.dispose();
		return b;
	}
	
	public static BufferedImage drawRect(BufferedImage img, int x, int y, int w, int h, Color c)
	{
		float[] res = new float[4];
		
		res[1] = (float) x / (float) img.getWidth();
		res[0] = (float) y / (float) img.getHeight();
		res[3] = (float) (x + w) / (float) img.getWidth();
		res[2] = (float) (y + h) / (float) img.getHeight();
		
		return drawRect(img, res, c);
	}
	
	public static BufferedImage drawRect(BufferedImage img, float[] rect, Color c)
	{
		BufferedImage result = deepCopy(img);
		Graphics2D graph = (Graphics2D) result.getGraphics();
		graph.setColor(c);
		int x = (int) (img.getWidth() * rect[1]);
		int y = (int) (img.getHeight() * rect[0]);
		int width = (int) (img.getWidth() * rect[3] - x);
		int height = (int) (img.getHeight() * rect[2] - y);
		graph.drawRect(x, y, width, height);
		graph.dispose();
		
		return result;
	}
}
