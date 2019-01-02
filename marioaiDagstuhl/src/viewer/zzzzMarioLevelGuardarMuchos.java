package viewer;

import java.awt.Graphics2D;

// Mariana
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;


import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
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
public class zzzzMarioLevelGuardarMuchos {

	public static final int BLOCK_SIZE = 16;
	public static final int LEVEL_HEIGHT = 14;
	
	// Mariana
	public static final String FILENAME = "Creatividad" + File.separator + "zVariablesLatentesGeneradas4.txt";
	public static FileWriter fw;
	public static BufferedWriter bw;
	public static final int numeroNiveles = 1000;


	
	
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
		double valorFun;
		double[] variableLatente = new double[dim];
		String valores = "";
		
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
				valorFun = eval.valueOf(variableLatente);
				valores = valores + "\n" + String.valueOf(i) + "," + String.valueOf(valorFun) ;
			} else {
				variableLatente = randomGaussianDoubleArray(dim);
				level = eval.levelFromLatentVector(variableLatente);
				valorFun = eval.valueOf(variableLatente);
				valores = String.valueOf(i) + "," + String.valueOf(valorFun) +  "\n" ;
			}
			
//			saveLevel(level, "randomSamples" + File.separator + filenameHead + "LevelCortado_" + i, true);
//			saveLevel(level, "zNivelesAleatorios" + File.separator + filenameHead+ "Nivel_" + i, false);
			saveLevel(level, "Creatividad" + File.separator + "zNivelesAleatorios4" + File.separator + i, false);


			// Mariana escribe una nueva línea que contiene la variable latente con la que se generó el nivel correspondiente.
			String aEscribir = "\n" + String.valueOf(i) + "\n " + Arrays.toString(variableLatente) + "\n \n" ;
			writeToFile( FILENAME , aEscribir );

			// Mariana escribe una nueva línea que contiene el valor de Fun del nivel.
			String fileName2 = "Creatividad" + File.separator + "EvaluacionesDic4.csv";
			writeToFile( fileName2 , valores );
			
			
			
			saveLevelToFileText( "Creatividad" + File.separator + "zTextosNiveles4/", "Nivel_" + i, level );
			
		}
		eval.exit();
		System.exit(0);
	}

	
	
	
	public static void saveLevelToFileText(String fileName, String fileNameDos, Level level) throws IOException {	
	
		
		File tempFile = new File("./" + fileName + fileNameDos + ".csv");		
		 try(FileOutputStream fout = new FileOutputStream(tempFile);
	 		PrintStream out = new PrintStream(fout);){
	 	
	 		level.zzzzSaveText(out);

		 }
		 catch (IOException ex) {
		   	System.out.println("There was a problem creating/writing to the temp file");
	    	ex.printStackTrace();
		}
	}

	
	
	public static void readASCIIlevel(String filenameIn)  throws IOException  {
		LevelParser parser = new LevelParser();
		Level level = parser.createLevelASCII(filenameIn);
		
		saveLevelToFileText( "Creatividad" + File.separator + "NivelOriginal/", "Completo", level);
		
		
	}
	
	public static void readLatentVariablesToText(String fileInput, String secondName, String folderSave ) throws IOException {
		
		LinkedList<double[]> latentRows = readLVtoArray(fileInput);
		MarioEvalFunction eval = new MarioEvalFunction();
		Level level;
		double valorFun;
		String valores = "";

		
		int i = 0;
		
		for( double[] rowLat : latentRows) {
			level = eval.levelFromLatentVector(rowLat);
//				Save ASCII confuguration of level
			saveLevelToFileText( folderSave,  "zTexto" + secondName + File.separator +  "Evolved_" + i , level);
//				Save image of level
			saveLevel(level, "Creatividad" + File.separator + "z" + secondName  + File.separator + i, false);

		
		
		valorFun = eval.valueOf(rowLat);
		valores = String.valueOf(i) + "," + String.valueOf(valorFun) +  "\n" ;

		
		// Escribe una nueva línea que contiene el valor de Fun del nivel.
		String fileName2 = "Creatividad" + File.separator + "EvaluacionesDic" + secondName + ".csv";
		writeToFile( fileName2 , valores );
		
		}
	
	
	}

	
	
	
    public static LinkedList<double[]> readLVtoArray(String fileNameDefined ){

        File file = new File(fileNameDefined);

        LinkedList<double[]> rows = new LinkedList<double[]>();
        
        try{
            // -read from filePooped with Scanner class
            Scanner inputStream = new Scanner(file);
            // hashNext() loops line-by-line
            while(inputStream.hasNext()){
                //read single line, put in string
                String data = inputStream.nextLine();
                data = data.replace("[","");
                data = data.replace("]","");
                String[] mData = data.split(",");
                double[] latentVector = new double[mData.length];                
                for( int i = 0; i < mData.length; i++ ){
                    latentVector[i] = Double.parseDouble(mData[i]);
                }
                rows.add(latentVector);
            }
            // after loop, close scanner
            inputStream.close();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
        
        return rows;
//        System.out.println(Arrays.toString(rows.getFirst()) + "***");
    }
	
	
	
	public static void main(String[] args) throws IOException {
		generaNiveles();

		
//			Guardar los niveles evolucionados en archivos de texto y de imagen a partir de archivo con lista de variables latentes.
		String nombre = "EvolvedBest";
		String folderSave = "Creatividad"  + File.separator;
		String fileInput = "Evolved"  + File.separator + "xBestEvolved.txt" ;
		readLatentVariablesToText(fileInput, nombre, folderSave);

		
//		String fileLevel = "marioaiDagstuhl" + File.separator + "data" + File.separator + "mario" + File.separator + "levels" + File.separator + "mario-1-1.txt"; 
//		readASCIIlevel(fileLevel);
	}


}
