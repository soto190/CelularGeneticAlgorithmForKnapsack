# CelularGeneticAlgorithmForKnapsack
A Celular Genetic algorithm to solve the Knapsack problem.

This project is the implementation of a Celular Genetic Algorithm (CGA) where each cell is a Genetic Algorithm with different probability configurations, mutation operator, and crossover operators. 
The CGA is composed of four cells:
- The first cell has a position based crossover and an insertion mutation.
- The second cell has a two-point crossover and a bit-flip mutation.
- The third cell has a uniform crossover and custom mutation. The mutation turns off randomly 10% of the genes (items); then some genes are randomly turned on until the sack is full.
- The fourth cell has a position based crossover and a custom mutation. The mutation turns off randomly five genes(items) of the sack; then some genes are randomly turned on until the sack is full.
