package fr.unistra.pelican.algorithms.frequential;

import fr.unistra.pelican.Algorithm;
import fr.unistra.pelican.AlgorithmException;
import fr.unistra.pelican.DoubleImage;
import fr.unistra.pelican.Image;
import fr.unistra.pelican.PelicanException;
import fr.unistra.pelican.algorithms.io.ImageLoader;
import fr.unistra.pelican.algorithms.visualisation.MViewer;

/**
 * High pass circular frequencial filter. Remove main features, keep details! 
 * A smaller radius means more major frequencies are kept...
 * 
 * @author  Benjamin Perret
 */
public class HighPass extends Algorithm {
	
	/**
	 * Image input
	 */
	public Image input;

	/**
	 * Radius of circular filter
	 */
	public double radius;

	/**
	 * Filtered image
	 */
	public Image output;


	/**
	 * Constructor
	 * 
	 */
	public HighPass() {

		super();
		super.inputs = "input,radius";
		super.outputs = "output";
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.unistra.pelican.Algorithm#launch()
	 */
	public void launch() throws AlgorithmException {


		DoubleImage inputRe=input.newDoubleImage(true);
		DoubleImage inputIm=null;
		DoubleImage[] outputs = FFT2.exec(inputRe,inputIm,false);

		int i2, j2;
		double r2 = radius * radius;

		int xdim=input.xdim;
		int ydim=input.ydim;
		int cx=xdim/2;
		int cy=ydim/2;

		for (int j = 0; j < ydim; j++) {
			for (int i = 0; i < xdim; i++) {
				if (i >= cx)
					i2 = i - xdim;
				else
					i2 = i;
				if (j >= cy)
					j2 = j - ydim;
				else
					j2 = j;
				double r=i2*i2+j2*j2;
				if(r<=r2)
				{
					for(int b=0;b<input.bdim;b++)
					{
						outputs[0].setPixelXYBDouble(i, j,b, 0.0);
						outputs[1].setPixelXYBDouble(i, j,b, 0.0);
					}
				}

			}
		}


		outputs = FFT2.exec(outputs[0],outputs[1],true);
		Image res = Magnitude.exec(outputs);
		if(inputIm instanceof DoubleImage)
		{
			output=res;
		}
		else{
			output=input.copyImage(false);
			for(int i=0;i<output.size();i++)
				output.setPixelDouble(i, res.getPixelDouble(i));
		}



	}
	
	/**
	 * High pass circular frequential filter. A
	 * smaller radius means more major frequences are kept...
	 * 
	 *   
	 * @param input Input Image
	 * @param radius Filter radius
	 * @return High Pass filtered image
	 */
	public static <T extends Image> T exec(T input, double radius)
	{
		return (T) new HighPass().process(input,radius);
	}
	
	/*public static void main(String [] args)
	{
		Image im1=ImageLoader.exec("samples/lennaGray256.png");
		Image im2=ImageLoader.exec("samples/AstronomicalImagesFITS/img1-10.fits");
		MViewer.exec(im1,HighPass.exec(im1,40),im2,HighPass.exec(im2,40));
	}*/
}
