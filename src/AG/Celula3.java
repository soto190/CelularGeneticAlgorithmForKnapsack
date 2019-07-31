package AG;

import java.util.Random;

public class Celula3 extends Celula {

	public Celula3(Instance inst, double probCruza, double probMut, int tamPob) {
		super(inst, probCruza, probMut, tamPob);
		id = 3;
	}

	/**
	 * Cruza Crossover.
	 * 
	 * @param poblacion
	 * @return
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

				/*
				 * Si true se mantiene, en otro caso se intercambia por el alelo
				 * del padre2.
				 */
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
 * apaga el 10% de los genes y rellena hasta que vuelva a ser factible.
 */
	protected Individuo mutar(Individuo ind) {
		Random rnd = new Random();
		int totalGenesToChange = (int) (chromosoma_size * 0.10);

		for (int i = 0; i < totalGenesToChange; i++) {
			int genToTurnOff = rnd.nextInt(chromosoma_size);
			int genToTurnOn = rnd.nextInt(chromosoma_size);

			ind.setGen(genToTurnOff, 0, getInst().getItemGain(genToTurnOff),
					getInst().getItemWeight(genToTurnOff));

			if (ind.getWeight() + getInst().getItemWeight(genToTurnOn)
					- getInst().getItemWeight(genToTurnOff) <= getInst()
					.getCapacity())
				ind.setGen(genToTurnOn, 1, getInst().getItemGain(genToTurnOn),
						getInst().getItemWeight(genToTurnOn));

		}

		return ind;
	}

}
