package viewer;

import java.awt.Graphics2D;

// Mariana
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.FileOutputStream;


import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;
import java.util.Arrays;

import javax.imageio.ImageIO;

import basicMap.Settings;
import ch.idsia.ai.tasks.ProgressTask;
import ch.idsia.mario.engine.LevelRenderer;
import ch.idsia.mario.engine.level.Level;
import ch.idsia.mario.engine.level.LevelParser;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.tools.EvaluationOptions;
import cmatest.MarioEvalFunction;

/**
 * This file generates several level images by querying the
 * trained GAN with random latent vectors.
 */
public class MarioLevelGuardarMuchos {

	public static final int BLOCK_SIZE = 16;
	public static final int LEVEL_HEIGHT = 14;
	
	// Mariana
	public static final String FILENAME = "Creatividad" + File.separator + "zVariablesLatentesGeneradas.txt";
	public static FileWriter fw;
	public static BufferedWriter bw;
	public static final int numeroNiveles = 200;


	
	
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
		LevelRenderer.renderAreaWhiteBack((Graphics2D) image.getGraphics(), level, 0, 0, excludeBufferRegion ? LevelParser.BUFFER_WIDTH*BLOCK_SIZE : 0, 0, relevantWidth, LEVEL_HEIGHT*BLOCK_SIZE);
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

	public static double[] randomUniformDoubleArray(int dim) {
		Random rdm = new Random();
		double[] array = new double[dim];
		for (int i=0; i<dim; i++) {
			array[i] = rdm.nextDouble();
		}
		return array;
	}

	public static double[] randomGaussianDoubleArray(int dim) {
		Random rdm = new Random();
		double[] array = new double[dim];
		for (int i=0; i<dim; i++) {
			array[i] = rdm.nextGaussian();
		}
		return array;
	}
	

	
	public static void createFile(String fileName, String firstLine) throws IOException {
		writeToFile(fileName, firstLine, false);
	}

	public static void writeToFile(String fileName, String aEscribirse) throws IOException {
		writeToFile(fileName, aEscribirse, true);
	}
	
	public static void writeToFile(String fileName, String aEscribirse, boolean append ) throws IOException {
		// Mariana. Inicializa escritor de variables latentes.
		try {
			fw = new FileWriter(fileName, append);
			bw = new BufferedWriter(fw);
			bw.append(aEscribirse);
		}catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null)
					bw.close();
				if (fw != null)
					fw.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
		

	
	
	
	
		
	public static void generaNiveles() throws IOException {
		Settings.setPythonProgram();

	
		// This is used because it contains code for communicating with the GAN
		MarioEvalFunction eval = new MarioEvalFunction();

		int nbLevels = numeroNiveles;
		int dim = 32;
		boolean uniform = false;
		Level level;
		double[] variableLatente = new double[dim];
		
		writeToFile( FILENAME, " Estas AAAAA son las variables latentes (vectores de 32 valores Float) con las que se generaron los niveles encontrados en la carpeta ./randomSamples/ \n \n \n ", false);

		

		String filenameHead = "";
		if (uniform) {
			filenameHead = "uniform";
		} else {
			filenameHead = "gaussian";
		}
		
		for (int i=1; i<=nbLevels; i++) {
			if (uniform) {
				variableLatente = randomUniformDoubleArray(dim);
				level = eval.levelFromLatentVector(variableLatente);
			} else {
				variableLatente = randomGaussianDoubleArray(dim);
				level = eval.levelFromLatentVector(variableLatente);
			}
			
//			saveLevel(level, "randomSamples" + File.separator + filenameHead + "LevelCortado_" + i, true);
//			saveLevel(level, "zNivelesAleatorios" + File.separator + filenameHead+ "Nivel_" + i, false);
			saveLevel(level, "Creatividad" + File.separator + "zNivelesAleatorios" + File.separator + i, false);


			// Mariana agrega metodo que escribe una nueva línea que contiene la variable latente con la que se generó el nivel correspondiente.
			String aEscribir = "\n" + String.valueOf(i) + "\n " + Arrays.toString(variableLatente) + "\n \n" ;
			writeToFile( FILENAME , aEscribir );
			
			saveLevelToFileText( "Creatividad" + File.separator + "zTextosNiveles/", "Nivel_" + i, level );
			
		}
		eval.exit();
		System.exit(0);
	}

	
	
	
	public static void saveLevelToFileText(String fileName, String fileNameDos, Level level) throws IOException {	
	
		
		
		 try {
			File tempFile = new File("./" + fileName + fileNameDos + ".csv");
	 		FileOutputStream fout = new FileOutputStream(tempFile);
	 		PrintStream out = new PrintStream(fout);
	 	
	 		level.saveText(out);

		 } catch (IOException ex) {
		   	System.out.println("There was a problem creating/writing to the temp file");
	    	ex.printStackTrace();
		}
	}

	
	
	public static void readASCIIlevel(String filename) {
		LevelParser parser = new LevelParser();
		
		
	}
	
	
	
	public static void main(String[] args) throws IOException {
		generaNiveles();
	}


}
