package viewer;

import static reader.JsonReader.JsonToDoubleArray;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;

import basicMap.Settings;
import ch.idsia.ai.tasks.ProgressTask;
import ch.idsia.mario.engine.LevelRenderer;
import ch.idsia.mario.engine.level.Level;
import ch.idsia.mario.engine.level.LevelParser;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.tools.EvaluationOptions;
import cmatest.MarioEvalFunction;
import reader.JsonReader;

/**
 * This file allows you to generate a level image for any latent vector
 * or your choice. The vector must have a length of 32 numbers separated
 * by commas enclosed in square brackets [ ]. For example,
 * [0.9881835842209917, -0.9986077315374948, 0.9995512051242508, 0.9998643432807639, -0.9976165917284504, -0.9995247114230822, -0.9997001909358728, 0.9995694511739592, -0.9431036754879115, 0.9998155541290887, 0.9997863689962382, -0.8761392912669269, -0.999843833016589, 0.9993230720045649, 0.9995470247917402, -0.9998847606084427, -0.9998322053148382, 0.9997707200294411, -0.9998905141832997, -0.9999512510490688, -0.9533512808031753, 0.9997703088007039, -0.9992229823819915, 0.9953917828622341, 0.9973473366437476, 0.9943030781608361, 0.9995290290713732, -0.9994945079679955, 0.9997109900652238, -0.9988379572928884, 0.9995070647543864, 0.9994132207570211]
 * 
 */
public class DosMarioLevelViewer {

	public static final int BLOCK_SIZE = 16;
	public static final int LEVEL_HEIGHT = 14;
	
	/**
	 * Return an image of the level, excluding 
	 * the background, Mario, and enemy sprites.
	 * @param level
	 * @return
	 */
	public static BufferedImage getLevelImage(Level level, boolean excludeBufferRegion) {
		EvaluationOptions options = new CmdLineOptions(new String[0]);
		ProgressTask task = new ProgressTask(options);
		// Added to change level
        options.setLevel(level);
		task.setOptions(options);

		int relevantWidth = (level.width - (excludeBufferRegion ? 2*LevelParser.BUFFER_WIDTH : 0)) * BLOCK_SIZE;
		BufferedImage image = new BufferedImage(relevantWidth, LEVEL_HEIGHT*BLOCK_SIZE, BufferedImage.TYPE_INT_RGB);
		// Skips buffer zones at start and end of level
		LevelRenderer.renderArea((Graphics2D) image.getGraphics(), level, 0, 0, excludeBufferRegion ? LevelParser.BUFFER_WIDTH*BLOCK_SIZE : 0, 0, relevantWidth, LEVEL_HEIGHT*BLOCK_SIZE);
		return image;
	}

	/**
	 * Save level as an image
	 * @param level Mario Level
	 * @param name Filename, not including jpg extension
	 * @param clipBuffer Whether to exclude the buffer region we add to all levels
	 * @throws IOException
	 */
	public static void saveLevel(Level level, String name, boolean clipBuffer) throws IOException {
		BufferedImage image = getLevelImage(level, clipBuffer);


		File file = new File(name + ".jpg");
		ImageIO.write(image, "jpg", file);
		System.out.println("File saved: " + file);
	}

	
	public static double[] creaArreglo(String str) {		
	//	str = "0.17086754005791266, 0.46251449898310093, 0.9106104980782328, 0.5568853761871468, 0.5718372682278052, -0.134141640955785, 0.31219600831130123, 0.34336298812035787, -0.7003416845807743, -0.04430332217134178, -0.6294564853437262, -0.7204203795464695, 1.0145176144753092, 1.149565063635259, 0.9284914480448097, -0.5543792505055857, 0.9685794957881608, 0.9941386367529972, -0.5199507782389469, -0.8579900532864979, -0.36963034966904407, -0.3087838501091796, 0.2673183787345444, 0.410422041763176, 0.48662916012384816, 0.5497270998082449, -1.1528930057613105, -0.748832921203682, 0.9528233005613492, -0.4005678325139035, -0.3553389134264086, 1.025410603166747";
		
		 String[] arrOfStr = str.split(", "); 
		 
		 
		System.out.println(" \n \n \n " + arrOfStr.toString());

		
		double[] r = new double[arrOfStr.length];
		 
		 
				 
		 for (int i=0; i<arrOfStr.length; i++) {
			 double d = Double.parseDouble(arrOfStr[i]);
			 r[i] = d;
		 }
		 return r;
	}
	
	
	
	public static void main(String[] args) throws IOException {
		Settings.setPythonProgram();
		// This is used because it contains code for communicating with the GAN
		MarioEvalFunction eval = new MarioEvalFunction();
		
		System.out.println("Iniciamos");
		int i =0;


		Level level;
		// Read input level

		System.out.println("Generating level with read vectors");

		
		
		String strLatentVector = "";
		StringBuilder builder = new StringBuilder();
		String str = "";
		Level nivelYa;
		
	    
	    // pass the path to the file as a parameter ********************
	    File file = new File("NuevosNiveles/Ya.txt"); 
	    Scanner sc = new Scanner(file); 
	  
	    while (sc.hasNextLine()) {
	    	str = sc.nextLine();
	    	builder.append(str);
	    }
	    
	    sc.close();
	    
	    strLatentVector = builder.toString();
	    
		Settings.printInfoMsg("Passed vector(s): " + strLatentVector);
		// If the input starts with two square brackets, then it must be an array of arrays,
		// and hence a series of several latent vectors rather than just one. In this case,
		// patch all of the levels together into one long level. 
			// remove opening/closing brackets
//			strLatentVector = strLatentVector.substring(1,strLatentVector.length()-1);
			String levels = "";
			while(strLatentVector.length() > 0) {
				int inicial = strLatentVector.indexOf("[") + 1;
				int end = strLatentVector.indexOf("]");
				String oneVector = strLatentVector.substring(inicial,end);
				System.out.println("ONE VECTOR: " + oneVector);
				
//				levels += eval.stringToFromGAN(oneVector); // Use the GAN

				nivelYa = eval.levelFromLatentVector(creaArreglo(oneVector));
				saveLevel(nivelYa, "NuevosNiveles" + File.separator + "generados" + File.separator  + "Nivel_" + i++, true);

				
				strLatentVector = strLatentVector.substring(end); // discard processed vector
				if(strLatentVector.length() > 0) {
//					levels += ",";
					strLatentVector = strLatentVector.substring(1); // discard leading comma
				}
			}

			
			
		eval.exit();
		System.exit(0);
	}
}
