# Egalitarian Agreements using Genetic Algorithms

This project implements a genetic algorithm approach to find egalitarian agreements among multiple agents. It focuses on optimizing resource allocation by considering individual preferences and maximizing the minimum utility across all agents.

## Overview

The system works with a population of potential solutions (individuals), where each individual represents a specific resource assignment. Using genetic operations like selection, crossover, and mutation, the algorithm evolves better solutions over generations.

Key features:
- Resource allocation optimization using genetic algorithms
- Support for multiple agents with individual preferences
- Egalitarian approach that maximizes the minimum utility
- File-based persistence for preferences and results

## Structure

- `Engine`: Controls execution flow and user interaction
- `Algorithm`: Implements genetic operations (evolution, crossover, mutation)
- `Individual`: Represents a solution in the genetic population
- `Population`: Manages collections of individuals
- `FitnessCalc`: Calculates fitness based on agent preferences
- `WeightedRandomSelect`: Implements weighted selection for the genetic algorithm

## Usage

1. Run the program
2. Choose:
   - Option 1: Read existing data and analyze it
   - Option 2: Generate new random preferences

## Implementation Details

The genetic algorithm:
- Uses binary encoding (genes of 0s and 1s) to represent resource assignments
- Implements elitism to preserve the best solutions
- Uses weighted random selection to favor better individuals
- Performs uniform crossover and random mutation
- Evaluates solutions based on an egalitarian metric (maximizing minimum utility)

## Author

Jonathan Carrero  
Contact: joncarre@ucm.es
