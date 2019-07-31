package AG;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class AlgoritmoCelular {

	private Celula[] cell = new Celula[3];
	private Individuo globalSol = new Individuo();

	private boolean globaSearchStagnated = false;
	private int indexBestCell;
	public static Instance inst;

	public AlgoritmoCelular(Instance inst) {
		AlgoritmoCelular.inst = inst;
	}

	public boolean anyCellNotStagnated() {
		boolean stagnated = true;
		for (int i = 0; i < cell.length; i++)
			stagnated &= cell[i].isStagnated();

		return !stagnated;

	}

	/**
	 * Realiza la comunicacion entre celulas al comparar los resultados.
	 * 
	 * @param cell
	 * @return el indice de la celula que encontro la mejor solución.
	 */
	private int getBestCell(Celula[] cell) {

		int best = 0;

		for (int i = 0; i < cell.length; i++)
			if (cell[i].getIndividuoGlobal().getAptitud() > cell[best]
					.getIndividuoGlobal().getAptitud())
				best = i;

		return best;
	}

	private boolean globalSearchNotStagnated() {
		return !globaSearchStagnated;
	}

	private void globaSearchSetStagnated() {
		globaSearchStagnated = true;
	}

	public void start(double probCruza, double probMut, int tamPob, int numGen) {

		int iterationsWithouttImprove = 0;

		cell[0] = new Celula1(inst, probCruza, probMut, tamPob);
		cell[1] = new Celula2(inst, probCruza, probMut, tamPob);
		cell[2] = new Celula3(inst, probCruza, probMut, tamPob);
		// cell[3] = new Celula4(inst, probCruza, probMut, tamPob);

		Individuo[] poblacion = cell[0].generarPoblacionInicial(tamPob);
		Individuo bestSolInCell = new Individuo();

		for (Celula c : cell)
			c.setPoblacion(poblacion);

		while (globalSearchNotStagnated()) {
			while (anyCellNotStagnated())
				for (Celula cel : cell)
					if (!cel.isStagnated())
						cel.nextGeneration();

			/**
			 * Comunicacion offline. Obtiene el indice de la mejor solucion.
			 */
			int indexBestCell = getBestCell(cell);

			/**
			 * La mejor solucion se copia temporalmente.
			 */
			bestSolInCell.copyThis(cell[indexBestCell].getIndividuoGlobal());

			/**
			 * Si la mejor solucion encontrada por las celulas es mejor que la
			 * global entonces se almacena.
			 * 
			 * El contador de iteracion sin mejora vuelve a cero.
			 * 
			 * La poblacion de la celula que encontro la mejor solucion se pasa
			 * a las demas celulas.
			 * 
			 */
			if (bestSolInCell.getAptitud() > globalSol.getAptitud()) {
				iterationsWithouttImprove = 0;
				globalSol.copyThis(bestSolInCell);
				poblacion = cell[indexBestCell].getPoblacion();
				this.indexBestCell = indexBestCell;

				for (Celula c : cell)
					c.restart(globalSol, poblacion);

			} else if (iterationsWithouttImprove++ == 50)
				/**
				 * Si en X generaciones no hubo mejora entonces se llego a un
				 * estancamiento global.
				 */
				globaSearchSetStagnated();

		}
		System.out.println("->>>Global search is stagnated...");
		System.out.println("IG ->" + globalSol);
		System.out.println("Found in Cell: " + (this.indexBestCell + 1));
		System.out.println("Total time: "
				+ cell[this.indexBestCell].getTotalTime());
		System.out.println("Generaciones por celula: ");
		System.out.println("\t Cell 1-> " + cell[0].getCurrentGeneration());
		System.out.println("\t Cell 2-> " + cell[1].getCurrentGeneration());
		System.out.println("\t Cell 3-> " + cell[2].getCurrentGeneration());
		// System.out.println("\t Cell 4-> " + cell[3].getCurrentGeneration());

	}

	public String toString() {
		return String
				.format("AC [ SolGlobal: %7s | GC1: %7d GC2: %7d GC3 %7d | TotalTime  %6.6f]",
						this.globalSol, cell[0].getCurrentGeneration(),
						cell[1].getCurrentGeneration(),
						cell[2].getCurrentGeneration(),
						cell[this.indexBestCell].getTotalTime());
	}

	public void saveResults(String file) {
		String workPath = System.getProperty("user.dir") + File.separator
				+ "Experiments" + File.separator + file;
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
					workPath), true));

			bw.write(cell[0].getCurrentGeneration() + " "
					+ cell[1].getCurrentGeneration() + " "
					+ cell[2].getCurrentGeneration() + " "
					+ (this.indexBestCell + 1) + " "
					+ cell[this.indexBestCell].getTotalTime() + " " + globalSol);
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {

		for (int i = 4; i <= 4; i++) {
			for (int j = 0; j < 5; j++) {

				Instance instance = Instance.readInstance(Instance
						.getInstance(i));

				AlgoritmoCelular AC = new AlgoritmoCelular(instance);
				AC.start(0.8, 0.1, 100, 200);
				AC.saveResults("E8-AlgoritmoCelular");
			}
		}
	}

}
