package AG;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import Tools.Utilities;

public class Knapsackv2 {

	int replicas;
	int numero_generaciones;
	int numero_genes;
	int tamano_poblacion;
	int numero_padres;
	int chromosoma_size;
	int current_generation;
	double probabilidad_cruza;
	double probabilidad_mutacion;

	Individuo individuoGlobal;
	Individuo poblacion[];
	Individuo padre[];
	Individuo descendientes[];

	// int indicePadres[];
	int indiceDescartados[];

	Instance inst;
	long init_time;
	double timeBestSolution;
	double totalTime;

	Knapsackv2(Instance inst) {
		this.inst = inst;
		init_time = System.currentTimeMillis();
	}

	public void AlgoritmoGenetico(double probCruza, double probMut, int tamPob,
			int numGen) {

		inicializarParametrosGeneticos(probCruza, probMut, tamPob, numGen);
		generarPoblacionInicial();
		//generarPoblacionElite();
		poblacion = mutarHijosInsercion(poblacion);
		evaluarPoblacion();
		ordenarPoblacion();

		Utilities.printObject(poblacion);
		individuoGlobal.copyThis(poblacion[0]);
		System.out.print("->>");
		// Utilities.printObject(poblacion);

		for (current_generation = 1; current_generation < numero_generaciones; current_generation++) {
			int[] padres = seleccionarPadresSobranteEstocastico();
			// int[] padres = seleccionarPadresEscalamientoSigma();

			descendientes = cruzaPadresCrossover(padres);
			// descendientes = cruzaDosPuntos(padres);

			// descendientes = mutarHijosFlip(getDescendientes());
			descendientes = mutarHijosInsercion(getDescendientes());

			poblacion = generarNuevaPoblacion(getDescartados(),
					getDescendientes(), getPoblacion());

			evaluarPoblacion();
			ordenarPoblacion();

			for (int i = 0; i < 10; i++)
				if (poblacion[i].getAptitud() > individuoGlobal.getAptitud()) {
					individuoGlobal.copyThis(poblacion[i]);
					setTimeBestSolution((System.currentTimeMillis() - init_time) / 1000.0);

				}
			// poblacion[11].copyThis(individuoGlobal);

			// System.out.println("->>>");
			// Utilities.printObject(descendientes);
			// System.out.println("->>>");

			// System.out.println("IG -> " + individuoGlobal);
		}
		setTotalTime((System.currentTimeMillis() - init_time) / 1000.0);
		System.out.println("IG ->" + individuoGlobal);
		System.out.print("->>");

		// System.out.println(individuoGlobal.getX());
	}

	/**
	 * 
	 * @param probCruza
	 *            between 0 and 1.
	 * @param probMutacion
	 *            between 0 and 1.
	 * @param tamPoblacion
	 *            Positive integer.
	 * @param numeroGeneraciones
	 *            Positive integer.
	 */
	private void inicializarParametrosGeneticos(double probCruza,
			double probMutacion, int tamPoblacion, int numeroGeneraciones) {

		chromosoma_size = inst.getNoItems();
		probabilidad_cruza = probCruza;
		probabilidad_mutacion = probMutacion;
		tamano_poblacion = tamPoblacion;
		numero_generaciones = numeroGeneraciones;

		poblacion = new Individuo[tamano_poblacion];
		individuoGlobal = new Individuo(new int[chromosoma_size],
				current_generation);

		numero_padres = tamano_poblacion / 2;
		if (numero_padres % 2 != 0)
			numero_padres++;

		padre = new Individuo[tamano_poblacion];
		descendientes = new Individuo[numero_padres];
	}

	private void generarPoblacionInicial() {
		Random rnd = new Random();
		for (int i = 0; i < tamano_poblacion; i++) {
			poblacion[i] = new Individuo(new int[chromosoma_size],
					current_generation);
			for (int j = 0; j < chromosoma_size; j++)
				poblacion[i].setGen(j, rnd.nextInt(2));
		}
	}

	private void generarPoblacionElite() {
		for (int i = 0; i < tamano_poblacion; i++) {
			poblacion[i] = new Individuo(new int[chromosoma_size],
					current_generation);
			for (int j = 0; j < chromosoma_size; j++) {
				poblacion[i].setGen(inst.getItem(j).getId(), 1);
				poblacion[i].setWeight(poblacion[i].getWeight()
						+ inst.getItem(j).getWeight());
				if (poblacion[i].getWeight() > inst.getCapacity())
					j = chromosoma_size;
			}

			poblacion[i] = mutar(poblacion[i]);
		}
	}

	private Individuo mutar(Individuo ind){
		
		Random rnd = new Random();		
		int totalGenesToChange = (int) (ind.getTotalItemsInTheBin() * 0.10);
		for (int i = 0; i < totalGenesToChange; i++) {
				int genToTurnOff = rnd.nextInt(ind.getTotalItemsInTheBin());
				int genToTurnOn = rnd.nextInt(ind.getTotalItemsInTheBin());
				
				ind.setGen(genToTurnOff, 0);

				if(ind.getWeight() + inst.getItem(genToTurnOn).getWeight() - inst.getItem(genToTurnOff).getWeight() <= inst.getCapacity() ){
					ind.setGen(genToTurnOn, 1);
				}
		}
		
		return ind;
	}

	private void ordenarPoblacion() {
		ordenarBurbuja(poblacion);
	}

	private int[] seleccionarPadresSobranteEstocastico() {

		int padres[] = new int[numero_padres], totalPicked = 0;
		int descartados[] = new int[tamano_poblacion], totalDescartados = 0;
		int enteros = 0;
		double mat_cuadrados[] = new double[tamano_poblacion];
		double desviacion_estandar, sumacuadrados = 0, sumatoria = 0;
		double promedio = 0;
		double matriz[][] = new double[tamano_poblacion][5];

		for (int i = 0; i < tamano_poblacion; i++) {
			sumatoria += poblacion[i].getAptitud();
			mat_cuadrados[i] = poblacion[i].getAptitud()
					* poblacion[i].getAptitud();
			sumacuadrados += mat_cuadrados[i];
		}
		promedio = sumatoria / tamano_poblacion;

		desviacion_estandar = Math
				.sqrt((tamano_poblacion * sumacuadrados - Math
						.pow(sumatoria, 2)) / Math.pow(tamano_poblacion, 2));

		// System.out.println("/*******Seleccionar padres.***************/");

		for (int i = 0; i < tamano_poblacion; i++) {
			matriz[i][0] = i; /* Numero de individuo. */
			matriz[i][1] = poblacion[i].getAptitud(); /* Aptitud. */
			matriz[i][2] = 1 + ((poblacion[i].getAptitud() - promedio))
					/ (2 * desviacion_estandar); /* Valor esperado. */
			if (matriz[i][2] < 0)
				matriz[i][2] *= -1;

			matriz[i][3] = Math.floor(matriz[i][2]); /* enteros */
			matriz[i][4] = matriz[i][2] - matriz[i][3]; /* diferencia */
			enteros += matriz[i][3];

			if (matriz[i][3] > 0 && totalPicked < numero_padres)
				padres[totalPicked++] = i;
			else
				descartados[totalDescartados++] = i;
			// System.out.print(matriz[i][2] + " " +matriz[i][3] +" "+
			// matriz[i][4] +" "+poblacion[i]);
		}
		// System.out.println("/*******Fin seleccionar padres.***********/");

		for (int i = 0; totalPicked < numero_padres; i++) {
			padres[totalPicked++] = descartados[i];
			descartados[i] = descartados[totalDescartados - 1];
			descartados[totalDescartados - 1] = 0;
			totalDescartados--;
		}

		setDescartados(descartados);
		return padres;
	}

	public int[] seleccionarPadresEscalamientoSigma() {
		double suma = 0, aux = 0, aleat = 0;
		boolean repetido = false;

		// for(i=0; i<valores_poblacion.length; i++)
		// {
		// System.out.println(valores_poblacion[i] + "----" + i);
		// }

		int i, j;
		int indicePadre[] = new int[numero_padres];
		double mat_cuadrados[] = new double[tamano_poblacion];
		double desviacion, sumacuadrados = 0, sumatoria = 0;
		double mat_esperados[] = new double[tamano_poblacion];
		double promedio = 0;

		double ruleta[][] = new double[tamano_poblacion][5];

		/*
		 * Para la seleccion de los padres se selecciono el metodo de la
		 * escalaamiento sigma
		 */

		for (i = 0; i < tamano_poblacion; i++) {
			sumatoria = sumatoria + poblacion[i].getAptitud();
		}
		promedio = sumatoria / tamano_poblacion;

		for (i = 0; i < tamano_poblacion; i++) {
			mat_cuadrados[i] = poblacion[i].getAptitud()
					* poblacion[i].getAptitud();
		}
		for (i = 0; i < tamano_poblacion; i++) {
			sumacuadrados = sumacuadrados + mat_cuadrados[i];
		}

		desviacion = Math.sqrt((tamano_poblacion * sumacuadrados - Math.pow(
				sumatoria, 2)) / Math.pow(tamano_poblacion, 2));

		if (desviacion != 0) {
			for (i = 0; i < tamano_poblacion; i++) {
				mat_esperados[i] = 1 + (poblacion[i].getAptitud() - promedio)
						/ (2 * desviacion);
			}
		} else {
			for (i = 0; i < tamano_poblacion; i++) {
				mat_esperados[i] = 1.0;
			}
		}

		// for (i = 0; i < tamano_poblacion; i++) {
		// System.out.println("---" + mat_esperados[i]);
		//
		// }

		// ------------------------------------------------------------------------------

		/*
		 * Primero se debe crear la tabla de frecuencias relativas (ruleta)
		 * empezamos llenando las dos primeras columnas
		 */
		suma = 0;
		for (i = 0; i < tamano_poblacion; i++) {
			ruleta[i][0] = i;
			ruleta[i][1] = poblacion[i].getAptitud();
			suma += poblacion[i].getAptitud();
		}

		/*
		 * Ahora, para construir la tercera columna, se tomara en cuenta el
		 * valor de la variable acumuladora "suma", obteniendo la frecuencia
		 * relativa
		 */
		for (i = 0; i < tamano_poblacion; i++)
			ruleta[i][2] = ruleta[i][1] / suma;

		/*
		 * Una vez hecho esto, es necesario invertir las frecuencias relativas
		 * de modo que el de menor valor objetivo tenga la mayor probabilidad de
		 * ser elegido
		 */
		for (i = 0; i < tamano_poblacion / 2; i++) {
			aux = ruleta[i][2];
			ruleta[i][2] = ruleta[tamano_poblacion - i - 1][2];
			ruleta[tamano_poblacion - i - 1][2] = aux;
		}

		/*
		 * Lo que sigue es crear los rangos de las frecuencias, el primero y el
		 * ultimo se calculan fuera del ciclo
		 */
		ruleta[0][3] = 0;
		ruleta[0][4] = ruleta[0][2];

		for (i = 1; i < tamano_poblacion; i++) {
			ruleta[i][3] = ruleta[i - 1][4];
			ruleta[i][4] = ruleta[i][3] + ruleta[i][2];
		}
		ruleta[tamano_poblacion - 1][4] = 1;

		/*
		 * Ahora es necesario seleccionar los padres, el numero de padres
		 * seleccionados se encuentra definido en la variable "numero_padres"
		 */
		int apt_padres = 0;
		for (i = 0; i < numero_padres; i++) {
			/* Genera un numero aleatorio */
			aleat = Math.random();

			/* Verifica dentro de cual rango cayo el aleatorio */
			for (j = 0; j < tamano_poblacion; j++) {
				/* Si se encuentra el padre seleccionado */
				if (ruleta[j][3] < aleat && ruleta[j][4] >= aleat) {
					repetido = false;
					/* Checar que no haya sido seleccionado con anterioridad */
					for (int k = 0; k < apt_padres; k++) {
						/* si el padre ya se encuentra seleccionado */
						if (ruleta[j][0] == indicePadre[k]) {
							repetido = true;
							i--;
						}
					}

					if (repetido == false) {
						/*
						 * Si el padre no habia sido seleccionado con
						 * anterioridad entonces el id se copia a
						 * "indice_padres_seleccionados" y el padre a la matriz
						 * "padres"
						 */

						indicePadre[apt_padres] = (int) ruleta[j][0];
						apt_padres++;
					}
					j = tamano_poblacion;
				}
			}
		}
		return indicePadre;
	}

	private Individuo[] cruzaDosPuntos(int[] indicePadre) {
		double num_aleat;

		for (int i = 0; i < numero_padres; i += 2) {

			num_aleat = Math.random();
			if (num_aleat <= probabilidad_cruza) {

				descendientes[i] = new Individuo(poblacion[indicePadre[i]],
						poblacion[indicePadre[i + 1]], current_generation);

				descendientes[i + 1] = new Individuo(
						poblacion[indicePadre[i + 1]],
						poblacion[indicePadre[i]], current_generation);

				for (int l = 0; l < chromosoma_size; l++) {
					if (l < chromosoma_size / 2)
						descendientes[i].setGen((l + chromosoma_size / 2),
								poblacion[indicePadre[i + 1]].getGen(l));

					else
						descendientes[i + 1].setGen(l - chromosoma_size / 2,
								poblacion[indicePadre[i]].getGen(l));
				}
				// System.out.println("/***/");
				//
				// Utilities.printArray(poblacion[indicePadre[i]].getChromosoma());
				// Utilities.printArray(poblacion[indicePadre[i +
				// 1]].getChromosoma());
				//
				// Utilities.printArray(descendientes[i].getChromosoma());
				// Utilities.printArray(descendientes[i + 1].getChromosoma());
				//
				// System.out.println("/***/");
				//
			} else {
				descendientes[i] = new Individuo(poblacion[indicePadre[i]]);

				descendientes[i + 1] = new Individuo(
						poblacion[indicePadre[i + 1]]);
			}
		}

		return descendientes;
	}

	/**
	 * Selecciona diferentes puntos del padre 1 y acompleta tomando los valores
	 * del padre con los puntos no seleccionados para generar el hijo 1.
	 * 
	 * @param indicePadre
	 * @return
	 */
	private Individuo[] cruzaPadresCrossover(int[] indicePadre) {
		double num_aleat;
		Random rand = new Random();

		for (int i = 0; i < numero_padres; i += 2) {

			num_aleat = Math.random();
			if (num_aleat <= probabilidad_cruza) {

				descendientes[i] = new Individuo(poblacion[indicePadre[i]],
						poblacion[indicePadre[i + 1]], current_generation);

				descendientes[i + 1] = new Individuo(
						poblacion[indicePadre[i + 1]],
						poblacion[indicePadre[i]], current_generation);

				/* 1.- Seleccionar puntos aleatorios. */

				/*
				 * Si true se mantiene, si false se intercambia por el alelo del
				 * padre2.
				 */
				for (int l = 0; l < chromosoma_size; l++) {
					if (!rand.nextBoolean())
						descendientes[i].setGen(l,
								poblacion[indicePadre[i + 1]].getGen(l));

					if (!rand.nextBoolean())
						descendientes[i + 1].setGen(l,
								poblacion[indicePadre[i]].getGen(l));
				}
				// System.out.println("/***/");
				//
				// Utilities.printArray(poblacion[indicePadre[i]].getChromosoma());
				// Utilities.printArray(poblacion[indicePadre[i +
				// 1]].getChromosoma());
				//
				// Utilities.printArray(descendientes[i].getChromosoma());
				// Utilities.printArray(descendientes[i + 1].getChromosoma());
				//
				// System.out.println("/***/");
				//
			} else {
				descendientes[i] = new Individuo(poblacion[indicePadre[i]]);

				descendientes[i + 1] = new Individuo(
						poblacion[indicePadre[i + 1]]);
			}
		}
		return descendientes;
	}

	private Individuo[] cruzarPadresPositionBasedCrossover(int[] indicePadre) {

		Individuo hijo[] = new Individuo[2], padre1tmp, padre2tmp;
		Individuo padre1 = null;
		Individuo padre2 = null;
		int valores[] = new int[inst.getNoItems()];
		int t = 0, ptrDesc = 0;
		double num_aleat;
		Random rand = new Random();

		for (int i = 0; i < numero_padres; i += 2) {

			num_aleat = Math.random();
			if (num_aleat <= probabilidad_cruza) {
				hijo = new Individuo[2];

				padre1 = poblacion[indicePadre[i]];
				padre2 = poblacion[indicePadre[i + 1]];
				padre2tmp = padre2;
				padre1tmp = padre1;

				for (int k = 0; k < 2; k++) {

					/* 1.- Seleccionar puntos aleatorios. */

					boolean posiciones_aleatorias[] = new boolean[chromosoma_size];
					t = 0;
					for (int l = 0; l < inst.getNoItems(); l++)
						posiciones_aleatorias[l] = rand.nextBoolean();

					if (k == 0) {
						/*
						 * 2.- Produce hijo utilizando de P1 los puntos
						 * seleccionados
						 */
						hijo[k] = new Individuo(new int[chromosoma_size],
								current_generation);

						for (int l = 0; l < chromosoma_size; l++)
							if (posiciones_aleatorias[l]) {
								hijo[k].setGen(l, padre1.getGen(l));
								valores[t++] = padre1.getGen(l);
							}

						/*
						 * 3.- Borra los valores seleccionados de P2. La
						 * secuencia resultante de valores se usará para
						 * completar el hijo.
						 */
						for (int l = 0; l < t; l++)
							for (int j = 0; j < chromosoma_size; j++)
								if (valores[l] == padre2tmp.getGen(l)) {
									padre2tmp.setGen(j, 0);
									j = chromosoma_size;
								}

						/*
						 * 4.- Coloca en el hijo los valores faltantes de
						 * izquierda a derecha, de acuerdo a la secuencia P2.
						 */

						int pointer = 0;
						for (int l = 0; l < chromosoma_size; l++)
							if (hijo[k].getGen(l) == 0)
								for (int j = pointer; j < chromosoma_size; j++)
									if (padre2tmp.getGen(j) != 0) {
										hijo[k].setGen(l, padre2tmp.getGen(j));
										pointer = j + 1;
										j = chromosoma_size;
									}
					}

					if (k == 1) {
						/*
						 * Se repite del paso 1 - 4 para el segundo hijo
						 * utilizando P2.
						 */
						hijo[k] = new Individuo(new int[chromosoma_size],
								current_generation);
						for (int l = 0; l < chromosoma_size; l++)
							if (posiciones_aleatorias[l]) {
								hijo[k].setGen(l, padre2.getGen(l));
								valores[t++] = padre2.getGen(l);
							}

						for (int l = 0; l < t; l++)
							for (int j = 0; j < chromosoma_size; j++)
								if (valores[l] == padre1tmp.getGen(j)) {
									padre1tmp.setGen(j, 0);
									j = chromosoma_size;
								}

						int pointer = 0;
						for (int l = 0; l < chromosoma_size; l++)
							if (hijo[k].getGen(l) == 0)
								for (int j = pointer; j < chromosoma_size; j++)
									if (padre1tmp.getGen(j) != 0) {
										hijo[k].setGen(l, padre1tmp.getGen(j));
										pointer = j + 1;
										j = chromosoma_size;
									}
					}

					descendientes[ptrDesc] = new Individuo(hijo[k]);
					ptrDesc++;

				}
				System.out.println(/** Cruza **/
				);
				Utilities.printArray(padre1.getChromosoma());
				Utilities.printArray(padre2.getChromosoma());

				Utilities.printArray(hijo[0].getChromosoma());
				Utilities.printArray(hijo[1].getChromosoma());
				System.out.println("/**Fin de cruza**/");

			} else {

				descendientes[ptrDesc] = new Individuo(
						poblacion[indicePadre[i]]);
				descendientes[ptrDesc + 1] = new Individuo(
						poblacion[indicePadre[i + 1]]);

				ptrDesc += 2;

			}

		}
		return descendientes;
	}

	private Individuo[] mutarHijosInsercion(Individuo[] descendientes) {
		Random rnd = new Random();
		for (int i = 0; i < descendientes.length; i++) {
			int posOrigen = rnd.nextInt(chromosoma_size);
			int posDestino = rnd.nextInt(chromosoma_size);

			int origen = descendientes[i].getGen(posOrigen);

			if (posOrigen > posDestino)
				/* Desplaza los elementos hacia la derecha */
				for (int j = posOrigen; j > posDestino; j--)
					descendientes[i].setGen(j, descendientes[i].getGen(j - 1));
			else
				/* Desplaza los elementos hacia la izquierda */
				for (int j = posOrigen; j < posDestino; j++)
					descendientes[i].setGen(j, descendientes[i].getGen(j + 1));

			descendientes[i].setGen(posDestino, origen);
		}

		return descendientes;
	}

	private Individuo[] mutarHijosFlip(Individuo[] descendientes) {
		Random rnd = new Random();
		for (int ind = 0; ind < descendientes.length; ind++) {
			int pos = rnd.nextInt(chromosoma_size);
			if (descendientes[ind].getGen(pos) == 1)
				descendientes[ind].setGen(pos, 0);
			else
				descendientes[ind].setGen(pos, 1);
		}

		return descendientes;
	}

	private Individuo[] generarNuevaPoblacion(int[] descartados,
			Individuo[] descendientes, Individuo[] poblacion) {

		for (int i = 0; i < numero_padres; i++)
			poblacion[descartados[i]].copyThis(descendientes[i]);
		return poblacion;
	}

	private void seleccionarMasAptos(Individuo[] ind) {
		int apto[] = new int[tamano_poblacion];
		int noApto[] = new int[tamano_poblacion];
		int ptrAptos = 0, ptrNoAptos = 0;

		// Probar con el Promedio
		for (int i = 0; i < tamano_poblacion; i++)
			if (ind[i].getAptitud() > 40 && !isOverWeight(ind[i].getWeight()))
				apto[ptrAptos++] = i;
			else
				noApto[ptrNoAptos++] = i;
		// setAptos();
		// setNoAptos();
	}

	private boolean isOverWeight(int weight) {
		if (weight > inst.getCapacity())
			return true;
		return false;
	}

	private void evaluarPoblacion() {
		for (int i = 0; i < tamano_poblacion; i++)
			poblacion[i] = evaluarIndividuo(poblacion[i]);

	}

	/**
	 * 
	 * @param ind
	 *            Sample to be evaluate.
	 * @return An array of size 3 including the weight, gain, and 1 if is a
	 *         feasible solution, 0 elsewhere.
	 */

	private Individuo evaluarIndividuo(Individuo ind) {
		double[] result = new double[5];
		Random rnd = new Random();

		ind.setWeight(0);
		ind.setGain(0);
		ind.setTotalItemsInTheBin(0);

		for (int i = 0; i < chromosoma_size; i++)
			if (ind.getGen(i) == 1)
				ind.addItem(i, inst.getItems()[i][0], inst.getItems()[i][1]);

		int tmp = ind.getTotalItemsInTheBin();

		while (ind.getWeight() > inst.getCapacity()) {
			int index = rnd.nextInt(tmp);
			int item = ind.getItems()[index];
			ind.removeItem(item, inst.getItems()[item][0],
					inst.getItems()[item][1]);
		}
		ind.setFeasible(1);
		ind.setAptitud(ind.getGain() * 0.01);

		return ind;
	}

	/**
	 * 
	 * @param ind
	 *            An array of the population to be sort.
	 * @return An array with the sorted population.
	 */
	public Individuo[] ordenarBurbuja(Individuo[] ind) {
		Individuo aux;
		for (int j = 0; j < tamano_poblacion; j++)
			for (int i = 0; i < tamano_poblacion - 1; i++)
				if (ind[i].getAptitud() < ind[i + 1].getAptitud()) {
					/*
					 * Si el valor mas a la izquierda es mayor que su
					 * consecutivo de la derecha entonces se realiza el
					 * intercambio tanto en el vector que almacena los valores
					 * objetivos (valores_poblacion) como en la misma poblacion.
					 */

					/* Intercambio en los valores objetivo */
					aux = ind[i];
					ind[i] = ind[i + 1];
					ind[i + 1] = aux;

				}

		return ind;
	}

	/**
	 * @return the individuoGlobal
	 */
	public Individuo getIndividuoGlobal() {
		return individuoGlobal;
	}

	/**
	 * @param individuoGlobal
	 *            the individuoGlobal to set
	 */
	public void setIndividuoGlobal(Individuo individuoGlobal) {
		this.individuoGlobal = individuoGlobal;
	}

	private void setDescendientes(Individuo[] descendientes) {
		this.descendientes = descendientes;
	}

	private void setPoblacion(Individuo[] poblacion) {
		this.poblacion = poblacion;
	}

	private Individuo[] getPoblacion() {
		return this.poblacion;
	}

	/**
	 * @return the timeBestSolution
	 */
	public double getTimeBestSolution() {
		return timeBestSolution;
	}

	/**
	 * @param timeBestSolution
	 *            the timeBestSolution to set
	 */
	public void setTimeBestSolution(double timeBestSolution) {
		this.timeBestSolution = timeBestSolution;
	}

	/**
	 * @return the totalTime
	 */
	public double getTotalTime() {
		return totalTime;
	}

	/**
	 * @param totalTime
	 *            the totalTime to set
	 */
	public void setTotalTime(double totalTime) {
		this.totalTime = totalTime;
	}

	private Individuo[] getDescendientes() {
		return this.descendientes;
	}

	private void setDescartados(int[] descartados) {
		this.indiceDescartados = descartados;
	}

	private int[] getDescartados() {
		return this.indiceDescartados;
	}

	public void saveResults(String file) {
		String workPath = System.getProperty("user.dir") + File.separator
				+ "Experiments" + File.separator + file;
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
					workPath), true));

			bw.write("" + this);
			bw.flush();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String toString() {
		return String
				.format("Instance: %7s AG: %1.2f probCruza %1.2f probMutacion %4s TamPob %4s NumGener TotalTime  %6.6f seg BestTime   %6.6f seg %s ",
						inst, this.probabilidad_cruza,
						this.probabilidad_mutacion, this.tamano_poblacion,
						this.numero_generaciones, this.getTotalTime(),
						this.getTimeBestSolution(), this.individuoGlobal);
	}

	public static void main(String[] args) {
		// for (int i = 1; i <= 2; i++) {
		// Instance instance = Instance.readInstance(Instance.getInstance(i));
		// for (double pc = 0.1; pc < 1; pc += 0.2)
		// for (double pm = 0.1; pm < 1; pm += 0.2)
		// for (int ng = 100; ng <= 1000; ng += 200)
		// for (int j = 1; j <= 5; j++) {
		// Knapsackv2 bpp = new Knapsackv2(instance);
		// bpp.AlgoritmoGenetico(pc, pm, 100, ng);
		// bpp.saveResults("E4");
		// }
		// }

		Knapsackv2 bpp = new Knapsackv2(Instance.readInstance(Instance.getInstance(1)));
		bpp.AlgoritmoGenetico(0.8, 0.1, 100, 200);
		bpp.saveResults("E5-Big Generation");
	}
}
