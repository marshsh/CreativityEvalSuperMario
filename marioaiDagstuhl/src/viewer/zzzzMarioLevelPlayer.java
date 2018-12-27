package viewer;

import static reader.JsonReader.JsonToDoubleArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import basicMap.Settings;
import ch.idsia.mario.engine.level.Level;
import ch.idsia.mario.engine.level.LevelParser;
import cmatest.MarioEvalFunction;
import communication.MarioProcess;
import reader.JsonReader;

/**
 * This file allows you to generate a level and play it for any latent vector
 * or your choice. The vector must have a length of 32 numbers separated
 * by commas enclosed in square brackets [ ]. For example,
 * [0.9881835842209917, -0.9986077315374948, 0.9995512051242508, 0.9998643432807639, -0.9976165917284504, -0.9995247114230822, -0.9997001909358728, 0.9995694511739592, -0.9431036754879115, 0.9998155541290887, 0.9997863689962382, -0.8761392912669269, -0.999843833016589, 0.9993230720045649, 0.9995470247917402, -0.9998847606084427, -0.9998322053148382, 0.9997707200294411, -0.9998905141832997, -0.9999512510490688, -0.9533512808031753, 0.9997703088007039, -0.9992229823819915, 0.9953917828622341, 0.9973473366437476, 0.9943030781608361, 0.9995290290713732, -0.9994945079679955, 0.9997109900652238, -0.9988379572928884, 0.9995070647543864, 0.9994132207570211]
 * 
 * Additionally, if you send in a 2D array where each sub-array has length 32, this will be interpreted as several
 * latent vectors. Each latent vector will create a separate level segment, and the segments will be stitched together
 * into one level for you to play.
 * 
 */
public class zzzzMarioLevelPlayer {
	
	public zzzzMarioLevelPlayer(Level level){	

		System.out.println("Iniciamos en z Mario Level Player");
		this.levelm = level;
	}

	public Level levelm;
	public static final int BLOCK_SIZE = 16;
	public static final int LEVEL_HEIGHT = 14;

	public zzzzMarioLevelPlayer(String[] args) throws IOException {		

//	public static void main(String[] args) throws IOException {
		Settings.setPythonProgram();
		// This is used because it contains code for communicating with the GAN
		MarioEvalFunction eval = new MarioEvalFunction();

		Level level;
		// Read input level
		String strLatentVector = "";
		if (args.length > 0) {
			StringBuilder builder = new StringBuilder();
			for (String str : args) {
				builder.append(str);
			}
			strLatentVector = builder.toString();
			Settings.printInfoMsg("Passed vector(s): " + strLatentVector);
			// If the input starts with two square brackets, then it must be an array of arrays,
			// and hence a series of several latent vectors rather than just one. In this case,
			// patch all of the levels together into one long level. 
			if(strLatentVector.subSequence(0, 2).equals("[[")) {
				// remove opening/closing brackets
				strLatentVector = strLatentVector.substring(1,strLatentVector.length()-1);
				String levels = "";
				while(strLatentVector.length() > 0) {
					int end = strLatentVector.indexOf("]")+1;
					String oneVector = strLatentVector.substring(0,end);
					System.out.println("ONE VECTOR: " + oneVector);
					levels += eval.stringToFromGAN(oneVector); // Use the GAN
					strLatentVector = strLatentVector.substring(end); // discard processed vector
					if(strLatentVector.length() > 0) {
						levels += ",";
						strLatentVector = strLatentVector.substring(1); // discard leading comma
					}
				}
				levels = "["+levels+"]"; // Put back in brackets
				System.out.println(levels);
				List<List<List<Integer>>> allLevels = JsonReader.JsonToInt(levels);
				// This list contains several separate levels. The following code
				// merges the levels by appending adjacent rows
				ArrayList<List<Integer>> oneLevel = new ArrayList<List<Integer>>();
				// Create the appropriate number of rows in the array
				for(List<Integer> row : allLevels.get(0)) { // Look at first level (assume all are same size)
					oneLevel.add(new ArrayList<Integer>()); // Empty row
				}
				// Now fill up the rows, one level at a time
				for(List<List<Integer>> aLevel : allLevels) {
					int index = 0;
					for(List<Integer> row : aLevel) { // Loot at each row
						oneLevel.get(index++).addAll(row);
					}	
				}
				// Now create the Mario level from the combined list representation
				level = LevelParser.createLevelJson(oneLevel);
			} else { // Otherwise, there must be a single latent vector, and thus a single level
				double[] latentVector = JsonToDoubleArray(strLatentVector);
				level = eval.levelFromLatentVector(latentVector);
			}
		} else {
			System.out.println("Generating level with default vector");
			level = eval.levelFromLatentVector(new double[] {-0.1255295858906291, -0.07870020405899382, 1.5890161572439236, 0.7267247308768985, -1.8041067912924469, 0.27205042018571657, 0.515601906633114, -0.18630356460338765, -1.7256065805398573, -0.8600105210369277, 0.8760548751799427, -0.6862592059643964, -1.8138466117737018, 1.3766828452306128, 0.34598127561066794, 0.40801882969529085, -1.0961122349311918, 0.13520704963829866, 0.9805480997069628, 1.448770300371064, -0.5321173651621988, -0.47910616246859067, 0.5951644926914282, 0.12338540962728808, 1.5641824643017623, 2.0345430414668533, 0.5188942083134859, -0.034099901523537236, -2.3610676236010697, 1.267749758958046, 0.914225209423455, 0.8425461882211073});

//			level = eval.levelFromLatentVector(new double[] {-0.1, 0.2, 0.9145874949043783, 0.22040496692732314, -0.26937122224836074, 0.4, 0.409441181163928, 0.335226952455004, 0.6596558859062596, 0.8, 0.8519559335457034, -0.383165971606384, 0.9, 0.6589536932518415, 0.5417303684063293, 0.06553202264247504, 0.9455146124833034, 0.7573030377127574, 0.1, -0.9677017211946602, 0.05221276117523632, 0.8246512534210801, -0.6053419097271375, 0.09331684907429949, -0.0603188731413758, 0.8401807647690578, -0.13515561779162255, 0.46093402270535655, -0.6999819374760691, 0.04793476638208927, 0.5046556909851677, -0.9});

		}
		
		this.levelm = level;
        
		eval.exit();
        System.exit(0);

	}
	

	public void play() throws IOException {
		Settings.setPythonProgram();
		MarioEvalFunction eval = new MarioEvalFunction();

		
		MarioProcess marioProcess = new MarioProcess();
		marioProcess.launchMario(new String[0], true); // true means there is a human player       
		marioProcess.simulateOneLevel(levelm);
		
		
        eval.exit();
		System.exit(0);
	}
}
