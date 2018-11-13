package src;
import org.jgap.*;
import org.jgap.impl.*;
import robocode.control.BattleSpecification;
import robocode.control.BattlefieldSpecification;
import robocode.control.RobocodeEngine;
import robocode.control.RobotSpecification;

/*
 * Sam Ternent
 * RoboCodeGATemplate
 * Simple config for starting a project with RoboCode and JGAP
 */

@SuppressWarnings("serial")
public class robocodeGA extends FitnessFunction{

	// set amount of generations to evolve
	public static final int MAX_GENERATIONS = 40;
	// set population size per generation
	public static final int POPULATION_SIZE = 10;
	// amount of chromosomes
	public static final int CHROMOSOME_AMOUNT = 4;
	// track scores
	public static int robotScore,enemyScore;

	public void run() throws Exception {

		Configuration conf = new DefaultConfiguration(); // setup GA with default config
		conf.addGeneticOperator(new MutationOperator(conf, 10)); // add new crossover opp 1/10% rate to the GA
		conf.setPreservFittestIndividual(true); // use elitsim
		conf.setFitnessFunction(this); // Set fitness function to conf

		//set up sample genes - add multiple genes to the array
		Gene[] sampleGenes = new Gene[ CHROMOSOME_AMOUNT ];
		sampleGenes[0] = new DoubleGene(conf, 0, 30);
		sampleGenes[1] = new DoubleGene(conf, 0, 30 );
		sampleGenes[2] = new DoubleGene(conf, 0, 30);
		sampleGenes[3] = new DoubleGene(conf, 0, 30);


		IChromosome sampleChromosome = new Chromosome(conf, sampleGenes); // create chromo from genes
		conf.setSampleChromosome(sampleChromosome); // set chromo to conf

		conf.setPopulationSize(POPULATION_SIZE); // create a population

		//set random population
		Genotype population = Genotype.randomInitialGenotype(conf);
		IChromosome fittestSolution = null;

		//evolve population
		for ( int gen = 0; gen<MAX_GENERATIONS; gen++ ) {
			population.evolve(); // evolve population
			fittestSolution = population.getFittestChromosome(); // find fittest of population
			System.out.printf("\nafter %d generations the best solution is %s \n",gen + 1,fittestSolution);
		}

		buildRobot(fittestSolution); //pass best solution to build
		System.exit(0); // clean exit
	}

	public static void main(String[] args) throws Exception {
		robocodeGA robocode = new robocodeGA();

		robocode.run(); // run main
	}

	public boolean battleResults(String name,int score){
		String same = "sample.SamBot"; // enter robot name here with folder prefix

		//get results of battle
		if(name.equals(same)){
			robotScore = score;
			return true;
		}
		else {
			enemyScore = score;
			return false;
		}
	}

	private void buildRobot (IChromosome chromosome) {
		int i = 0;

		//break down chromosome to array
		double[ ] chromo = new double[ CHROMOSOME_AMOUNT ];
		for ( Gene gene : chromosome.getGenes() ) {
			chromo[i] += (Double) gene.getAllele(); // get value from gene
			i++;
		}
		createRobot.create(chromo);	 // create robot - func in createRobot.java
	}

	protected double evaluate( IChromosome chromosome) {
		int fitness,
				numberOfRounds = 40;

		buildRobot(chromosome); // build robot

		RobocodeEngine engine = new RobocodeEngine(new java.io.File("/home/marcos/robocode")); // create robocode engine
		engine.addBattleListener(new battleObserver()); // add battle listener to engine
		engine.setVisible(false); // show battle in GUI ?

		BattlefieldSpecification battlefield = new BattlefieldSpecification(800, 600); // battle field size
		RobotSpecification[] selectedRobots = engine.getLocalRepository("sample.VelociRobot,sample.SamBot"); // which sample bots to take to battle
		BattleSpecification battleSpec = new BattleSpecification(numberOfRounds, battlefield, selectedRobots);

		engine.runBattle(battleSpec, true); // run battle - wait till the battle is over
		engine.close(); // clean up engine
		fitness = robotScore - enemyScore; // set fitness score

		return fitness > 0 ? fitness : 0; // return fitness score if it's over 0
	}

	public void sortScore(int roboScore,int enemScore){
		robotScore = roboScore;
		enemyScore = enemScore;
	}
}
