package AG;

import java.util.Random;

public class Celula4 extends Celula {

	public Celula4(Instance inst, double probCruza, double probMut, int tamPob) {
		super(inst, probCruza, probMut, tamPob);
		id = 4;
	}

	/**
	 * Cruza: position based crossover.
	 */
	@Override
	protected Individuo[] cruza(int[] indicePadre) {
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

				/**
				 * Si true se mantiene, si false se intercambia por el alelo del
				 * padre2.
				 **/
				for (int l = 0; l < chromosoma_size; l++) {
					if (!rand.nextBoolean())
						descendientes[i].setGen(l,
								poblacion[indicePadre[i + 1]].getGen(l),
								getInst().getItemGain(l), getInst()
										.getItemWeight(l));

					if (!rand.nextBoolean())
						descendientes[i + 1].setGen(l,
								poblacion[indicePadre[i]].getGen(l), getInst()
										.getItemGain(l), getInst()
										.getItemWeight(l));
				}

			} else {
				descendientes[i] = new Individuo(poblacion[indicePadre[i]]);

				descendientes[i + 1] = new Individuo(
						poblacion[indicePadre[i + 1]]);
			}
		}
		return descendientes;
	}

	/**
	 * Mutacion elimina 5 genes y activa otros hasta que se llene la mochila.
	 **/
	@Override
	protected Individuo mutar(Individuo ind) {
		Random rnd = new Random();
		int totalGenesToChange = 5;//(int) (chromosoma_size * 0.10);

		for (int i = 0; i < totalGenesToChange; i++) {

			int genToTurnOff = rnd.nextInt(chromosoma_size);
			ind.setGen(genToTurnOff, 0, inst.getItemGain(genToTurnOff),
					inst.getItemWeight(genToTurnOff));

		}
		int genToTurnOn = rnd.nextInt(chromosoma_size);

		while (ind.getWeight() + inst.getItemWeight(genToTurnOn) <= inst
				.getCapacity()) {
			ind.setGen(genToTurnOn, 1, inst.getItemGain(genToTurnOn),
					inst.getItemWeight(genToTurnOn));
			genToTurnOn = rnd.nextInt(chromosoma_size);
		}
		return ind;
	}



}
