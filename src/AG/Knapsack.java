package AG;
import java.util.Random;

import Tools.Utilities;

public class Knapsack {
	int replicas;
	int numero_generaciones;
	int numero_genes;
	int tamano_poblacion;
	int numero_padres;
	int chromosoma_size;
	double probabilidad_cruza;
	double probabilidad_mutacion;

	int resultados[][]; // Valores de los resultados
						// instancias x replicas
	int aptitud_global;
	int chromosoma_global[];

	int aptitud_individuo[];
	int weight[];
	int poblacion[][];

	int chromosoma_padre[][];

	int descendientes[][];
	int indice_padres_seleccionados[];

	int descartados[];

	Instance inst;

	Knapsack(Instance inst) {
		this.inst = inst;
	}

	public void AlgoritmoGenetico() {

		inicializarParametrosGeneticos();
		generarPoblacionInicial();
		evaluarPoblacion();
		ordenarPoblacion();

		aptitud_global = aptitud_individuo[0];
		for (int gene = 0; gene < chromosoma_size; gene++)
			chromosoma_global[gene] = poblacion[0][gene];

		for (int generacion = 0; generacion < numero_generaciones; generacion++) {
			int[] padres = seleccionarPadresSobranteEstocastico();

			descendientes = cruzarPadresPositionBasedCrossover(padres);

			descendientes = mutarHijosInsercion(getDescendientes());
			generarNuevaPoblacion(getDescartados(), getDescendientes(),
					getPoblacion());

			evaluarPoblacion();
			ordenarNuevaPoblacion();
			seleccionarMasAptos(getPoblacion(), aptitud_individuo, weight);

			if (aptitud_individuo[0] < aptitud_global) {
				aptitud_global = aptitud_individuo[0];
				for (int gen = 0; gen < chromosoma_size; gen++)
					chromosoma_global[gen] = poblacion[0][gen];
			}
			Utilities.printArray(aptitud_individuo);
			reinicializarEstructuras();
		}
	}

	private void inicializarParametrosGeneticos() {

		chromosoma_size = inst.getNoItems();
		probabilidad_cruza = 0.9;
		probabilidad_mutacion = 0.1;
		tamano_poblacion = 100;
		numero_generaciones = 500;

		poblacion = new int[tamano_poblacion][chromosoma_size];
		chromosoma_global = new int[chromosoma_size];
		aptitud_individuo = new int[tamano_poblacion];
		weight = new int[tamano_poblacion];

		numero_padres = tamano_poblacion / 2;
		if (numero_padres % 2 != 0)
			numero_padres++;

		chromosoma_padre = new int[numero_padres][chromosoma_size];
		indice_padres_seleccionados = new int[numero_padres];
		descendientes = new int[numero_padres][chromosoma_size];
	}

	private void generarPoblacionInicial() {
		Random rnd = new Random();
		for (int i = 0; i < tamano_poblacion; i++)
			for (int j = 0; j < chromosoma_size; j++)
				poblacion[i][j] = rnd.nextInt(2);
	}

	private void evaluarPoblacion() {
		for (int i = 0; i < tamano_poblacion; i++) {
			int res[] = evaluar_chromosoma(poblacion[i]);
			weight[i] = res[0];
			aptitud_individuo[i] = res[1];
		}
	}

	private void ordenarPoblacion() {
		// quickSort(aptitud_individuo, 0,
		// aptitud_individuo.length - 1);

		ordenarBurbuja(aptitud_individuo, weight);
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
			sumatoria += aptitud_individuo[i];
			mat_cuadrados[i] = aptitud_individuo[i] * aptitud_individuo[i];
			sumacuadrados += mat_cuadrados[i];
		}
		promedio = sumatoria / tamano_poblacion;

		desviacion_estandar = Math
				.sqrt((tamano_poblacion * sumacuadrados - Math
						.pow(sumatoria, 2)) / Math.pow(tamano_poblacion, 2));

		for (int i = 0; i < tamano_poblacion; i++) {
			matriz[i][0] = i; /* Numero de individuo. */
			matriz[i][1] = aptitud_individuo[i]; /* Aptitud. */
			matriz[i][2] = 1 + ((aptitud_individuo[i] - promedio))
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
		}

		for (int i = 0; totalPicked < numero_padres; i++) {
			padres[totalPicked++] = descartados[i];
			descartados[i] = descartados[totalDescartados - 1];
			descartados[totalDescartados - 1] = 0;
			totalDescartados--;
		}
		
		setDescartados(descartados);
		return padres;
	}

	private int[][] cruzarPadresPositionBasedCrossover(int[] padres) {

		int hijos[][], padre1tmp[], padre2tmp[];
		int padre1[] = new int[inst.getNoItems()];
		int padre2[] = new int[inst.getNoItems()];
		int valores[] = new int[inst.getNoItems()];
		int t = 0, ptrDesc = 0;
		double num_aleat;
		Random rand = new Random();

		for (int i = 0; i < numero_padres; i += 2) {

			num_aleat = Math.random();
			if (num_aleat <= probabilidad_cruza) {
				hijos = new int[2][chromosoma_size];
				padre2tmp = new int[chromosoma_size];
				padre1tmp = new int[chromosoma_size];

				for (int j = 0; j < chromosoma_size; j++) {
					padre1[j] = poblacion[padres[i]][j];
					padre2[j] = poblacion[padres[i + 1]][j];
					padre2tmp[j] = padre2[j];
					padre1tmp[j] = padre1[j];
				}

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

						for (int l = 0; l < padre1.length; l++)
							if (posiciones_aleatorias[l]) {
								hijos[k][l] = padre1[l];
								valores[t++] = padre1[l];
							}

						/*
						 * 3.- Borra los valores seleccionados de P2. La
						 * secuencia resultante de valores se usara para
						 * completar el hijo.
						 */
						for (int l = 0; l < t; l++)
							for (int j = 0; j < padre2tmp.length; j++)
								if (valores[l] == padre2tmp[j]) {
									padre2tmp[j] = 0;
									j = padre2tmp.length;
								}

						/*
						 * 4.- Coloca en el hijo los valores faltantes de
						 * izquierda a derecha, de acuerdo a la secuencia P2.
						 */

						int pointer = 0;
						for (int l = 0; l < hijos[k].length; l++)
							if (hijos[k][l] == 0)
								for (int j = pointer; j < padre2tmp.length; j++)
									if (padre2tmp[j] != 0) {
										hijos[k][l] = padre2tmp[j];
										pointer = j + 1;
										j = padre2tmp.length;
									}
					}

					if (k == 1) {
						/*
						 * Se repite del paso 1 - 4 para el segundo hijo
						 * utilizando P2.
						 */
						for (int l = 0; l < padre2.length; l++)
							if (posiciones_aleatorias[l]) {
								hijos[k][l] = padre2[l];
								valores[t++] = padre2[l];
							}

						for (int l = 0; l < t; l++)
							for (int j = 0; j < padre1tmp.length; j++)
								if (valores[l] == padre1tmp[j]) {
									padre1tmp[j] = 0;
									j = padre1tmp.length;
								}

						int pointer = 0;
						for (int l = 0; l < hijos[k].length; l++)
							if (hijos[k][l] == 0)
								for (int j = pointer; j < padre1tmp.length; j++)
									if (padre1tmp[j] != 0) {
										hijos[k][l] = padre1tmp[j];
										pointer = j + 1;
										j = padre1tmp.length;
									}
					}

					for (int l = 0; l < chromosoma_size; l++)
						descendientes[ptrDesc][l] = hijos[k][l];
					ptrDesc++;

				}

			} else {
				for (int l = 0; l < chromosoma_size; l++) {
					descendientes[ptrDesc][l] = poblacion[padres[i]][l];
					descendientes[ptrDesc + 1][l] = poblacion[padres[i + 1]][l];
				}
				ptrDesc += 2;
			}

		}
		return descendientes;
	}

	private int[][] mutarHijosInsercion(int[][] descendientes) {
		Random rnd = new Random();
		for (int i = 0; i < descendientes.length; i++) {
			int posOrigen = rnd.nextInt(chromosoma_size);
			int posDestino = rnd.nextInt(chromosoma_size);

			int origen = descendientes[i][posOrigen];

			if (posOrigen > posDestino)
				/* Desplaza los elementos hacia la derecha */
				for (int j = posOrigen; j > posDestino; j--)
					descendientes[i][j] = descendientes[i][j - 1];
			else
				/* Desplaza los elementos hacia la izquierda */
				for (int j = posOrigen; j < posDestino; j++)
					descendientes[i][j] = descendientes[i][j + 1];

			descendientes[i][posDestino] = origen;
		}

		return descendientes;
	}

	private void generarNuevaPoblacion(int[] descartados,
			int[][] descendientes, int[][] poblacion) {
		
		for (int i = 0; i < numero_padres; i++)
			poblacion[descartados[i]] = descendientes[i];
	
	}

	private void ordenarNuevaPoblacion() {
	}

	private void seleccionarMasAptos(int[][] poblacion, int[] aptitud,
			int[] weight) {
		int apto[] = new int[tamano_poblacion];
		int noApto[] = new int[tamano_poblacion];
		int ptrAptos = 0, ptrNoAptos = 0;

		for (int i = 0; i < tamano_poblacion; i++)
			if (aptitud[i] > 100 && !isOverWeight(weight[i])) // Promedio
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

	private void reinicializarEstructuras() {
	}

	private int[] evaluar_chromosoma(int[] chromosoma) {
		int[] result = new int[2];

		for (int i = 0; i < chromosoma.length; i++)
			if (chromosoma[i] == 1) {
				result[0] += inst.getItems()[i][0];/* Peso del item */
				result[1] += inst.getItems()[i][1];/* Ganancia del item */
			}

		return result;
	}

	public int[] ordenarBurbuja(int[] arr, int[] weight) {
		// double aux = 0;
		int aux = 0;
		int aux2 = 0;
		for (int j = 0; j < tamano_poblacion; j++)
			for (int i = 0; i < tamano_poblacion - 1; i++) {
				if (aptitud_individuo[i] < aptitud_individuo[i + 1]
						&& !isOverWeight(weight[i])) {
					/*
					 * Si el valor mas a la izquierda es mayor que su
					 * consecutivo de la derecha entonces se realiza el
					 * intercambio tanto en el vector que almacena los valores
					 * objetivos (valores_poblacion) como en la misma poblacion.
					 */

					/* Intercambio en los valores objetivo */
					aux = aptitud_individuo[i];
					aptitud_individuo[i] = aptitud_individuo[i + 1];
					aptitud_individuo[i + 1] = aux;

					int auxW = weight[i];
					weight[i] = weight[i + 1];
					weight[i + 1] = auxW;

					/* Intercambio entre los miembros de la poblacion */
					for (int k = 0; k < chromosoma_size; k++) {
						aux2 = poblacion[i][k];
						poblacion[i][k] = poblacion[i + 1][k];
						poblacion[i + 1][k] = aux2;
					}
				}
			}
		return arr;
	}

	private void setDescendientes(int[][] descendientes) {
		this.descendientes = descendientes;
	}

	private void setPoblacion(int[][] poblacion) {
		this.descartados = descartados;
	}

	private int[][] getPoblacion() {
		return this.poblacion;
	}

	private int[][] getDescendientes() {
		return this.descendientes;
	}

	private void setDescartados(int[] descartados) {
		this.descartados = descartados;
	}

	private int[] getDescartados() {
		return this.descartados;
	}

	public static void main(String[] args) {

		Knapsack knapsack = new Knapsack(Instance.readInstance(Instance.getInstance(1)));
		knapsack.AlgoritmoGenetico();
	}
}
